package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.scenes

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.controllers.StageEffectController
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.FrameSprite
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageBackground
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageFloatingSprite
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageHud
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StagePlayer
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageProp
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageTorch
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageWaveSpike
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.MoveDirection
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageAssets
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageCatalog
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageCell
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageDataLoader
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageLayer
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageObjectSpec
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageObjectType
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageVisualConfig
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.res.BitmapPreloader
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(
    gctx: GameContext,
    private val stageNo: Int = FIRST_STAGE_NO,
) : Scene(gctx) {
    override val clipsRect = true

    private data class StageEntity(
        val type: StageObjectType,
        val objectRef: IGameObject,
        val tag: String?,
    )

    private val stageDefinition = StageCatalog.get(stageNo)
    private val stageData = StageDataLoader.load(gctx, stageDefinition.assetPath)
    private val objectVisuals = StageVisualConfig.objects(stageNo)
    private val floorCells = stageData.objects
        .filter { it.type == StageObjectType.FLOOR }
        .map { StageCell(it.col, it.row) }
        .toSet()
    private val stageEntities = mutableMapOf<StageCell, StageEntity>()
    private val stageCollectibles = mutableMapOf<StageCell, StageEntity>()
    private val stageHazards = mutableMapOf<StageCell, StageEntity>()
    private val stageProps = mutableSetOf<StageProp>()
    private val retiringPropTypes = mutableMapOf<StageProp, StageObjectType>()
    private val expiredProps = ArrayList<StageProp>()
    private val enemyCellsToDestroy = ArrayList<StageCell>()
    private val waveSpikes = mutableMapOf<StageCell, StageWaveSpike>()
    private val preloader = BitmapPreloader(gctx.res).apply {
        enqueue(TransitionScene.preloadResIds)
        enqueue(StageAssets.runtimeEffectPreloadFrames)
    }
    private val playerStart = stageData.objects.first { it.type == StageObjectType.PLAYER }
    private val targetCells = stageData.objects
        .filter { it.type == StageObjectType.TARGET }
        .map { StageCell(it.col, it.row) }
        .toSet()
    private var playerCell = StageCell(playerStart.col, playerStart.row)
    private var remainingMoves = stageData.moveCount
    private var hasKey = false
    private var waitsForClear = false
    private var pendingWaveSpikeDamageCheck = false
    private var isChangingScene = false
    private val player = StagePlayer(
        gctx,
        StageAssets.playerIdleFrames,
        StageAssets.playerMoveFrames,
        StageAssets.playerKickFrames,
        stageData.tileSize * PLAYER_HEIGHT_SCALE,
    ).apply {
        snapTo(cellCenterX(playerCell), playerCenterY(playerCell))
    }
    private val hud = StageHud(
        gctx = gctx,
        movesProvider = { remainingMoves },
        onRestart = ::restartStage,
        onMove = ::movePlayer,
    )

    override val world = World(StageLayer.entries.toTypedArray()).apply {
        add(StageBackground(gctx, stageDefinition.backgroundResId), StageLayer.BG)
        for (spec in stageData.objects) {
            when (spec.type) {
                StageObjectType.PLAYER -> Unit
                StageObjectType.FLOOR -> Unit
                else -> {
                    val obj = createStageObject(spec)
                    add(obj, layerOf(spec.type))
                    if (obj is StageProp) {
                        stageProps += obj
                    }
                    if (spec.type == StageObjectType.TARGET) {
                        add(createLoveSign(spec), StageLayer.CHARACTER)
                    }
                    if (spec.type != StageObjectType.TORCH) {
                        val cell = StageCell(spec.col, spec.row)
                        val entity = StageEntity(spec.type, obj, spec.tag)
                        if (spec.type == StageObjectType.SPIKE ||
                            spec.type == StageObjectType.MOVING_SPIKE
                        ) {
                            stageHazards[cell] = entity
                        } else if (spec.type == StageObjectType.KEY) {
                            stageCollectibles[cell] = entity
                        } else {
                            stageEntities[cell] = entity
                        }
                        if (obj is StageWaveSpike) {
                            waveSpikes[cell] = obj
                        }
                    }
                }
            }
        }
        add(player, StageLayer.PLAYER)
        add(hud, StageLayer.UI)
    }
    private val effects by lazy {
        StageEffectController(gctx, world, stageData.tileSize)
    }

    override fun onEnter() {
        gctx.res.sound.playMusic(R.raw.vitality)
    }

    override fun update(gctx: GameContext) {
        preloader.update()
        world.update(gctx)
        effects.cleanupFinished()
        cleanupFinishedObjects()
        if (isChangingScene || player.isBusy || stagePropsAreMoving()) return

        destroyEnemiesOnDamagingSpikes()

        if (pendingWaveSpikeDamageCheck) {
            pendingWaveSpikeDamageCheck = false
            if (isStandingOnActiveWaveSpike()) {
                applySpikeDamage()
            }
        }

        when {
            waitsForClear -> goNextStage()
        }
    }

    override fun touchObjects(): List<IGameObject>? {
        return world.objectsAt(StageLayer.UI)
    }

    override fun onBackPressed(): Boolean {
        openSettingScene()
        return true
    }

    private fun openSettingScene() {
        PauseScene(
            gctx,
            onSkipPuzzle = ::skipPuzzle,
            onMainMenu = ::goMainMenu,
        ).push()
    }

    private fun createStageObject(spec: StageObjectSpec): IGameObject {
        val cell = StageCell(spec.col, spec.row)
        val centerX = cellCenterX(cell)
        val centerY = cellCenterY(cell)
        val tileSize = stageData.tileSize

        return when (spec.type) {
            StageObjectType.FLOOR -> Sprite(gctx, R.mipmap.stage_floor).apply {
                setCenter(centerX, centerY)
                setSize(tileSize, tileSize)
            }
            StageObjectType.PLAYER -> error("Player object is created separately")
            StageObjectType.TARGET -> createTarget(centerX, centerY, tileSize)
            StageObjectType.ENEMY -> StageProp(
                gctx = gctx,
                idleResIds = StageAssets.enemyIdleFrames,
                height = tileSize * ENEMY_HEIGHT_SCALE,
                idleFps = ENEMY_IDLE_FPS,
                hitResIds = StageAssets.enemyHitFrames,
            ).apply {
                snapTo(centerX, centerY + tileSize * ENEMY_CENTER_Y_OFFSET)
            }
            StageObjectType.STONE -> StageProp(
                gctx,
                StageAssets.stoneResId(spec.tag),
                tileSize * stoneHeightScale(),
            ).apply {
                snapTo(centerX, centerY + tileSize * stoneCenterYOffset())
            }
            StageObjectType.TORCH -> {
                val isEmptyTorch = spec.tag?.startsWith(
                    TORCH_EMPTY_TAG,
                    ignoreCase = true,
                ) == true
                StageTorch(
                    gctx = gctx,
                    baseResId = if (isEmptyTorch) {
                        R.mipmap.flamebase0002
                    } else {
                        R.mipmap.flamebase0001
                    },
                    flameResIds = if (isEmptyTorch) {
                        emptyList()
                    } else {
                        StageAssets.fireFrames
                    },
                    fps = FIRE_FPS,
                    colorFilter = FIRE_COLOR_FILTER,
                ).apply {
                    val offset = StageVisualConfig.torchOffset(spec.tag)
                    setCenter(
                        centerX = centerX + tileSize * offset.x,
                        centerY = centerY + tileSize * offset.y,
                        baseWidth = tileSize * TORCH_BASE_WIDTH_SCALE,
                        baseHeight = tileSize * TORCH_BASE_HEIGHT_SCALE,
                        flameWidth = tileSize * FIRE_WIDTH_SCALE,
                        flameHeight = tileSize * FIRE_HEIGHT_SCALE,
                        flameOffsetX = tileSize * FIRE_CENTER_X_OFFSET,
                        flameOffsetY = tileSize * FIRE_CENTER_Y_OFFSET,
                    )
                }
            }
            StageObjectType.SPIKE -> FrameSprite(
                gctx = gctx,
                resIds = listOf(StageAssets.fixedSpikeResId),
                fps = 1f,
            ).apply {
                setCenterProportionalHeight(
                    centerX + tileSize * SPIKE_CENTER_X_OFFSET,
                    centerY + tileSize * SPIKE_CENTER_Y_OFFSET,
                    tileSize * SPIKE_HEIGHT_SCALE,
                )
            }
            StageObjectType.MOVING_SPIKE -> StageWaveSpike(
                gctx,
                StageAssets.waveSpikeFrames,
                StagePlayer.MOVE_DURATION,
                spec.tag.equals("active", ignoreCase = true),
            ).apply {
                setCenterProportionalHeight(
                    centerX + tileSize * SPIKE_CENTER_X_OFFSET,
                    centerY + tileSize * SPIKE_CENTER_Y_OFFSET,
                    tileSize * SPIKE_HEIGHT_SCALE,
                )
            }
            StageObjectType.KEY -> FrameSprite(
                gctx = gctx,
                resIds = StageAssets.keyFrames,
                fps = KEY_IDLE_FPS,
            ).apply {
                setCenterProportionalHeight(
                    centerX + tileSize * keyCenterXOffset(),
                    centerY + tileSize * keyCenterYOffset(),
                    tileSize * keyHeightScale(),
                )
            }
            StageObjectType.LOCKBOX -> StageProp(
                gctx,
                R.mipmap.stage_lockbox,
                tileSize * lockboxHeightScale(),
            ).apply {
                snapTo(centerX, centerY + tileSize * lockboxCenterYOffset())
            }
        }
    }

    private fun createTarget(centerX: Float, centerY: Float, tileSize: Float): IGameObject {
        val targetX = centerX + tileSize * TARGET_CENTER_X_OFFSET
        val targetY = centerY + tileSize * TARGET_CENTER_Y_OFFSET
        return FrameSprite(gctx, stageDefinition.targetFrames, TARGET_IDLE_FPS).apply {
            setCenterProportionalHeight(targetX, targetY, tileSize)
        }
    }

    private fun createLoveSign(spec: StageObjectSpec): IGameObject {
        val cell = StageCell(spec.col, spec.row)
        return StageFloatingSprite(
            gctx = gctx,
            resId = R.mipmap.lovesign,
            height = stageData.tileSize * LOVE_SIGN_HEIGHT_SCALE,
            amplitude = stageData.tileSize * LOVE_SIGN_BOB_AMPLITUDE,
            cycleSeconds = LOVE_SIGN_BOB_CYCLE_SECONDS,
        ).apply {
            setCenter(
                centerX = cellCenterX(cell) + stageData.tileSize * LOVE_SIGN_X_OFFSET,
                centerY = cellCenterY(cell) + stageData.tileSize * LOVE_SIGN_Y_OFFSET,
            )
        }
    }

    private fun layerOf(type: StageObjectType): StageLayer {
        return when (type) {
            StageObjectType.FLOOR -> StageLayer.TILE
            StageObjectType.SPIKE,
            StageObjectType.MOVING_SPIKE -> StageLayer.SPIKE
            StageObjectType.STONE,
            StageObjectType.TORCH,
            StageObjectType.KEY,
            StageObjectType.LOCKBOX -> StageLayer.OBJECT
            StageObjectType.TARGET,
            StageObjectType.ENEMY -> StageLayer.CHARACTER
            StageObjectType.PLAYER -> StageLayer.PLAYER
        }
    }

    private fun movePlayer(direction: MoveDirection) {
        if (isChangingScene || player.isBusy || stagePropsAreMoving()) return

        if (remainingMoves <= 0) {
            goDeathScene()
            return
        }

        val nextCell = playerCell.moved(direction)
        if (!floorCells.contains(nextCell)) return

        val entity = stageEntities[nextCell]
        var clearsAfterMove = false
        when (entity?.type) {
            StageObjectType.STONE,
            StageObjectType.ENEMY -> {
                kickEntity(nextCell, direction, entity)
                finishTurn(isPlayerOnDamagingSpike())
                return
            }
            StageObjectType.LOCKBOX -> {
                if (!hasKey) {
                    player.kick(direction)
                    effects.spawnImpact(cellCenterX(nextCell), cellCenterY(nextCell))
                    gctx.res.sound.playOneShot(R.raw.door_closed_kick_01)
                    finishTurn(isPlayerOnDamagingSpike())
                    return
                }
                clearsAfterMove = entity.tag.equals(GOAL_TAG, ignoreCase = true)
                removeEntity(nextCell)
                gctx.res.sound.playOneShot(R.raw.door_opening_01)
            }
            StageObjectType.TARGET -> return
            StageObjectType.FLOOR,
            StageObjectType.PLAYER,
            StageObjectType.TORCH,
            StageObjectType.SPIKE,
            StageObjectType.MOVING_SPIKE,
            StageObjectType.KEY,
            null -> Unit
        }

        val previousCell = playerCell
        playerCell = nextCell
        collectKey(nextCell)
        waitsForClear = clearsAfterMove || isAdjacentToTarget(nextCell)
        player.moveTo(cellCenterX(nextCell), playerCenterY(nextCell), direction)
        finishTurn(isPlayerOnDamagingSpike())
        gctx.res.sound.playOneShot(R.raw.character_move_01)
        effects.spawnMoveDust(cellCenterX(previousCell), cellCenterY(previousCell))
    }

    private fun kickEntity(cell: StageCell, direction: MoveDirection, entity: StageEntity) {
        val prop = entity.objectRef as? StageProp ?: return
        val pushedCell = cell.moved(direction)
        val destinationHasKey = stageCollectibles.containsKey(pushedCell)
        val canOverlapKey = entity.type == StageObjectType.STONE
        val canPush = floorCells.contains(pushedCell) &&
            !stageEntities.containsKey(pushedCell) &&
            (!destinationHasKey || canOverlapKey)
        val destroysEnemy = entity.type == StageObjectType.ENEMY && !canPush
        val destroysEnemyOnHazard = entity.type == StageObjectType.ENEMY &&
            canPush &&
            willDamageEnemyAfterTurn(pushedCell)

        if (canPush) {
            stageEntities.remove(cell)
            stageEntities[pushedCell] = entity
            prop.moveTo(cellCenterX(pushedCell), propCenterY(entity.type, pushedCell))
        } else if (destroysEnemy) {
            stageEntities.remove(cell)
            retiringPropTypes[prop] = entity.type
            prop.expireImmediately()
            effects.spawnShatter(
                cellCenterX(cell),
                propCenterY(StageObjectType.ENEMY, cell) -
                    stageData.tileSize * SHATTER_CENTER_Y_OFFSET,
            )
        } else {
            prop.moveTo(cellCenterX(cell), propCenterY(entity.type, cell))
        }

        player.kick(direction)
        if (!destroysEnemy && !destroysEnemyOnHazard) {
            effects.spawnImpact(cellCenterX(cell), cellCenterY(cell))
        }
        val soundId = when {
            entity.type == StageObjectType.STONE -> R.raw.stone_move_01
            destroysEnemy -> R.raw.enemy_die_01
            destroysEnemyOnHazard -> null
            else -> R.raw.enemy_kick_01
        }
        if (soundId != null) {
            gctx.res.sound.playOneShot(soundId)
        }
    }

    private fun removeEntity(cell: StageCell) {
        val entity = stageEntities.remove(cell) ?: return
        world.remove(entity.objectRef, layerOf(entity.type))
        (entity.objectRef as? StageProp)?.let(stageProps::remove)
    }

    private fun collectKey(cell: StageCell) {
        val key = stageCollectibles.remove(cell) ?: return
        hasKey = true
        world.remove(key.objectRef, layerOf(key.type))
        gctx.res.sound.playOneShot(R.raw.key_pick_up_01)
    }

    private fun stagePropsAreMoving(): Boolean {
        return stageProps.any { it.isMoving }
    }

    private fun finishTurn(takesSpikeDamage: Boolean) {
        remainingMoves--
        if (takesSpikeDamage) {
            applySpikeDamage()
        }
        if (waveSpikes.isNotEmpty()) {
            advanceWaveSpikes()
            pendingWaveSpikeDamageCheck = true
        }
    }

    private fun cleanupFinishedObjects() {
        expiredProps.clear()
        for (prop in stageProps) {
            if (prop.isExpired) expiredProps.add(prop)
        }
        for (prop in expiredProps) {
            val type = retiringPropTypes.remove(prop) ?: continue
            world.remove(prop, layerOf(type))
            stageProps.remove(prop)
        }
    }

    private fun isAdjacentToTarget(cell: StageCell): Boolean {
        return targetCells.any { targetCell ->
            val distance = kotlin.math.abs(cell.col - targetCell.col) +
                kotlin.math.abs(cell.row - targetCell.row)
            distance == 1
        }
    }

    private fun applySpikeDamage() {
        remainingMoves--
        player.takeSpikeHit()
        hud.notifyDamage()
        effects.spawnSpikeDamage(
            centerX = cellCenterX(playerCell),
            centerY = playerCenterY(playerCell),
        )
        gctx.res.sound.playOneShot(R.raw.spikes_damage_01)
    }

    private fun advanceWaveSpikes() {
        for (spike in waveSpikes.values) {
            spike.advance()
        }
    }

    private fun destroyEnemiesOnDamagingSpikes() {
        enemyCellsToDestroy.clear()
        for ((cell, entity) in stageEntities) {
            if (entity.type == StageObjectType.ENEMY && isDamagingHazard(cell)) {
                enemyCellsToDestroy.add(cell)
            }
        }

        for (cell in enemyCellsToDestroy) {
            val entity = stageEntities.remove(cell) ?: continue
            val prop = entity.objectRef as? StageProp ?: continue
            retiringPropTypes[prop] = entity.type
            prop.expireImmediately()
            effects.spawnShatter(
                cellCenterX(cell),
                propCenterY(StageObjectType.ENEMY, cell) -
                    stageData.tileSize * SHATTER_CENTER_Y_OFFSET,
            )
            gctx.res.sound.playOneShot(R.raw.enemy_die_01)
        }
    }

    private fun willDamageEnemyAfterTurn(cell: StageCell): Boolean {
        val hazard = stageHazards[cell] ?: return false
        return when (hazard.type) {
            StageObjectType.SPIKE -> true
            StageObjectType.MOVING_SPIKE ->
                (hazard.objectRef as? StageWaveSpike)?.isActive == false
            else -> false
        }
    }

    private fun isDamagingHazard(cell: StageCell): Boolean {
        val hazard = stageHazards[cell] ?: return false
        return when (hazard.type) {
            StageObjectType.SPIKE -> true
            StageObjectType.MOVING_SPIKE ->
                (hazard.objectRef as? StageWaveSpike)?.isActive == true
            else -> false
        }
    }

    private fun isStandingOnActiveWaveSpike(): Boolean {
        val entity = stageHazards[playerCell] ?: return false
        if (entity.type != StageObjectType.MOVING_SPIKE) return false
        return (entity.objectRef as? StageWaveSpike)?.isActive == true
    }

    private fun isPlayerOnDamagingSpike(): Boolean {
        val entity = stageHazards[playerCell] ?: return false
        return when (entity.type) {
            StageObjectType.SPIKE -> true
            StageObjectType.MOVING_SPIKE ->
                (entity.objectRef as? StageWaveSpike)?.isActive == true
            else -> false
        }
    }

    private fun cellCenterX(cell: StageCell): Float {
        return stageData.originX + cell.col * stageData.tileSize
    }

    private fun cellCenterY(cell: StageCell): Float {
        return stageData.originY + cell.row * stageData.tileSize
    }

    private fun playerCenterY(cell: StageCell): Float {
        return cellCenterY(cell) + stageData.tileSize * PLAYER_CENTER_Y_OFFSET
    }

    private fun propCenterY(type: StageObjectType, cell: StageCell): Float {
        val centerY = cellCenterY(cell)
        return when (type) {
            StageObjectType.ENEMY -> centerY + stageData.tileSize * ENEMY_CENTER_Y_OFFSET
            StageObjectType.STONE -> centerY + stageData.tileSize * stoneCenterYOffset()
            else -> centerY
        }
    }

    private fun stoneHeightScale(): Float {
        return objectVisuals.stoneHeightScale
    }

    private fun stoneCenterYOffset(): Float {
        return objectVisuals.stoneCenterYOffset
    }

    private fun keyHeightScale(): Float {
        return objectVisuals.keyHeightScale
    }

    private fun keyCenterXOffset(): Float {
        return objectVisuals.keyCenterXOffset
    }

    private fun keyCenterYOffset(): Float {
        return objectVisuals.keyCenterYOffset
    }

    private fun lockboxHeightScale(): Float {
        return objectVisuals.lockboxHeightScale
    }

    private fun lockboxCenterYOffset(): Float {
        return objectVisuals.lockboxCenterYOffset
    }

    private fun restartStage() {
        isChangingScene = true
        TransitionScene(gctx) { MainScene(gctx, stageNo) }.change()
    }

    private fun skipPuzzle() {
        isChangingScene = true
        val nextStageNo = StageCatalog.nextStageNo(stageNo)
        TransitionScene(gctx) {
            if (nextStageNo == null) {
                GameClearScene(gctx)
            } else {
                MainScene(gctx, nextStageNo)
            }
        }.change()
    }

    private fun goDeathScene() {
        isChangingScene = true
        DeathScene(
            gctx = gctx,
            originX = player.centerX,
            originY = player.centerY,
        ) {
            MainScene(gctx, stageNo)
        }.change()
    }

    private fun goNextStage() {
        isChangingScene = true
        val nextStageNo = StageCatalog.nextStageNo(stageNo)
        val dialogue = stageDefinition.dialogue
        if (nextStageNo == null || dialogue == null) {
            GameClearScene(gctx).change()
            return
        }

        val (effectCenterX, effectCenterY) = clearEffectCenter()
        StageDialogueScene(
            gctx = gctx,
            stageDefinition = stageDefinition,
            boardScene = this,
            successCenterX = effectCenterX,
            successCenterY = effectCenterY,
            nextSceneFactory = { MainScene(gctx, nextStageNo) },
            restartSceneFactory = { MainScene(gctx, stageNo) },
            preloadResIds = StageCatalog.get(nextStageNo).preloadResIds,
        ).change()
    }

    private fun clearEffectCenter(): Pair<Float, Float> {
        val nearestTarget = targetCells.minByOrNull { targetCell ->
            kotlin.math.abs(playerCell.col - targetCell.col) +
                kotlin.math.abs(playerCell.row - targetCell.row)
        } ?: return player.centerX to player.centerY
        val targetX = cellCenterX(nearestTarget)
        val targetY = cellCenterY(nearestTarget)
        return (player.centerX + targetX) * 0.5f to
            (player.centerY + targetY) * 0.5f
    }

    private fun goMainMenu() {
        isChangingScene = true
        StartScene(gctx).change()
    }

    companion object {
        private const val FIRST_STAGE_NO = 1
        private const val GOAL_TAG = "goal"
        private const val TORCH_EMPTY_TAG = "empty"
        private const val FIRE_FPS = 12f
        private const val TORCH_BASE_WIDTH_SCALE = 0.78f
        private const val TORCH_BASE_HEIGHT_SCALE = 0.84f
        private const val FIRE_CENTER_X_OFFSET = 0f
        private const val FIRE_CENTER_Y_OFFSET = -0.50f
        private const val FIRE_WIDTH_SCALE = 0.54f
        private const val FIRE_HEIGHT_SCALE = 0.68f
        private const val SPIKE_CENTER_X_OFFSET = 0f
        private const val SPIKE_CENTER_Y_OFFSET = 0.22f
        private const val SPIKE_HEIGHT_SCALE = 2.0f
        private const val PLAYER_HEIGHT_SCALE = 2.20f
        private const val PLAYER_CENTER_Y_OFFSET = 0.16f
        private const val ENEMY_HEIGHT_SCALE = 2.20f
        private const val ENEMY_CENTER_Y_OFFSET = 0.23f
        private const val ENEMY_IDLE_FPS = 12f
        private const val TARGET_CENTER_X_OFFSET = 0.105f
        private const val TARGET_CENTER_Y_OFFSET = -0.02f
        private const val TARGET_IDLE_FPS = 12f
        private const val KEY_IDLE_FPS = 12f
        private const val LOVE_SIGN_X_OFFSET = -0.45f
        private const val LOVE_SIGN_Y_OFFSET = -0.415f
        private const val LOVE_SIGN_HEIGHT_SCALE = 0.40f
        private const val LOVE_SIGN_BOB_AMPLITUDE = 0.08f
        private const val LOVE_SIGN_BOB_CYCLE_SECONDS = 0.90f
        private const val SHATTER_CENTER_Y_OFFSET = 0.14f
        private val FIRE_COLOR_FILTER = PorterDuffColorFilter(
            Color.rgb(247, 194, 57),
            PorterDuff.Mode.SRC_IN,
        )
    }
}

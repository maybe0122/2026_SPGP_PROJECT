package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.controllers

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageEffect
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.StageShatterEffect
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageAssets
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageLayer
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageEffectController(
    private val gctx: GameContext,
    private val world: World<StageLayer>,
    private val tileSize: Float,
) {
    private val effects = mutableSetOf<StageEffect>()
    private val shatterEffects = mutableSetOf<StageShatterEffect>()
    private val finishedEffects = ArrayList<StageEffect>()
    private val finishedShatters = ArrayList<StageShatterEffect>()

    fun spawnMoveDust(centerX: Float, centerY: Float) {
        add(
            StageEffect(
                gctx = gctx,
                frameResIds = StageAssets.moveDustFrames,
                fps = MOVE_DUST_FPS,
                height = tileSize,
                colorFilter = MOVE_DUST_COLOR_FILTER,
            ).apply {
                setCenter(centerX, centerY)
            },
        )
    }

    fun spawnImpact(centerX: Float, centerY: Float) {
        add(
            StageEffect(
                gctx = gctx,
                frameResIds = StageAssets.impactFrames,
                fps = IMPACT_FPS,
                height = tileSize * IMPACT_HEIGHT_SCALE,
            ).apply {
                setCenter(centerX, centerY)
            },
        )
    }

    fun spawnShatter(centerX: Float, centerY: Float) {
        val effect = StageShatterEffect(
            gctx = gctx,
            bitmapResIds = StageAssets.shatterPieces,
            originX = centerX,
            originY = centerY,
            tileSize = tileSize,
        )
        shatterEffects.add(effect)
        world.add(effect, StageLayer.EFFECT)
    }

    fun spawnSpikeDamage(centerX: Float, centerY: Float) {
        add(
            StageEffect(
                gctx = gctx,
                frameResIds = StageAssets.spikeDamageRingFrames,
                fps = SPIKE_DAMAGE_RING_FPS,
                height = tileSize * SPIKE_DAMAGE_RING_HEIGHT_SCALE,
                colorFilter = SPIKE_DAMAGE_RING_COLOR_FILTER,
            ).apply {
                setCenter(centerX, centerY)
            },
        )
        add(
            StageEffect(
                gctx = gctx,
                frameResIds = StageAssets.spikeDamageSmokeFrames,
                fps = SPIKE_DAMAGE_SMOKE_FPS,
                height = tileSize * SPIKE_DAMAGE_SMOKE_HEIGHT_SCALE,
            ).apply {
                setCenter(centerX, centerY)
            },
        )
    }

    fun cleanupFinished() {
        finishedEffects.clear()
        for (effect in effects) {
            if (effect.isFinished) finishedEffects.add(effect)
        }
        for (effect in finishedEffects) {
            world.remove(effect, StageLayer.EFFECT)
            effects.remove(effect)
        }

        finishedShatters.clear()
        for (effect in shatterEffects) {
            if (effect.isFinished) finishedShatters.add(effect)
        }
        for (effect in finishedShatters) {
            world.remove(effect, StageLayer.EFFECT)
            shatterEffects.remove(effect)
        }
    }

    private fun add(effect: StageEffect) {
        effects.add(effect)
        world.add(effect, StageLayer.EFFECT)
    }

    companion object {
        private const val MOVE_DUST_FPS = 24f
        private const val IMPACT_FPS = 30f
        private const val IMPACT_HEIGHT_SCALE = 2.28f
        private const val SPIKE_DAMAGE_RING_FPS = 24f
        private const val SPIKE_DAMAGE_SMOKE_FPS = 30f
        private const val SPIKE_DAMAGE_RING_HEIGHT_SCALE = 3.0f
        private const val SPIKE_DAMAGE_SMOKE_HEIGHT_SCALE = 2.7f
        private val MOVE_DUST_COLOR_FILTER = PorterDuffColorFilter(
            Color.rgb(241, 143, 158),
            PorterDuff.Mode.SRC_IN,
        )
        private val SPIKE_DAMAGE_RING_COLOR_FILTER = PorterDuffColorFilter(
            Color.WHITE,
            PorterDuff.Mode.SRC_IN,
        )
    }
}

package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.scenes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageAssets
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

private data class CutscenePanel(
    val bitmap: Bitmap,
    val lines: List<String>,
)

private class SkullTransitionRenderer(gctx: GameContext) {
    private val bitmaps = TransitionScene.preloadResIds.map { gctx.res.getBitmap(it) }
    private val frameRect = RectF()

    val duration: Float
        get() = bitmaps.size / FPS

    fun frameIndex(elapsed: Float): Int {
        return (elapsed * FPS).toInt().coerceIn(0, bitmaps.lastIndex)
    }

    fun revealsDestination(frameIndex: Int): Boolean {
        return frameIndex >= OPENING_FRAME_INDEX
    }

    fun draw(canvas: Canvas, elapsed: Float) {
        val frameIndex = frameIndex(elapsed)
        val bitmap = bitmaps[frameIndex]
        val scale = SCREEN_WIDTH / REFERENCE_FRAME_WIDTH
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        val left = (SCREEN_WIDTH - width) * 0.5f
        val top = when {
            frameIndex >= BOTTOM_ALIGNED_FRAME_INDEX -> SCREEN_HEIGHT - height
            else -> (SCREEN_HEIGHT - height) * 0.5f
        }
        frameRect.set(left, top, left + width, top + height)
        canvas.drawBitmap(bitmap, null, frameRect, null)
    }

    companion object {
        const val FPS = 20f
        const val PART2_DELAY = 1.088f
        private const val SCREEN_WIDTH = 1600f
        private const val SCREEN_HEIGHT = 900f
        private const val REFERENCE_FRAME_WIDTH = 1924f
        private const val OPENING_FRAME_INDEX = 23
        private const val BOTTOM_ALIGNED_FRAME_INDEX = 26
    }
}

private val TRANSITION_FRAMES = listOf(
            R.mipmap.transition0002,
            R.mipmap.transition0003,
            R.mipmap.transition0004,
            R.mipmap.transition0005,
            R.mipmap.transition0006,
            R.mipmap.transition0007,
            R.mipmap.transition0008,
            R.mipmap.transition0009,
            R.mipmap.transition0010,
            R.mipmap.transition0011,
            R.mipmap.transition0012,
            R.mipmap.transition0013,
            R.mipmap.transition0014,
            R.mipmap.transition0015,
            R.mipmap.transition0016,
            R.mipmap.transition0017,
            R.mipmap.transition0018,
            R.mipmap.transition0019,
            R.mipmap.transition0020,
            R.mipmap.transition0021,
            R.mipmap.transition0022,
            R.mipmap.transition0023,
            R.mipmap.transition0024,
            R.mipmap.transition0025,
            R.mipmap.transition0026,
            R.mipmap.transition0027,
            R.mipmap.transition0028,
            R.mipmap.transition0029,
            R.mipmap.transition0030,
)

class PrologueCutsceneScene(
    gctx: GameContext,
    private val nextSceneFactory: () -> Scene,
) : Scene(gctx) {
    override val clipsRect = true

    private enum class Phase {
        PANEL_REVEAL,
        PANEL_HOLD,
        PANEL_SWAP,
        TRANSITION_LEAD_IN,
        TRANSITION,
    }

    private val panels = PANEL_DATA.map { (resId, lines) ->
        CutscenePanel(gctx.res.getBitmap(resId), lines)
    }
    private val booperPrompt = BooperPrompt(gctx)
    private val transition = SkullTransitionRenderer(gctx)
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(111, 31, 55)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(236, 230, 234)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 29f
    }
    private var phase = Phase.PANEL_REVEAL
    private var phaseElapsed = 0f
    private var panelIndex = 0
    private var playedTransitionPart2 = false
    private var destinationScene: Scene? = null

    override fun onEnter() {
        gctx.res.sound.stopMusic()
    }

    override fun update(gctx: GameContext) {
        phaseElapsed += gctx.frameTime
        booperPrompt.update(gctx.frameTime)

        when (phase) {
            Phase.PANEL_REVEAL -> {
                if (phaseElapsed >= PANEL_REVEAL_DURATION) {
                    changePhase(Phase.PANEL_HOLD)
                }
            }
            Phase.PANEL_SWAP -> {
                if (phaseElapsed >= panelSwapDuration()) {
                    panelIndex++
                    changePhase(Phase.PANEL_HOLD)
                }
            }
            Phase.TRANSITION_LEAD_IN -> {
                if (!playedTransitionPart2 && phaseElapsed >= SkullTransitionRenderer.PART2_DELAY) {
                    playedTransitionPart2 = true
                    gctx.res.sound.playOneShot(R.raw.screen_changer_part2_01)
                }
                if (phaseElapsed >= TRANSITION_VISUAL_DELAY) {
                    changePhase(Phase.TRANSITION)
                }
            }
            Phase.TRANSITION -> {
                if (phaseElapsed >= transition.duration) {
                    destinationScene().change()
                }
            }
            Phase.PANEL_HOLD -> Unit
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 22))
        when (phase) {
            Phase.PANEL_REVEAL -> {
                val progress = smoothStep(phaseElapsed / PANEL_REVEAL_DURATION)
                drawAnimatedPanel(
                    canvas,
                    panelIndex,
                    lerp(0.30f, 1f, progress),
                    lerp(4f, 0f, progress),
                    progress,
                )
            }
            Phase.PANEL_HOLD,
            Phase.TRANSITION_LEAD_IN -> drawSettledPanel(canvas, panelIndex)
            Phase.PANEL_SWAP -> {
                drawPanelSwap(
                    canvas,
                    panelIndex,
                    panelIndex + 1,
                    phaseElapsed / panelSwapDuration(),
                )
            }
            Phase.TRANSITION -> drawTransition(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true
        if (phase != Phase.PANEL_HOLD) return true

        gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
        if (panelIndex < panels.lastIndex) {
            changePhase(Phase.PANEL_SWAP)
        } else {
            playedTransitionPart2 = false
            gctx.res.sound.playOneShot(R.raw.screen_changer_part1_01)
            changePhase(Phase.TRANSITION_LEAD_IN)
        }
        return true
    }

    private fun drawTransition(canvas: Canvas) {
        val frameIndex = transition.frameIndex(phaseElapsed)
        if (transition.revealsDestination(frameIndex)) {
            destinationScene().draw(canvas)
        } else {
            drawSettledPanel(canvas, panels.lastIndex)
        }
        transition.draw(canvas, phaseElapsed)
    }

    private fun drawPanelSwap(canvas: Canvas, outgoingIndex: Int, incomingIndex: Int, progress: Float) {
        if (progress < 0.5f) {
            val t = smoothStep(progress * 2f)
            drawAnimatedPanel(canvas, outgoingIndex, lerp(1f, 0.30f, t), lerp(0f, 4f, t), 1f - t)
        } else {
            val t = smoothStep((progress - 0.5f) * 2f)
            drawAnimatedPanel(canvas, incomingIndex, lerp(0.30f, 1f, t), lerp(-4f, 0f, t), t)
        }
    }

    private fun drawSettledPanel(canvas: Canvas, panelIndex: Int) {
        drawAnimatedPanel(canvas, panelIndex, 1f, 0f, 1f)
    }

    private fun panelSwapDuration(): Float {
        return if (panelIndex == 0) FIRST_SWAP_DURATION else SECOND_SWAP_DURATION
    }

    private fun changePhase(nextPhase: Phase) {
        phase = nextPhase
        phaseElapsed = 0f
    }

    private fun drawAnimatedPanel(
        canvas: Canvas,
        panelIndex: Int,
        scale: Float,
        rotation: Float,
        contentAlpha: Float,
    ) {
        val panel = panels[panelIndex]
        val centerX = PANEL_RECT.centerX()
        val centerY = PANEL_RECT.centerY()

        canvas.save()
        canvas.rotate(BACK_CARD_ROTATION + rotation * 0.35f, centerX, centerY)
        canvas.scale(scale * BACK_CARD_SCALE, scale * BACK_CARD_SCALE, centerX, centerY)
        canvas.drawRect(PANEL_RECT, cardPaint)
        canvas.restore()

        imagePaint.alpha = (255f * contentAlpha).toInt().coerceIn(0, 255)
        canvas.save()
        canvas.rotate(rotation, centerX, centerY)
        canvas.scale(scale, scale, centerX, centerY)
        canvas.drawBitmap(panel.bitmap, null, PANEL_RECT, imagePaint)
        canvas.restore()

        textPaint.alpha = imagePaint.alpha
        for ((index, line) in panel.lines.withIndex()) {
            canvas.drawText(line, SCREEN_CENTER_X, CAPTION_FIRST_Y + index * CAPTION_LINE_GAP, textPaint)
        }
        textPaint.alpha = 255
        booperPrompt.draw(canvas, SCREEN_CENTER_X, BOOPER_CENTER_Y)
    }

    private fun destinationScene(): Scene {
        return destinationScene ?: nextSceneFactory().also { destinationScene = it }
    }

    private fun smoothStep(value: Float): Float {
        val t = value.coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    companion object {
        private const val SCREEN_CENTER_X = 800f
        private const val CAPTION_FIRST_Y = 681f
        private const val CAPTION_LINE_GAP = 35f
        private const val BOOPER_CENTER_Y = 783f
        private const val BACK_CARD_SCALE = 1.035f
        private const val BACK_CARD_ROTATION = 2.2f
        private const val PANEL_REVEAL_DURATION = 0.100f
        private const val FIRST_SWAP_DURATION = 0.100f
        private const val SECOND_SWAP_DURATION = 0.150f
        private const val TRANSITION_VISUAL_DELAY = 1.355f
        private val PANEL_RECT = RectF(267f, 78f, 1333f, 621f)
        private val PANEL_DATA = listOf(
            R.mipmap.cutscene0002 to listOf(
                "You woke up one day with a dream.",
                "Harem full of demon girls.",
            ),
            R.mipmap.cutscene0003 to listOf(
                "It was, however, not an easy dream to achieve.",
                "It could cost you your life.",
            ),
            R.mipmap.cutscene0004 to listOf(
                "\"When demon girls are involved, no price is high enough.\"",
                "You said, as you ventured down to hell.",
            ),
        )
        internal val preloadResIds =
            PANEL_DATA.map { it.first } + TransitionScene.preloadResIds
    }
}

class TransitionScene(
    gctx: GameContext,
    private val nextSceneFactory: () -> Scene,
) : Scene(gctx) {
    override val clipsRect = true

    private val transition = SkullTransitionRenderer(gctx)
    private var elapsed = 0f
    private var playedPart2 = false
    private var destinationScene: Scene? = null

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.screen_changer_part1_01)
    }

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        if (destinationScene == null && elapsed >= DESTINATION_PREPARE_TIME) {
            destinationScene()
        }
        if (!playedPart2 && elapsed >= SkullTransitionRenderer.PART2_DELAY) {
            playedPart2 = true
            gctx.res.sound.playOneShot(R.raw.screen_changer_part2_01)
        }
        if (elapsed >= transition.duration) {
            destinationScene().change()
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val frameIndex = transition.frameIndex(elapsed)
        if (transition.revealsDestination(frameIndex)) {
            destinationScene().draw(canvas)
        }
        transition.draw(canvas, elapsed)
    }

    private fun destinationScene(): Scene {
        return destinationScene ?: nextSceneFactory().also { destinationScene = it }
    }

    companion object {
        private const val DESTINATION_PREPARE_TIME = 0.18f
        internal val preloadResIds = TRANSITION_FRAMES
    }
}

class DeathScene(
    gctx: GameContext,
    private val originX: Float,
    private val originY: Float,
    private val nextScene: () -> Scene,
) : Scene(gctx) {
    override val clipsRect = true

    private val bitmaps = StageAssets.deathFrames.map { gctx.res.getBitmap(it) }
    private val frameRect = RectF()
    private var elapsed = 0f

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.player_death_01)
    }

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        if (elapsed >= bitmaps.size / FPS + HOLD_DURATION) {
            TransitionScene(gctx, nextScene).change()
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 27))
        val bitmap = currentBitmap()
        val width = DEATH_HEIGHT * bitmap.width / bitmap.height.toFloat()
        val top = originY - DEATH_HEIGHT * DEATH_ANCHOR_Y
        frameRect.set(
            originX - width / 2f,
            top,
            originX + width / 2f,
            top + DEATH_HEIGHT,
        )
        canvas.drawBitmap(bitmap, null, frameRect, null)
    }

    private fun currentBitmap(): Bitmap {
        val index = ((elapsed * FPS).toInt()).coerceIn(0, bitmaps.lastIndex)
        return bitmaps[index]
    }

    companion object {
        private const val FPS = 14f
        private const val HOLD_DURATION = 0.15f
        private const val DEATH_HEIGHT = 616f
        private const val DEATH_ANCHOR_Y = 0.84f
    }
}

class GameClearScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true

    private data class EndingPage(
        val bitmap: Bitmap?,
        val lines: List<String>,
    )

    private val pages = ENDING_PAGES.map { (resId, lines) ->
        EndingPage(resId?.let(gctx.res::getBitmap), lines)
    }
    private val booperPrompt = BooperPrompt(gctx)
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(111, 31, 55)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(236, 230, 234)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 28f
    }
    private var pageIndex = 0
    private var isChangingScene = false

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.dialogue_start_01)
    }

    override fun update(gctx: GameContext) {
        booperPrompt.update(gctx.frameTime)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 22))
        val page = pages[pageIndex]
        page.bitmap?.let {
            canvas.save()
            canvas.rotate(BACK_CARD_ROTATION, PANEL_RECT.centerX(), PANEL_RECT.centerY())
            canvas.scale(BACK_CARD_SCALE, BACK_CARD_SCALE, PANEL_RECT.centerX(), PANEL_RECT.centerY())
            canvas.drawRect(PANEL_RECT, cardPaint)
            canvas.restore()
            canvas.drawBitmap(it, null, PANEL_RECT, imagePaint)
        }
        for ((index, line) in page.lines.withIndex()) {
            canvas.drawText(line, SCREEN_CENTER_X, CAPTION_FIRST_Y + index * CAPTION_LINE_GAP, textPaint)
        }
        booperPrompt.draw(canvas, SCREEN_CENTER_X, BOOPER_CENTER_Y)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN || isChangingScene) return true

        gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
        if (pageIndex < pages.lastIndex) {
            pageIndex++
            return true
        }

        isChangingScene = true
        TransitionScene(gctx) { StartScene(gctx) }.change()
        return true
    }

    companion object {
        private const val SCREEN_CENTER_X = 800f
        private const val CAPTION_FIRST_Y = 681f
        private const val CAPTION_LINE_GAP = 35f
        private const val BOOPER_CENTER_Y = 783f
        private const val BACK_CARD_SCALE = 1.035f
        private const val BACK_CARD_ROTATION = 2.2f
        private val PANEL_RECT = RectF(267f, 78f, 1333f, 621f)
        private val ENDING_PAGES = listOf(
            null to listOf(
                "Thus, your journey has come to an end.",
            ),
            R.mipmap.cutscene0006 to listOf(
                "You have successfully took girls from hell.",
                "And was henceforth known as the Helltaker.",
            ),
            R.mipmap.cutscene0007 to listOf(
                "It was, however, not an easy life to live.",
                "It was sure to be short and full of suffering.",
            ),
            R.mipmap.cutscene0008 to listOf(
                "But life is full of suffering no matter how you live it.",
                "So you might as well have some fun when you're at it.",
            ),
        )
    }
}

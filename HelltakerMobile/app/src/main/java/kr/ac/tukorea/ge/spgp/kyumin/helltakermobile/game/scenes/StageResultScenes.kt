package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.scenes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageAssets
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageChoice
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageChoiceResult
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageDefinition
import kr.ac.tukorea.ge.spgp2026.a2dg.res.BitmapPreloader
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageDialogueScene(
    gctx: GameContext,
    private val stageDefinition: StageDefinition,
    private val boardScene: Scene,
    private val successCenterX: Float,
    private val successCenterY: Float,
    private val nextSceneFactory: () -> Scene,
    private val restartSceneFactory: () -> Scene,
    preloadResIds: List<Int> = emptyList(),
) : Scene(gctx) {
    override val clipsRect = true

    private enum class Phase {
        PROMPT,
        CHOICES,
        RESPONSE,
        SUCCESS_EFFECT,
    }

    private val dialogue = requireNotNull(stageDefinition.dialogue)
    private val backgroundBitmap = gctx.res.getBitmap(R.mipmap.dialogue_bg_hell)
    private val buttonBitmap = gctx.res.getBitmap(R.mipmap.menu_button)
    private val selectedButtonBitmap = gctx.res.getBitmap(R.mipmap.menu_button_selected)
    private val successBitmaps = StageAssets.successFrames.map(gctx.res::getBitmap)
    private val loveExplosionBitmaps = StageAssets.loveExplosionFrames.map(gctx.res::getBitmap)
    private val booperPrompt = BooperPrompt(gctx)
    private val portraitRect = RectF()
    private val choiceDrawRects = Array(CHOICE_RECTS.size) { RectF() }
    private val successEffectRect = RectF()
    private val preloader = BitmapPreloader(gctx.res).apply {
        enqueue(TransitionScene.preloadResIds)
        enqueue(preloadResIds)
    }
    private var phase = Phase.PROMPT
    private var phaseElapsed = 0f
    private var selectedChoiceIndex: Int? = null
    private var selectedChoice: StageChoice? = null

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 61, 73)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 27f
    }
    private val bodyPaint = Paint(titlePaint).apply {
        color = Color.rgb(238, 232, 235)
        textSize = 24f
    }
    private val choicePaint = Paint(bodyPaint).apply {
        textSize = 21f
    }
    private val normalButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(112, 70, 82), PorterDuff.Mode.SRC_IN)
    }
    private val selectedButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(222, 68, 78), PorterDuff.Mode.SRC_IN)
    }

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.dialogue_start_01)
    }

    override fun update(gctx: GameContext) {
        phaseElapsed += gctx.frameTime
        booperPrompt.update(gctx.frameTime)
        preloader.update(2)
        if (phase == Phase.SUCCESS_EFFECT &&
            phaseElapsed >= SUCCESS_EFFECT_DURATION &&
            preloader.isComplete
        ) {
            TransitionScene(gctx, nextSceneFactory).change()
        }
    }

    override fun draw(canvas: Canvas) {
        if (phase == Phase.SUCCESS_EFFECT) {
            drawSuccessEffect(canvas)
            return
        }

        canvas.drawColor(Color.rgb(2, 2, 22))
        canvas.drawBitmap(backgroundBitmap, null, DIALOGUE_BACKGROUND_RECT, imagePaint)
        drawPortrait(canvas)
        canvas.drawRect(0f, DIALOGUE_TEXT_TOP, SCREEN_WIDTH, SCREEN_HEIGHT, TEXT_PANEL_PAINT)
        canvas.drawText("\u2022 ${dialogue.speaker} \u2022", SCREEN_CENTER_X, TITLE_Y, titlePaint)

        val lines = selectedChoice?.result?.responseLines ?: dialogue.promptLines
        drawCenteredLines(canvas, lines, BODY_FIRST_Y, BODY_LINE_GAP, bodyPaint)

        when (phase) {
            Phase.PROMPT,
            Phase.RESPONSE -> {
                if (phase == Phase.RESPONSE && selectedChoice?.result?.isSuccess == true) {
                    drawSuccessTitle(canvas)
                }
                booperPrompt.draw(canvas, SCREEN_CENTER_X, BOOPER_Y)
            }
            Phase.CHOICES -> drawChoices(canvas)
            Phase.SUCCESS_EFFECT -> Unit
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true

        when (phase) {
            Phase.PROMPT -> {
                phase = Phase.CHOICES
                phaseElapsed = 0f
                gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
            }
            Phase.CHOICES -> handleChoiceTouch(event)
            Phase.RESPONSE -> finishResponse()
            Phase.SUCCESS_EFFECT -> Unit
        }
        return true
    }

    private fun handleChoiceTouch(event: MotionEvent) {
        val point = gctx.metrics.fromScreen(event.x, event.y)
        val index = CHOICE_RECTS.indexOfFirst { it.contains(point.x, point.y) }
        if (index !in dialogue.choices.indices) return

        if (selectedChoiceIndex != index) {
            selectedChoiceIndex = index
            gctx.res.sound.playOneShot(R.raw.button_dialogue_highlight_01)
            return
        }

        selectedChoice = dialogue.choices[index]
        phase = Phase.RESPONSE
        phaseElapsed = 0f
        gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
        if (selectedChoice?.result?.isSuccess == true) {
            gctx.res.sound.playOneShot(R.raw.dialogue_success_01)
        }
    }

    private fun finishResponse() {
        val result = selectedChoice?.result ?: return
        if (result.isSuccess) {
            phase = Phase.SUCCESS_EFFECT
            phaseElapsed = 0f
            gctx.res.sound.playOneShot(R.raw.succub_capture_01)
            return
        }

        BadEndScene(
            gctx = gctx,
            result = result,
            restartSceneFactory = restartSceneFactory,
        ).change()
    }

    private fun drawPortrait(canvas: Canvas) {
        val resId = selectedChoice?.result?.portraitResId ?: dialogue.portraitResId
        val bitmap = gctx.res.getBitmap(resId)
        val scale = minOf(
            PORTRAIT_MAX_WIDTH / bitmap.width,
            PORTRAIT_MAX_HEIGHT / bitmap.height,
        )
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        val left = SCREEN_CENTER_X - width * 0.5f
        val top = PORTRAIT_BOTTOM - height
        portraitRect.set(left, top, left + width, top + height)
        canvas.drawBitmap(bitmap, null, portraitRect, imagePaint)
    }

    private fun drawChoices(canvas: Canvas) {
        for (index in dialogue.choices.indices) {
            val choice = dialogue.choices[index]
            val selected = selectedChoiceIndex == index
            val rect = choiceDrawRects[index]
            rect.set(CHOICE_RECTS[index])
            if (selected) rect.inset(-38f, -7f)
            val bitmap = if (selected) selectedButtonBitmap else buttonBitmap
            val paint = if (selected) selectedButtonPaint else normalButtonPaint
            canvas.drawBitmap(bitmap, null, rect, paint)
            choicePaint.color = if (selected) {
                Color.rgb(245, 238, 241)
            } else {
                Color.rgb(178, 156, 166)
            }
            choicePaint.textSize = fittedTextSize(
                text = choice.text,
                availableWidth = rect.width() - CHOICE_TEXT_PADDING * 2f,
            )
            canvas.drawText(choice.text, rect.centerX(), rect.centerY() + 8f, choicePaint)
        }
    }

    private fun fittedTextSize(text: String, availableWidth: Float): Float {
        choicePaint.textSize = CHOICE_TEXT_SIZE
        val measuredWidth = choicePaint.measureText(text)
        if (measuredWidth <= availableWidth) return CHOICE_TEXT_SIZE
        return (CHOICE_TEXT_SIZE * availableWidth / measuredWidth)
            .coerceAtLeast(MIN_CHOICE_TEXT_SIZE)
    }

    private fun drawSuccessTitle(canvas: Canvas) {
        val index = ((phaseElapsed * SUCCESS_FPS).toInt()).coerceIn(0, successBitmaps.lastIndex)
        canvas.drawBitmap(successBitmaps[index], null, SUCCESS_RECT, imagePaint)
    }

    private fun drawSuccessEffect(canvas: Canvas) {
        boardScene.draw(canvas)
        val index = ((phaseElapsed * LOVE_EXPLOSION_FPS).toInt())
            .coerceIn(0, loveExplosionBitmaps.lastIndex)
        val bitmap = loveExplosionBitmaps[index]
        successEffectRect.set(
            successCenterX - LOVE_EXPLOSION_SIZE * 0.5f,
            successCenterY - LOVE_EXPLOSION_SIZE * 0.5f,
            successCenterX + LOVE_EXPLOSION_SIZE * 0.5f,
            successCenterY + LOVE_EXPLOSION_SIZE * 0.5f,
        )
        canvas.drawBitmap(bitmap, null, successEffectRect, imagePaint)
    }

    private fun drawCenteredLines(
        canvas: Canvas,
        lines: List<String>,
        firstY: Float,
        lineGap: Float,
        paint: Paint,
    ) {
        for ((index, line) in lines.withIndex()) {
            canvas.drawText(line, SCREEN_CENTER_X, firstY + index * lineGap, paint)
        }
    }

    companion object {
        private const val SCREEN_WIDTH = 1600f
        private const val SCREEN_HEIGHT = 900f
        private const val SCREEN_CENTER_X = 800f
        private const val DIALOGUE_TEXT_TOP = 562f
        private const val TITLE_Y = 620f
        private const val BODY_FIRST_Y = 662f
        private const val BODY_LINE_GAP = 31f
        private const val BOOPER_Y = 840f
        private const val CHOICE_TEXT_SIZE = 21f
        private const val MIN_CHOICE_TEXT_SIZE = 15f
        private const val CHOICE_TEXT_PADDING = 28f
        private const val PORTRAIT_MAX_WIDTH = 980f
        private const val PORTRAIT_MAX_HEIGHT = 560f
        private const val PORTRAIT_BOTTOM = 584f
        private const val SUCCESS_FPS = 14f
        private const val LOVE_EXPLOSION_FPS = 15f
        private const val SUCCESS_EFFECT_DURATION = 1.2f
        private const val LOVE_EXPLOSION_SIZE = 360f
        private val DIALOGUE_BACKGROUND_RECT = RectF(0f, 55f, 1600f, 570f)
        private val SUCCESS_RECT = RectF(450f, 710f, 1150f, 840f)
        private val CHOICE_RECTS = listOf(
            RectF(430f, 720f, 1170f, 775f),
            RectF(430f, 790f, 1170f, 845f),
        )
        private val TEXT_PANEL_PAINT = Paint().apply {
            color = Color.rgb(2, 2, 22)
        }
    }
}

class BadEndScene(
    gctx: GameContext,
    private val result: StageChoiceResult,
    private val restartSceneFactory: () -> Scene,
) : Scene(gctx) {
    override val clipsRect = true

    private val bitmaps = StageAssets.badEndFrames.map(gctx.res::getBitmap)
    private val booperPrompt = BooperPrompt(gctx)
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(200, 42, 53)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 25f
    }
    private var elapsed = 0f

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.bad_end_screen_01)
    }

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        booperPrompt.update(gctx.frameTime)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 22))
        canvas.drawBitmap(currentBitmap(), null, BAD_END_RECT, imagePaint)
        for ((index, line) in result.badEndLines.withIndex()) {
            canvas.drawText(line, SCREEN_CENTER_X, TEXT_FIRST_Y + index * TEXT_LINE_GAP, textPaint)
        }
        if (elapsed >= animationDuration) {
            booperPrompt.draw(canvas, SCREEN_CENTER_X, BOOPER_Y)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN && elapsed >= animationDuration) {
            gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
            TransitionScene(gctx, restartSceneFactory).change()
        }
        return true
    }

    private val animationDuration: Float
        get() = bitmaps.size / FPS

    private fun currentBitmap(): Bitmap {
        val index = ((elapsed * FPS).toInt()).coerceIn(0, bitmaps.lastIndex)
        return bitmaps[index]
    }

    companion object {
        private const val SCREEN_CENTER_X = 800f
        private const val FPS = 12f
        private const val TEXT_FIRST_Y = 735f
        private const val TEXT_LINE_GAP = 34f
        private const val BOOPER_Y = 835f
        private val BAD_END_RECT = RectF(16f, 180f, 1584f, 660f)
    }
}

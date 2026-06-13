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
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.audio.GameAudio
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageCatalog
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.StageSelectionEntry
import kr.ac.tukorea.ge.spgp2026.a2dg.res.BitmapPreloader
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

private const val DIALOGUE_BOOPER_X = 800f
private const val DIALOGUE_BOOPER_Y = 783f

private class AbyssBackdrop(gctx: GameContext) {
    private val bgBitmap = gctx.res.getBitmap(R.mipmap.dialogue_bg_abyss02)
    private val dimPaint = Paint().apply {
        color = Color.argb(155, 2, 2, 22)
    }
    private val layerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val layerRect = RectF()
    private var elapsed = 0f

    fun update(frameTime: Float) {
        elapsed += frameTime
    }

    fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 22))
        drawLoopingLayer(canvas, bgBitmap, ABYSS_TOP, ABYSS_BOTTOM, 48f)
        canvas.drawRect(0f, 0f, 1600f, ABYSS_TOP, dimPaint)
        canvas.drawRect(0f, ABYSS_BOTTOM, 1600f, 900f, dimPaint)
    }

    private fun drawLoopingLayer(
        canvas: Canvas,
        bitmap: Bitmap,
        top: Float,
        bottom: Float,
        speed: Float,
    ) {
        val height = bottom - top
        val width = height * bitmap.width / bitmap.height.toFloat()
        val offset = (elapsed * speed) % width
        layerPaint.alpha = 255

        var x = offset - width
        while (x < 1600f) {
            layerRect.set(x, top, x + width, bottom)
            canvas.drawBitmap(bitmap, null, layerRect, layerPaint)
            x += width
        }
    }

    companion object {
        private const val ABYSS_TOP = 130f
        private const val ABYSS_BOTTOM = 574f
    }
}

internal class BooperPrompt(gctx: GameContext) {
    private val bitmaps = (BOOPER_FRAMES + BOOPER_FRAMES.drop(1).dropLast(1).asReversed())
        .map { gctx.res.getBitmap(it) }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(222, 68, 78), PorterDuff.Mode.SRC_IN)
    }
    private val dstRect = RectF()
    private var elapsed = 0f

    fun update(frameTime: Float) {
        elapsed += frameTime
    }

    fun draw(canvas: Canvas, centerX: Float, centerY: Float) {
        paint.alpha = 255
        dstRect.set(centerX - 42f, centerY - 42f, centerX + 42f, centerY + 42f)
        canvas.drawBitmap(currentBitmap(), null, dstRect, paint)
    }

    private fun currentBitmap(): Bitmap {
        val index = ((elapsed * FPS).toInt() % bitmaps.size).coerceAtLeast(0)
        return bitmaps[index]
    }

    companion object {
        private const val FPS = 18f
        private val BOOPER_FRAMES = listOf(
            R.mipmap.booper0001,
            R.mipmap.booper0002,
            R.mipmap.booper0003,
            R.mipmap.booper0004,
            R.mipmap.booper0005,
            R.mipmap.booper0006,
            R.mipmap.booper0007,
            R.mipmap.booper0008,
            R.mipmap.booper0009,
            R.mipmap.booper0010,
            R.mipmap.booper0011,
            R.mipmap.booper0012,
            R.mipmap.booper0013,
            R.mipmap.booper0014,
            R.mipmap.booper0015,
            R.mipmap.booper0016,
            R.mipmap.booper0017,
            R.mipmap.booper0018,
            R.mipmap.booper0019,
        )
    }
}

class EnterScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true

    private val backdrop = AbyssBackdrop(gctx)
    private val booperPrompt = BooperPrompt(gctx)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(235, 229, 232)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 30f
    }
    private val subTextPaint = Paint(textPaint).apply {
        textSize = 29f
    }

    override fun onEnter() {
        GameAudio.preload(gctx.res.sound)
        gctx.res.sound.playMusic(R.raw.abyss_amb_loop_01)
        gctx.res.sound.playOneShot(R.raw.abyss_start_01)
    }

    override fun update(gctx: GameContext) {
        backdrop.update(gctx.frameTime)
        booperPrompt.update(gctx.frameTime)
    }

    override fun draw(canvas: Canvas) {
        backdrop.draw(canvas)
        canvas.drawText("You find yourself surrounded by the void.", 800f, 664f, textPaint)
        canvas.drawText("Touch to continue.", 800f, 696f, subTextPaint)
        booperPrompt.draw(canvas, DIALOGUE_BOOPER_X, DIALOGUE_BOOPER_Y)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
            gctx.res.sound.playOneShot(R.raw.abyss_portal_opening_01)
            StartScene(gctx).change()
            return true
        }
        return true
    }
}

class StartScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true

    private enum class Phase {
        DIALOGUE, MENU_REVEAL, MENU
    }

    private enum class MenuAction {
        NEW_GAME, SELECT_CHAPTER, EXIT
    }

    private data class MenuItem(
        val action: MenuAction,
        val label: String,
        val rect: RectF,
    )

    private val backdrop = AbyssBackdrop(gctx)
    private val booperPrompt = BooperPrompt(gctx)
    private val beelBitmap = gctx.res.getBitmap(R.mipmap.beel_fly)
    private val buttonBitmap = gctx.res.getBitmap(R.mipmap.menu_button)
    private val selectedButtonBitmap = gctx.res.getBitmap(R.mipmap.menu_button_selected)
    private val beelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val beelRect = RectF().apply {
        val height = 1600f * beelBitmap.height / beelBitmap.width
        set(0f, 0f, 1600f, height)
    }
    private val menuButtonRect = RectF()

    private val menuItems = listOf(
        MenuItem(MenuAction.NEW_GAME, "NEW GAME", RectF(500f, 646f, 1100f, 702f)),
        MenuItem(MenuAction.SELECT_CHAPTER, "CHAPTER SELECT", RectF(500f, 712f, 1100f, 768f)),
        MenuItem(MenuAction.EXIT, "EXIT", RectF(500f, 778f, 1100f, 834f))
    )
    private var selectedAction: MenuAction? = null
    private var phase = Phase.DIALOGUE
    private var phaseElapsed = 0f
    private var beelElapsed = 0f

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 61, 73)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 28f
    }
    private val bodyPaint = Paint(titlePaint).apply {
        color = Color.rgb(236, 230, 234)
        textSize = 24f
    }
    private val buttonPaint = Paint(bodyPaint).apply {
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 25f
    }
    private val normalButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(112, 70, 82), PorterDuff.Mode.SRC_IN)
    }
    private val selectedButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(222, 68, 78), PorterDuff.Mode.SRC_IN)
    }

    override fun onEnter() {
        gctx.res.sound.playMusic(R.raw.abyss_amb_loop_01)
        gctx.res.sound.playOneShot(R.raw.dialogue_start_01)
    }

    override fun update(gctx: GameContext) {
        phaseElapsed += gctx.frameTime
        beelElapsed += gctx.frameTime
        backdrop.update(gctx.frameTime)
        booperPrompt.update(gctx.frameTime)
        if (phase == Phase.MENU_REVEAL && phaseElapsed >= MENU_REVEAL_DURATION) {
            phase = Phase.MENU
            phaseElapsed = MENU_REVEAL_DURATION
        }
    }

    override fun draw(canvas: Canvas) {
        backdrop.draw(canvas)
        drawBeel(canvas)
        canvas.drawText(TITLE_TEXT, 800f, 618f, titlePaint)
        if (phase == Phase.DIALOGUE) {
            canvas.drawText("Greetings little one. Please don't mind me.", 800f, 660f, bodyPaint)
            canvas.drawText("It is just I, good old Beelzebub.", 800f, 694f, bodyPaint)
            drawTouchPrompt(canvas)
            return
        }

        val revealOffset = menuRevealOffset()
        for (item in menuItems) {
            drawMenuButton(canvas, item, selectedAction == item.action, revealOffset)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true

        if (phase == Phase.DIALOGUE) {
            phase = Phase.MENU_REVEAL
            phaseElapsed = 0f
            gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
            return true
        }
        if (phase == Phase.MENU_REVEAL) return true

        val point = gctx.metrics.fromScreen(event.x, event.y)
        val item = menuItems.firstOrNull { it.rect.contains(point.x, point.y) } ?: return true
        if (selectedAction != item.action) {
            selectedAction = item.action
            gctx.res.sound.playOneShot(R.raw.button_menu_highlight_01)
            return true
        }

        gctx.res.sound.playOneShot(R.raw.button_menu_confirm_01)
        when (item.action) {
            MenuAction.NEW_GAME -> NewGameIntroScene(gctx).change()
            MenuAction.SELECT_CHAPTER -> SelectLevelScene(gctx).change()
            MenuAction.EXIT -> {
                gctx.res.sound.stopMusic()
                gctx.sceneStack.popAll()
            }
        }
        return true
    }

    private fun drawBeel(canvas: Canvas) {
        val fade = (beelElapsed / BEEL_FADE_DURATION).coerceIn(0f, 1f)
        beelPaint.alpha = (fade * 255f).toInt()
        canvas.drawBitmap(beelBitmap, null, beelRect, beelPaint)
    }

    private fun drawTouchPrompt(canvas: Canvas) {
        booperPrompt.draw(canvas, DIALOGUE_BOOPER_X, DIALOGUE_BOOPER_Y)
    }

    private fun drawMenuButton(canvas: Canvas, item: MenuItem, selected: Boolean, revealOffset: Float) {
        menuButtonRect.set(item.rect)
        if (selected) menuButtonRect.inset(-42f, -8f)
        menuButtonRect.offset(0f, revealOffset)
        val bitmap: Bitmap = if (selected) selectedButtonBitmap else buttonBitmap
        val paint = if (selected) selectedButtonPaint else normalButtonPaint
        canvas.drawBitmap(bitmap, null, menuButtonRect, paint)
        buttonPaint.color = if (selected) Color.rgb(245, 238, 241) else Color.rgb(178, 156, 166)
        canvas.drawText(
            item.label,
            menuButtonRect.centerX(),
            menuButtonRect.centerY() + 9f,
            buttonPaint,
        )
    }

    private fun menuRevealOffset(): Float {
        if (phase == Phase.MENU) return 0f
        val t = (phaseElapsed / MENU_REVEAL_DURATION).coerceIn(0f, 1f)
        val eased = 1f - (1f - t) * (1f - t)
        return (1f - eased) * MENU_REVEAL_OFFSET
    }

    companion object {
        private const val TITLE_TEXT = "\u2022 Beelzebub, The Great Fly \u2022"
        private const val BEEL_FADE_DURATION = 0.45f
        private const val MENU_REVEAL_DURATION = 0.34f
        private const val MENU_REVEAL_OFFSET = 170f
    }
}

class SelectLevelScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true

    private data class LevelButton(
        val entry: StageSelectionEntry?,
        val label: String,
        val rect: RectF,
    )

    private val backdrop = AbyssBackdrop(gctx)
    private val beelBitmap = gctx.res.getBitmap(R.mipmap.beel_fly)
    private val frameBitmap = gctx.res.getBitmap(R.mipmap.w_chapter_select)
    private val selectionBitmap = gctx.res.getBitmap(R.mipmap.w_chapter_selected)
    private val levelButtons = createLevelButtons()
    private val beelRect = RectF().apply {
        val height = SCREEN_WIDTH * beelBitmap.height / beelBitmap.width
        set(0f, 0f, SCREEN_WIDTH, height)
    }
    private val topFrameDrawRect = RectF()
    private val bottomFrameDrawRect = RectF()
    private val levelButtonDrawRects = Array(levelButtons.size) { RectF() }
    private val preloader = BitmapPreloader(gctx.res)
    private var selectedIndex = 0
    private var revealElapsed = 0f
    private var isChangingScene = false
    private var pendingStageNo: Int? = null

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val normalCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(NORMAL_CELL_COLOR, PorterDuff.Mode.SRC_IN)
    }
    private val exitCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(EXIT_CELL_COLOR, PorterDuff.Mode.SRC_IN)
    }
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 27f
    }
    private val descriptionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(238, 232, 235)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 25f
    }

    init {
        queueStagePreload(levelButtons.first().entry)
    }

    override fun update(gctx: GameContext) {
        revealElapsed = (revealElapsed + gctx.frameTime).coerceAtMost(REVEAL_DURATION)
        backdrop.update(gctx.frameTime)
        preloader.update(2)
        val stageNo = pendingStageNo
        if (stageNo != null && preloader.isComplete) {
            pendingStageNo = null
            gctx.res.sound.stopMusic()
            TransitionScene(gctx) { MainScene(gctx, stageNo) }.change()
        }
    }

    override fun draw(canvas: Canvas) {
        backdrop.draw(canvas)
        drawBeel(canvas)

        val offsetY = revealOffsetY()
        drawChapterFrame(canvas, offsetY)
        for (index in levelButtons.indices) {
            drawLevelButton(
                canvas,
                levelButtons[index],
                index,
                index == selectedIndex,
                offsetY,
            )
        }

        val selectedEntry = levelButtons[selectedIndex].entry
        if (selectedEntry != null) {
            canvas.drawText(
                selectedEntry.description,
                SCREEN_CENTER_X,
                DESCRIPTION_BASELINE_Y + offsetY,
                descriptionPaint,
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN ||
            revealElapsed < REVEAL_DURATION ||
            isChangingScene
        ) {
            return true
        }

        val point = gctx.metrics.fromScreen(event.x, event.y)
        val index = levelButtons.indexOfFirst { it.rect.contains(point.x, point.y) }
        if (index < 0) return true

        if (selectedIndex != index) {
            selectedIndex = index
            queueStagePreload(levelButtons[index].entry)
            gctx.res.sound.playOneShot(R.raw.button_chapter_highlight_01)
            return true
        }

        gctx.res.sound.playOneShot(R.raw.button_chapter_confirm_01)
        val entry = levelButtons[index].entry
        if (entry == null) {
            StartScene(gctx).change()
            return true
        }

        isChangingScene = true
        pendingStageNo = entry.stageNo
        return true
    }

    override fun onBackPressed(): Boolean {
        if (!isChangingScene) {
            StartScene(gctx).change()
        }
        return true
    }

    private fun drawBeel(canvas: Canvas) {
        canvas.drawBitmap(beelBitmap, null, beelRect, imagePaint)
    }

    private fun drawChapterFrame(canvas: Canvas, offsetY: Float) {
        topFrameDrawRect.set(TOP_FRAME_RECT)
        topFrameDrawRect.offset(0f, offsetY)
        bottomFrameDrawRect.set(BOTTOM_FRAME_RECT)
        bottomFrameDrawRect.offset(0f, offsetY)
        canvas.drawBitmap(frameBitmap, null, topFrameDrawRect, imagePaint)
        canvas.save()
        canvas.scale(
            1f,
            -1f,
            bottomFrameDrawRect.centerX(),
            bottomFrameDrawRect.centerY(),
        )
        canvas.drawBitmap(frameBitmap, null, bottomFrameDrawRect, imagePaint)
        canvas.restore()
    }

    private fun drawLevelButton(
        canvas: Canvas,
        button: LevelButton,
        index: Int,
        selected: Boolean,
        offsetY: Float,
    ) {
        val rect = levelButtonDrawRects[index]
        rect.set(button.rect)
        rect.offset(0f, offsetY)
        if (selected) rect.inset(-FOCUS_INSET_X, -FOCUS_INSET_Y)
        val paint = when {
            selected -> imagePaint
            button.entry == null -> exitCellPaint
            else -> normalCellPaint
        }
        canvas.drawBitmap(selectionBitmap, null, rect, paint)

        numberPaint.color = when {
            selected -> Color.WHITE
            button.entry == null -> EXIT_TEXT_COLOR
            else -> NORMAL_TEXT_COLOR
        }
        numberPaint.textSize = if (selected) SELECTED_TEXT_SIZE else NORMAL_TEXT_SIZE
        canvas.drawText(button.label, rect.centerX(), rect.centerY() + 9f, numberPaint)
    }

    private fun queueStagePreload(entry: StageSelectionEntry?) {
        preloader.clear()
        if (entry == null) return
        preloader.enqueue(TransitionScene.preloadResIds)
        preloader.enqueue(StageCatalog.get(entry.stageNo).preloadResIds)
    }

    private fun revealOffsetY(): Float {
        val t = (revealElapsed / REVEAL_DURATION).coerceIn(0f, 1f)
        val eased = 1f - (1f - t) * (1f - t)
        return (1f - eased) * REVEAL_OFFSET_Y
    }

    private fun createLevelButtons(): List<LevelButton> {
        val buttonCount = StageCatalog.selectionEntries.size
        val stageWidth = buttonCount * CELL_WIDTH + (buttonCount - 1) * CELL_GAP
        val totalWidth = stageWidth + EXIT_GAP + CELL_WIDTH
        val firstLeft = SCREEN_CENTER_X - totalWidth * 0.5f
        val buttons = StageCatalog.selectionEntries.mapIndexed { index, entry ->
            val left = firstLeft + index * (CELL_WIDTH + CELL_GAP)
            LevelButton(
                entry = entry,
                label = entry.romanNumeral,
                rect = RectF(left, CELL_TOP, left + CELL_WIDTH, CELL_BOTTOM),
            )
        }.toMutableList()
        val exitLeft = firstLeft + stageWidth + EXIT_GAP
        buttons += LevelButton(
            entry = null,
            label = "EX",
            rect = RectF(exitLeft, CELL_TOP, exitLeft + CELL_WIDTH, CELL_BOTTOM),
        )
        return buttons
    }

    companion object {
        private const val SCREEN_WIDTH = 1600f
        private const val SCREEN_CENTER_X = 800f
        private const val REVEAL_DURATION = 0.34f
        private const val REVEAL_OFFSET_Y = 190f
        private const val CELL_WIDTH = 92f
        private const val CELL_TOP = 668f
        private const val CELL_BOTTOM = 734f
        private const val CELL_GAP = 10f
        private const val EXIT_GAP = 26f
        private const val FOCUS_INSET_X = 6f
        private const val FOCUS_INSET_Y = 4f
        private const val NORMAL_TEXT_SIZE = 25f
        private const val SELECTED_TEXT_SIZE = 27f
        private const val DESCRIPTION_BASELINE_Y = 796f
        private val TOP_FRAME_RECT = RectF(240f, 624f, 1360f, 684f)
        private val BOTTOM_FRAME_RECT = RectF(240f, 720f, 1360f, 780f)
        private val NORMAL_CELL_COLOR = Color.rgb(73, 63, 87)
        private val NORMAL_TEXT_COLOR = Color.rgb(111, 99, 122)
        private val EXIT_CELL_COLOR = Color.rgb(105, 30, 53)
        private val EXIT_TEXT_COLOR = Color.rgb(165, 48, 72)
    }
}

class NewGameIntroScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true

    private data class DialoguePage(
        val title: String?,
        val lines: List<String>,
        val showsBeel: Boolean,
    )

    private val backdrop = AbyssBackdrop(gctx)
    private val booperPrompt = BooperPrompt(gctx)
    private val beelBitmap = gctx.res.getBitmap(R.mipmap.beel_fly)
    private val beelRect = RectF().apply {
        val height = 1600f * beelBitmap.height / beelBitmap.width
        set(0f, 0f, 1600f, height)
    }
    private val preloader = BitmapPreloader(gctx.res).apply {
        enqueue(PrologueCutsceneScene.preloadResIds)
        enqueue(StageCatalog.get(1).preloadResIds)
    }
    private var pageIndex = 0
    private var pageElapsed = 0f
    private var isStartingGame = false

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 61, 73)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 28f
    }
    private val bodyPaint = Paint(titlePaint).apply {
        color = Color.rgb(236, 230, 234)
        textSize = 26f
    }

    override fun onEnter() {
        gctx.res.sound.playOneShot(R.raw.dialogue_start_01)
    }

    override fun update(gctx: GameContext) {
        pageElapsed += gctx.frameTime
        backdrop.update(gctx.frameTime)
        booperPrompt.update(gctx.frameTime)
        preloader.update(2)
        if (isStartingGame && preloader.isComplete) {
            PrologueCutsceneScene(gctx) { MainScene(gctx) }.change()
        }
    }

    override fun draw(canvas: Canvas) {
        val page = PAGES[pageIndex]
        if (page.showsBeel) {
            backdrop.draw(canvas)
            drawBeel(canvas)
            page.title?.let {
                canvas.drawText(it, 800f, 618f, titlePaint)
            }
            drawLines(canvas, page.lines, 660f)
            booperPrompt.draw(canvas, DIALOGUE_BOOPER_X, DIALOGUE_BOOPER_Y)
        } else {
            canvas.drawColor(Color.rgb(2, 2, 22))
            bodyPaint.alpha = (255f * (pageElapsed / LAST_PAGE_FADE_DURATION).coerceIn(0f, 1f)).toInt()
            drawLines(canvas, page.lines, 684f)
            bodyPaint.alpha = 255
            booperPrompt.draw(canvas, DIALOGUE_BOOPER_X, DIALOGUE_BOOPER_Y)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true
        if (isStartingGame) return true

        gctx.res.sound.playOneShot(R.raw.button_dialogue_confirm_01)
        if (pageIndex < PAGES.lastIndex) {
            pageIndex += 1
            pageElapsed = 0f
        } else {
            isStartingGame = true
        }
        return true
    }

    private fun drawBeel(canvas: Canvas) {
        canvas.drawBitmap(beelBitmap, null, beelRect, null)
    }

    private fun drawLines(canvas: Canvas, lines: List<String>, firstY: Float) {
        for ((index, line) in lines.withIndex()) {
            canvas.drawText(line, 800f, firstY + index * 34f, bodyPaint)
        }
    }

    companion object {
        private const val TITLE_TEXT = "\u2022 Beelzebub, The Great Fly \u2022"
        private const val LAST_PAGE_FADE_DURATION = 0.10f
        private val PAGES = listOf(
            DialoguePage(
                title = TITLE_TEXT,
                lines = listOf("Story of the Helltaker again? Interesting..."),
                showsBeel = true,
            ),
            DialoguePage(
                title = TITLE_TEXT,
                lines = listOf("Do you, by any chance, need a narrator?"),
                showsBeel = true,
            ),
            DialoguePage(
                title = null,
                lines = listOf("Why please, allow me. It will be a pleasure."),
                showsBeel = false,
            ),
        )
    }
}

class PauseScene(
    gctx: GameContext,
    private val onSkipPuzzle: () -> Unit,
    private val onMainMenu: () -> Unit,
) : Scene(gctx) {
    override val clipsRect = true
    override val isTransparent = true

    private enum class MenuAction {
        RESUME, SKIP_PUZZLE, MUSIC, SOUND, MAIN_MENU
    }

    private data class MenuItem(
        val action: MenuAction,
        val label: String,
        val rect: RectF,
    )

    private val buttonBitmap = gctx.res.getBitmap(R.mipmap.menu_button)
    private val selectedButtonBitmap = gctx.res.getBitmap(R.mipmap.menu_button_selected)
    private val menuItems = listOf(
        MenuItem(MenuAction.RESUME, "RESUME", RectF(520f, 220f, 1080f, 270f)),
        MenuItem(MenuAction.SKIP_PUZZLE, "SKIP PUZZLE", RectF(520f, 288f, 1080f, 338f)),
        MenuItem(MenuAction.MUSIC, "MUSIC", RectF(520f, 356f, 1080f, 406f)),
        MenuItem(MenuAction.SOUND, "SOUND", RectF(520f, 464f, 1080f, 514f)),
        MenuItem(MenuAction.MAIN_MENU, "MAIN MENU", RectF(520f, 572f, 1080f, 622f)),
    )
    private var selectedAction = MenuAction.RESUME

    private val dimPaint = Paint().apply {
        color = Color.argb(205, 2, 2, 22)
    }
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(142, 93, 103)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(218, 61, 73)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 44f
    }
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(236, 230, 234)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 30f
    }
    private val valuePaint = Paint(buttonPaint).apply {
        color = Color.rgb(174, 151, 159)
        textSize = 28f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, gctx.metrics.width, gctx.metrics.height, dimPaint)
        canvas.drawText("PAUSE MENU", 800f, 170f, titlePaint)
        canvas.drawCircle(690f, 163f, 7f, titlePaint)
        canvas.drawCircle(910f, 163f, 7f, titlePaint)
        canvas.drawRect(480f, 188f, 1120f, 735f, framePaint)
        for (item in menuItems) {
            drawMenuButton(canvas, item, selectedAction == item.action)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true

        val point = gctx.metrics.fromScreen(event.x, event.y)
        val item = menuItems.firstOrNull { it.rect.contains(point.x, point.y) } ?: return true
        if (selectedAction != item.action) {
            selectedAction = item.action
            return true
        }

        when (item.action) {
            MenuAction.RESUME -> pop()
            MenuAction.SKIP_PUZZLE -> {
                pop()
                onSkipPuzzle()
            }
            MenuAction.MUSIC -> gctx.res.sound.cycleMusicVolume()
            MenuAction.SOUND -> gctx.res.sound.cycleEffectVolume()
            MenuAction.MAIN_MENU -> {
                pop()
                onMainMenu()
            }
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        pop()
        return true
    }

    private fun drawMenuButton(canvas: Canvas, item: MenuItem, selected: Boolean) {
        val bitmap = if (selected) selectedButtonBitmap else buttonBitmap
        canvas.drawBitmap(bitmap, null, item.rect, null)
        buttonPaint.color = if (selected) Color.WHITE else Color.rgb(174, 151, 159)
        canvas.drawText(item.label, item.rect.centerX(), item.rect.centerY() + 10f, buttonPaint)
        val value = when (item.action) {
            MenuAction.MUSIC -> gctx.res.sound.musicVolumeText
            MenuAction.SOUND -> gctx.res.sound.effectVolumeText
            else -> null
        }
        value?.let {
            canvas.drawText(it, item.rect.centerX(), item.rect.bottom + 34f, valuePaint)
        }
    }
}

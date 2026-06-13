package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.core.graphics.withScale
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.MoveDirection
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.ITouchable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageHud(
    private val gctx: GameContext,
    private val movesProvider: () -> Int,
    private val onRestart: () -> Unit,
    private val onMove: (MoveDirection) -> Unit,
) : IGameObject, ITouchable {
    private data class MoveButton(
        val direction: MoveDirection,
        val rect: RectF,
        val srcRect: Rect,
    )

    private val backFrameBitmap = gctx.res.getBitmap(R.mipmap.main_ui_export_bui2)
    private val frontFrameBitmap = gctx.res.getBitmap(R.mipmap.main_ui_export_ui0001)
    private val buttonBitmap = gctx.res.getBitmap(R.mipmap.menu_button)
    private val selectedButtonBitmap = gctx.res.getBitmap(R.mipmap.menu_button_selected)
    private val arrowBarBitmap = gctx.res.getBitmap(R.mipmap.arrow_bar)
    private val backFrameRect = RectF(0f, 0f, 320f, 580f)
    private val frontFrameRect = RectF(0f, 348f, 385f, 890f)
    private val restartRect = RectF(600f, 812f, 1000f, 868f)
    private val drawnRestartRect = RectF(restartRect)
    private val moveButtons = listOf(
        MoveButton(MoveDirection.UP, dpadRect(0f, -DPAD_STEP), ARROW_UP_SRC),
        MoveButton(MoveDirection.LEFT, dpadRect(-DPAD_STEP, 0f), ARROW_LEFT_SRC),
        MoveButton(MoveDirection.RIGHT, dpadRect(DPAD_STEP, 0f), ARROW_RIGHT_SRC),
        MoveButton(MoveDirection.DOWN, dpadRect(0f, DPAD_STEP), ARROW_DOWN_SRC),
    )

    private val hudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(236, 230, 234)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 72f
    }
    private val buttonPaint = Paint(hudPaint).apply {
        textSize = 24f
    }
    private val normalButtonImagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(112, 70, 82), PorterDuff.Mode.SRC_IN)
    }
    private val selectedButtonImagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(Color.rgb(222, 68, 78), PorterDuff.Mode.SRC_IN)
    }
    private var restartFocused = false
    private var damageElapsed = DAMAGE_ANIMATION_DURATION

    override fun update(gctx: GameContext) {
        damageElapsed = (damageElapsed + gctx.frameTime).coerceAtMost(DAMAGE_ANIMATION_DURATION)
    }

    override fun draw(canvas: Canvas) {
        drawSideFrames(canvas)
        drawMoves(canvas)
        drawRestartButton(canvas)
        drawDpad(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true

        val point = gctx.metrics.fromScreen(event.x, event.y)
        if (restartRect.contains(point.x, point.y)) {
            if (!restartFocused) {
                restartFocused = true
                gctx.res.sound.playOneShot(R.raw.button_menu_highlight_01)
                return true
            }
            gctx.res.sound.playOneShot(R.raw.button_menu_confirm_01)
            onRestart()
            return true
        }

        val moveButton = moveButtons.firstOrNull { it.rect.contains(point.x, point.y) }
        if (moveButton != null) {
            restartFocused = false
            onMove(moveButton.direction)
            return true
        }
        return false
    }

    private fun drawSideFrames(canvas: Canvas) {
        drawLeftFrames(canvas)
        canvas.withScale(-1f, 1f, 800f, 450f) {
            drawLeftFrames(this)
        }
    }

    private fun drawLeftFrames(canvas: Canvas) {
        canvas.drawBitmap(backFrameBitmap, null, backFrameRect, null)
        canvas.drawBitmap(frontFrameBitmap, null, frontFrameRect, null)
    }

    private fun drawMoves(canvas: Canvas) {
        val moves = movesProvider()
        val damageProgress = (damageElapsed / DAMAGE_ANIMATION_DURATION).coerceIn(0f, 1f)
        val damageScale = if (damageElapsed < DAMAGE_ANIMATION_DURATION) {
            1f + (1f - damageProgress) * DAMAGE_NUMBER_SCALE
        } else {
            1f
        }
        hudPaint.color = if (damageElapsed < DAMAGE_ANIMATION_DURATION) {
            blendColor(DAMAGE_NUMBER_COLOR, NORMAL_NUMBER_COLOR, damageProgress)
        } else {
            NORMAL_NUMBER_COLOR
        }
        canvas.withScale(damageScale, damageScale, MOVES_CENTER_X, MOVES_BASELINE_Y) {
            drawText(
                if (moves <= 0) "X" else moves.toString(),
                MOVES_CENTER_X,
                MOVES_BASELINE_Y,
                hudPaint,
            )
        }
        hudPaint.color = NORMAL_NUMBER_COLOR
    }

    fun notifyDamage() {
        damageElapsed = 0f
    }

    private fun blendColor(from: Int, to: Int, progress: Float): Int {
        val t = progress.coerceIn(0f, 1f)
        return Color.rgb(
            (Color.red(from) + (Color.red(to) - Color.red(from)) * t).toInt(),
            (Color.green(from) + (Color.green(to) - Color.green(from)) * t).toInt(),
            (Color.blue(from) + (Color.blue(to) - Color.blue(from)) * t).toInt(),
        )
    }

    private fun drawRestartButton(canvas: Canvas) {
        drawnRestartRect.set(restartRect)
        if (restartFocused) {
            drawnRestartRect.inset(-42f, -8f)
        }
        val bitmap = if (restartFocused) selectedButtonBitmap else buttonBitmap
        val paint = if (restartFocused) selectedButtonImagePaint else normalButtonImagePaint
        canvas.drawBitmap(bitmap, null, drawnRestartRect, paint)
        buttonPaint.color = if (restartFocused) {
            Color.rgb(245, 238, 241)
        } else {
            Color.rgb(178, 156, 166)
        }
        canvas.drawText(
            "RESTART",
            drawnRestartRect.centerX(),
            drawnRestartRect.centerY() + 9f,
            buttonPaint,
        )
    }

    private fun drawDpad(canvas: Canvas) {
        for (button in moveButtons) {
            canvas.drawBitmap(arrowBarBitmap, button.srcRect, button.rect, null)
        }
    }

    companion object {
        private const val DPAD_CENTER_X = 1454f
        private const val DPAD_CENTER_Y = 695f
        private const val DPAD_BUTTON_SIZE = 80f
        private const val DPAD_STEP = 82f
        private const val MOVES_CENTER_X = 144f
        private const val MOVES_BASELINE_Y = 724f
        private const val DAMAGE_ANIMATION_DURATION = 0.34f
        private const val DAMAGE_NUMBER_SCALE = 0.42f
        private val NORMAL_NUMBER_COLOR = Color.rgb(236, 230, 234)
        private val DAMAGE_NUMBER_COLOR = Color.rgb(245, 67, 31)

        private fun dpadRect(offsetX: Float, offsetY: Float): RectF {
            val halfSize = DPAD_BUTTON_SIZE / 2f
            val centerX = DPAD_CENTER_X + offsetX
            val centerY = DPAD_CENTER_Y + offsetY
            return RectF(
                centerX - halfSize,
                centerY - halfSize,
                centerX + halfSize,
                centerY + halfSize,
            )
        }

        private val ARROW_UP_SRC = Rect(140, 55, 532, 435)
        private val ARROW_DOWN_SRC = Rect(654, 55, 1048, 435)
        private val ARROW_LEFT_SRC = Rect(140, 480, 532, 858)
        private val ARROW_RIGHT_SRC = Rect(654, 480, 1048, 858)
    }
}

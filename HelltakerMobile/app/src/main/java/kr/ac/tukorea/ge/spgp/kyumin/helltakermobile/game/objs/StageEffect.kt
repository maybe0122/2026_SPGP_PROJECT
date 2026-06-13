package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageEffect(
    gctx: GameContext,
    frameResIds: List<Int>,
    private val fps: Float,
    private val height: Float,
    colorFilter: ColorFilter? = null,
) : IGameObject {
    private val frames: List<Bitmap> = frameResIds.map { gctx.res.getBitmap(it) }
    private val dstRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.colorFilter = colorFilter
    }
    private var elapsed = 0f

    var isFinished = false
        private set

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        if (elapsed >= frames.size / fps) {
            isFinished = true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isFinished) return
        val frameIndex = (elapsed * fps).toInt().coerceIn(0, frames.lastIndex)
        canvas.drawBitmap(frames[frameIndex], null, dstRect, paint)
    }

    fun setCenter(centerX: Float, centerY: Float) {
        val bitmap = frames.first()
        val width = height * bitmap.width / bitmap.height.toFloat()
        dstRect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f,
        )
    }
}

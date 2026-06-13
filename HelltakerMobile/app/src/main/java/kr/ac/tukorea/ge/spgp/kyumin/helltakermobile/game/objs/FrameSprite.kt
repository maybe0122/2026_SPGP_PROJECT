package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class FrameSprite(
    gctx: GameContext,
    resIds: List<Int>,
    private val fps: Float,
    colorFilter: ColorFilter? = null,
) : IGameObject {
    private val bitmaps: List<Bitmap> = resIds.map { gctx.res.getBitmap(it) }
    private val dstRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.colorFilter = colorFilter
    }
    private var elapsed = 0f
    private var x = 0f
    private var y = 0f
    private var width = 0f
    private var height = 0f

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmaps[currentFrameIndex()], null, dstRect, paint)
    }

    fun setCenterProportionalHeight(centerX: Float, centerY: Float, height: Float) {
        val bitmap = bitmaps.first()
        x = centerX
        y = centerY
        this.height = height
        width = height * bitmap.width / bitmap.height.toFloat()
        syncDstRect()
    }

    private fun currentFrameIndex(): Int {
        return ((elapsed * fps).toInt() % bitmaps.size).coerceAtLeast(0)
    }

    private fun syncDstRect() {
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }
}

package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageTorch(
    gctx: GameContext,
    baseResId: Int,
    flameResIds: List<Int>,
    private val fps: Float,
    colorFilter: ColorFilter?,
) : IGameObject {
    private val baseBitmap = gctx.res.getBitmap(baseResId)
    private val flameBitmaps: List<Bitmap> = flameResIds.map { gctx.res.getBitmap(it) }
    private val baseRect = RectF()
    private val flameRect = RectF()
    private val flamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.colorFilter = colorFilter
    }
    private var elapsed = 0f

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(baseBitmap, null, baseRect, null)
        if (flameBitmaps.isEmpty()) return

        val frameIndex = (elapsed * fps).toInt() % flameBitmaps.size
        canvas.drawBitmap(flameBitmaps[frameIndex], null, flameRect, flamePaint)
    }

    fun setCenter(
        centerX: Float,
        centerY: Float,
        baseWidth: Float,
        baseHeight: Float,
        flameWidth: Float,
        flameHeight: Float,
        flameOffsetX: Float,
        flameOffsetY: Float,
    ) {
        setRect(
            baseRect,
            centerX,
            centerY,
            baseWidth,
            baseHeight,
        )
        setRect(
            flameRect,
            centerX + flameOffsetX,
            centerY + flameOffsetY,
            flameWidth,
            flameHeight,
        )
    }

    private fun setRect(
        rect: RectF,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
    ) {
        rect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f,
        )
    }
}

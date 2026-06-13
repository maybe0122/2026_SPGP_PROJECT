package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.PI
import kotlin.math.sin

class StageFloatingSprite(
    gctx: GameContext,
    resId: Int,
    private val height: Float,
    private val amplitude: Float,
    private val cycleSeconds: Float,
) : IGameObject {
    private val bitmap: Bitmap = gctx.res.getBitmap(resId)
    private val dstRect = RectF()
    private var elapsed = 0f
    private var centerX = 0f
    private var baseCenterY = 0f

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        syncDstRect()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, dstRect, null)
    }

    fun setCenter(centerX: Float, centerY: Float) {
        this.centerX = centerX
        baseCenterY = centerY
        syncDstRect()
    }

    private fun syncDstRect() {
        val width = height * bitmap.width / bitmap.height.toFloat()
        val phase = elapsed / cycleSeconds * (2f * PI.toFloat())
        val centerY = baseCenterY + sin(phase) * amplitude
        dstRect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f,
        )
    }
}

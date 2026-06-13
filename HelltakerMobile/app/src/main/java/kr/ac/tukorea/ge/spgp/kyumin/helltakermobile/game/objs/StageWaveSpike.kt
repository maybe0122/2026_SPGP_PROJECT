package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageWaveSpike(
    gctx: GameContext,
    frameResIds: List<Int>,
    private val transitionDuration: Float,
    startsActive: Boolean,
) : IGameObject {
    private val frames: List<Bitmap> = frameResIds.map { gctx.res.getBitmap(it) }
    private val dstRect = RectF()
    private var elapsed = 0f
    private var isTransitioning = false
    private var width = 0f
    private var height = 0f

    var isActive = startsActive
        private set

    override fun update(gctx: GameContext) {
        if (!isTransitioning) return
        elapsed += gctx.frameTime
        if (elapsed >= transitionDuration) {
            elapsed = transitionDuration
            isTransitioning = false
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(currentBitmap(), null, dstRect, null)
    }

    fun setCenterProportionalHeight(centerX: Float, centerY: Float, height: Float) {
        val bitmap = frames.first()
        this.height = height
        width = height * bitmap.width / bitmap.height.toFloat()
        dstRect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f,
        )
    }

    fun advance() {
        isActive = !isActive
        elapsed = 0f
        isTransitioning = true
    }

    private fun currentBitmap(): Bitmap {
        if (!isTransitioning) {
            return frames[if (isActive) ACTIVE_FRAME_INDEX else INACTIVE_FRAME_INDEX]
        }
        val sequence = if (isActive) ACTIVE_SEQUENCE else INACTIVE_SEQUENCE
        val progress = (elapsed / transitionDuration).coerceIn(0f, 1f)
        val sequenceIndex = (progress * sequence.size)
            .toInt()
            .coerceIn(0, sequence.lastIndex)
        return frames[sequence[sequenceIndex]]
    }

    companion object {
        private const val ACTIVE_FRAME_INDEX = 0
        private const val INACTIVE_FRAME_INDEX = 4
        private val ACTIVE_SEQUENCE = intArrayOf(4, 5, 6, 7, 0)
        private val INACTIVE_SEQUENCE = intArrayOf(0, 1, 2, 3, 4)
    }
}

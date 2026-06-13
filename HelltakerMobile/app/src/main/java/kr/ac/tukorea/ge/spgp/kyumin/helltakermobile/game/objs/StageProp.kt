package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageProp(
    gctx: GameContext,
    idleResIds: List<Int>,
    private val height: Float,
    private val idleFps: Float = 0f,
    hitResIds: List<Int> = emptyList(),
    private val hitFps: Float = DEFAULT_HIT_FPS,
) : IGameObject {
    private val idleFrames: List<Bitmap> = idleResIds.map { gctx.res.getBitmap(it) }
    private val hitFrames: List<Bitmap> = hitResIds.map { gctx.res.getBitmap(it) }
    private val dstRect = RectF()
    private var elapsed = 0f
    private var startX = 0f
    private var startY = 0f
    private var targetX = 0f
    private var targetY = 0f
    private var x = 0f
    private var y = 0f
    private var disappearsAfterHit = false

    var isMoving = false
        private set
    var isExpired = false
        private set

    constructor(
        gctx: GameContext,
        resId: Int,
        height: Float,
    ) : this(gctx, listOf(resId), height)

    override fun update(gctx: GameContext) {
        if (!isMoving) {
            elapsed += gctx.frameTime
            return
        }

        elapsed += gctx.frameTime
        val t = (elapsed / MOVE_DURATION).coerceIn(0f, 1f)
        val eased = 1f - (1f - t) * (1f - t)
        x = lerp(startX, targetX, eased)
        y = lerp(startY, targetY, eased)
        syncDstRect()

        if (t >= 1f) {
            isMoving = false
            x = targetX
            y = targetY
            syncDstRect()
            if (disappearsAfterHit) {
                isExpired = true
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (isExpired) return
        canvas.drawBitmap(currentBitmap(), null, dstRect, null)
    }

    fun snapTo(centerX: Float, centerY: Float) {
        x = centerX
        y = centerY
        startX = centerX
        startY = centerY
        targetX = centerX
        targetY = centerY
        syncDstRect()
    }

    fun moveTo(centerX: Float, centerY: Float) {
        beginHit(centerX, centerY, false)
    }

    fun hitAndDisappear() {
        beginHit(x, y, true)
    }

    fun expireImmediately() {
        isMoving = false
        isExpired = true
    }

    private fun beginHit(centerX: Float, centerY: Float, disappears: Boolean) {
        startX = x
        startY = y
        targetX = centerX
        targetY = centerY
        elapsed = 0f
        isMoving = true
        disappearsAfterHit = disappears
    }

    private fun syncDstRect() {
        val bitmap = idleFrames.first()
        val width = height * bitmap.width / bitmap.height.toFloat()
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }

    private fun currentBitmap(): Bitmap {
        val frames = if (isMoving && hitFrames.isNotEmpty()) hitFrames else idleFrames
        val fps = if (isMoving && hitFrames.isNotEmpty()) hitFps else idleFps
        if (fps <= 0f) return frames.first()

        val index = if (isMoving) {
            (elapsed * fps).toInt().coerceIn(0, frames.lastIndex)
        } else {
            (elapsed * fps).toInt() % frames.size
        }
        return frames[index]
    }

    private fun lerp(from: Float, to: Float, t: Float): Float {
        return from + (to - from) * t
    }

    companion object {
        private const val MOVE_DURATION = 0.33f
        private const val DEFAULT_HIT_FPS = 18f
    }
}

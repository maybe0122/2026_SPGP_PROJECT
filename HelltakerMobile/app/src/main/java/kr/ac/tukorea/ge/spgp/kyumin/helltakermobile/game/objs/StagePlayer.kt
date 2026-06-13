package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import androidx.core.graphics.withScale
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage.MoveDirection
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StagePlayer(
    gctx: GameContext,
    idleFrameResIds: List<Int>,
    moveFrameResIds: List<Int>,
    kickFrameResIds: List<Int>,
    private val height: Float,
) : IGameObject {
    private enum class Action {
        IDLE, MOVE, KICK
    }

    private val idleFrames: List<Bitmap> = idleFrameResIds.map { gctx.res.getBitmap(it) }
    private val moveFrames: List<Bitmap> = moveFrameResIds.map { gctx.res.getBitmap(it) }
    private val kickFrames: List<Bitmap> = kickFrameResIds.map { gctx.res.getBitmap(it) }
    private val dstRect = RectF()
    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var elapsed = 0f
    private var idleElapsed = 0f
    private var spikeHitElapsed = SPIKE_HIT_DURATION
    private var startX = 0f
    private var startY = 0f
    private var targetX = 0f
    private var targetY = 0f
    private var x = 0f
    private var y = 0f
    private var facingLeft = false
    private var action = Action.IDLE

    val isBusy: Boolean
        get() = action != Action.IDLE

    val centerX: Float
        get() = x

    val centerY: Float
        get() = y

    override fun update(gctx: GameContext) {
        idleElapsed += gctx.frameTime
        spikeHitElapsed = (spikeHitElapsed + gctx.frameTime).coerceAtMost(SPIKE_HIT_DURATION)
        if (action == Action.IDLE) return

        elapsed += gctx.frameTime
        if (action == Action.MOVE) {
            val t = (elapsed / MOVE_DURATION).coerceIn(0f, 1f)
            val eased = t * t * (3f - 2f * t)
            x = lerp(startX, targetX, eased)
            y = lerp(startY, targetY, eased)
            syncDstRect()
        }
        if (elapsed >= actionDuration()) {
            if (action == Action.MOVE) {
                x = targetX
                y = targetY
                syncDstRect()
            }
            action = Action.IDLE
            elapsed = 0f
            idleElapsed = 0f
        }
    }

    override fun draw(canvas: Canvas) {
        val bitmap = currentBitmap()
        val hitProgress = (spikeHitElapsed / SPIKE_HIT_DURATION).coerceIn(0f, 1f)
        val hitScale = if (spikeHitElapsed < SPIKE_HIT_DURATION) {
            1f + (1f - hitProgress) * SPIKE_HIT_SCALE
        } else {
            1f
        }
        drawPaint.colorFilter = if (spikeHitElapsed < SPIKE_HIT_DURATION) {
            SPIKE_HIT_COLOR_FILTER
        } else {
            null
        }
        val scaleX = if (facingLeft) -hitScale else hitScale
        canvas.withScale(scaleX, hitScale, dstRect.centerX(), dstRect.centerY()) {
            drawBitmap(bitmap, null, dstRect, drawPaint)
        }
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

    fun moveTo(centerX: Float, centerY: Float, direction: MoveDirection) {
        updateFacing(direction)
        startX = x
        startY = y
        targetX = centerX
        targetY = centerY
        startAction(Action.MOVE)
    }

    fun kick(direction: MoveDirection) {
        updateFacing(direction)
        startAction(Action.KICK)
    }

    fun takeSpikeHit() {
        spikeHitElapsed = 0f
    }

    private fun updateFacing(direction: MoveDirection) {
        if (direction == MoveDirection.LEFT) facingLeft = true
        if (direction == MoveDirection.RIGHT) facingLeft = false
    }

    private fun startAction(nextAction: Action) {
        action = nextAction
        elapsed = 0f
    }

    private fun currentBitmap(): Bitmap {
        val frames = when (action) {
            Action.IDLE -> idleFrames
            Action.MOVE -> moveFrames
            Action.KICK -> kickFrames
        }
        if (action == Action.IDLE) {
            return frames[idleFrameIndex(frames.size)]
        }
        if (action == Action.MOVE) {
            val progress = (elapsed / MOVE_DURATION).coerceIn(0f, 1f)
            val index = (progress * frames.size).toInt().coerceIn(0, frames.lastIndex)
            return frames[index]
        }

        val index = (elapsed * KICK_FPS).toInt().coerceIn(0, frames.lastIndex)
        return frames[index]
    }

    private fun idleFrameIndex(frameCount: Int): Int {
        if (frameCount <= 1) return 0

        val sequenceIndex = (idleElapsed * IDLE_FPS).toInt() % IDLE_FRAME_SEQUENCE.size
        return IDLE_FRAME_SEQUENCE[sequenceIndex].coerceAtMost(frameCount - 1)
    }

    private fun actionDuration(): Float {
        return when (action) {
            Action.IDLE -> 0f
            Action.MOVE -> MOVE_DURATION
            Action.KICK -> kickFrames.size / KICK_FPS
        }
    }

    private fun syncDstRect() {
        val bitmap = moveFrames.first()
        val width = height * bitmap.width / bitmap.height.toFloat()
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }

    private fun lerp(from: Float, to: Float, t: Float): Float {
        return from + (to - from) * t
    }

    companion object {
        private const val IDLE_FPS = 10f
        private const val KICK_FPS = 18f
        const val MOVE_DURATION = 0.28f
        private const val SPIKE_HIT_DURATION = 0.24f
        private const val SPIKE_HIT_SCALE = 0.08f
        private val IDLE_FRAME_SEQUENCE = intArrayOf(0, 1, 2, 3, 4, 5, 4, 3, 2, 1)
        private val SPIKE_HIT_COLOR_FILTER = PorterDuffColorFilter(
            Color.rgb(226, 42, 49),
            PorterDuff.Mode.SRC_IN,
        )
    }
}

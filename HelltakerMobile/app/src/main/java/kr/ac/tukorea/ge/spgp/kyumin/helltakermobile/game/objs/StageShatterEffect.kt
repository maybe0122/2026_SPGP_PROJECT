package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageShatterEffect(
    gctx: GameContext,
    bitmapResIds: List<Int>,
    private val originX: Float,
    private val originY: Float,
    tileSize: Float,
) : IGameObject {
    private data class Motion(
        val offsetX: Float,
        val offsetY: Float,
        val velocityX: Float,
        val velocityY: Float,
        val rotation: Float,
        val rotationSpeed: Float,
        val scale: Float,
    )

    private data class Piece(
        val bitmap: Bitmap,
        val motion: Motion,
    )

    private val unitScale = tileSize / REFERENCE_TILE_SIZE
    private val pieces = bitmapResIds.mapIndexed { index, resId ->
        Piece(
            bitmap = gctx.res.getBitmap(resId),
            motion = MOTIONS[index % MOTIONS.size],
        )
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dstRect = RectF()
    private var elapsed = 0f

    var isFinished = false
        private set

    override fun update(gctx: GameContext) {
        elapsed += gctx.frameTime
        if (elapsed >= DURATION) {
            isFinished = true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isFinished) return
        val progress = (elapsed / DURATION).coerceIn(0f, 1f)
        paint.alpha = if (progress < FADE_START) {
            255
        } else {
            (((1f - progress) / (1f - FADE_START)) * 255f).toInt()
        }

        for (piece in pieces) {
            val motion = piece.motion
            val x = originX + unitScale * (motion.offsetX + motion.velocityX * elapsed)
            val y = originY + unitScale * (
                motion.offsetY +
                    motion.velocityY * elapsed +
                    0.5f * GRAVITY * elapsed * elapsed
                )
            val scale = BASE_BITMAP_SCALE * unitScale * motion.scale
            val width = piece.bitmap.width * scale
            val height = piece.bitmap.height * scale
            dstRect.set(
                x - width / 2f,
                y - height / 2f,
                x + width / 2f,
                y + height / 2f,
            )
            canvas.withRotation(
                motion.rotation + motion.rotationSpeed * elapsed,
                x,
                y,
            ) {
                drawBitmap(piece.bitmap, null, dstRect, paint)
            }
        }
    }

    companion object {
        private const val REFERENCE_TILE_SIZE = 83.333f
        private const val DURATION = 0.92f
        private const val FADE_START = 0.78f
        private const val GRAVITY = 1050f
        private const val BASE_BITMAP_SCALE = 1f

        private val MOTIONS = listOf(
            Motion(-4f, -6f, -270f, -360f, -20f, -320f, 0.95f),
            Motion(0f, -10f, -110f, -480f, 25f, 270f, 1.10f),
            Motion(5f, -5f, 120f, -430f, -35f, -220f, 0.90f),
            Motion(8f, -2f, 280f, -320f, 15f, 180f, 1.08f),
            Motion(-4f, 2f, -330f, -150f, 30f, -360f, 0.88f),
            Motion(3f, 3f, 220f, -80f, -15f, 300f, 0.92f),
            Motion(0f, 5f, 40f, -250f, 40f, 400f, 1.02f),
        )
    }
}

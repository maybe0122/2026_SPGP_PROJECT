package kr.ac.tukorea.ge.spgp2026.a2dg.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {
    private val gctx = GameContext(this)
    private var running = true

    private val activity: Activity?
        get() {
            var current = context
            while (current is ContextWrapper) {
                if (current is Activity) return current
                current = current.baseContext
            }
            return null
        }

    init {
        gctx.sceneStack.onEmptyStack = {
            activity?.finish()
        }
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun setRootScene(factory: (GameContext) -> Scene) {
        gctx.sceneStack.push(factory(gctx))
    }

    fun update() {
        gctx.sceneStack.top?.update(gctx)
    }

    fun pauseGame() {
        if (!running) return
        running = false
        gctx.sceneStack.top?.onPause()
    }

    fun resumeGame() {
        if (running) return
        running = true
        gctx.currentTimeNanos = 0L
        Choreographer.getInstance().postFrameCallback(this)
        gctx.sceneStack.top?.onResume()
    }

    fun destroyGame() {
        gctx.sceneStack.popAll(finishesActivity = false)
        gctx.res.release()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(2, 2, 22))
        canvas.withMatrix(gctx.metrics.transformMatrix) {
            drawScenes(canvas)
        }
    }

    private fun drawScenes(canvas: Canvas) {
        val stack = gctx.sceneStack
        if (stack.isEmpty) return

        var firstIndex = stack.lastIndex
        while (firstIndex > 0 && stack.sceneAt(firstIndex).isTransparent) {
            firstIndex--
        }

        var index = firstIndex
        while (index <= stack.lastIndex) {
            val scene = stack.sceneAt(index)
            val saveCount = canvas.save()
            if (scene.clipsRect) {
                canvas.clipRect(gctx.metrics.borderRect)
            }
            scene.draw(canvas)
            canvas.restoreToCount(saveCount)
            index++
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gctx.metrics.onSize(w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gctx.sceneStack.top?.onTouchEvent(event) ?: false
        return handled || super.onTouchEvent(event)
    }

    fun onBackPressed(): Boolean {
        return gctx.sceneStack.top?.onBackPressed() ?: false
    }

    override fun doFrame(nanos: Long) {
        if (!running) return

        val previousNanos = gctx.currentTimeNanos
        gctx.currentTimeNanos = nanos
        if (previousNanos != 0L) {
            gctx.frameTime = ((nanos - previousNanos) / NANOS_PER_SECOND)
                .coerceAtMost(MAX_FRAME_TIME)
            update()
            if (gctx.sceneStack.top == null) return
            invalidate()
        }

        if (running && isShown) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    companion object {
        private const val NANOS_PER_SECOND = 1_000_000_000f
        private const val MAX_FRAME_TIME = 0.05f
    }
}

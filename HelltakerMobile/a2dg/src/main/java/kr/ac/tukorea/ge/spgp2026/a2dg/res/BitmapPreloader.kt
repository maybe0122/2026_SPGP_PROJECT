package kr.ac.tukorea.ge.spgp2026.a2dg.res

import java.util.ArrayDeque

class BitmapPreloader(
    private val resources: GameResources,
) {
    private val queue = ArrayDeque<Int>()
    private val queuedIds = mutableSetOf<Int>()

    val isComplete: Boolean
        get() = queue.isEmpty()

    fun replace(resIds: Iterable<Int>) {
        clear()
        enqueue(resIds)
    }

    fun enqueue(resIds: Iterable<Int>) {
        for (resId in resIds) {
            if (resources.isBitmapLoaded(resId) || !queuedIds.add(resId)) continue
            queue.addLast(resId)
        }
    }

    fun update(maxLoadsPerFrame: Int = 1) {
        repeat(maxLoadsPerFrame.coerceAtLeast(0)) {
            val resId = queue.pollFirst() ?: return
            queuedIds.remove(resId)
            resources.getBitmap(resId)
        }
    }

    fun clear() {
        queue.clear()
        queuedIds.clear()
    }
}

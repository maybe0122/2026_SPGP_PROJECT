package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StageBackground(
    gctx: GameContext,
    resId: Int,
) : IGameObject {
    private val bitmap = gctx.res.getBitmap(resId)

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(2, 2, 22))
        canvas.drawBitmap(bitmap, null, SCREEN_RECT, null)
    }

    companion object {
        private val SCREEN_RECT = RectF(0f, 0f, 1600f, 900f)
    }
}

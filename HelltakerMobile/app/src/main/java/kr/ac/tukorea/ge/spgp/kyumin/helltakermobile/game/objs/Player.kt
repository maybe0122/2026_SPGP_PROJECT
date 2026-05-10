package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Player(gctx: GameContext): Sprite(gctx, R.mipmap.assets100v20053) {
    init {
        setCenterProportionalWidth(960f, 240f, 100f)
    }
}

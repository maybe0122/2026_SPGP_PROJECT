package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.scenes

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.objs.Player
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {

    enum class Layer {
        BG, PLAYER
    }

    override val clipsRect = true
    override val world = World(Layer.entries.toTypedArray()).apply {
        add(Sprite(gctx, R.mipmap.chapter_bg0001).apply {
            setCenterProportionalWidth(800f, 450f, 1600f)
        }, Layer.BG)
        add(Player(gctx), Layer.PLAYER)
    }
}
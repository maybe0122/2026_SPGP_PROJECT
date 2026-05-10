package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.app

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.BuildConfig
import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.scenes.MainScene
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class HelltakerMobileActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = BuildConfig.DEBUG
    override val drawsDebugInfo: Boolean = BuildConfig.DEBUG
    override val drawsFpsGraph: Boolean = BuildConfig.DEBUG
    override fun createRootScene(gctx: GameContext): Scene {
        gctx.metrics.setSize(1600f, 900f)
        return MainScene(gctx)
    }
}
package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R

internal object StageAssets {
    val playerIdleFrames = listOf(
        R.mipmap.hero0025,
        R.mipmap.hero0026,
        R.mipmap.hero0027,
        R.mipmap.hero0028,
        R.mipmap.hero0029,
        R.mipmap.hero0030,
    )

    val playerMoveFrames = listOf(
        R.mipmap.assets100v20053,
        R.mipmap.assets100v20054,
        R.mipmap.assets100v20055,
        R.mipmap.assets100v20056,
        R.mipmap.assets100v20057,
        R.mipmap.assets100v20058,
    )

    val playerKickFrames = listOf(
        R.mipmap.hero0040,
        R.mipmap.hero0041,
        R.mipmap.hero0042,
        R.mipmap.hero0043,
        R.mipmap.hero0044,
        R.mipmap.hero0045,
    )

    val enemyIdleFrames = listOf(
        R.mipmap.assets100v20225,
        R.mipmap.assets100v20226,
        R.mipmap.assets100v20227,
        R.mipmap.assets100v20228,
        R.mipmap.assets100v20229,
        R.mipmap.assets100v20230,
        R.mipmap.assets100v20231,
        R.mipmap.assets100v20232,
        R.mipmap.assets100v20233,
        R.mipmap.assets100v20234,
        R.mipmap.assets100v20235,
        R.mipmap.assets100v20236,
    )

    val enemyHitFrames = listOf(
        R.mipmap.assets100v20198,
        R.mipmap.assets100v20199,
        R.mipmap.assets100v20200,
        R.mipmap.assets100v20201,
        R.mipmap.assets100v20202,
        R.mipmap.assets100v20203,
    )

    val moveDustFrames = listOf(
        R.mipmap.small_vfx0001,
        R.mipmap.small_vfx0002,
        R.mipmap.small_vfx0003,
        R.mipmap.small_vfx0004,
        R.mipmap.small_vfx0005,
        R.mipmap.small_vfx0006,
        R.mipmap.small_vfx0007,
        R.mipmap.small_vfx0008,
        R.mipmap.small_vfx0009,
    )

    val impactFrames = listOf(
        R.mipmap.vfx0001,
        R.mipmap.vfx0002,
        R.mipmap.vfx0003,
        R.mipmap.vfx0004,
        R.mipmap.vfx0005,
        R.mipmap.vfx0006,
        R.mipmap.vfx0007,
        R.mipmap.vfx0008,
        R.mipmap.vfx0009,
        R.mipmap.vfx0010,
    )

    val shatterPieces = listOf(
        R.mipmap.particle0001,
        R.mipmap.particle0002,
        R.mipmap.particle0003,
        R.mipmap.particle0004,
        R.mipmap.particle0006,
        R.mipmap.particle0007,
        R.mipmap.particle0008,
    )

    val fireFrames = listOf(
        R.mipmap.fire0001,
        R.mipmap.fire0002,
        R.mipmap.fire0003,
        R.mipmap.fire0004,
        R.mipmap.fire0005,
        R.mipmap.fire0006,
        R.mipmap.fire0007,
        R.mipmap.fire0008,
        R.mipmap.fire0009,
        R.mipmap.fire0010,
        R.mipmap.fire0011,
        R.mipmap.fire0012,
    )

    val keyFrames = listOf(
        R.mipmap.assets100v20104,
        R.mipmap.assets100v20105,
        R.mipmap.assets100v20106,
        R.mipmap.assets100v20107,
        R.mipmap.assets100v20108,
        R.mipmap.assets100v20109,
        R.mipmap.assets100v20110,
        R.mipmap.assets100v20111,
        R.mipmap.assets100v20112,
        R.mipmap.assets100v20113,
        R.mipmap.assets100v20114,
        R.mipmap.assets100v20115,
    )

    val fixedSpikeResId = R.mipmap.assets100v20116

    val waveSpikeFrames = listOf(
        R.mipmap.assets100v20116,
        R.mipmap.assets100v20117,
        R.mipmap.assets100v20118,
        R.mipmap.assets100v20119,
        R.mipmap.assets100v20120,
        R.mipmap.assets100v20121,
        R.mipmap.assets100v20122,
        R.mipmap.assets100v20123,
    )

    val spikeDamageRingFrames = listOf(
        R.mipmap.huge_vfx0001,
        R.mipmap.huge_vfx0002,
        R.mipmap.huge_vfx0003,
        R.mipmap.huge_vfx0004,
        R.mipmap.huge_vfx0005,
        R.mipmap.huge_vfx0006,
        R.mipmap.huge_vfx0007,
        R.mipmap.huge_vfx0008,
        R.mipmap.huge_vfx0009,
    )

    val spikeDamageSmokeFrames = listOf(
        R.mipmap.huge_vfx0010,
        R.mipmap.huge_vfx0011,
        R.mipmap.huge_vfx0012,
        R.mipmap.huge_vfx0013,
        R.mipmap.huge_vfx0014,
        R.mipmap.huge_vfx0015,
        R.mipmap.huge_vfx0016,
        R.mipmap.huge_vfx0017,
        R.mipmap.huge_vfx0018,
        R.mipmap.huge_vfx0019,
        R.mipmap.huge_vfx0020,
        R.mipmap.huge_vfx0021,
        R.mipmap.huge_vfx0022,
        R.mipmap.huge_vfx0023,
        R.mipmap.huge_vfx0024,
        R.mipmap.huge_vfx0025,
        R.mipmap.huge_vfx0026,
        R.mipmap.huge_vfx0027,
    )

    val runtimeEffectPreloadFrames =
        spikeDamageRingFrames + spikeDamageSmokeFrames

    private val stageUiFrames = listOf(
        R.mipmap.main_ui_export_bui2,
        R.mipmap.main_ui_export_ui0001,
        R.mipmap.menu_button,
        R.mipmap.menu_button_selected,
        R.mipmap.arrow_bar,
        R.mipmap.lovesign,
        R.mipmap.flamebase0001,
        R.mipmap.flamebase0002,
        R.mipmap.stage_lockbox,
    )

    val stagePreloadFrames =
        playerIdleFrames +
        playerMoveFrames +
        playerKickFrames +
        enemyIdleFrames +
        enemyHitFrames +
        moveDustFrames +
        impactFrames +
        shatterPieces +
        fireFrames +
        stageUiFrames +
        waveSpikeFrames +
        listOf(
        fixedSpikeResId,
        R.mipmap.box_export0002,
        R.mipmap.box_export0003,
        R.mipmap.box_export0004,
        R.mipmap.box_export0005,
        R.mipmap.box_export0006,
        R.mipmap.box_export0007,
        R.mipmap.box_export0008,
        R.mipmap.box_export0009,
        R.mipmap.box_export0010,
        R.mipmap.box_export0011,
    ) + keyFrames + runtimeEffectPreloadFrames

    fun stoneResId(tag: String?): Int {
        return when (tag) {
            "1" -> R.mipmap.stage_stone
            "2" -> R.mipmap.box_export0002
            "3" -> R.mipmap.box_export0003
            "4" -> R.mipmap.box_export0004
            "5" -> R.mipmap.box_export0005
            "6" -> R.mipmap.box_export0006
            "7" -> R.mipmap.box_export0007
            "8" -> R.mipmap.box_export0008
            "9" -> R.mipmap.box_export0009
            "10" -> R.mipmap.box_export0010
            "11" -> R.mipmap.box_export0011
            else -> R.mipmap.stage_stone
        }
    }

    val loveExplosionFrames = listOf(
        R.mipmap.love_plosion0001,
        R.mipmap.love_plosion0002,
        R.mipmap.love_plosion0003,
        R.mipmap.love_plosion0004,
        R.mipmap.love_plosion0005,
        R.mipmap.love_plosion0006,
        R.mipmap.love_plosion0007,
        R.mipmap.love_plosion0008,
        R.mipmap.love_plosion0009,
        R.mipmap.love_plosion0010,
        R.mipmap.love_plosion0011,
        R.mipmap.love_plosion0012,
        R.mipmap.love_plosion0013,
        R.mipmap.love_plosion0014,
        R.mipmap.love_plosion0015,
    )

    val badEndFrames = listOf(
        R.mipmap.dialogue_death0001,
        R.mipmap.dialogue_death0002,
        R.mipmap.dialogue_death0003,
        R.mipmap.dialogue_death0004,
        R.mipmap.dialogue_death0005,
        R.mipmap.dialogue_death0006,
        R.mipmap.dialogue_death0007,
        R.mipmap.dialogue_death0008,
        R.mipmap.dialogue_death0009,
    )

    val successFrames = listOf(
        R.mipmap.success0001,
        R.mipmap.success0002,
        R.mipmap.success0003,
        R.mipmap.success0004,
        R.mipmap.success0005,
        R.mipmap.success0006,
        R.mipmap.success0007,
        R.mipmap.success0008,
        R.mipmap.success0009,
        R.mipmap.success0010,
        R.mipmap.success0011,
        R.mipmap.success0012,
        R.mipmap.success0013,
        R.mipmap.success0014,
        R.mipmap.success0015,
        R.mipmap.success0016,
    )

    val deathFrames = listOf(
        R.mipmap.death_p20001,
        R.mipmap.death_p20002,
        R.mipmap.death_p20003,
        R.mipmap.death_p20004,
        R.mipmap.death_p20005,
        R.mipmap.death_p20006,
        R.mipmap.death_p20007,
        R.mipmap.death_p20008,
        R.mipmap.death_p20009,
        R.mipmap.death_p20010,
        R.mipmap.death_p20011,
        R.mipmap.death_p20012,
        R.mipmap.death_p20013,
        R.mipmap.death_p20014,
        R.mipmap.death_p20015,
        R.mipmap.death_p20016,
        R.mipmap.death_p20017,
        R.mipmap.death_p20018,
    )

    val resultPreloadFrames =
        successFrames +
        loveExplosionFrames +
        badEndFrames +
        deathFrames +
        listOf(
            R.mipmap.dialogue_bg_hell,
            R.mipmap.menu_button,
            R.mipmap.menu_button_selected,
        )

    fun targetFrames(stageNo: Int): List<Int> {
        return when (stageNo) {
            1 -> pandemonicaFrames
            2 -> modeusFrames
            3 -> cerberusFrames
            4 -> malinaFrames
            5 -> zdradaFrames
            6 -> azazelFrames
            7 -> justiceFrames
            8 -> judgementFrames
            else -> pandemonicaFrames
        }
    }

    private val pandemonicaFrames = listOf(
        R.mipmap.pandemonica_finalmodel0001,
        R.mipmap.pandemonica_finalmodel0002,
        R.mipmap.pandemonica_finalmodel0003,
        R.mipmap.pandemonica_finalmodel0004,
        R.mipmap.pandemonica_finalmodel0005,
        R.mipmap.pandemonica_finalmodel0006,
        R.mipmap.pandemonica_finalmodel0007,
        R.mipmap.pandemonica_finalmodel0008,
        R.mipmap.pandemonica_finalmodel0009,
        R.mipmap.pandemonica_finalmodel0010,
        R.mipmap.pandemonica_finalmodel0011,
        R.mipmap.pandemonica_finalmodel0012,
    )

    private val modeusFrames = listOf(
        R.mipmap.modeus_finalmodel0001,
        R.mipmap.modeus_finalmodel0002,
        R.mipmap.modeus_finalmodel0003,
        R.mipmap.modeus_finalmodel0004,
        R.mipmap.modeus_finalmodel0005,
        R.mipmap.modeus_finalmodel0006,
        R.mipmap.modeus_finalmodel0007,
        R.mipmap.modeus_finalmodel0008,
        R.mipmap.modeus_finalmodel0009,
        R.mipmap.modeus_finalmodel0010,
        R.mipmap.modeus_finalmodel0011,
        R.mipmap.modeus_finalmodel0012,
    )

    private val cerberusFrames = listOf(
        R.mipmap.cerberus_finalmodel0001,
        R.mipmap.cerberus_finalmodel0002,
        R.mipmap.cerberus_finalmodel0003,
        R.mipmap.cerberus_finalmodel0004,
        R.mipmap.cerberus_finalmodel0005,
        R.mipmap.cerberus_finalmodel0006,
        R.mipmap.cerberus_finalmodel0007,
        R.mipmap.cerberus_finalmodel0008,
        R.mipmap.cerberus_finalmodel0009,
        R.mipmap.cerberus_finalmodel0010,
        R.mipmap.cerberus_finalmodel0011,
        R.mipmap.cerberus_finalmodel0012,
    )

    private val malinaFrames = listOf(
        R.mipmap.malina_finalmodel0001,
        R.mipmap.malina_finalmodel0002,
        R.mipmap.malina_finalmodel0003,
        R.mipmap.malina_finalmodel0004,
        R.mipmap.malina_finalmodel0005,
        R.mipmap.malina_finalmodel0006,
        R.mipmap.malina_finalmodel0007,
        R.mipmap.malina_finalmodel0008,
        R.mipmap.malina_finalmodel0009,
        R.mipmap.malina_finalmodel0010,
        R.mipmap.malina_finalmodel0011,
        R.mipmap.malina_finalmodel0012,
    )

    private val zdradaFrames = listOf(
        R.mipmap.zdrada_finalmodel0001,
        R.mipmap.zdrada_finalmodel0002,
        R.mipmap.zdrada_finalmodel0003,
        R.mipmap.zdrada_finalmodel0004,
        R.mipmap.zdrada_finalmodel0005,
        R.mipmap.zdrada_finalmodel0006,
        R.mipmap.zdrada_finalmodel0007,
        R.mipmap.zdrada_finalmodel0008,
        R.mipmap.zdrada_finalmodel0009,
        R.mipmap.zdrada_finalmodel0010,
        R.mipmap.zdrada_finalmodel0011,
        R.mipmap.zdrada_finalmodel0012,
    )

    private val azazelFrames = listOf(
        R.mipmap.azazel_finalmodel0001,
        R.mipmap.azazel_finalmodel0002,
        R.mipmap.azazel_finalmodel0003,
        R.mipmap.azazel_finalmodel0004,
        R.mipmap.azazel_finalmodel0005,
        R.mipmap.azazel_finalmodel0006,
        R.mipmap.azazel_finalmodel0007,
        R.mipmap.azazel_finalmodel0008,
        R.mipmap.azazel_finalmodel0009,
        R.mipmap.azazel_finalmodel0010,
        R.mipmap.azazel_finalmodel0011,
        R.mipmap.azazel_finalmodel0012,
    )

    private val justiceFrames = listOf(
        R.mipmap.justice_finalmodel0001,
        R.mipmap.justice_finalmodel0002,
        R.mipmap.justice_finalmodel0003,
        R.mipmap.justice_finalmodel0004,
        R.mipmap.justice_finalmodel0005,
        R.mipmap.justice_finalmodel0006,
        R.mipmap.justice_finalmodel0007,
        R.mipmap.justice_finalmodel0008,
        R.mipmap.justice_finalmodel0009,
        R.mipmap.justice_finalmodel0010,
        R.mipmap.justice_finalmodel0011,
        R.mipmap.justice_finalmodel0012,
    )

    private val judgementFrames = listOf(
        R.mipmap.judgement_finalmodel0001,
        R.mipmap.judgement_finalmodel0002,
        R.mipmap.judgement_finalmodel0003,
        R.mipmap.judgement_finalmodel0004,
        R.mipmap.judgement_finalmodel0005,
        R.mipmap.judgement_finalmodel0006,
        R.mipmap.judgement_finalmodel0007,
        R.mipmap.judgement_finalmodel0008,
        R.mipmap.judgement_finalmodel0009,
        R.mipmap.judgement_finalmodel0010,
        R.mipmap.judgement_finalmodel0011,
        R.mipmap.judgement_finalmodel0012,
    )
}

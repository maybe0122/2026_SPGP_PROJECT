package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.audio

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R
import kr.ac.tukorea.ge.spgp2026.a2dg.res.Sound

object GameAudio {
    val effectResIds = listOf(
        R.raw.abyss_portal_opening_01,
        R.raw.abyss_start_01,
        R.raw.bad_end_screen_01,
        R.raw.button_chapter_confirm_01,
        R.raw.button_chapter_highlight_01,
        R.raw.button_dialogue_confirm_01,
        R.raw.button_dialogue_highlight_01,
        R.raw.button_menu_confirm_01,
        R.raw.button_menu_highlight_01,
        R.raw.character_move_01,
        R.raw.dialogue_start_01,
        R.raw.dialogue_success_01,
        R.raw.door_closed_kick_01,
        R.raw.door_opening_01,
        R.raw.enemy_die_01,
        R.raw.enemy_kick_01,
        R.raw.key_pick_up_01,
        R.raw.player_death_01,
        R.raw.screen_changer_part1_01,
        R.raw.screen_changer_part2_01,
        R.raw.spikes_damage_01,
        R.raw.stone_move_01,
        R.raw.succub_capture_01,
    )

    fun preload(sound: Sound) {
        sound.preloadEffects(effectResIds)
    }
}

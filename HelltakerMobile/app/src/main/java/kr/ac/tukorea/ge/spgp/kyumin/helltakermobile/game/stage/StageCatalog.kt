package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

import kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.R

data class StageChoiceResult(
    val isSuccess: Boolean,
    val responseLines: List<String>,
    val portraitResId: Int,
    val badEndLines: List<String> = emptyList(),
)

data class StageChoice(
    val text: String,
    val result: StageChoiceResult,
)

data class StageDialogue(
    val speaker: String,
    val promptLines: List<String>,
    val portraitResId: Int,
    val choices: List<StageChoice>,
)

data class StageDefinition(
    val stageNo: Int,
    val backgroundResId: Int,
    val dialogue: StageDialogue?,
) {
    val assetPath: String
        get() = "stages/stage${stageNo.toString().padStart(2, '0')}.csv"

    val targetFrames: List<Int>
        get() = StageAssets.targetFrames(stageNo)

    val preloadResIds: List<Int>
        get() {
            val portraitResIds = dialogue?.let { dialogue ->
                buildList {
                    add(dialogue.portraitResId)
                    for (choice in dialogue.choices) {
                        add(choice.result.portraitResId)
                    }
                }
            }.orEmpty()
            return (
                listOf(backgroundResId) +
                    targetFrames +
                    portraitResIds +
                    StageAssets.stagePreloadFrames +
                    StageAssets.resultPreloadFrames
                ).distinct()
        }
}

data class StageSelectionEntry(
    val displayNo: Int,
    val stageNo: Int,
    val romanNumeral: String,
    val title: String,
) {
    val description: String
        get() = "Chapter $romanNumeral - $title"
}

object StageCatalog {
    val selectionEntries = listOf(
        StageSelectionEntry(1, 1, "I", "The Tired Demon"),
        StageSelectionEntry(2, 2, "II", "The Lustful Demon"),
        StageSelectionEntry(3, 3, "III", "The Triple Demon"),
        StageSelectionEntry(4, 4, "IV", "The Sour Demon"),
        StageSelectionEntry(5, 5, "V", "The Bitch Demon"),
        StageSelectionEntry(6, 6, "VI", "The Curious Angel"),
        StageSelectionEntry(7, 7, "VII", "The Awesome Demon"),
        StageSelectionEntry(8, 8, "VIII", "Judgement"),
    )
    val stageNumbers = selectionEntries.map(StageSelectionEntry::stageNo)

    fun get(stageNo: Int): StageDefinition {
        return definitions[stageNo] ?: error("Unsupported stage number: $stageNo")
    }

    fun nextStageNo(stageNo: Int): Int? {
        val index = stageNumbers.indexOf(stageNo)
        if (index < 0 || index == stageNumbers.lastIndex) return null
        return stageNumbers[index + 1]
    }

    private val definitions = listOf(
        StageDefinition(
            stageNo = 1,
            backgroundResId = R.mipmap.chapter_bg0001,
            dialogue = StageDialogue(
                speaker = "Pandemonica, the Tired Demon",
                promptLines = listOf(
                    "Name's Pandemonica, Hell's Customer Service.",
                    "How may I serve you?",
                ),
                portraitResId = R.mipmap.dialogue_pand_idle,
                choices = listOf(
                    StageChoice(
                        text = "We can figure something out at my place.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "You thought you're leaving hell alive? How delusional.",
                            ),
                            portraitResId = R.mipmap.dialogue_pand_idle,
                            badEndLines = listOf(
                                "She took your face in her hands and snapped your neck",
                                "with professional gentleness.",
                            ),
                        ),
                    ),
                    StageChoice(
                        text = "Maybe I can serve YOU instead?",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Sweet of you to offer. I could really use some coffee.",
                                "I'm not myself without it.",
                            ),
                            portraitResId = R.mipmap.dialogue_pand_flust,
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 2,
            backgroundResId = R.mipmap.chapter_bg0002,
            dialogue = StageDialogue(
                speaker = "Modeus, the Lustful Demon",
                promptLines = listOf("You and me. Now."),
                portraitResId = R.mipmap.dialogue_mod_idle,
                choices = listOf(
                    StageChoice(
                        text = "Deal. No questions asked.",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Demon harem? You poor fool... They will rip you to shreds,",
                                "and I HAVE to see this.",
                            ),
                            portraitResId = R.mipmap.dialogue_mod_close,
                        ),
                    ),
                    StageChoice(
                        text = "No time. Busy gathering girls.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "They always say that... and they always try to run away.",
                                "I'll have to break your knees, just in case.",
                            ),
                            portraitResId = R.mipmap.dialogue_mod_close,
                            badEndLines = listOf(
                                "She pulled out a sledgehammer.",
                                "It was not going to be pretty.",
                            ),
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 3,
            backgroundResId = R.mipmap.chapter_bg0003,
            dialogue = StageDialogue(
                speaker = "Cerberus, the Triple Demon",
                promptLines = listOf(
                    "Are you a human? A real human?",
                    "Please, take us with you.",
                ),
                portraitResId = R.mipmap.dialogue_cer_idle,
                choices = listOf(
                    StageChoice(
                        text = "Deal. No questions asked.",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Yes! At last! It's time to corrupt the mortal realm!",
                            ),
                            portraitResId = R.mipmap.dialogue_cer_happy,
                        ),
                    ),
                    StageChoice(
                        text = "This is getting too easy. I have questions.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "We need a human to cross the hell's gate.",
                                "You don't have to be in one piece, though.",
                            ),
                            portraitResId = R.mipmap.dialogue_cer_bad,
                            badEndLines = listOf(
                                "They jumped you like rabid dogs. Canine sounds included.",
                                "The cuteness of it all gave you a heart attack.",
                            ),
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 4,
            backgroundResId = R.mipmap.chapter_bg0004,
            dialogue = StageDialogue(
                speaker = "Malina, the Sour Demon",
                promptLines = listOf(
                    "Great, more braindead idiots... Never seen your ugly face before.",
                    "What are you playing at?",
                ),
                portraitResId = R.mipmap.dialogue_mal_idle,
                choices = listOf(
                    StageChoice(
                        text = "I'd sure love to play with you.",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Like what... video games? Sure, why not.",
                                "As long as you're okay with turn based strategies.",
                            ),
                            portraitResId = R.mipmap.dialogue_mal_phone,
                        ),
                    ),
                    StageChoice(
                        text = "This is delicious. Please, insult me more.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "Fantastic. Just my luck to find another masochist creep.",
                            ),
                            portraitResId = R.mipmap.dialogue_mal_puzzled,
                            badEndLines = listOf(
                                "She made a swiping motion and a fountain of blood",
                                "exploded from your devastated throat.",
                            ),
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 5,
            backgroundResId = R.mipmap.chapter_bg0005,
            dialogue = StageDialogue(
                speaker = "Zdrada, the Bitch Demon",
                promptLines = listOf("Yo. I've heard about your harem. I'm in."),
                portraitResId = R.mipmap.dialogue_z_idle,
                choices = listOf(
                    StageChoice(
                        text = "Wait. I have a feeling I'll regret it.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "Holy shit. Let me put you out of your misery.",
                            ),
                            portraitResId = R.mipmap.dialogue_z_snap,
                            badEndLines = listOf(
                                "Your vision swam. The last thing you noticed was a knife",
                                "buried hilt deep between your ribs.",
                            ),
                        ),
                    ),
                    StageChoice(
                        text = "It's not really a harem anymore. We just play turn based strategies.",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Too bad, I'm coming anyway. Go ahead, try to stop me.",
                            ),
                            portraitResId = R.mipmap.dialogue_z_laugh,
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 6,
            backgroundResId = R.mipmap.chapter_bg0006,
            dialogue = StageDialogue(
                speaker = "Azazel, the Curious Angel",
                promptLines = listOf(
                    "Oh my heavens! What would a living human be doing in hell?",
                    "Most unusual.",
                ),
                portraitResId = R.mipmap.dialogue_az_idle,
                choices = listOf(
                    StageChoice(
                        text = "Looking for demons.",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "Oh, you too? Let me guess, thesis on Modern Sin?",
                                "Or is it Demonology? We should join forces.",
                            ),
                            portraitResId = R.mipmap.dialogue_az_note,
                        ),
                    ),
                    StageChoice(
                        text = "Looking for angels.",
                        result = StageChoiceResult(
                            isSuccess = false,
                            responseLines = listOf(
                                "You won't find many here. Lucky for you, I know a better place.",
                                "Please, hold my hand.",
                            ),
                            portraitResId = R.mipmap.dialogue_az_shock,
                            badEndLines = listOf(
                                "Who would've guessed that lying will get you into heaven.",
                                "Say goodbye to your demon harem, though.",
                            ),
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 7,
            backgroundResId = R.mipmap.chapter_bg0007,
            dialogue = StageDialogue(
                speaker = "Justice, the Awesome Demon",
                promptLines = listOf(
                    "Yo, did you just solve that puzzle? That's awesome!",
                ),
                portraitResId = R.mipmap.dialogue_jus_idle,
                choices = listOf(
                    StageChoice(
                        text = "It took some work. Wanna join my harem?",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "You don't have to ask me. I'd never miss a party.",
                            ),
                            portraitResId = R.mipmap.dialogue_jus_idle,
                        ),
                    ),
                    StageChoice(
                        text = "I just skipped it in the menu. Wanna join my harem?",
                        result = StageChoiceResult(
                            isSuccess = true,
                            responseLines = listOf(
                                "That totally sounds like something I would've done.",
                                "Sure, let's go. The more the merrier, right?",
                            ),
                            portraitResId = R.mipmap.dialogue_jus_curious,
                        ),
                    ),
                ),
            ),
        ),
        StageDefinition(
            stageNo = 8,
            backgroundResId = R.mipmap.chapter_bg0008,
            dialogue = null,
        ),
    ).associateBy(StageDefinition::stageNo)
}

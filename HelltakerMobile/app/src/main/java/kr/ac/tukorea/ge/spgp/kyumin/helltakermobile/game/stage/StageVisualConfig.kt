package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

data class StageObjectVisuals(
    val stoneHeightScale: Float = 1.40f,
    val stoneCenterYOffset: Float = 0.20f,
    val keyHeightScale: Float = 2.0f,
    val keyCenterXOffset: Float = 0f,
    val keyCenterYOffset: Float = 0.22f,
    val lockboxHeightScale: Float = 0.95f,
    val lockboxCenterYOffset: Float = 0f,
)

data class StageOffset(
    val x: Float,
    val y: Float,
)

object StageVisualConfig {
    private val defaultObjectVisuals = StageObjectVisuals()

    private val objectVisualsByStage = mapOf(
        8 to StageObjectVisuals(
            stoneHeightScale = 1.40f,
            stoneCenterYOffset = 0.20f,
            keyHeightScale = 2.0f,
            keyCenterXOffset = 0f,
            keyCenterYOffset = 0.22f,
            lockboxHeightScale = 0.95f,
            lockboxCenterYOffset = 0f,
        ),
    )

    private val torchOffsets = mapOf(
        "lower" to StageOffset(0.05f, 0.42f),
        "empty_stage1" to StageOffset(-0.04f, 0.32f),
        "stage2_left" to StageOffset(-0.02f, -0.65f),
        "empty" to StageOffset(0.16f, -0.22f),
        "stage2_right" to StageOffset(0.22f, -0.15f),
        "stage3_left" to StageOffset(-0.04f, -0.08f),
        "stage3_right" to StageOffset(0.15f, 0.34f),
        "stage4_lit_left" to StageOffset(0f, 0.2f),
        "empty_stage4_left" to StageOffset(0.12f, -0.28f),
        "empty_stage4_right" to StageOffset(0.16f, -0.24f),
        "stage4_lit_right" to StageOffset(0.16f, -0.26f),
        "stage5_lit_left" to StageOffset(-0.04f, 0.34f),
        "stage5_lit_center" to StageOffset(0f, -0.14f),
        "empty_stage5_right" to StageOffset(0.12f, 0.34f),
        "stage6_lit_left" to StageOffset(-0.12f, 0.24f),
        "empty_stage6_center" to StageOffset(0.04f, 0.24f),
        "stage6_lit_right" to StageOffset(0.08f, -0.16f),
        "stage7_lit_left" to StageOffset(-0.1f, 0.28f),
        "empty_stage7_center" to StageOffset(0.14f, 0.2f),
        "stage7_lit_right" to StageOffset(0.2f, -0.24f),
        "stage8_lit_outer_left" to StageOffset(0.08f, -0.4f),
        "stage8_lit_outer_right" to StageOffset(1.08f, -0.4f),
        "stage8_lit_inner_left" to StageOffset(0.06f, 0.56f),
        "stage8_lit_inner_right" to StageOffset(0.12f, 0.56f),
    )

    fun objects(stageNo: Int): StageObjectVisuals {
        return objectVisualsByStage[stageNo] ?: defaultObjectVisuals
    }

    fun torchOffset(tag: String?): StageOffset {
        if (tag == null) return DEFAULT_TORCH_OFFSET
        return torchOffsets[tag.lowercase()] ?: DEFAULT_TORCH_OFFSET
    }

    private val DEFAULT_TORCH_OFFSET = StageOffset(0.05f, -0.14f)
}

package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

data class StageData(
    val stageNo: Int,
    val moveCount: Int,
    val originX: Float,
    val originY: Float,
    val tileSize: Float,
    val objects: List<StageObjectSpec>,
)

data class StageObjectSpec(
    val type: StageObjectType,
    val col: Int,
    val row: Int,
    val tag: String? = null,
)

enum class StageObjectType {
    FLOOR,
    PLAYER,
    TARGET,
    ENEMY,
    STONE,
    TORCH,
    SPIKE,
    MOVING_SPIKE,
    KEY,
    LOCKBOX,
}

object StageDataLoader {
    fun load(gctx: GameContext, assetPath: String): StageData {
        var stageNo = 0
        var moveCount = 0
        var originX = 0f
        var originY = 0f
        var tileSize = 0f
        val objects = mutableListOf<StageObjectSpec>()

        val text = gctx.res.readTextAsset(assetPath)
        text.lineSequence().forEachIndexed { index, rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed

            val parts = line.split(',').map { it.trim() }
            when (parts.firstOrNull()) {
                "stage" -> stageNo = parts.requireInt(1, index)
                "moves" -> moveCount = parts.requireInt(1, index)
                "origin" -> {
                    originX = parts.requireFloat(1, index)
                    originY = parts.requireFloat(2, index)
                }
                "tile" -> tileSize = parts.requireFloat(1, index)
                "floor_rect" -> {
                    val left = parts.requireInt(1, index)
                    val top = parts.requireInt(2, index)
                    val right = parts.requireInt(3, index)
                    val bottom = parts.requireInt(4, index)
                    for (row in top..bottom) {
                        for (col in left..right) {
                            objects += StageObjectSpec(StageObjectType.FLOOR, col, row)
                        }
                    }
                }
                "object" -> objects += StageObjectSpec(
                    type = StageObjectType.valueOf(parts.requireText(1, index).uppercase()),
                    col = parts.requireInt(2, index),
                    row = parts.requireInt(3, index),
                    tag = parts.getOrNull(4)?.takeIf { it.isNotBlank() },
                )
                else -> error("Unknown stage data key at ${index + 1}: $line")
            }
        }

        require(stageNo > 0) { "Stage number is missing in $assetPath" }
        require(moveCount > 0) { "Move count is missing in $assetPath" }
        require(tileSize > 0f) { "Tile size is missing in $assetPath" }

        return StageData(stageNo, moveCount, originX, originY, tileSize, objects)
    }

    private fun List<String>.requireText(partIndex: Int, lineIndex: Int): String {
        return getOrNull(partIndex)
            ?: error("Missing column ${partIndex + 1} at line ${lineIndex + 1}")
    }

    private fun List<String>.requireInt(partIndex: Int, lineIndex: Int): Int {
        return requireText(partIndex, lineIndex).toInt()
    }

    private fun List<String>.requireFloat(partIndex: Int, lineIndex: Int): Float {
        return requireText(partIndex, lineIndex).toFloat()
    }
}

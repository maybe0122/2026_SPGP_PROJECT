package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

data class StageCell(
    val col: Int,
    val row: Int,
) {
    fun moved(direction: MoveDirection): StageCell {
        return StageCell(col + direction.dx, row + direction.dy)
    }
}

enum class MoveDirection(
    val dx: Int,
    val dy: Int,
) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
}

import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day18.txt").toFile().readText()
    val lines = input.split("\n")

    val instructions = lines.map { Day18Instruction.fromString(it) }
    val points = mutableListOf(Pair(0L, 0L))
    var currentX = 0L
    var currentY = 0L

    instructions.forEach {
        currentX += it.direction.x * it.steps
        currentY += it.direction.y * it.steps
        points.add(Pair(currentX, currentY))
    }

    println("Part one: ${day18FindArea(points)}")

    val instructions2 = instructions.map { it.toPart2() }
    val points2 = mutableListOf(Pair(0L, 0L))
    var currentX2 = 0L
    var currentY2 = 0L

    instructions2.forEach {
        currentX2 += it.direction.x * it.steps
        currentY2 += it.direction.y * it.steps
        points2.add(Pair(currentX2, currentY2))
    }

    println("Part two: ${day18FindArea(points2)}")
}

fun day18FindArea(points: List<Pair<Long, Long>>): Long = points.windowed(2, 1).sumOf { (first, second) ->
    abs(first.first - second.first) + abs(first.second - second.second) +
            first.first * second.second - first.second * second.first
} / 2 + 1

data class Day18Instruction(val direction: Day16Direction, val steps: Long, val color: String) {
    fun toPart2(): Day18Instruction {
        val newDirection = when (color.last()) {
            '0' -> Day16Direction.RIGHT
            '1' -> Day16Direction.DOWN
            '2' -> Day16Direction.LEFT
            '3' -> Day16Direction.UP
            else -> throw IllegalArgumentException("Unknown color $color")
        }
        val newSteps = color.substring(1, color.lastIndex).toInt(16)
        return Day18Instruction(newDirection, newSteps.toLong(), color)
    }

    companion object {
        fun fromString(line: String): Day18Instruction {
            val parts = line.split(" ")
            val direction = when (parts[0]) {
                "R" -> Day16Direction.RIGHT
                "L" -> Day16Direction.LEFT
                "U" -> Day16Direction.UP
                "D" -> Day16Direction.DOWN
                else -> throw IllegalArgumentException("Unknown direction ${parts[0]}")
            }
            return Day18Instruction(direction, parts[1].toLong(), parts[2].substring(1, 8))
        }
    }
}
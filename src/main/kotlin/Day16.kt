import java.nio.file.Paths
import kotlin.math.max

fun main() {
    val input = Paths.get("src/main/resources/day16.txt").toFile().readText()
    val lines = input.split("\n")

    var maxX = 0
    var maxY = 0
    val mirrors = lines.mapIndexed { y, line ->
        maxY = max(maxY, y)
        line.mapIndexed { x, c ->
            maxX = max(maxX, x)
            if (c == '.') null else Day16Mirror(x, y, c)
        }.filterNotNull()
    }.flatten()


    val count = day16ShootBeam(Day16Beam(-1, 0, Day16Direction.RIGHT), mirrors, maxX, maxY)
    println("Part one: $count")

    var maxCount = 0
    (0..maxX).forEach { x ->
        maxCount = max(maxCount, day16ShootBeam(Day16Beam(x, -1, Day16Direction.DOWN), mirrors, maxX, maxY))
        maxCount = max(maxCount, day16ShootBeam(Day16Beam(x, maxY + 1, Day16Direction.UP), mirrors, maxX, maxY))
    }

    (0..maxY).forEach { y ->
        maxCount = max(maxCount, day16ShootBeam(Day16Beam(-1, y, Day16Direction.RIGHT), mirrors, maxX, maxY))
        maxCount = max(maxCount, day16ShootBeam(Day16Beam(maxX + 1, y, Day16Direction.LEFT), mirrors, maxX, maxY))
    }

    println("Part two: $maxCount")
}

fun day16ShootBeam(startBeam: Day16Beam, mirrors: List<Day16Mirror>, maxX: Int, maxY: Int): Int {
    val beams = mutableListOf(startBeam)
    val visited = mutableListOf<Pair<Int, Int>>()
    val visitedMirrors = mutableListOf<Pair<Day16Mirror, Day16Direction>>()

    while (beams.any { it.active }) {
        val currentBeam = beams.first { it.active }
        currentBeam.move()

        if (currentBeam.x < 0 || currentBeam.y < 0 || currentBeam.x > maxX || currentBeam.y > maxY) {
            currentBeam.active = false
            continue
        }

        val mirror = mirrors.find { it.x == currentBeam.x && it.y == currentBeam.y }

        if (Pair(currentBeam.x, currentBeam.y) !in visited) {
            visited.add(Pair(currentBeam.x, currentBeam.y))
        }

        if (mirror != null) {
            if (Pair(mirror, currentBeam.direction) !in visitedMirrors) {
                visitedMirrors.add(Pair(mirror, currentBeam.direction))
            } else {
                currentBeam.active = false
                continue
            }
        }

        if (mirror != null) {
            when (mirror.type) {
                Day16MirrorType.VERTICAL -> when (currentBeam.direction) {
                    Day16Direction.RIGHT, Day16Direction.LEFT -> {
                        currentBeam.direction = Day16Direction.UP
                        beams.add(Day16Beam(currentBeam.x, currentBeam.y, Day16Direction.DOWN))
                    }

                    else -> Unit
                }

                Day16MirrorType.HORIZONTAL -> when (currentBeam.direction) {
                    Day16Direction.UP, Day16Direction.DOWN -> {
                        currentBeam.direction = Day16Direction.RIGHT
                        beams.add(Day16Beam(currentBeam.x, currentBeam.y, Day16Direction.LEFT))
                    }

                    else -> Unit
                }

                else -> currentBeam.direction = mirror.reflect(currentBeam.direction)
            }
        }
    }

    return visited.size
}

enum class Day16Direction(val x: Int, val y: Int) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0)
}

enum class Day16MirrorType {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL_UP_RIGHT,
    DIAGONAL_UP_LEFT
}

data class Day16Beam(var x: Int, var y: Int, var direction: Day16Direction, var active: Boolean = true) {
    fun move() {
        x += direction.x
        y += direction.y
    }
}

data class Day16Mirror(val x: Int, val y: Int, val type: Day16MirrorType) {
    constructor(x: Int, y: Int, c: Char) : this(
        x, y, when (c) {
            '|' -> Day16MirrorType.VERTICAL
            '-' -> Day16MirrorType.HORIZONTAL
            '/' -> Day16MirrorType.DIAGONAL_UP_LEFT
            '\\' -> Day16MirrorType.DIAGONAL_UP_RIGHT
            else -> throw IllegalArgumentException("Invalid mirror type")
        }
    )

    fun reflect(incomingDirection: Day16Direction): Day16Direction = when (type) {
        Day16MirrorType.DIAGONAL_UP_RIGHT -> when (incomingDirection) {
            Day16Direction.UP -> Day16Direction.LEFT
            Day16Direction.RIGHT -> Day16Direction.DOWN
            Day16Direction.DOWN -> Day16Direction.RIGHT
            Day16Direction.LEFT -> Day16Direction.UP
        }

        Day16MirrorType.DIAGONAL_UP_LEFT -> when (incomingDirection) {
            Day16Direction.UP -> Day16Direction.RIGHT
            Day16Direction.RIGHT -> Day16Direction.UP
            Day16Direction.DOWN -> Day16Direction.LEFT
            Day16Direction.LEFT -> Day16Direction.DOWN
        }

        else -> incomingDirection
    }
}
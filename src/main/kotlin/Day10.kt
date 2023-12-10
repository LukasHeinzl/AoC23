import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day10.txt").toFile().readText()
    val lines = input.split("\n")

    val grid = lines.mapIndexed { y, line -> line.mapIndexed { x, symbol -> Day10Pipe(x, y, symbol) }.toTypedArray() }
        .toTypedArray()
    val start = grid.flatten().first { it.type == Day10PipeType.START }

    val (loop, result) = day10FindPath(start, grid)
    val maxCorner = result.entries.minBy { abs(it.value.second - it.value.first) }
    val maxDistance = if (maxCorner.value.first == maxCorner.value.second) maxCorner.value.first else {
        if (maxCorner.value.first < maxCorner.value.second) {
            (maxCorner.value.second - maxCorner.value.first) / 2 + maxCorner.value.first
        } else {
            (maxCorner.value.first - maxCorner.value.second) / 2 + maxCorner.value.second
        }
    }

    println("Part one: $maxDistance")

    val area = day10FindArea(loop, grid[0].size, grid.size)
    println("Part two: $area")
}

fun day10FindArea(loop: List<Day10Pipe>, maxX: Int, maxY: Int): Int {
    val queue = mutableListOf(Pair(-1, -1))
    val visited = loop.map { it.scaled() }.flatten().toMutableList()
    var area = 0

    while (queue.size > 0) {
        val current = queue.removeAt(0)

        if (current in visited) {
            continue
        }

        visited.add(current)

        if (current.first % 3 == 1 && current.second % 3 == 1) {
            area++
        }

        listOf(
            Pair(current.first, current.second - 1),
            Pair(current.first, current.second + 1),
            Pair(current.first - 1, current.second),
            Pair(current.first + 1, current.second)
        ).filter { it.first >= -1 && it.second >= -1 && it.first < maxX * 3 && it.second < maxY * 3 && it !in queue && it !in visited }
            .forEach { queue.add(it) }
    }

    return (maxX * maxY) - area - loop.size
}

fun day10FindPath(
    start: Day10Pipe,
    grid: Array<Array<Day10Pipe>>
): Pair<List<Day10Pipe>, Map<Day10Pipe, Pair<Int, Int>>> {
    val result = mutableMapOf(start to Pair(0, 0))
    val loop = mutableListOf(start)
    var current = start.findClosestCorner(grid)
    var lastDirection = start.getDirection(current)

    (start.x..<current.x).forEach { loop.add(Day10Pipe(it, start.y, Day10PipeType.WEST_EAST)) }
    (current.x + 1..<start.x).forEach { loop.add(Day10Pipe(it, start.y, Day10PipeType.WEST_EAST)) }
    (start.y..<current.y).forEach { loop.add(Day10Pipe(start.x, it, Day10PipeType.NORTH_SOUTH)) }
    (current.y + 1..<start.y).forEach { loop.add(Day10Pipe(start.x, it, Day10PipeType.NORTH_SOUTH)) }

    while (true) {
        val last = result.entries.last()
        result[current] = Pair(last.value.first + last.key.distanceTo(current), 0)

        var x = current.x
        var y = current.y
        val nextDirection = current.type.getDirection(lastDirection)

        do {
            loop.add(grid[y][x])

            when (nextDirection) {
                Day10Direction.NORTH -> y--
                Day10Direction.SOUTH -> y++
                Day10Direction.WEST -> x--
                Day10Direction.EAST -> x++
                else -> throw IllegalStateException()
            }
        } while (!grid[y][x].type.isCorner && grid[y][x].type != Day10PipeType.START)

        val newCurrent = grid[y][x]

        if (newCurrent.type == Day10PipeType.START) {
            break
        }

        lastDirection = current.getDirection(newCurrent)
        current = newCurrent
    }

    var totalDistance = 0

    for (i in result.entries.size - 1 downTo 0) {
        val entry = result.entries.elementAt(i)
        val prev = result.entries.elementAtOrNull(i + 1) ?: result.entries.first()
        totalDistance += prev.key.distanceTo(entry.key)
        result[entry.key] = Pair(entry.value.first, totalDistance)
    }

    val firstStartDirection = result.entries.elementAt(1).key.getDirection(start)
    val lastStartDirection = result.entries.last().key.getDirection(start)

    start.type = Day10PipeType.fromDirections(firstStartDirection, lastStartDirection)

    return Pair(loop.distinctBy { Pair(it.x, it.y) }, result)
}

enum class Day10Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NONE
}

enum class Day10PipeType(private val symbol: Char) {
    NORTH_SOUTH('|'),
    WEST_EAST('-'),
    NORTH_EAST('L'),
    NORTH_WEST('J'),
    SOUTH_WEST('7'),
    SOUTH_EAST('F'),
    START('S'),
    GROUND('.');

    val isCorner get() = this != NORTH_SOUTH && this != WEST_EAST && this != START
    val isNorth get() = this == NORTH_EAST || this == NORTH_WEST || this == START
    val isSouth get() = this == SOUTH_EAST || this == SOUTH_WEST || this == START
    val isWest get() = this == NORTH_WEST || this == SOUTH_WEST || this == START
    val isEast get() = this == NORTH_EAST || this == SOUTH_EAST || this == START

    fun getDirection(firstPart: Day10Direction): Day10Direction = when (this) {
        NORTH_EAST -> if (firstPart == Day10Direction.NORTH) Day10Direction.EAST else Day10Direction.NORTH
        NORTH_WEST -> if (firstPart == Day10Direction.NORTH) Day10Direction.WEST else Day10Direction.NORTH
        SOUTH_EAST -> if (firstPart == Day10Direction.SOUTH) Day10Direction.EAST else Day10Direction.SOUTH
        SOUTH_WEST -> if (firstPart == Day10Direction.SOUTH) Day10Direction.WEST else Day10Direction.SOUTH
        else -> Day10Direction.NONE
    }

    companion object {
        fun fromChar(c: Char): Day10PipeType = entries.first { it.symbol == c }

        fun fromDirections(firstPart: Day10Direction, secondPart: Day10Direction): Day10PipeType = when (firstPart) {
            Day10Direction.NORTH -> when (secondPart) {
                Day10Direction.NORTH -> NORTH_SOUTH
                Day10Direction.SOUTH -> NORTH_SOUTH
                Day10Direction.WEST -> NORTH_WEST
                Day10Direction.EAST -> NORTH_EAST
                else -> throw IllegalStateException()
            }

            Day10Direction.SOUTH -> when (secondPart) {
                Day10Direction.NORTH -> NORTH_SOUTH
                Day10Direction.SOUTH -> NORTH_SOUTH
                Day10Direction.WEST -> SOUTH_WEST
                Day10Direction.EAST -> SOUTH_EAST
                else -> throw IllegalStateException()
            }

            Day10Direction.WEST -> when (secondPart) {
                Day10Direction.NORTH -> NORTH_WEST
                Day10Direction.SOUTH -> SOUTH_WEST
                Day10Direction.WEST -> WEST_EAST
                Day10Direction.EAST -> WEST_EAST
                else -> throw IllegalStateException()
            }

            Day10Direction.EAST -> when (secondPart) {
                Day10Direction.NORTH -> NORTH_EAST
                Day10Direction.SOUTH -> SOUTH_EAST
                Day10Direction.WEST -> WEST_EAST
                Day10Direction.EAST -> WEST_EAST
                else -> throw IllegalStateException()
            }

            else -> throw IllegalStateException()
        }
    }
}

data class Day10Pipe(val x: Int, val y: Int, var type: Day10PipeType) {
    constructor(x: Int, y: Int, symbol: Char) : this(x, y, Day10PipeType.fromChar(symbol))

    fun distanceTo(other: Day10Pipe): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    fun getDirection(other: Day10Pipe): Day10Direction = if (x == other.x) {
        if (y < other.y) Day10Direction.NORTH else Day10Direction.SOUTH
    } else if (y == other.y) {
        if (x < other.x) Day10Direction.WEST else Day10Direction.EAST
    } else {
        Day10Direction.NONE
    }

    fun findClosestCorner(grid: Array<Array<Day10Pipe>>): Day10Pipe = grid.flatten().filter {
        it.type.isCorner &&
                if (x == it.x) {
                    if (y < it.y) {
                        type.isSouth && it.type.isNorth
                    } else {
                        type.isNorth && it.type.isSouth
                    }
                } else if (y == it.y) {
                    if (x < it.x) {
                        type.isEast && it.type.isWest
                    } else {
                        type.isWest && it.type.isEast
                    }
                } else {
                    false
                }
    }.minBy { it.distanceTo(this) }

    fun scaled(): List<Pair<Int, Int>> {
        val newX = x * 3 + 1
        val newY = y * 3 + 1
        val list = mutableListOf(Pair(newX, newY))

        when (type) {
            Day10PipeType.NORTH_SOUTH -> {
                list.add(Pair(newX, newY - 1))
                list.add(Pair(newX, newY + 1))
            }

            Day10PipeType.WEST_EAST -> {
                list.add(Pair(newX - 1, newY))
                list.add(Pair(newX + 1, newY))
            }

            Day10PipeType.NORTH_EAST -> {
                list.add(Pair(newX, newY - 1))
                list.add(Pair(newX + 1, newY))
            }

            Day10PipeType.NORTH_WEST -> {
                list.add(Pair(newX, newY - 1))
                list.add(Pair(newX - 1, newY))
            }

            Day10PipeType.SOUTH_WEST -> {
                list.add(Pair(newX, newY + 1))
                list.add(Pair(newX - 1, newY))
            }

            Day10PipeType.SOUTH_EAST -> {
                list.add(Pair(newX, newY + 1))
                list.add(Pair(newX + 1, newY))
            }

            else -> throw IllegalStateException()
        }

        return list
    }
}
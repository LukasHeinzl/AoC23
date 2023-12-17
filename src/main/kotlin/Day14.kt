import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day14.txt").toFile().readText()

    val map = Day14Map(input)
    println("Part one: ${map.move().sum}")

    val cycles = mutableMapOf<String, Int>()
    val total = 1000000000
    var currentMap = map

    for (currentCycle in 0..total) {
        if (currentMap.toString() in cycles) {
            val length = currentCycle - cycles[currentMap.toString()]!!
            val remaining = (total - currentCycle) % length
            repeat(remaining) { currentMap = currentMap.cycle() }
            break
        }

        cycles[currentMap.toString()] = currentCycle
        currentMap = currentMap.cycle()
    }

    println("Part two: ${currentMap.sum}")
}

enum class Day14RockType {
    ROCK, STOP
}

data class Day14Rock(val x: Int, val y: Int, val stopPosition: Int, val rocksAbove: Int, val type: Day14RockType)

data class Day14Map(val input: String) {
    private val maxX = input.split("\n").first().lastIndex
    private val maxY = input.split("\n").lastIndex
    private val rocks = mutableListOf<Day14Rock>()
    private val stopPositions = mutableMapOf<Int, Int>()
    val sum: Int
        get() = rocks.filter { it.type == Day14RockType.ROCK }.sumOf { maxY + 1 - it.y }

    init {
        input.split("\n").forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == 'O') {
                    val stopPosition = stopPositions.getOrDefault(x, 0)
                    val rocksAbove = rocks.filter { it.x == x && it.y < y && it.y >= stopPosition }.size
                    rocks.add(Day14Rock(x, y, stopPosition, rocksAbove, Day14RockType.ROCK))
                } else if (c == '#') {
                    stopPositions[x] = y + 1
                    rocks.add(Day14Rock(x, y, 0, 0, Day14RockType.STOP))
                }
            }
        }
    }

    fun move(): Day14Map = Day14Map(toString(rocks.map {
        Day14Rock(
            it.x,
            if (it.type == Day14RockType.ROCK) it.stopPosition + it.rocksAbove else it.y,
            0,
            0,
            it.type
        )
    }))

    private fun rotate(): Day14Map {
        // rotate 90 degrees clockwise
        val newRocks = rocks.map { Day14Rock(maxY - it.y, it.x, 0, 0, it.type) }
        return Day14Map(toString(newRocks))
    }

    fun cycle(): Day14Map = move().rotate().move().rotate().move().rotate().move().rotate()

    override fun toString(): String = toString(rocks)

    private fun toString(rockData: List<Day14Rock>): String {
        var string = ""

        for (y in 0..maxY) {
            for (x in 0..maxX) {
                val rock = rockData.firstOrNull { it.x == x && it.y == y }
                string += if (rock != null) {
                    if (rock.type == Day14RockType.STOP) {
                        "#"
                    } else {
                        "O"
                    }
                } else {
                    "."
                }
            }

            string += "\n"
        }

        return string.dropLast(1) // remove trailing newline
    }
}
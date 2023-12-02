import java.nio.file.Paths
import kotlin.math.max

fun main() {
    val input = Paths.get("src/main/resources/day2.txt").toFile().readText()
    val lines = input.split("\n")

    val maxCubes = mapOf("red" to 12, "green" to 13, "blue" to 14)
    val games = lines.map { Day2Game.fromString(it) }
    val sum = games.filter { it.isPossible(maxCubes) }.sumOf { it.id }
    println("Part one: $sum")

    val sum2 = games.sumOf { it.minPower() }
    println("Part two: $sum2")
}

data class Day2Game(val id: Int, val draws: List<Day2GameDraw>) {
    fun isPossible(maxCubes: Map<String, Int>): Boolean = draws.all { draw ->
        draw.cubes.all { (color, count) ->
            count <= (maxCubes[color] ?: 0)
        }
    }

    fun minPower(): Int {
        val cubes = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)

        draws.forEach { draw ->
            draw.cubes.forEach { (color, count) ->
                cubes[color] = max(cubes[color]!!, count)
            }
        }

        return cubes.values.reduce { acc, i -> acc * i }
    }

    companion object {
        fun fromString(input: String): Day2Game {
            val split = input.split(": ")
            val id = split[0].replace("Game ", "").toInt()
            val draws = split[1].split(";").map { Day2GameDraw.fromString(it) }
            return Day2Game(id, draws)
        }
    }
}

data class Day2GameDraw(val cubes: Map<String, Int>) {
    companion object {
        fun fromString(input: String): Day2GameDraw {
            val cubes = input.split(",")
                .map { it.trim().split(" ") }
                .associate { it[1] to it[0].toInt() }
            return Day2GameDraw(cubes)
        }
    }
}
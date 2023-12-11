import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day11.txt").toFile().readText()
    val lines = input.split("\n")

    val emptyRows = lines.indices.filter { y ->
        lines[y].all { it == '.' }
    }

    val emptyCols = lines[0].indices.filter { x ->
        lines.all { it[x] == '.' }
    }

    val galaxies = lines.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            if (c == '#') {
                val galaxyX = x + emptyCols.count { it < x }
                val galaxyY = y + emptyRows.count { it < y }
                Pair(galaxyX, galaxyY)
            } else {
                null
            }
        }.filterNotNull()
    }.flatten()

    val sum = galaxies.mapIndexed { idx, galaxy ->
        galaxies.subList(idx, galaxies.size).sumOf { g ->
            abs(galaxy.first - g.first) + abs(galaxy.second - g.second)
        }
    }.sum()

    println("Part one: $sum")

    val galaxies2 = lines.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            if (c == '#') {
                val scale = 1_000_000L
                val galaxyX = x + emptyCols.count { it < x } * (scale - 1)
                val galaxyY = y + emptyRows.count { it < y } * (scale - 1)
                Pair(galaxyX, galaxyY)
            } else {
                null
            }
        }.filterNotNull()
    }.flatten()

    val sum2 = galaxies2.mapIndexed { idx, galaxy ->
        galaxies2.subList(idx, galaxies2.size).sumOf { g ->
            abs(galaxy.first - g.first) + abs(galaxy.second - g.second)
        }
    }.sum()

    println("Part two: $sum2")
}
import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day5.txt").toFile().readText()
    val parts = input.split("\n\n")

    var maps = parts.map { Day5Map.fromString(it) }
    val seeds = maps[0].maps
    maps = maps.drop(1)
    var minPosition = Long.MAX_VALUE
    seeds.forEach {
        val position = day5Convert(maps, it.first)

        if (position < minPosition) {
            minPosition = position
        }
    }

    println("Part one: $minPosition")

    minPosition = Long.MAX_VALUE
    seeds.chunked(2).forEach {
        for (i in it[0].first..<it[0].first + it[1].first) {
            val position = day5Convert(maps, i)

            if (position < minPosition) {
                minPosition = position
            }
        }
    }

    println("Part two: $minPosition")
}

fun day5Convert(maps: List<Day5Map>, input: Long): Long {
    var current = input

    maps.forEach { map ->
        val range = map.maps.find { range -> current >= range.second && current <= range.second + range.third }

        if (range != null) {
            current = range.first + current - range.second
        }
    }

    return current
}

data class Day5Map(val maps: List<Triple<Long, Long, Long>>) {
    companion object {
        fun fromString(input: String): Day5Map {
            val lines = input.split("\n")

            if (lines.size == 1) {
                return Day5Map(lines[0].split(" ").drop(1).map { Triple(it.toLong(), 0, 0) })
            }

            return Day5Map(lines.drop(1).map { line ->
                val parts = line.split(" ")
                Triple(parts[0].toLong(), parts[1].toLong(), parts[2].toLong())
            })
        }
    }
}
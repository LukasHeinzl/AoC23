import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day12.txt").toFile().readText()
    val lines = input.split("\n")

    val problems = lines.map { Day12Problem.fromString(it) }
    val sum = problems.sumOf { day12Solve(it) }
    println("Part one: $sum")

    val sum2 = problems.sumOf { day12Solve(it.toPart2()) }
    println("Part two: $sum2")
}

// Credit, because I gave up:
// https://github.com/ClouddJR/advent-of-code-2023/blob/main/src/main/kotlin/com/clouddjr/advent2023/Day12.kt
fun day12Solve(
    problem: Day12Problem,
    currentPosition: Int = 0,
    currentGroup: Int = 0,
    currentGroupLength: Int = 0,
    cache: MutableMap<Triple<Int, Int, Int>, Long> = mutableMapOf()
): Long {
    if (Triple(currentPosition, currentGroup, currentGroupLength) in cache) {
        return cache[Triple(currentPosition, currentGroup, currentGroupLength)]!!
    }

    if (currentPosition == problem.pattern.length) {
        return if (currentGroup == problem.groups.size && currentGroupLength == 0) 1 else 0
    }

    var sum = 0L

    if (problem.pattern[currentPosition] in ".?") {
        if (currentGroup < problem.groups.size && currentGroupLength == problem.groups[currentGroup]) {
            sum += day12Solve(problem, currentPosition + 1, currentGroup + 1, 0, cache)
        }

        if (currentGroupLength == 0) {
            sum += day12Solve(problem, currentPosition + 1, currentGroup, currentGroupLength, cache)
        }
    }

    if (problem.pattern[currentPosition] in "#?") {
        sum += day12Solve(problem, currentPosition + 1, currentGroup, currentGroupLength + 1, cache)
    }

    cache[Triple(currentPosition, currentGroup, currentGroupLength)] = sum
    return sum
}

data class Day12Problem(val pattern: String, val groups: List<Int>) {
    fun toPart2(): Day12Problem {
        val newPattern = "${pattern.dropLast(1)}?".repeat(5).dropLast(1) + "."
        val newGroups = List(5) { groups }.flatten()
        return Day12Problem(newPattern, newGroups)
    }

    companion object {
        fun fromString(input: String): Day12Problem {
            val split = input.split(" ")
            val pattern = split[0] + "."
            val groups = split[1].split(",").map { it.toInt() }
            return Day12Problem(pattern, groups)
        }
    }
}
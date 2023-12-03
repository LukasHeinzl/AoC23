import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day3.txt").toFile().readText()
    val lines = input.split("\n")

    val symbols = mutableListOf<Triple<Int, Int, String>>()
    lines.forEachIndexed { y, line ->
        var acc = ""

        line.forEachIndexed { x, it ->
            if (it != '.') {
                if (acc.isNotEmpty() && ((acc[0].isDigit() && !it.isDigit()) || (!acc[0].isDigit() && it.isDigit()))) {
                    symbols.add(Triple(x - acc.length, y, acc))
                    acc = ""
                }

                acc += it
            } else if (acc.isNotEmpty()) {
                symbols.add(Triple(x - acc.length, y, acc))
                acc = ""
            }
        }

        if (acc.isNotEmpty()) {
            symbols.add(Triple(line.length - acc.length, y, acc))
        }
    }

    val partNumbers = symbols.filter {
        val (x, y, symbol) = it

        symbol[0].isDigit() && symbols.any { s ->
            !s.third[0].isDigit() &&
                    abs(y - s.second) <= 1 && (
                    (x >= s.first - 1 && x <= s.first + s.third.length) ||
                            (x + symbol.length - 1 >= s.first - 1 && x + symbol.length - 1 <= s.first + s.third.length)
                    )
        }
    }

    val sum = partNumbers.sumOf { it.third.toInt() }
    println("Part one: $sum")

    val gears = symbols.map {
        val (x, y, symbol) = it
        val numbers = symbols.filter { s ->
            s.third[0].isDigit() &&
                    abs(y - s.second) <= 1 && (
                    (x >= s.first - 1 && x <= s.first + s.third.length) ||
                            (x + symbol.length - 1 >= s.first - 1 && x + symbol.length - 1 <= s.first + s.third.length)
                    )
        }

        if (symbol == "*" && numbers.size == 2) numbers.map { n -> n.third.toInt() }.reduce { acc, n -> acc * n } else 0
    }

    val sum2 = gears.sum()
    println("Part two: $sum2")
}
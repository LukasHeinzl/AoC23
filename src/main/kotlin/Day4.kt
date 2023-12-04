import java.nio.file.Paths
import kotlin.math.pow

fun main() {
    val input = Paths.get("src/main/resources/day4.txt").toFile().readText()
    val lines = input.split("\n")

    val cards = lines.map { Day4Card.fromString(it) }
    val sum = cards.sumOf { it.points }
    println("Part one: $sum")

    val cardCounts = mutableMapOf<Int, Int>()
    cards.forEachIndexed { idx, _ -> cardCounts[idx] = 1 }
    cards.forEachIndexed { idx, card ->
        val count = card.matches

        for (i in idx + 1..(idx + count).coerceAtMost(cards.lastIndex)) {
            cardCounts[i] = cardCounts[i]!! + cardCounts[idx]!!
        }
    }

    val sum2 = cardCounts.values.sum()
    println("Part two: $sum2")
}

data class Day4Card(val winningNumbers: List<Int>, val numbers: List<Int>) {
    val matches = numbers.count { it in winningNumbers }
    val points = 2.toDouble().pow((matches - 1).toDouble()).toInt()

    companion object {
        fun fromString(input: String): Day4Card {
            val allNumbers = input.substringAfter(":")
            val winningNumbers = allNumbers.substringBefore("|")
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt() }
            val numbers = allNumbers.substringAfter("|")
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt() }

            return Day4Card(winningNumbers, numbers)
        }
    }
}
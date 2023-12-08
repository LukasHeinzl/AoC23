import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day7.txt").toFile().readText()
    val lines = input.split("\n")

    val cards = lines.map { Day7Hand.fromString(it) }.sorted()
    val sum = cards.mapIndexed { idx, card ->
        (idx + 1) * card.bid
    }.sum()
    println("Part one: $sum")

    val cards2 = cards.sortedWith { o1, o2 -> o1.compare(o2, true) }
    val sum2 = cards2.mapIndexed { idx, card ->
        (idx + 1) * card.bid
    }.sum()
    println("Part two: $sum2")
}

enum class Day7HandType(val power: Int) {
    FIVE_OF_A_KIND(6),
    FOUR_OF_A_KIND(5),
    FULL_HOUSE(4),
    THREE_OF_A_KIND(3),
    TWO_PAIR(2),
    ONE_PAIR(1),
    HIGH_CARD(0);

    companion object {
        fun handToType(cardCounts: Map<Char, Int>): Day7HandType = when (cardCounts.size) {
            1 -> FIVE_OF_A_KIND

            2 -> {
                if (cardCounts.values.contains(4)) {
                    FOUR_OF_A_KIND
                } else {
                    FULL_HOUSE
                }
            }

            3 -> {
                if (cardCounts.values.contains(3)) {
                    THREE_OF_A_KIND
                } else {
                    TWO_PAIR
                }
            }

            4 -> ONE_PAIR
            else -> HIGH_CARD
        }

    }
}

data class Day7Hand(val cards: List<Char>, val bid: Int) : Comparable<Day7Hand> {
    private val cardCounts = cards.groupingBy { it }.eachCount()

    private fun getHandType(partTwo: Boolean): Day7HandType {
        if (!partTwo) {
            return Day7HandType.handToType(cardCounts)
        }

        val jokerCount = cards.find { it == 'J' }?.let { cardCounts[it] } ?: 0

        if (jokerCount == 0) {
            return Day7HandType.handToType(cardCounts)
        }

        if (jokerCount == 5) {
            return Day7HandType.FIVE_OF_A_KIND
        }

        val maxOccurrence = cardCounts.filter { it.key != 'J' }.values.max()
        val maxLetter = cardCounts.filter { it.key != 'J' && it.value == maxOccurrence }
            .entries.maxBy { mapCardToValue(it.key, true) }
            .key

        val newCardCounts = cardCounts.toMutableMap()
        newCardCounts[maxLetter] = newCardCounts[maxLetter]!! + jokerCount
        newCardCounts.remove('J')

        return Day7HandType.handToType(newCardCounts)
    }

    companion object {
        fun fromString(input: String): Day7Hand {
            val split = input.split(" ")
            val cards = split[0].map { it }

            return Day7Hand(cards, split[1].toInt())
        }
    }

    override fun compareTo(other: Day7Hand): Int = compare(other)

    fun compare(other: Day7Hand, partTwo: Boolean = false): Int {
        if (this.getHandType(partTwo).power != other.getHandType(partTwo).power) {
            return this.getHandType(partTwo).power.compareTo(other.getHandType(partTwo).power)
        }

        for (i in 0..cards.lastIndex) {
            if (this.cards[i] != other.cards[i]) {
                return mapCardToValue(this.cards[i], partTwo).compareTo(mapCardToValue(other.cards[i], partTwo))
            }
        }

        return 0
    }

    private fun mapCardToValue(card: Char, partTwo: Boolean = false): Int = when (card) {
        'A' -> 14
        'K' -> 13
        'Q' -> 12
        'J' -> if (partTwo) 1 else 11
        'T' -> if (partTwo) 11 else 10
        else -> card.toString().toInt() + if (partTwo) 1 else 0

    }
}
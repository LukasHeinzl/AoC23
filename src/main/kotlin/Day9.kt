import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day9.txt").toFile().readText()
    val lines = input.split("\n")

    val sequences = lines.map { line -> line.split(" ").map { it.toLong() } }
    val sum = sequences.sumOf {
        val allSubSequences = mutableListOf(it)
        var nextSequence = it.windowed(2, 1).map { window -> window[1] - window[0] }

        while (nextSequence.any { n -> n != 0L }) {
            allSubSequences.add(nextSequence)
            nextSequence = nextSequence.windowed(2, 1).map { window -> window[1] - window[0] }
        }

        allSubSequences.sumOf { subSequence -> subSequence.last() }
    }

    println("Part one: $sum")

    val sum2 = sequences.sumOf {
        val allSubSequences = mutableListOf(it)
        var nextSequence = it.windowed(2, 1).map { window -> window[0] - window[1] }

        while (nextSequence.any { n -> n != 0L }) {
            allSubSequences.add(nextSequence)
            nextSequence = nextSequence.windowed(2, 1).map { window -> window[0] - window[1] }
        }

        allSubSequences.sumOf { subSequence -> subSequence.first() }
    }

    println("Part two: $sum2")
}
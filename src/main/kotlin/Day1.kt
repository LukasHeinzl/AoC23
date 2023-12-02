import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day1.txt").toFile().readText()
    val lines = input.split("\n")

    val sum = lines.sumOf {
        val digits = it.toCharArray().filter { c -> c.isDigit() }
        val first = digits.first() - '0'
        val last = digits.last() - '0'
        first * 10 + last
    }

    println("Part one: $sum")

    val numbers = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )
    val sum2 = lines.sumOf {
        val digitPositions = mutableMapOf<Int, Int>()

        for((word, number) in numbers) {
            val firstWordIndex = it.indexOf(word)
            val firstNumberIndex = it.indexOf(number.toString())
            val lastWordIndex = it.lastIndexOf(word)
            val lastNumberIndex = it.lastIndexOf(number.toString())
            val indices = listOf(firstWordIndex, firstNumberIndex, lastWordIndex, lastNumberIndex)

            for(index in indices) {
                if(index != -1) {
                    digitPositions[index] = number
                }
            }
        }

        val first = digitPositions.minBy { entry -> entry.key }.value
        val last = digitPositions.maxBy { entry -> entry.key }.value
        first * 10 + last
    }

    println("Part two: $sum2")
}
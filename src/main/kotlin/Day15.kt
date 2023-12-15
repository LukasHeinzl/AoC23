import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day15.txt").toFile().readText()
    val steps = input.split(",")

    val sum = steps.sumOf { it.day15Hash() }
    println("Part one: $sum")

    val boxes = mutableListOf<MutableList<Pair<String, Int>>>()
    (0..256).forEach { _ -> boxes.add(mutableListOf()) }

    steps.forEach { step ->
        val operationIdx = step.indexOfFirst { it == '=' || it == '-' }
        val label = step.substring(0..<operationIdx)
        val boxIdx = label.day15Hash()
        val operation = step[operationIdx]

        if (operation == '-') {
            boxes[boxIdx].removeAll { it.first == label }
        } else {
            val existingLensIdx = boxes[boxIdx].indexOfFirst { it.first == label }
            val focalLength = step[operationIdx + 1].digitToInt()

            if (existingLensIdx != -1) {
                boxes[boxIdx][existingLensIdx] = Pair(label, focalLength)
            } else {
                boxes[boxIdx].add(Pair(label, focalLength))
            }
        }
    }

    val sum2 = boxes.mapIndexed { boxIdx, box ->
        box.mapIndexed { stepIdx, step ->
            (boxIdx + 1) * (stepIdx + 1) * step.second
        }.sum()
    }.sum()

    println("Part two: $sum2")
}

fun String.day15Hash(): Int {
    var currentValue = 0

    for (c in this) {
        currentValue += c.code
        currentValue *= 17
        currentValue %= 256
    }

    return currentValue
}
import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day6.txt").toFile().readText()
    val lines = input.split("\n")

    val times = lines[0].substringAfter(":").split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    val distances = lines[1].substringAfter(":").split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    val races = times.zip(distances)

    val count = races.map { (time, distance) ->
        var newRecordCount = 0

        for (i in 0..time) {
            val distanceCovered = i * (time - i)

            if (distanceCovered > distance) {
                newRecordCount++
            }
        }

        newRecordCount
    }.reduce { acc, i -> acc * i }

    println("Part one: $count")

    val time = lines[0].substringAfter(":").replace(" ", "").toLong()
    val distance = lines[1].substringAfter(":").replace(" ", "").toLong()
    var count2 = 0

    for (i in 0..time) {
        val distanceCovered = i * (time - i)

        if (distanceCovered > distance) {
            count2++
        }
    }

    println("Part two: $count2")
}
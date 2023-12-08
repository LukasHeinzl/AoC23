import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day8.txt").toFile().readText()
    val lines = input.split("\n")

    val instructions = lines[0]
    val nodes = lines.drop(2).map { Day8Node.fromString(it) }.associateBy { it.name }

    var currentInstruction = 0
    var currentNode = nodes["AAA"]!!
    var steps = 0

    while (currentNode.name != "ZZZ") {
        val instruction = instructions[currentInstruction]
        currentNode = if (instruction == 'L') nodes[currentNode.left]!! else nodes[currentNode.right]!!
        currentInstruction = if (currentInstruction == instructions.lastIndex) 0 else currentInstruction + 1
        steps++
    }

    println("Part one: $steps")

    val startNodes = nodes.filter { it.value.name.endsWith('A') }.values
    val steps2 = startNodes.map {
        var current = it
        var step = 0L

        while (!current.name.endsWith('Z')) {
            val instruction = instructions[(step++ % instructions.length).toInt()]
            current = if (instruction == 'L') nodes[current.left]!! else nodes[current.right]!!
        }

        step
    }.reduce(::lcm)

    println("Part two: $steps2")
}

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a * b / gcd(a, b)

data class Day8Node(val name: String, val left: String, val right: String) {
    companion object {
        fun fromString(input: String): Day8Node {
            val name = input.substringBefore(" = ")
            val parts = input.substringAfter("(").substringBefore(")").split(", ")
            return Day8Node(name, parts[0], parts[1])
        }
    }
}
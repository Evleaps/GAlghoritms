package lab4

import kotlin.math.pow

private const val LENGTH_OF_ENCODING = 8
private const val STEP_QUANTITY = 10
private const val MAX_HAMMING_DISTANCE = 3L

private val ENVIRONMENT_DISTANCE = 10.0.pow(2.0).toInt()
private val INDIVIDUALS_QUANTITY = 32.0.pow(2.0).toInt()

@ExperimentalStdlibApi
fun main() {
    val encodingList = generateEncodingList()
    encodingList.take(32).forEachIndexed { index, entity ->
        println("${index.inc()}) кодировка: ${entity.encoding}, приспособленность: ${entity.adaptation}")
    }
    println("\n")


    val bestSolutions = mutableListOf<Entity>()
    var maxAdaptation = -1L
    var maxS = ""
    var currentPos = (Math.random() * (INDIVIDUALS_QUANTITY - 1)).toInt()
    for (step in 0 until STEP_QUANTITY) {
        println("\nШаг $step")

        val environmentEntityList = generateEnvironmentEntityList(encodingList, currentPos)

        println("Получена окрестность на основе Хэменовова расстояния")
        environmentEntityList.take(32).forEachIndexed { index, entity ->
            println("${index.inc()}) кодировка: ${entity.encoding}, приспособленность: ${entity.adaptation}")
        }
        println("\n")

        var environmentMaxAdaptation = -1L
        var environmentMaxS = ""
        var environmentBestPos = -1
        environmentEntityList.forEach {
            println("Выбрана кодировка из окрестности: ${it.encoding}, приспособленность: ${it.adaptation}")
            if (it.adaptation > environmentMaxAdaptation) {
                environmentMaxAdaptation = it.adaptation
                environmentMaxS = it.encoding
                bestSolutions.add(it)
                environmentBestPos = it.id
                println("Смена локального максимума внутри окрестности, кодировка: $environmentMaxS | приспособленность:$environmentMaxAdaptation")
            }
        }

        if (environmentMaxAdaptation > maxAdaptation) {
            maxAdaptation = environmentMaxAdaptation
            maxS = environmentMaxS
            currentPos = environmentBestPos
            println("Сработал if, смена максимума, кодировка: $maxS | приспособленность:$maxAdaptation")
        } else {
            currentPos = (Math.random() * (INDIVIDUALS_QUANTITY - 1)).toInt()
        }
    }

    println("\n\nЛучшие решения:")
    bestSolutions.forEachIndexed { index, entity ->
        println("${index.inc()}) кодировка: ${entity.encoding}, приспособленность: ${entity.adaptation}")
    }

    print("\nИтоговое решение: кодировка = $maxS; приспособленность: $maxAdaptation")
}

private fun generateEnvironmentEntityList(encodingList: List<Entity>, position: Int): List<Entity> {
    val currentStepEntity = encodingList[position]
    val environmentEntityList = mutableListOf<Entity>()

    for (pos in getRange(encodingList, position)) {
        val entity = encodingList[pos]
        if (isMatchEnvironment(currentStepEntity, entity)) {
            environmentEntityList.add(entity)
        }
    }

    return environmentEntityList
}

private fun getRange(list: List<*>, position: Int): IntRange {
    val max = list.size - 1
    val min = 0

    var startRange = position - ENVIRONMENT_DISTANCE
    var endRange = position + ENVIRONMENT_DISTANCE

    if (startRange < min) startRange = min
    if (endRange > max) endRange = max

    return startRange..endRange
}


private fun getHammingDistance(n1: Long, n2: Long): Long {
    var x = n1 xor n2
    var setBits = 0L
    while (x > 0) {
        setBits += x and 1
        x = x shr 1
    }
    return setBits
}

private fun isMatchEnvironment(e1: Entity, e2: Entity): Boolean {
    return getHammingDistance(e1.encoding.toLong(), e2.encoding.toLong()) in 1..MAX_HAMMING_DISTANCE
}

private fun grayEncode(n: Int): Int {
    return n xor (n ushr 1)
}

private fun grayDecode(n: Long): Long {
    var n = n
    var p = n
    while (1.let { n = n ushr it; n } != 0L) p = p xor n
    return p
}

private fun generateEncodingList(): List<Entity> {
    fun generateEncode(pos: Int): String {
        return String
            .format("%${LENGTH_OF_ENCODING}s", Integer.toBinaryString(grayEncode(pos)))
            .replace(' ', '0')
    }

    fun generateAdaptation(): Long {
        val code = StringBuilder()
        for (i in 0 until LENGTH_OF_ENCODING) {
            code.append((0..9).random())
        }
        return code.toString().toLong()
    }

    val entities = mutableListOf<Entity>()
    for (individualNumber in 0 until INDIVIDUALS_QUANTITY) {
        entities.add(
            Entity(
                id = individualNumber,
                encoding = generateEncode(individualNumber),
                adaptation = generateAdaptation()
            )
        )
    }

    return entities
}

private data class Entity(
    val id: Int,
    val encoding: String,
    val adaptation: Long
)
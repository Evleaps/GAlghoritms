package lab1

import kotlin.math.pow

private const val LENGTH_OF_ENCODING = 15
private const val STEP_QUANTITY = 10

private val INDIVIDUALS_QUANTITY = 32.0.pow(2.0).toInt()

fun main() {
    val encodingList = generateEncodingList()
    encodingList.take(32).forEachIndexed { index, entity ->
        println("${index.inc()}) кодировка: ${entity.encoding}, приспособленность: ${entity.adaptation}")
    }
    println("\n")

    var maxAdaptation = -1L
    var maxS = ""
    for (step in 0..STEP_QUANTITY) {
        println("\nШаг $step")
        val randValue = (Math.random() * INDIVIDUALS_QUANTITY).toInt()
        val entity = encodingList[randValue]
        println("Случайно выбрана кодировка: ${entity.encoding}, приспособленность: ${entity.adaptation}")
        if (entity.adaptation > maxAdaptation) {
            maxAdaptation = entity.adaptation
            maxS = entity.encoding
            println("Сработал if, смена максимума, кодировка: $maxS | приспособленность:$maxAdaptation")
        }
    }

    print("\n\nИтоговое решение: кодировка = $maxS; приспособленность: $maxAdaptation")
}

private fun generateEncodingList(): List<Entity> {
    fun generateEncode(pos: Int): String {
        return String
            .format("%${LENGTH_OF_ENCODING}s", Integer.toBinaryString(pos))
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
        entities.add(Entity(
            encoding = generateEncode(individualNumber),
            adaptation = generateAdaptation()
        ))
    }

    return entities
}

private data class Entity(
    val encoding: String,
    val adaptation: Long
)
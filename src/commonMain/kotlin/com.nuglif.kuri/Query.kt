package com.nuglif.kuri

import kotlin.jvm.JvmInline

@JvmInline
public value class Query(public val value: String)

public fun String.asQuery(): Query = Query(this)
public fun Query.mapToMap(
    variableSeparator: Regex = "&".toRegex(),
    valueSeparator: Regex = "=".toRegex(),
): Map<String, String> {
    return if (value.isEmpty()) emptyMap()
    else value.decoded()
        .splitToSequence(variableSeparator) // TODO Remove custom implementation when bumping Kotlin to 1.6
        .associate { variableDefinition ->
            valueSeparator.find(variableDefinition)
                ?.range
                ?.let { range -> variableDefinition.take(range.first) to variableDefinition.drop(range.last + 1) }
                ?: (variableDefinition to "")
        }
}

private fun String.splitToSequence(separator: Regex): Sequence<String> = sequence {
    val separations = separator.findAll(this@splitToSequence)
        .mapTo(mutableListOf()) { it.range }
    if (separations.isEmpty()) yield(this@splitToSequence)
    else {
        yield(this@splitToSequence.substring(0 until separations.first().first))
        separations.asSequence()
            .zipWithNext { previousSeparationRange, nextSeparationRange ->
                this@splitToSequence.substring(previousSeparationRange.last + 1, nextSeparationRange.first)
            }
            .let { yieldAll(it) }
        yield(this@splitToSequence.substring(separations.last().last + 1, this@splitToSequence.length))
    }
}

public fun Map<String, String>.mapToQuery(
    variableSeparator: String = "&",
    valueSeparator: String = "=",
    shouldEncode: Boolean = true,
): Query {
    return entries.joinToString(separator = variableSeparator) { (parameter, value) ->
        "$parameter$valueSeparator$value"
    }
        .encoded(shouldEncode)
        .asQuery()
}
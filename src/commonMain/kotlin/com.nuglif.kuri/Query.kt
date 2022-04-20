package com.nuglif.kuri

import kotlin.jvm.JvmInline

public interface Query {
    public val raw: Raw

    @JvmInline
    public value class Raw(internal val value: String): Query {
        override val raw: Raw
            get() = this

        public fun toParameters(variableSeparator: Regex, valueSeparator: Regex): Parameters {
            return value.asParametersQuery(variableSeparator, valueSeparator)
        }

        public fun toParameters(variableSeparator: String = "&", valueSeparator: String = "="): Parameters {
            return toParameters(variableSeparator.toRegex(), valueSeparator.toRegex())
        }
    }
    public class Parameters(
        values: Map<String, String>,
        private val variableSeparator: String = "&",
        private val valueSeparator: String = "=",
    ): Query, Map<String, String> by values {
        override val raw: Raw by lazy {
            entries.joinToString(separator = variableSeparator) { (parameterName, parameterValue) ->
                "$parameterName$valueSeparator$parameterValue"
            }
                .let(::Raw)
        }
    }
}

public fun String.asQuery(): Query.Raw = Query.Raw(this)
public fun String.asParametersQuery(variableSeparator: Regex, valueSeparator: Regex): Query.Parameters {
    return takeUnless { it.isEmpty() }
        ?.decoded()
        ?.splitToSequence(variableSeparator)
        ?.associate { variableDefinition ->
            val parameters =  valueSeparator.split(variableDefinition, 2)
            parameters.first() to parameters.getOrElse(1) { "" }
        }
        .orEmpty()
        .let(Query::Parameters)
}

public fun String.asParametersQuery(variableSeparator: String = "&", valueSeparator: String = "="): Query.Parameters {
    return asParametersQuery(variableSeparator.toRegex(), valueSeparator.toRegex())
}

public fun Map<String, String>.asQuery(
    variableSeparator: String = "&",
    valueSeparator: String = "=",
): Query.Parameters = Query.Parameters(this, variableSeparator, valueSeparator)

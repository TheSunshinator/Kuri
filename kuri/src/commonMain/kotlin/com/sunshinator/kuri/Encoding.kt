package com.sunshinator.kuri

internal fun String.encoded(shouldEncode: Boolean): String {
    return if (shouldEncode) encoded() else this
}

public fun String.encoded(): String = buildString {
    this@encoded.asSequence().forEach { char ->
        val string = char.toString()
        if (string matches unreservedCharacters) append(char)
        else char.toString()
            .encodeToByteArray()
            .joinToString(separator = "") { byte ->
                byte.toUByte()
                    .toString(radix = 16)
                    .uppercase()
                    .padStart(2, '0')
                    .let { "%$it" }
            }
            .let(::append)
    }
}

private val unreservedCharacters = "[a-zA-Z0-9-_.~]".toRegex()

public fun String.decoded(): String = encodedCharacters.replace(this) { match ->
    val bytes = match.value
        .splitToSequence('%')
        .drop(1)
        .mapTo(mutableListOf()) { it.toByte(radix = 16) }
    ByteArray(bytes.size, bytes::get).decodeToString()
}.replace('+', ' ')

private val encodedCharacters = "(?:%[0-9A-Fa-f]{2})+".toRegex()


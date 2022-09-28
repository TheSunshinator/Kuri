package com.sunshinator.kuri

public data class Authority(
    val host: String,
    val userInformation: String? = null,
    val port: Int? = null,
)

internal fun Authority.mapToString(shouldEncode: Boolean = true): String = buildString {
    if (userInformation != null) {
        append(userInformation.encoded(shouldEncode))
        append('@')
    }
    append(host.encoded(shouldEncode))
    if (port != null) {
        append(':')
        append(port)
    }
}

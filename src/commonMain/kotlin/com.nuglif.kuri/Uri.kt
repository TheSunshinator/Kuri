package com.nuglif.kuri

public data class Uri(
    val scheme: Scheme,
    val authority: Authority? = null,
    val path: List<String> = emptyList(),
    val query: Query? = null,
    val fragment: String? = null,
) {
    val host: String?
        inline get() = authority?.host

    val userInformation: String?
        inline get() = authority?.userInformation

    val port: Int?
        inline get() = authority?.port
}

public fun String.mapToUri(pathSeparatorRegex: Regex? = "/".toRegex()): Result<Uri> {
    val matchResult = parsingRegex.matchEntire(decoded())
    return matchResult?.groupValues
        ?.get(1)
        ?.asScheme()
        ?.map { scheme ->
            Uri(
                scheme = scheme,
                authority = matchResult.groupValues[3]
                    .takeUnless { it.isEmpty() }
                    ?.let { host ->
                        Authority(
                            host = host,
                            userInformation = matchResult.groupValues[2].takeUnless { it.isEmpty() },
                            port = matchResult.groupValues[4].takeUnless { it.isEmpty() }?.toInt()
                        )
                    },
                path = matchResult.groupValues[5]
                    .let { if (it.startsWith('/')) it.drop(1) else it }
                    .let { path ->
                        if (pathSeparatorRegex == null) listOf(path) else path.split(pathSeparatorRegex)
                    },
                query = matchResult.groupValues[6]
                    .takeUnless { it.isEmpty() }
                    ?.asQuery(),
                fragment = matchResult.groupValues[7].takeUnless { it.isEmpty() },
            )
        }
        ?: Result.failure(IllegalArgumentException("URI is invalid: $this"))
}

private val parsingRegex = run {
    val scheme = "[a-zA-Z][a-zA-Z0-9+.-]+?".toRegex()
    val userInformation = "(?:[a-zA-Z0-9-._~!$&'()*+,;=:]|%[0-9A-F]{2})*?".toRegex()
    val host = "(?:[a-zA-Z0-9-._~!$&'()*+,:;=\\[\\]]|%[0-9A-F]{2})*(?<!:\\d{0,6})".toRegex()
    val port = "\\d*".toRegex()
    val path = "(?:[a-zA-Z0-9-._~!$&'()*+,;=:@/]|%[0-9A-F]{2})*".toRegex()
    val query = "(?:[a-zA-Z0-9-._~!\$&'()*+,;=:/?@]|%[0-9A-F]{2})*".toRegex()
    val fragment = "(?:[a-zA-Z0-9-._~!$&'()*+,;=:/?@]|%[0-9A-F]{2})*".toRegex()
    "^($scheme):(?://(?:($userInformation)@)?($host)(?::($port))?)?($path)(?:\\?($query))?(?:#($fragment))?$".toRegex()
}

public fun Uri.mapToString(
    pathSeparator: String = "/",
    shouldEncode: Boolean = true,
): String = buildString {
    append(scheme.value)
    append(':')
    if (authority != null) {
        append("//")
        append(authority.mapToString(shouldEncode))
        if (path.isNotEmpty()) append('/')
    }
    path.joinTo(this, separator = pathSeparator.encoded(shouldEncode))
    if (query != null) {
        append('?')
        append(query.value.encoded(shouldEncode))
    }
    if (fragment != null) {
        append('#')
        append(fragment.encoded(shouldEncode))
    }
}
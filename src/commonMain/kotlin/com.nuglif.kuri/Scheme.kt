package com.nuglif.kuri

import kotlin.jvm.JvmInline

@JvmInline
public value class Scheme internal constructor(
    public val value: String,
)

internal val scheme = "[a-zA-Z][a-zA-Z0-9+.-]+"
private val schemeRegex = "^$scheme$".toRegex()
public fun String.asScheme(): Result<Scheme> {
    return if (this matches schemeRegex) Result.success(Scheme(this))
    else Result.failure(IllegalArgumentException("Scheme $this does not conform to standards"))
}

public fun String.asSchemeOrThrow(): Scheme {
    return if (this matches schemeRegex) Scheme(this)
    else throw IllegalArgumentException("Scheme $this does not conform to standards")
}

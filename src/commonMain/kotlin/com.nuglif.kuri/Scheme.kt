package com.nuglif.kuri

import kotlin.jvm.JvmInline

@JvmInline
public value class Scheme internal constructor(
    public val value: String,
)

private val validationRegex = "^[a-zA-Z][a-zA-Z0-9+.-]+$".toRegex()
public fun String.asScheme(): Result<Scheme> {
    return if (this matches validationRegex) Result.success(Scheme(this))
    else Result.failure(IllegalArgumentException("Scheme $this does not conform to standards"))
}

public fun String.asSchemeOrThrow(): Scheme {
    return if (this matches validationRegex) Scheme(this)
    else throw IllegalArgumentException("Scheme $this does not conform to standards")
}

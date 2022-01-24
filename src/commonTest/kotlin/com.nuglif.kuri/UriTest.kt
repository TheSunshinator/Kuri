package com.nuglif.kuri

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MapToUriTest {
    @Test
    fun givenInvalidUri_thenReturnFailure() {
        "some invalid URI".mapToUri().isFailure shouldBe true
    }

    @Test
    fun givenInvalidScheme_thenReturnFailure() {
        "!validScheme:path".mapToUri().isFailure shouldBe true
    }

    @Test
    fun givenValidUri_thenReturnUriInstance() {
        val subject = "someScheme://someUser@someHost:42/some/path?someQuery#someFragment"
        val result = subject.mapToUri().getOrThrow()

        assertSoftly {
            result.run {
                scheme.value shouldBe "someScheme"
                userInformation shouldBe "someUser"
                host shouldBe "someHost"
                port shouldBe 42
                path.shouldContainExactly("some", "path")
                query?.value shouldBe "someQuery"
                fragment shouldBe "someFragment"
            }
        }
    }
}

class MapToStringTest {
    @Test
    fun givenNoFragment_thenFragmentPartIsMissing() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = null,
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someUser@someHost:42/some/path?someQuery"
    }

    @Test
    fun givenNoQuery_thenQueryPartIsMissing() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = null,
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someUser@someHost:42/some/path#someFragment"
    }

    @Test
    fun givenEmptyPath_thenPathPartIsEmpty() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = emptyList(),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someUser@someHost:42?someQuery#someFragment"
    }

    @Test
    fun givenNoPort_thenPortIsMissing() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = null,
            ),
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someUser@someHost/some/path?someQuery#someFragment"
    }

    @Test
    fun givenNoUserInformation_thenUserInformationIsMissing() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = null,
                port = 42,
            ),
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someHost:42/some/path?someQuery#someFragment"
    }

    @Test
    fun givenEmptyHost_thenHostIsEmpty() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme://someUser@:42/some/path?someQuery#someFragment"
    }

    @Test
    fun givenNoAuthority_thenAuthorityIsMissing() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = null,
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false) shouldBe "someScheme:some/path?someQuery#someFragment"
    }

    @Test
    fun givenCustomPathSeparator_thenPathIsSeparatedWithSeparator() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = "someQuery".asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(
            pathSeparator = "<+>",
            shouldEncode = false
        ) shouldBe "someScheme://someUser@someHost:42/some<+>path?someQuery#someFragment"
    }
}

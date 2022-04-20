package com.nuglif.kuri

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class MapToUriTest {
    @Test
    fun givenInvalidUri_thenReturnFailure() {
        "some invalid URI".mapToUri().isFailure shouldBe true
    }

    @Test
    fun givenStringContainingValidUri_thenReturnFailure() {
        " someScheme://someUser@someHost:42/some/path?someQuery#someFragment".mapToUri().isFailure shouldBe true
        "someScheme://someUser@someHost:42/some/path?someQuery#someFragment ".mapToUri().isFailure shouldBe true
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
                query.shouldNotBeNull()
                    .shouldBeInstanceOf<Query.Raw>()
                    .value shouldBe "someQuery"
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
    fun givenParametersQuery_thenQueryPartFormattedToStringProperly() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = mapOf(
                "someParameter" to "someValue",
                "someOtherParameter" to "someOtherValue",
            ).asQuery(),
            fragment = "someFragment",
        )

        subject.mapToString(shouldEncode = false)
            .shouldBe("someScheme://someUser@someHost:42/some/path?someParameter=someValue&someOtherParameter=someOtherValue#someFragment")
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

class ModifyParameters {
    @Test
    fun givenRawQuery_whenModifyingParameters_thenDoNothing() {
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

        assertSoftly {
            subject.modifyParameters { put("someParameter", "someValue") }
                .query
                .shouldNotBeNull()
                .shouldBeInstanceOf<Query.Raw>()
                .value shouldBe "someQuery"
        }
    }

    @Test
    fun givenNullQuery_whenAddingParameters_thenReturnMapOfAddedParametersOnly() {
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

        assertSoftly {
            subject.modifyParameters { put("someParameter", "someValue") }
                .query
                .shouldNotBeNull()
                .shouldBeInstanceOf<Query.Parameters>()
                .shouldContainExactly(mapOf("someParameter" to "someValue"))
        }
    }

    @Test
    fun givenNonEmptyQuery_whenRemovingAllParameters_thenReturnNull() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = mapOf("someParameter" to "someValue").asQuery(),
            fragment = "someFragment",
        )

        subject.modifyParameters { clear() }.query should beNull()
    }

    @Test
    fun givenNonEmptyQuery_whenModifyingParameters_thenReturnModifiedParameters() {
        val subject = Uri(
            scheme = "someScheme".asSchemeOrThrow(),
            authority = Authority(
                host = "someHost",
                userInformation = "someUser",
                port = 42,
            ),
            path = listOf("some", "path"),
            query = mapOf(
                "someParameter" to "someValue",
                "someOtherParameter" to "someOtherValue",
            ).asQuery(),
            fragment = "someFragment",
        )

        subject.modifyParameters {
            remove("someOtherParameter")
            put("someThirdParameter", "someThirdValue")
        }
            .query
            .shouldNotBeNull()
            .shouldBeInstanceOf<Query.Parameters>()
            .shouldContainExactly(mapOf("someParameter" to "someValue", "someThirdParameter" to "someThirdValue"))
    }
}

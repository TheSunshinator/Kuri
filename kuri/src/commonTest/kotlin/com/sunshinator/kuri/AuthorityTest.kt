package com.sunshinator.kuri

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beEmpty
import kotlin.test.Test

class AuthorityTest {
    @Test
    fun givenEmptyAuthority_thenReturnEmptyString() {
        Authority("").mapToString(shouldEncode = false) should beEmpty()
    }

    @Test
    fun givenAuthorityWithOnlyHost_thenReturnHost() {
        Authority("some.host").mapToString(shouldEncode = false) shouldBe "some.host"
    }

    @Test
    fun givenFullAuthority_thenReturnProperlySeparatedString() {
        val subject = Authority(
            host = "some.host",
            userInformation = "some_user_information",
            port = 42,
        )

        subject.mapToString(shouldEncode = false) shouldBe "some_user_information@some.host:42"
    }

    @Test
    fun givenAuthorityWithoutPort_thenReturnStringWithoutPort() {
        val subject = Authority(
            host = "some.host",
            userInformation = "some_user_information",
        )

        subject.mapToString(shouldEncode = false) shouldBe "some_user_information@some.host"
    }

    @Test
    fun givenAuthorityWithoutUserInformation_thenReturnHostAndPort() {
        val subject = Authority(
            host = "some.host",
            port = 42,
        )

        subject.mapToString(shouldEncode = false) shouldBe "some.host:42"
    }

    @Test
    fun givenAuthorityWithEmptyStrings_thenReturnSeparator() {
        val subject = Authority(
            host = "",
            userInformation = "",
        )

        subject.mapToString(shouldEncode = false) shouldBe "@"
    }
}

package com.nuglif.kuri

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SchemeTest {
    @Test
    fun givenEmptyString_thenReturnFailure() {
        "".asScheme().isFailure shouldBe true
    }

    @Test
    fun givenStringStartingWithoutALetter_thenReturnFailure() {
        "3someScheme".asScheme().isFailure shouldBe true
    }

    @Test
    fun givenStringContainsInvalidCharacter_thenReturnFailure() {
        "some#Scheme".asScheme().isFailure shouldBe true
    }

    @Test
    fun givenValidString_thenReturnSuccess() {
        "someScheme".asScheme().isSuccess shouldBe true
    }
}
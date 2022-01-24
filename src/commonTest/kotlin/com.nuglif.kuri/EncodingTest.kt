package com.nuglif.kuri

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EncodedTest {
    @Test
    fun givenStringWithoutSpecialCharacters_thenReturnStringUnchanged() {
        "someNormalString".encoded() shouldBe "someNormalString"
    }

    @Test
    fun givenStringWithSpecialCharacter_thenSpecialCharactersArePercentEncoded(){
        "some@bnormal\$tring".encoded() shouldBe "some%40bnormal%24tring"
    }
}

class DecodedTest {
    @Test
    fun givenStringWithoutEncodedCharacters_thenReturnStringUnchanged() {
        "someNormalString".decoded() shouldBe "someNormalString"
    }

    @Test
    fun givenStringWithPercentEncodedCharacter_thenReturnStringWithSpecialCharacters(){
        "some%40bnormal%24tring".decoded() shouldBe "some@bnormal\$tring"
    }

    @Test
    fun givenStringWithPlusSign_thenReturnStringWithSpaces(){
        "some+string+with+pluses".decoded() shouldBe "some string with pluses"
    }
}
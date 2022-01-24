package com.nuglif.kuri

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.haveKey
import io.kotest.matchers.maps.haveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlin.test.Test

class MapToMapTest {
    @Test
    fun givenEmptyQuery_thenReturnEmptyMap() {
        "".asQuery().mapToMap() should beEmpty()
    }

    @Test
    fun givenQueryWithoutVariableSeparator_thenReturnSingleElement() {
        "someVariable=someValue".asQuery().mapToMap() should haveSize(1)
    }

    @Test
    fun givenQueryWithoutValueSeparator_thenReturnElementMappedToEmptyString() {
        val result = "someVariable&someOtherValue".asQuery().mapToMap()
        assertSoftly {
            result should haveSize(2)
            result shouldContain ("someVariable" to "")
            result shouldContain ("someOtherValue" to "")
        }
    }

    @Test
    fun givenQueryCustomSeparators_thenReturnElementMappedToProperValue() {
        val subject = "someVariable->someValue<>someOtherVariable>someOtherValue><someThirdVariable=>someThirdValue"
        val result = subject.asQuery().mapToMap(
            variableSeparator = "<>|><".toRegex(),
            valueSeparator = "[-=]?>".toRegex(),
        )
        assertSoftly {
            result should haveSize(3)
            result shouldContain ("someVariable" to "someValue")
            result shouldContain ("someOtherVariable" to "someOtherValue")
            result shouldContain ("someThirdVariable" to "someThirdValue")
        }
    }
}

class MapToQueryTest {
    @Test
    fun givenEmptyMap_thenReturnEmptyQuery() {
        emptyMap<String, String>().mapToQuery().value.shouldBeEmpty()
    }

    @Test
    fun givenMap_thenReturnEachEntryJoined() {
        val result = mapOf("a" to "1", "b" to "2", "" to "emptyKey", "emptyVariable" to "")
            .mapToQuery(shouldEncode = false)
            .value

        result shouldBe "a=1&b=2&=emptyKey&emptyVariable="
    }
}

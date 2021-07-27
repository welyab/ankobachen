package com.welyab.ankobachen

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CircularListTest {

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -2, Int.MIN_VALUE])
    fun `instantiation should throw IllegalArgumentException if capacity is less than 1`() {
        assertThrows<IllegalArgumentException> {
            CircularList<String>(0)
        }
    }
}

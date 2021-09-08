/*
 * Copyright (C) 2021 Welyab da Silva Paula
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.welyab.ankobachen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ColorTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE, w",
            "BLACK, b"
        ]
    )
    fun `from string should create properly color`(expectedColor: Color, color: String) {
        assertEquals(expectedColor, Color.from(color))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE, w",
            "BLACK, b"
        ]
    )
    fun `from char should create properly color`(expectedColor: Color, color: Char) {
        assertEquals(expectedColor, Color.from(color))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "W",
            "B",
            "white",
            "black"
        ]
    )
    fun `from string should throw ColorException when color string is invalid`(color: String) {
        assertThrows<ColorException> { Color.from(color) }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "W",
            "B",
            "0",
            "1"
        ]
    )
    fun `from char should throw ColorException when color char is invalid`(color: Char) {
        assertThrows<ColorException> { Color.from(color) }
    }
}

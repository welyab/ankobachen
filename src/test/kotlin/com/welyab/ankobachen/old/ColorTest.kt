/*
 * Copyright (C) 2020 Welyab da Silva Paula
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
package com.welyab.ankobachen.old

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ColorTest {

    @ParameterizedTest
    @CsvSource(
        "WHITE, BLACK",
        "BLACK, WHITE"
    )
    fun `opposite should return opposite color`(color: Color, oppositeColor: Color) {
        Assertions.assertEquals(oppositeColor, color.opposite)
    }

    @ParameterizedTest
    @CsvSource(
        "WHITE, true",
        "BLACK, false"
    )
    fun `isWhite should return true if the color is white`(color: Color, expectedValue: Boolean) {
        Assertions.assertEquals(expectedValue, color.isWhite)
    }

    @ParameterizedTest
    @CsvSource(
        "WHITE, false",
        "BLACK, true"
    )
    fun `isBlack should return true if the color is black`(color: Color, expectedValue: Boolean) {
        Assertions.assertEquals(expectedValue, color.isBlack)
    }

    @Test
    fun `letter for white color is w`() {
        Assertions.assertEquals('w', Color.WHITE.letter)
    }

    @Test
    fun `letter for black color is b`() {
        Assertions.assertEquals('b', Color.BLACK.letter)
    }

    @Test
    fun `fromValue should throw ColorException when color letter is invalid`() {
        assertThrows<ColorException> {
            Color.from('x')
        }
    }
}

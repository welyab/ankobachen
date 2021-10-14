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
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConverter
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource

class BitOperationsTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "0000000000000000000000000000000000000000000000000000000000000000, 4, 0000000000000000000000000000000000000000000000000000000000000000",
            "1000000000000000000000000000000000000000000000000000000000000000, 9, 0000000001000000000000000000000000000000000000000000000000000000",
            "0000000000000000000000000000000000000000000000000000000000000000, 8, 0000000000000000000000000000000000000000000000000000000000000000",
            "0000000000000000000000000000000000000000000000000000000000000100, 7, 0000000000000000000000000000000000000000000000000000000000000000",
            "0000000000000001000000000000000000000000000000000000000000000000, 6, 0000000000000000000001000000000000000000000000000000000000000000",
            "1000000000000000000000000000000000000000000000000000000000000000, 63, 0000000000000000000000000000000000000000000000000000000000000001",
            "1000000000000000000000000000000000000000000000000000000000000000, 64, 0000000000000000000000000000000000000000000000000000000000000000"
        ]
    )
    fun `shiftRight should shift bit to right`(
        @ConvertWith(BitStringToULong::class) value: Long,
        shift: Int,
        @ConvertWith(BitStringToULong::class) expectedValue: Long
    ) {
        assertEquals(expectedValue.toULong(), value.toULong().shiftRight(shift))
    }

    private class BitStringToULong : ArgumentConverter {
        override fun convert(source: Any, context: ParameterContext?): Any? {
            return source.toString().toULong(2).toLong()
        }
    }
}

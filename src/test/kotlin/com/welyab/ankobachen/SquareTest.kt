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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class SquareTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "a8, 0, 0", "b8, 0, 1", "c8, 0, 2", "d8, 0, 3", "e8, 0, 4", "f8, 0, 5", "g8, 0, 6", "h8, 0, 7",
            "a7, 1, 0", "b7, 1, 1", "c7, 1, 2", "d7, 1, 3", "e7, 1, 4", "f7, 1, 5", "g7, 1, 6", "h7, 1, 7",
            "a6, 2, 0", "b6, 2, 1", "c6, 2, 2", "d6, 2, 3", "e6, 2, 4", "f6, 2, 5", "g6, 2, 6", "h6, 2, 7",
            "a5, 3, 0", "b5, 3, 1", "c5, 3, 2", "d5, 3, 3", "e5, 3, 4", "f5, 3, 5", "g5, 3, 6", "h5, 3, 7",
            "a4, 4, 0", "b4, 4, 1", "c4, 4, 2", "d4, 4, 3", "e4, 4, 4", "f4, 4, 5", "g4, 4, 6", "h4, 4, 7",
            "a3, 5, 0", "b3, 5, 1", "c3, 5, 2", "d3, 5, 3", "e3, 5, 4", "f3, 5, 5", "g3, 5, 6", "h3, 5, 7",
            "a2, 6, 0", "b2, 6, 1", "c2, 6, 2", "d2, 6, 3", "e2, 6, 4", "f2, 6, 5", "g2, 6, 6", "h2, 6, 7",
            "a1, 7, 0", "b1, 7, 1", "c1, 7, 2", "d1, 7, 3", "e1, 7, 4", "f1, 7, 5", "g1, 7, 6", "h1, 7, 7"
        ]
    )
    fun `square should have properly row and column`(squareString: String, row: Int, column: Int) {
        val square = Square.from(squareString)
        Assertions.assertEquals(square.row, row)
        Assertions.assertEquals(square.column, column)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "a8, a, 8", "b8, b, 8", "c8, c, 8", "d8, d, 8", "e8, e, 8", "f8, f, 8", "g8, g, 8", "h8, h, 8",
            "a7, a, 7", "b7, b, 7", "c7, c, 7", "d7, d, 7", "e7, e, 7", "f7, f, 7", "g7, g, 7", "h7, h, 7",
            "a6, a, 6", "b6, b, 6", "c6, c, 6", "d6, d, 6", "e6, e, 6", "f6, f, 6", "g6, g, 6", "h6, h, 6",
            "a5, a, 5", "b5, b, 5", "c5, c, 5", "d5, d, 5", "e5, e, 5", "f5, f, 5", "g5, g, 5", "h5, h, 5",
            "a4, a, 4", "b4, b, 4", "c4, c, 4", "d4, d, 4", "e4, e, 4", "f4, f, 4", "g4, g, 4", "h4, h, 4",
            "a3, a, 3", "b3, b, 3", "c3, c, 3", "d3, d, 3", "e3, e, 3", "f3, f, 3", "g3, g, 3", "h3, h, 3",
            "a2, a, 2", "b2, b, 2", "c2, c, 2", "d2, d, 2", "e2, e, 2", "f2, f, 2", "g2, g, 2", "h2, h, 2",
            "a1, a, 1", "b1, b, 1", "c1, c, 1", "d1, d, 1", "e1, e, 1", "f1, f, 1", "g1, g, 1", "h1, h, 1"
        ]
    )
    fun `square should have properly file and rank`(squareString: String, file: Char, rank: Int) {
        val square = Square.from(squareString)
        Assertions.assertEquals(square.file, file)
        Assertions.assertEquals(square.rank, rank)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        ]
    )
    fun `from(char, char) should parse square properly`(square: String) {
        Assertions.assertEquals(
            Square.from(square),
            Square.from(square[0].lowercaseChar(), square[1])
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "a8, 0, 0", "b8, 0, 1", "c8, 0, 2", "d8, 0, 3", "e8, 0, 4", "f8, 0, 5", "g8, 0, 6", "h8, 0, 7",
            "a7, 1, 0", "b7, 1, 1", "c7, 1, 2", "d7, 1, 3", "e7, 1, 4", "f7, 1, 5", "g7, 1, 6", "h7, 1, 7",
            "a6, 2, 0", "b6, 2, 1", "c6, 2, 2", "d6, 2, 3", "e6, 2, 4", "f6, 2, 5", "g6, 2, 6", "h6, 2, 7",
            "a5, 3, 0", "b5, 3, 1", "c5, 3, 2", "d5, 3, 3", "e5, 3, 4", "f5, 3, 5", "g5, 3, 6", "h5, 3, 7",
            "a4, 4, 0", "b4, 4, 1", "c4, 4, 2", "d4, 4, 3", "e4, 4, 4", "f4, 4, 5", "g4, 4, 6", "h4, 4, 7",
            "a3, 5, 0", "b3, 5, 1", "c3, 5, 2", "d3, 5, 3", "e3, 5, 4", "f3, 5, 5", "g3, 5, 6", "h3, 5, 7",
            "a2, 6, 0", "b2, 6, 1", "c2, 6, 2", "d2, 6, 3", "e2, 6, 4", "f2, 6, 5", "g2, 6, 6", "h2, 6, 7",
            "a1, 7, 0", "b1, 7, 1", "c1, 7, 2", "d1, 7, 3", "e1, 7, 4", "f1, 7, 5", "g1, 7, 6", "h1, 7, 7"
        ]
    )
    fun `from(int, int) should parse square properly`(square: String, row: Int, column: Int) {
        Assertions.assertEquals(
            Square.from(square),
            Square.from(row, column)
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "a8, a, 8", "b8, b, 8", "c8, c, 8", "d8, d, 8", "e8, e, 8", "f8, f, 8", "g8, g, 8", "h8, h, 8",
            "a7, a, 7", "b7, b, 7", "c7, c, 7", "d7, d, 7", "e7, e, 7", "f7, f, 7", "g7, g, 7", "h7, h, 7",
            "a6, a, 6", "b6, b, 6", "c6, c, 6", "d6, d, 6", "e6, e, 6", "f6, f, 6", "g6, g, 6", "h6, h, 6",
            "a5, a, 5", "b5, b, 5", "c5, c, 5", "d5, d, 5", "e5, e, 5", "f5, f, 5", "g5, g, 5", "h5, h, 5",
            "a4, a, 4", "b4, b, 4", "c4, c, 4", "d4, d, 4", "e4, e, 4", "f4, f, 4", "g4, g, 4", "h4, h, 4",
            "a3, a, 3", "b3, b, 3", "c3, c, 3", "d3, d, 3", "e3, e, 3", "f3, f, 3", "g3, g, 3", "h3, h, 3",
            "a2, a, 2", "b2, b, 2", "c2, c, 2", "d2, d, 2", "e2, e, 2", "f2, f, 2", "g2, g, 2", "h2, h, 2",
            "a1, a, 1", "b1, b, 1", "c1, c, 1", "d1, d, 1", "e1, e, 1", "f1, f, 1", "g1, g, 1", "h1, h, 1"
        ]
    )
    fun `from(char, int) should parse square properly`(square: String, file: Char, rank: Int) {
        Assertions.assertEquals(
            Square.from(square),
            Square.from(file, rank)
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "A1", "A2", "A8", "8a", "6d", "0a", "00", "0"
        ]
    )
    fun `from san should throw SquareException when san position is invalid`(san: String) {
        assertThrows<SquareException> { Square.from(san) }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "x, 1",
            "., 2",
            "a, 9",
            "b, -",
            "c, /",
            "1, =",
            "g, &",
            "h, *",
            "A, 1",
            "B, d"
        ]
    )
    fun `from(char, char) should throw SquareException when file or rank is invalid`(file: Char, rank: Char) {
        assertThrows<SquareException> { Square.from(file, rank) }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1, -2",
            "b, 0",
            "c, 9",
            "d, -1",
            "e, 10",
            "f, 999",
            "A, 1",
            "B, 2",
            "x, 3",
            "$, 4"
        ]
    )
    fun `from(char, int) should throw SquareException when file or rank is invalid`(file: Char, rank: Int) {
        assertThrows<SquareException> { Square.from(file, rank) }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "-1, -1",
            "-999, 2",
            "-2, 3",
            "-1, 4",
            "9, 6",
            "10, 7",
            "2, -999",
            "3, -2",
            "4, -1",
            "6, 9",
            "7, 10"
        ]
    )
    fun `from(int, int) should throw SquareException when file or rank is invalid`(row: Int, column: Int) {
        assertThrows<SquareException> { Square.from(row, column) }
    }
}

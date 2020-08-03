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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class PositionTest {

    @ParameterizedTest
    @CsvSource(
        "a, 0",
        "b, 1",
        "c, 2",
        "d, 3",
        "e, 4",
        "f, 5",
        "g, 6",
        "h, 7"
    )
    fun `fileToColumn should convert file to column properly`(file: Char, expectedColumn: Int) {
        Assertions.assertEquals(expectedColumn, Position.fileToColumn(file))
    }

    @ParameterizedTest
    @ValueSource(
        chars = ['_', '&', '9', '0', '+', '#', 'A', 'B', 'C', 'i', 'j', 'k', 'z']
    )
    fun `fileToColumn should thrown PositionException if  file is not in range a-h`(file: Char) {
        assertThrows<PositionException> {
            Position.fileToColumn(file)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "0, a",
        "1, b",
        "2, c",
        "3, d",
        "4, e",
        "5, f",
        "6, g",
        "7, h"
    )
    fun `columnToFile should convert column to file properly`(column: Int, expectedFile: Char) {
        Assertions.assertEquals(expectedFile, Position.columnToFile(column))
    }

    @ParameterizedTest
    @ValueSource(
        ints = [-10, -1, 8, 9]
    )
    fun `columnToFile should thrown PositionException if  column is not in range 0-7`(column: Int) {
        assertThrows<PositionException> {
            Position.columnToFile(column)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "1, 7",
        "2, 6",
        "3, 5",
        "4, 4",
        "5, 3",
        "6, 2",
        "7, 1",
        "8, 0"
    )
    fun `rankToRow should convert rank to row properly`(rank: Int, expectedRow: Int) {
        Assertions.assertEquals(expectedRow, Position.rankToRow(rank))
    }

    @ParameterizedTest
    @ValueSource(
        ints = [-10, -1, 0, 9, 10, 99]
    )
    fun `rankToRow should throw PositionException if rank is not in range 1-8`(rank: Int) {
        assertThrows<PositionException> {
            Position.rankToRow(rank)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "7, 1",
        "6, 2",
        "5, 3",
        "4, 4",
        "3, 5",
        "2, 6",
        "1, 7",
        "0, 8"
    )
    fun `rowToRank should convert row to rank properly`(row: Int, expectedRank: Int) {
        Assertions.assertEquals(expectedRank, Position.rowToRank(row))
    }

    @ParameterizedTest
    @ValueSource(
        ints = [-10, -1, 8, 9]
    )
    fun `rowToRank should throw PositionException if row is not in range 0-7`(row: Int) {
        assertThrows<PositionException> {
            Position.rowToRank(row)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0, A8", "0, 1, B8", "0, 2, C8", "0, 3, D8", "0, 4, E8", "0, 5, F8", "0, 6, G8", "0, 7, H8",
        "1, 0, A7", "1, 1, B7", "1, 2, C7", "1, 3, D7", "1, 4, E7", "1, 5, F7", "1, 6, G7", "1, 7, H7",
        "2, 0, A6", "2, 1, B6", "2, 2, C6", "2, 3, D6", "2, 4, E6", "2, 5, F6", "2, 6, G6", "2, 7, H6",
        "3, 0, A5", "3, 1, B5", "3, 2, C5", "3, 3, D5", "3, 4, E5", "3, 5, F5", "3, 6, G5", "3, 7, H5",
        "4, 0, A4", "4, 1, B4", "4, 2, C4", "4, 3, D4", "4, 4, E4", "4, 5, F4", "4, 6, G4", "4, 7, H4",
        "5, 0, A3", "5, 1, B3", "5, 2, C3", "5, 3, D3", "5, 4, E3", "5, 5, F3", "5, 6, G3", "5, 7, H3",
        "6, 0, A2", "6, 1, B2", "6, 2, C2", "6, 3, D2", "6, 4, E2", "6, 5, F2", "6, 6, G2", "6, 7, H2",
        "7, 0, A1", "7, 1, B1", "7, 2, C1", "7, 3, D1", "7, 4, E1", "7, 5, F1", "7, 6, G1", "7, 7, H1"
    )
    fun `from should return properly Position when requested from column and column pair`(
        row: Int,
        column: Int,
        expectedPosition: Position
    ) {
        Assertions.assertEquals(expectedPosition, Position.from(row, column))
    }

    @ParameterizedTest
    @CsvSource(
        "0, -1",
        "1, 9",
        "2, -20",
        "-1, 1",
        "8, 2",
        "9, 3",
        "10, 4"
    )
    fun `from should throw PositionException if row and column pair are invalid`(
        row: Int,
        column: Int
    ) {
        assertThrows<PositionException> {
            Position.from(row, column)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "a, 8, A8", "b, 8, B8", "c, 8, C8", "d, 8, D8", "e, 8, E8", "f, 8, F8", "g, 8, G8", "h, 8, H8",
        "a, 7, A7", "b, 7, B7", "c, 7, C7", "d, 7, D7", "e, 7, E7", "f, 7, F7", "g, 7, G7", "h, 7, H7",
        "a, 6, A6", "b, 6, B6", "c, 6, C6", "d, 6, D6", "e, 6, E6", "f, 6, F6", "g, 6, G6", "h, 6, H6",
        "a, 5, A5", "b, 5, B5", "c, 5, C5", "d, 5, D5", "e, 5, E5", "f, 5, F5", "g, 5, G5", "h, 5, H5",
        "a, 4, A4", "b, 4, B4", "c, 4, C4", "d, 4, D4", "e, 4, E4", "f, 4, F4", "g, 4, G4", "h, 4, H4",
        "a, 3, A3", "b, 3, B3", "c, 3, C3", "d, 3, D3", "e, 3, E3", "f, 3, F3", "g, 3, G3", "h, 3, H3",
        "a, 2, A2", "b, 2, B2", "c, 2, C2", "d, 2, D2", "e, 2, E2", "f, 2, F2", "g, 2, G2", "h, 2, H2",
        "a, 1, A1", "b, 1, B1", "c, 1, C1", "d, 1, D1", "e, 1, E1", "f, 1, F1", "g, 1, G1", "h, 1, H1"
    )
    fun `from should return properly Position when requested from file and rank pair`(
        file: Char,
        rank: Int,
        expectedPosition: Position
    ) {
        Assertions.assertEquals(expectedPosition, Position.from(file, rank))
    }

    @ParameterizedTest
    @CsvSource(
        "a, -1",
        "b, 0",
        "c, 9",
        "x, 1",
        "_, 2",
        "1, 3",
        "-, 4"
    )
    fun `from should throw PositionException if file and rank pair are invalid`(
        file: Char,
        rank: Int
    ) {
        assertThrows<PositionException> {
            Position.from(file, rank)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "a8, A8", "b8, B8", "c8, C8", "d8, D8", "e8, E8", "f8, F8", "g8, G8", "h8, H8",
        "a7, A7", "b7, B7", "c7, C7", "d7, D7", "e7, E7", "f7, F7", "g7, G7", "h7, H7",
        "a6, A6", "b6, B6", "c6, C6", "d6, D6", "e6, E6", "f6, F6", "g6, G6", "h6, H6",
        "a5, A5", "b5, B5", "c5, C5", "d5, D5", "e5, E5", "f5, F5", "g5, G5", "h5, H5",
        "a4, A4", "b4, B4", "c4, C4", "d4, D4", "e4, E4", "f4, F4", "g4, G4", "h4, H4",
        "a3, A3", "b3, B3", "c3, C3", "d3, D3", "e3, E3", "f3, F3", "g3, G3", "h3, H3",
        "a2, A2", "b2, B2", "c2, C2", "d2, D2", "e2, E2", "f2, F2", "g2, G2", "h2, H2",
        "a1, A1", "b1, B1", "c1, C1", "d1, D1", "e1, E1", "f1, F1", "g1, G1", "h1, H1"
    )
    fun `from should return properly Position when requested from SAN position`(
        sanPosition: String,
        expectedPosition: Position
    ) {
        Assertions.assertEquals(expectedPosition, Position.from(sanPosition))
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["", "a9", "x", "52", "ab", "1a", "A1", "BB", "-1", "a5xd3"]
    )
    fun `from should throw PositionException if sanPosition is invalid`(sanPosition: String) {
        assertThrows<PositionException> {
            Position.from(sanPosition)
        }
    }
}

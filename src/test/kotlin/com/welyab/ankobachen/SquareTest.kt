/**
 * Copyright 2024 Welyab da Silva Paula
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.welyab.ankobachen

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class SquareTest {
    fun `squares should have properly rank numbers`() {
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            A8, a
            A7, a
            A6, a
            A5, a
            A4, a
            A3, a
            A2, a
            A1, a
            B8, b
            B7, b
            B6, b
            B5, b
            B4, b
            B3, b
            B2, b
            B1, b
            C8, c
            C7, c
            C6, c
            C5, c
            C4, c
            C3, c
            C2, c
            C1, c
            D8, d
            D7, d
            D6, d
            D5, d
            D4, d
            D3, d
            D2, d
            D1, d
            E8, e
            E7, e
            E6, e
            E5, e
            E4, e
            E3, e
            E2, e
            E1, e
            F8, f
            F7, f
            F6, f
            F5, f
            F4, f
            F3, f
            F2, f
            F1, f
            G8, g
            G7, g
            G6, g
            G5, g
            G4, g
            G3, g
            G2, g
            G1, g
            H8, h,
            H7, h,
            H6, h,
            H5, h,
            H4, h,
            H3, h,
            H2, h,
            H1, h"""
    )
    fun `squares should have properly file symbols`(
        square: Square,
        expectedFileSymbol: Char
    ) {
        assertEquals(expectedFileSymbol, square.file)
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            A8, 8
            A7, 7
            A6, 6
            A5, 5
            A4, 4
            A3, 3
            A2, 2
            A1, 1
            B8, 8
            B7, 7
            B6, 6
            B5, 5
            B4, 4
            B3, 3
            B2, 2
            B1, 1
            C8, 8
            C7, 7
            C6, 6
            C5, 5
            C4, 4
            C3, 3
            C2, 2
            C1, 1
            D8, 8
            D7, 7
            D6, 6
            D5, 5
            D4, 4
            D3, 3
            D2, 2
            D1, 1
            E8, 8
            E7, 7
            E6, 6
            E5, 5
            E4, 4
            E3, 3
            E2, 2
            E1, 1
            F8, 8
            F7, 7
            F6, 6
            F5, 5
            F4, 4
            F3, 3
            F2, 2
            F1, 1
            G8, 8
            G7, 7
            G6, 6
            G5, 5
            G4, 4
            G3, 3
            G2, 2
            G1, 1
            H8, 8,
            H7, 7,
            H6, 6,
            H5, 5,
            H4, 4,
            H3, 3,
            H2, 2,
            H1, 1"""
    )
    fun `squares should have properly rank number`(
        square: Square,
        expectedRankNumber: Int
    ) {
        assertEquals(expectedRankNumber, square.rank)
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            A8, 0
            A7, 1
            A6, 2
            A5, 3
            A4, 4
            A3, 5
            A2, 6
            A1, 7
            B8, 0
            B7, 1
            B6, 2
            B5, 3
            B4, 4
            B3, 5
            B2, 6
            B1, 7
            C8, 0
            C7, 1
            C6, 2
            C5, 3
            C4, 4
            C3, 5
            C2, 6
            C1, 7
            D8, 0
            D7, 1
            D6, 2
            D5, 3
            D4, 4
            D3, 5
            D2, 6
            D1, 7
            E8, 0
            E7, 1
            E6, 2
            E5, 3
            E4, 4
            E3, 5
            E2, 6
            E1, 7
            F8, 0
            F7, 1
            F6, 2
            F5, 3
            F4, 4
            F3, 5
            F2, 6
            F1, 7
            G8, 0
            G7, 1
            G6, 2
            G5, 3
            G4, 4
            G3, 5
            G2, 6
            G1, 7
            H8, 0,
            H7, 1,
            H6, 2,
            H5, 3,
            H4, 4,
            H3, 5,
            H2, 6,
            H1, 7"""
    )
    fun `squares should have properly row number`(
        square: Square,
        expectedRankNumber: Int
    ) {
        assertEquals(expectedRankNumber, square.row)
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
            A8, 0
            A7, 0
            A6, 0
            A5, 0
            A4, 0
            A3, 0
            A2, 0
            A1, 0
            B8, 1
            B7, 1
            B6, 1
            B5, 1
            B4, 1
            B3, 1
            B2, 1
            B1, 1
            C8, 2
            C7, 2
            C6, 2
            C5, 2
            C4, 2
            C3, 2
            C2, 2
            C1, 2
            D8, 3
            D7, 3
            D6, 3
            D5, 3
            D4, 3
            D3, 3
            D2, 3
            D1, 3
            E8, 4
            E7, 4
            E6, 4
            E5, 4
            E4, 4
            E3, 4
            E2, 4
            E1, 4
            F8, 5
            F7, 5
            F6, 5
            F5, 5
            F4, 5
            F3, 5
            F2, 5
            F1, 5
            G8, 6
            G7, 6
            G6, 6
            G5, 6
            G4, 6
            G3, 6
            G2, 6
            G1, 6
            H8, 7,
            H7, 7,
            H6, 7,
            H5, 7,
            H4, 7,
            H3, 7,
            H2, 7,
            H1, 7"""
    )
    fun `squares should have properly col number`(
        square: Square,
        expectedColNumber: Int
    ) {
        assertEquals(expectedColNumber, square.col)
    }
}

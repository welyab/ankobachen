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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConverter
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class FenTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, 1",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5 11, 11",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 0 16, 16",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -, 1",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5, 1",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b -, 1",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w, 1"
        ]
    )
    fun `full move counter should be parsed properly`(
        fen: String, expectedFullMoveCounter: Int
    ) {
        assertEquals(
            expectedFullMoveCounter,
            FenString(fen).createParser().parse().fullMoveCounter
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5 -11",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 0 9999999999999999999999999",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 --",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - !5"
        ]
    )
    fun `parse should throw FenException when full move counter is invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, 0",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5 11, 5",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 0 16, 0",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -, 0",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5, 5",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0, 0",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - -, 0",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b, 0",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - -, 0"
        ]
    )
    fun `half move clock should be parsed properly`(
        fen: String, expectedFullMoveCounter: Int
    ) {
        assertEquals(
            expectedFullMoveCounter,
            FenString(fen).createParser().parse().halfMoveClock
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5! 11",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 -- 16",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 99999999999999999999999 -",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5x",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -9",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - _-"
        ]
    )
    fun `parse should thrown FenException when half move clock is invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq c6 0 4, c6",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq d3 0 7, d3",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq c6 0 -, c6",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq d3, d3",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq c6, c6"
        ]
    )
    fun `en passant target square should be parsed properlly`(fen: String, square: String) {
        assertEquals(
            FenString(fen).createParser().parse().enPassantTarget,
            Square.from(square)
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq c9 0 4",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq 08 0 7",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq h0 0 -",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq 00",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq --",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq -- 0 4"
        ]
    )
    fun `parse should thrown FenException when en passant target is invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq - 0 4",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq - 0 -",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b KQkq -",
            "rnbqkbnr/pp3ppp/8/1Pppp3/4P3/8/P1PP1PPP/RNBQKBNR w KQkq",
            "rn1qkb1r/pp3ppp/5n2/1P1pp3/2pPP1b1/5NP1/P1P2PBP/RNBQK2R b"
        ]
    )
    fun `en passant target square should be parsed to null`(fen: String) {
        assertNull(FenString(fen).createParser().parse().enPassantTarget)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, H1, A1, H8, A8",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5 11, null, null, null, null",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 0 16, null, null, null, null",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -, H1, A1, H8, A8",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5, null, null, null, null",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b -, null, null, null, null",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w, null, null, null, null",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kkq - 0 1, H1, null, H8, A8",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQq - 0 -, H1, A1, null, A8",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b KQ - 5 11, null, null, null, null",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b kq - 5 11, null, null, null, null",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b Kq, null, null, null, null",
            "1rkqrbn1/pp1b1ppp/2n1p3/2pp4/2PP4/2N5/PP2PPPP/Q1BRKBRN w KQkq - 0 4, G1, D1, E8, B8",
            "r1nqnbkr/1p1b1ppp/1p2p3/2pp4/2PP4/2N1B3/PP1QPPPP/RKR2B1N w KQkq - 0 4, C1, A1, H8, A8",
            "1rkn1bnr/pppppppp/3q4/8/3P4/1B2P3/PPPBQPPP/1NKRN2R w KQkq - 0 1, null, null, H8, B8",
            "2knrbnr/pppppppp/3q4/8/3P4/1B1KP3/PPPBQPPP/RN2N2R w k - 0 1, null, null, null, null"
        ]
    )
    fun `castling flags should be parsed properly`(
        fen: String,
        @ConvertWith(StringToSquareConverter::class) whiteShort: Square?,
        @ConvertWith(StringToSquareConverter::class) whiteLong: Square?,
        @ConvertWith(StringToSquareConverter::class) blackShort: Square?,
        @ConvertWith(StringToSquareConverter::class) blackLong: Square?
    ) {
        FenString(fen).createParser().parse().apply {
            assertEquals(whiteShort, castlingFlags.whiteShort)
            assertEquals(whiteLong, castlingFlags.whiteLong)
            assertEquals(blackShort, castlingFlags.blackShort)
            assertEquals(blackLong, castlingFlags.blackLong)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kK - 0 1",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b x - 5 11",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b -- f3 0 16",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkkq - 0 -",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b 15 - 5",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b 9",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kk1q - 0 1",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KK - 0 -"
        ]
    )
    fun `parse should thrown FenException when castling flags are invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, w",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5 11, b",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R b - f3 0 16, b",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -, w",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R b - - 5, b",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w, w"
        ]
    )
    fun `color to move should be parsed properly`(
        fen: String,
        color: String
    ) {
        assertEquals(
            Color.from(color),
            FenString(fen).createParser().parse().colorToMove
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR - KQkq - 0 1",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R x - - 5 11",
            "r1b4r/p1p3pp/3k4/2bn1pB1/1pPNpP2/P1N4P/1PKQ2B1/R6R 0 - f3 0 16",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR 1 KQkq - 0 -",
            "r1b4r/ppp2ppp/3k1n2/2b1p1B1/2Pn4/2NK1N2/PP1Q1PBP/R6R black - - 5",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR white"
        ]
    )
    fun `parse should throw FenException when color to move is invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, R, a1, P, e2, n, b8, k, e8",
            "rn1qkb1r/pbpp1p1p/1p3np1/4p3/4P3/6P1/PPPPNPBP/RNBQ1RK1 w kq - 2 6, P, e4, Q, d1, b, b7, n, f6",
            "2kr1b1r/pbppqp1p/1pn2np1/4p3/4P3/1PN3P1/PBPPNPBP/R2Q1RK1 w - - 5 9, P, a2, K, g1, k, c8, q, e7"
        ]
    )
    fun `piece disposition should be parsed properly`(
        fen: String,
        piece1: String,
        square1: String,
        piece2: String,
        square2: String,
        piece3: String,
        square3: String,
        piece4: String,
        square4: String
    ) {
        val pieces = FenString(fen).createParser().parse().pieceDisposition
        listOf(
            Pair(Piece.from(piece1), Square.from(square1)),
            Pair(Piece.from(piece2), Square.from(square2)),
            Pair(Piece.from(piece3), Square.from(square3)),
            Pair(Piece.from(piece4), Square.from(square4))
        ).forEach { expected ->
            assertTrue(pieces.any { it.piece == expected.first && it.square == expected.second }) {
                "Expecting ${expected.first} on square ${expected.second}"
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1, a3, b4, c5, d6",
            "rn1qkb1r/pbpp1p1p/1p3np1/4p3/4P3/6P1/PPPPNPBP/RNBQ1RK1 w kq - 2 6, c8, e1, h1, g7",
            "2kr1b1r/pbppqp1p/1pn2np1/4p3/4P3/1PN3P1/PBPPNPBP/R2Q1RK1 w - - 5 9, a8, c1, d5, d6"
        ]
    )
    fun `piece disposition should parse empty square`(
        fen: String,
        square1: String,
        square2: String,
        square3: String,
        square4: String
    ) {
        val pieces = FenString(fen).createParser().parse().pieceDisposition
        listOf(
            Square.from(square1),
            Square.from(square2),
            Square.from(square3),
            Square.from(square4)
        ).forEach { expected ->
            val piece = pieces.firstOrNull { it.square == expected }
            assertNull(piece) {
                "Expecting a empty square in $expected, but found a piece ${piece?.piece}"
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "w KQkq - 0 1",
            "rbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "rn1qkb1r/pbpp1p1p/1p0np1/4p3/4P3/6P1/PPPPNPBP/RNBQ1RK1 w kq - 2 6",
            "rn1qkb1r/pbpp1p1p/1x3np1/4p3/4P3/6P1/PPPPNPBP/RNBQ1RK1 w kq - 2 6",
            "2kr1b1r/pbppqp1p/1pn2np1/4p3/4P3/1PN3P1/PBPPNPBP/RQ1RK1 w - - 5 9",
            "rnbqkbnr/pppppppp/8/8/8/8/RNBQKBNR w KQkq - 0 1"
        ]
    )
    fun `parse piece disposition should throw FenException when is invalid`(fen: String) {
        assertThrows<FenException> { FenString(fen).createParser().parse() }
    }

    private class StringToSquareConverter : ArgumentConverter {
        override fun convert(source: Any, context: ParameterContext?): Any? {
            return if (source != "null") Square.valueOf(source as String) else null
        }
    }
}

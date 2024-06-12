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

package com.welyab.ankobachen.fen

import com.welyab.ankobachen.Color
import com.welyab.ankobachen.Square
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FENTest {

    @Test
    fun `fen string should be parsed with correct color to move`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3")
        Assertions.assertEquals(
            Color.WHITE,
            fen.getColorToMove()
        )
    }

    @Test
    fun `fen string should be parsed with all castling flags`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3")
        Assertions.assertTrue(fen.hasBlackKingSideCastling())
        Assertions.assertTrue(fen.hasBlackQueenSideCastling())
        Assertions.assertTrue(fen.hasWhiteKingSideCastling())
        Assertions.assertTrue(fen.hasWhiteQueenSideCastling())
    }

    @Test
    fun `fen string should be parsed with castling flags for black pieces only`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w kq f6 0 3")
        Assertions.assertTrue(fen.hasBlackKingSideCastling())
        Assertions.assertTrue(fen.hasBlackQueenSideCastling())
        Assertions.assertFalse(fen.hasWhiteKingSideCastling())
        Assertions.assertFalse(fen.hasWhiteQueenSideCastling())
    }

    @Test
    fun `fen string should be parsed with castling flags for white pieces only`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w KQ f6 0 3")
        Assertions.assertFalse(fen.hasBlackKingSideCastling())
        Assertions.assertFalse(fen.hasBlackQueenSideCastling())
        Assertions.assertTrue(fen.hasWhiteKingSideCastling())
        Assertions.assertTrue(fen.hasWhiteQueenSideCastling())
    }

    @Test
    fun `fen string should be parsed with castling flags for white king side and black queen side`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w Kq f6 0 3")
        Assertions.assertFalse(fen.hasBlackKingSideCastling())
        Assertions.assertTrue(fen.hasBlackQueenSideCastling())
        Assertions.assertTrue(fen.hasWhiteKingSideCastling())
        Assertions.assertFalse(fen.hasWhiteQueenSideCastling())
    }

    @Test
    fun `fen string should be parsed with no castling flags`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 0 3")
        Assertions.assertFalse(fen.hasBlackKingSideCastling())
        Assertions.assertFalse(fen.hasBlackQueenSideCastling())
        Assertions.assertFalse(fen.hasWhiteKingSideCastling())
        Assertions.assertFalse(fen.hasWhiteQueenSideCastling())
    }

    @Test
    fun `fen string should be parsed with properly en passant target square`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 0 3")
        Assertions.assertTrue(fen.hasEnPassantTargetSquare())
        Assertions.assertEquals(Square.F6, fen.getEnPassantTargetSquare())
    }

    @Test
    fun `fen string should be parsed with no en passant target square`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - - 0 3")
        Assertions.assertFalse(fen.hasEnPassantTargetSquare())
    }

    @Test
    fun `fen string should be parsed properly half move clock`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 3 17")
        Assertions.assertEquals(3, fen.getHalfMoveClock())
    }

    @Test
    fun `fen string should be parsed properly full move clock`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 3 17")
        Assertions.assertEquals(17, fen.getFullMoveCount())
    }

    @Test
    fun `fen string should be parsed with default values when non required aren't provided`() {
        val fen = FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w")
        assertThrows<FENException> { fen.getEnPassantTargetSquare() }
        Assertions.assertFalse(fen.hasEnPassantTargetSquare())
        Assertions.assertFalse(fen.hasBlackKingSideCastling())
        Assertions.assertFalse(fen.hasBlackQueenSideCastling())
        Assertions.assertFalse(fen.hasWhiteKingSideCastling())
        Assertions.assertFalse(fen.hasWhiteQueenSideCastling())
        Assertions.assertEquals(0, fen.getHalfMoveClock())
        Assertions.assertEquals(1, fen.getFullMoveCount())
    }

    @Test
    fun `fen string should not be parse when en passant target square has invalid file symbol`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - s6 3 17") }
    }

    @Test
    fun `fen string should not be parse when en passant target square has invalid rank number`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f9 3 17") }
    }

    @Test
    fun `fen string should not be parse when en passant target square is not complety`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f") }
    }

    @Test
    fun `fen string should not be parsed when no color to move informed`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR") }
    }

    @Test
    fun `fen string should not be parsed when piece placement is invalid with more than 8 squares in rank`() {
        assertThrows<FENException> { FEN(value = "rnbqk3nr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 3 17") }
    }

    @Test
    fun `fen string should not be parsed when piece placement is invalid with less than 8 squares in rank`() {
        assertThrows<FENException> { FEN(value = "rnbknr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/RNBQKBNR w - f6 3 17") }
    }

    @Test
    fun `fen string should not be parsed when piece placement is invalid with more than 8 ranks`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/8/8/PPPP1PPP/PPPP1PPP/RNBQKBNR w Kq f6 0 3") }
    }

    @Test
    fun `fen string should not be parsed when piece placement is invalid with less than 8 ranks`() {
        assertThrows<FENException> { FEN(value = "rnbqkbnr/pp1pp1pp/8/2p1Pp2/PPPP1PPP/RNBQKBNR w Kq f6 0 3") }
    }
}

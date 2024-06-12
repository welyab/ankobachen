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

import com.welyab.ankobachen.fen.FEN
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class BoardTest {

    @Test
    fun `board created with FEN string should place piece in proper places`() {
        val board = Board(FEN("7Q/4b1p1/3k1r2/8/3P4/2K5/4R1N1/n7 w - - 0 1"))
        Assertions.assertEquals(Piece.WHITE_QUEEN, board.getPiece(Square.H8))
        Assertions.assertEquals(Piece.BLACK_BISHOP, board.getPiece(Square.E7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.G7))
        Assertions.assertEquals(Piece.BLACK_KING, board.getPiece(Square.D6))
        Assertions.assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.F6))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.D4))
        Assertions.assertEquals(Piece.WHITE_KING, board.getPiece(Square.C3))
        Assertions.assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.E2))
        Assertions.assertEquals(Piece.WHITE_KNIGHT, board.getPiece(Square.G2))
        Assertions.assertEquals(Piece.BLACK_KNIGHT, board.getPiece(Square.A1))
    }

    @Test
    fun `default constructor should create a board with initial chess position`() {
        val board = Board()
        Assertions.assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.A8))
        Assertions.assertEquals(Piece.BLACK_KNIGHT, board.getPiece(Square.B8))
        Assertions.assertEquals(Piece.BLACK_BISHOP, board.getPiece(Square.C8))
        Assertions.assertEquals(Piece.BLACK_QUEEN, board.getPiece(Square.D8))
        Assertions.assertEquals(Piece.BLACK_KING, board.getPiece(Square.E8))
        Assertions.assertEquals(Piece.BLACK_BISHOP, board.getPiece(Square.F8))
        Assertions.assertEquals(Piece.BLACK_KNIGHT, board.getPiece(Square.G8))
        Assertions.assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.H8))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.A7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.B7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.C7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.D7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.E7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.F7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.G7))
        Assertions.assertEquals(Piece.BLACK_PAWN, board.getPiece(Square.H7))

        for(square in Square.A6..Square.H3) {
            assertTrue { board.isSquareEmpty(square) }
        }

        Assertions.assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.A1))
        Assertions.assertEquals(Piece.WHITE_KNIGHT, board.getPiece(Square.B1))
        Assertions.assertEquals(Piece.WHITE_BISHOP, board.getPiece(Square.C1))
        Assertions.assertEquals(Piece.WHITE_QUEEN, board.getPiece(Square.D1))
        Assertions.assertEquals(Piece.WHITE_KING, board.getPiece(Square.E1))
        Assertions.assertEquals(Piece.WHITE_BISHOP, board.getPiece(Square.F1))
        Assertions.assertEquals(Piece.WHITE_KNIGHT, board.getPiece(Square.G1))
        Assertions.assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.H1))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.A2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.B2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.C2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.D2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.E2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.F2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.G2))
        Assertions.assertEquals(Piece.WHITE_PAWN, board.getPiece(Square.H2))
    }
}

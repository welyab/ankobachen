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
import org.junit.jupiter.params.provider.ValueSource

class PieceTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "0, WHITE_KING",
            "1, WHITE_QUEEN",
            "2, WHITE_ROOK",
            "3, WHITE_BISHOP",
            "4, WHITE_KNIGHT",
            "5, WHITE_PAWN",
            "6, BLACK_KING",
            "7, BLACK_QUEEN",
            "8, BLACK_ROOK",
            "9, BLACK_BISHOP",
            "10, BLACK_KNIGHT",
            "11, BLACK_PAWN"
        ]
    )
    fun `from index should return properly piece`(pieceIndex: Int, expectedPiece: Piece) {
        assertEquals(expectedPiece, Piece.from(pieceIndex))
    }

    @ParameterizedTest
    @ValueSource(ints = [Int.MIN_VALUE, -2, -1, 12, 13, Int.MAX_VALUE])
    fun `from index should throw PieceException when index is out of bound`(pieceIndex: Int) {
        assertThrows<PieceException> { Piece.from(pieceIndex) }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, K",
            "WHITE_QUEEN, Q",
            "WHITE_ROOK, R",
            "WHITE_BISHOP, B",
            "WHITE_KNIGHT, N",
            "WHITE_PAWN, P",
            "BLACK_KING, k",
            "BLACK_QUEEN, q",
            "BLACK_ROOK, r",
            "BLACK_BISHOP, b",
            "BLACK_KNIGHT, n",
            "BLACK_PAWN, p"
        ]
    )
    fun `piece should have properly char value`(piece: Piece, expectedValue: Char) {
        assertEquals(expectedValue, piece.value)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, KING",
            "WHITE_QUEEN, QUEEN",
            "WHITE_ROOK, ROOK",
            "WHITE_BISHOP, BISHOP",
            "WHITE_KNIGHT, KNIGHT",
            "WHITE_PAWN, PAWN",
            "BLACK_KING, KING",
            "BLACK_QUEEN, QUEEN",
            "BLACK_ROOK, ROOK",
            "BLACK_BISHOP, BISHOP",
            "BLACK_KNIGHT, KNIGHT",
            "BLACK_PAWN, PAWN"
        ]
    )
    fun `piece should have properly type`(piece: Piece, expectedType: PieceType) {
        assertEquals(expectedType, piece.type)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, WHITE",
            "WHITE_QUEEN, WHITE",
            "WHITE_ROOK, WHITE",
            "WHITE_BISHOP, WHITE",
            "WHITE_KNIGHT, WHITE",
            "WHITE_PAWN, WHITE",
            "BLACK_KING, BLACK",
            "BLACK_QUEEN, BLACK",
            "BLACK_ROOK, BLACK",
            "BLACK_BISHOP, BLACK",
            "BLACK_KNIGHT, BLACK",
            "BLACK_PAWN, BLACK"
        ]
    )
    fun `piece should have properly color`(piece: Piece, expectedColor: Color) {
        assertEquals(expectedColor, piece.color)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, K",
            "WHITE_QUEEN, Q",
            "WHITE_ROOK, R",
            "WHITE_BISHOP, B",
            "WHITE_KNIGHT, N",
            "WHITE_PAWN, P",
            "BLACK_KING, k",
            "BLACK_QUEEN, q",
            "BLACK_ROOK, r",
            "BLACK_BISHOP, b",
            "BLACK_KNIGHT, n",
            "BLACK_PAWN, p",
        ]
    )
    fun `fun should parse piece string properly`(expected: Piece, piece: String) {
        assertEquals(expected, Piece.from(piece))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, K",
            "WHITE_QUEEN, Q",
            "WHITE_ROOK, R",
            "WHITE_BISHOP, B",
            "WHITE_KNIGHT, N",
            "WHITE_PAWN, P",
            "BLACK_KING, k",
            "BLACK_QUEEN, q",
            "BLACK_ROOK, r",
            "BLACK_BISHOP, b",
            "BLACK_KNIGHT, n",
            "BLACK_PAWN, p",
        ]
    )
    fun `fun should parse piece char properly`(expected: Piece, piece: Char) {
        assertEquals(expected, Piece.from(piece))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, true",
            "WHITE_QUEEN, true",
            "WHITE_ROOK, true",
            "WHITE_BISHOP, true",
            "WHITE_KNIGHT, true",
            "WHITE_PAWN, true",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhite should return true when color is white`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhite)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, true",
            "BLACK_QUEEN, true",
            "BLACK_ROOK, true",
            "BLACK_BISHOP, true",
            "BLACK_KNIGHT, true",
            "BLACK_PAWN, true"
        ]
    )
    fun `piece isBlack should return true when color is black`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlack)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, true",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, true",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isKing should return true when piece is king`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isKing)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, true",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, true",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isQueen should return true when piece is queen`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isQueen)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, true",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, true",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isRook should return true when piece is rook`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isRook)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, true",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, true",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBishop should return true when piece is bishop`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBishop)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, true",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, true",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isKnight should return true when piece is knight`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isKnight)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, true",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, true"
        ]
    )
    fun `piece isPawn should return true when piece is pawn`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isPawn)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, true",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhiteKing should return true when piece is white king`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhiteKing)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, true",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhiteQueen should return true when piece is white queen`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhiteQueen)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, true",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhiteRook should return true when piece is white rook`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhiteRook)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, true",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhiteBishop should return true when piece is white bishop`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhiteBishop)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, true",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhiteKnight should return true when piece is white knight`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhiteKnight)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, true",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isWhitePawn should return true when piece is white pawn`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isWhitePawn)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, true",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBlackKing should return true when piece is black king`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackKing)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, true",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBlackQueen should return true when piece is black queen`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackQueen)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, true",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBlackRook should return true when piece is black rook`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackRook)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, true",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBlackBishop should return true when piece is black bishop`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackBishop)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, true",
            "BLACK_PAWN, false"
        ]
    )
    fun `piece isBlackKnight should return true when piece is black knight`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackKnight)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "WHITE_KING, false",
            "WHITE_QUEEN, false",
            "WHITE_ROOK, false",
            "WHITE_BISHOP, false",
            "WHITE_KNIGHT, false",
            "WHITE_PAWN, false",
            "BLACK_KING, false",
            "BLACK_QUEEN, false",
            "BLACK_ROOK, false",
            "BLACK_BISHOP, false",
            "BLACK_KNIGHT, false",
            "BLACK_PAWN, true"
        ]
    )
    fun `piece isBlackPawn should return true when piece is black pawn`(piece: Piece, expectedValue: Boolean) {
        assertEquals(expectedValue, piece.isBlackPawn)
    }
}

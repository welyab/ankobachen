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
import org.junit.jupiter.params.provider.ValueSource

class PieceTypeTest {

    @ParameterizedTest
    @CsvSource(
        "KING, true",
        "QUEEN, false",
        "ROOK, false",
        "BISHOP, false",
        "KNIGHT, false",
        "PAWN, false"
    )
    fun `isKing should return true when piece type is king and false when isn't`(
        pieceType: PieceType,
        expectedIsKing: Boolean
    ) {
        Assertions.assertEquals(expectedIsKing, pieceType.isKing)
    }

    @ParameterizedTest
    @CsvSource(
        "KING, false",
        "QUEEN, true",
        "ROOK, false",
        "BISHOP, false",
        "KNIGHT, false",
        "PAWN, false"
    )
    fun `isQueen should return true when piece type is queen and false when isn't`(
        pieceType: PieceType,
        expectedIsQueen: Boolean
    ) {
        Assertions.assertEquals(expectedIsQueen, pieceType.isQueen)
    }

    @ParameterizedTest
    @CsvSource(
        "KING, false",
        "QUEEN, false",
        "ROOK, true",
        "BISHOP, false",
        "KNIGHT, false",
        "PAWN, false"
    )
    fun `isRook should return true when piece type is rook and false when isn't`(
        pieceType: PieceType,
        expectedIsRook: Boolean
    ) {
        Assertions.assertEquals(expectedIsRook, pieceType.isRook)
    }

    @ParameterizedTest
    @CsvSource(
        "KING, false",
        "QUEEN, false",
        "ROOK, false",
        "BISHOP, true",
        "KNIGHT, false",
        "PAWN, false"
    )
    fun `isBishop should return true when piece type is bishop and false when isn't`(
        pieceType: PieceType,
        expectedIsBishop: Boolean
    ) {
        Assertions.assertEquals(expectedIsBishop, pieceType.isBishop)
    }

    @ParameterizedTest
    @CsvSource(
        "KING, false",
        "QUEEN, false",
        "ROOK, false",
        "BISHOP, false",
        "KNIGHT, true",
        "PAWN, false"
    )
    fun `isKnight should return true when piece type is knight and false when isn't`(
        pieceType: PieceType,
        expectedIsKnight: Boolean
    ) {
        Assertions.assertEquals(expectedIsKnight, pieceType.isKnight)
    }

    @ParameterizedTest
    @CsvSource(
        "KING, false",
        "QUEEN, false",
        "ROOK, false",
        "BISHOP, false",
        "KNIGHT, false",
        "PAWN, true"
    )
    fun `isPawn should return true when piece type is pawn and false when isn't`(
        pieceType: PieceType,
        expectedIsPawn: Boolean
    ) {
        Assertions.assertEquals(expectedIsPawn, pieceType.isPawn)
    }
}

class PieceTest {

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `each piece should have its specific letter identification`(piece: Piece, expectedLetter: Char) {
        Assertions.assertEquals(expectedLetter, piece.letter)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `each piece should have its specific color `(piece: Piece, expectedColor: Color) {
        Assertions.assertEquals(expectedColor, piece.color)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `each piece should have its specific type`(piece: Piece, expectedType: PieceType) {
        Assertions.assertEquals(expectedType, piece.type)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isKing should return true when piece is king and false when isn't`(
        piece: Piece,
        expectedIsKing: Boolean
    ) {
        Assertions.assertEquals(expectedIsKing, piece.isKing)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isQueen should return true when piece is queen and false when isn't`(
        piece: Piece,
        expectedIsQueen: Boolean
    ) {
        Assertions.assertEquals(expectedIsQueen, piece.isQueen)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isRook should return true when piece is rook and false when isn't`(
        piece: Piece,
        expectedIsRook: Boolean
    ) {
        Assertions.assertEquals(expectedIsRook, piece.isRook)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBishop should return true when piece is bishop and false when isn't`(
        piece: Piece,
        expectedIsBishop: Boolean
    ) {
        Assertions.assertEquals(expectedIsBishop, piece.isBishop)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isKnight should return true when piece is knight and false when isn't`(
        piece: Piece,
        expectedIsKnight: Boolean
    ) {
        Assertions.assertEquals(expectedIsKnight, piece.isKnight)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isPawn should return true when piece is pawn and false when isn't`(
        piece: Piece,
        expectedIsPawn: Boolean
    ) {
        Assertions.assertEquals(expectedIsPawn, piece.isPawn)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhiteKing should return true when piece is white king and false when isn't`(
        piece: Piece,
        expectedIsWhiteKing: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhiteKing, piece.isWhiteKing)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhiteQueen should return true when piece is white queen and false when isn't`(
        piece: Piece,
        expectedIsWhiteQueen: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhiteQueen, piece.isWhiteQueen)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhiteRook should return true when piece is white rook and false when isn't`(
        piece: Piece,
        expectedIsWhiteRook: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhiteRook, piece.isWhiteRook)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhiteBishop should return true when piece is white bishop and false when isn't`(
        piece: Piece,
        expectedIsWhiteBishop: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhiteBishop, piece.isWhiteBishop)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhiteKnight should return true when piece is white knight and false when isn't`(
        piece: Piece,
        expectedIsWhiteKnight: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhiteKnight, piece.isWhiteKnight)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhitePawn should return true when piece is white pawn and false when isn't`(
        piece: Piece,
        expectedIsWhitePawn: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhitePawn, piece.isWhitePawn)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackKing should return true when piece is black king and false when isn't`(
        piece: Piece,
        expectedIsBlackKing: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackKing, piece.isBlackKing)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackQueen should return true when piece is black queen and false when isn't`(
        piece: Piece,
        expectedIsBlackQueen: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackQueen, piece.isBlackQueen)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackRook should return true when piece is black rook and false when isn't`(
        piece: Piece,
        expectedIsBlackRook: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackRook, piece.isBlackRook)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackBishop should return true when piece is black bishop and false when isn't`(
        piece: Piece,
        expectedIsBlackBishop: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackBishop, piece.isBlackBishop)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackKnight should return true when piece is black knight and false when isn't`(
        piece: Piece,
        expectedIsBlackKnight: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackKnight, piece.isBlackKnight)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlackPawn should return true when piece is black pawn and false when isn't`(
        piece: Piece,
        expectedIsBlackPawn: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlackPawn, piece.isBlackPawn)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isWhite should return true when piece is white and false when isn't`(
        piece: Piece,
        expectedIsWhite: Boolean
    ) {
        Assertions.assertEquals(expectedIsWhite, piece.isWhite)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `isBlack should return true when piece is black and false when isn't`(
        piece: Piece,
        expectedIsBlack: Boolean
    ) {
        Assertions.assertEquals(expectedIsBlack, piece.isBlack)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "WHITE_KING",
            "WHITE_QUEEN",
            "WHITE_ROOK",
            "WHITE_BISHOP",
            "WHITE_KNIGHT",
            "WHITE_PAWN",
            "BLACK_KING",
            "BLACK_QUEEN",
            "BLACK_ROOK",
            "BLACK_BISHOP",
            "BLACK_KNIGHT",
            "BLACK_PAWN"
        ]
    )
    fun `isEmpty should always return false`(piece: Piece) {
        Assertions.assertFalse(piece.isEmpty)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "WHITE_KING",
            "WHITE_QUEEN",
            "WHITE_ROOK",
            "WHITE_BISHOP",
            "WHITE_KNIGHT",
            "WHITE_PAWN",
            "BLACK_KING",
            "BLACK_QUEEN",
            "BLACK_ROOK",
            "BLACK_BISHOP",
            "BLACK_KNIGHT",
            "BLACK_PAWN"
        ]
    )
    fun `isNotEmpty should always return true`(piece: Piece) {
        Assertions.assertTrue(piece.isNotEmpty)
    }

    @ParameterizedTest
    @CsvSource(
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
    )
    fun `from should return proper piece for given piece letter`(expectedPiece: Piece, pieceLetter: Char) {
        Assertions.assertEquals(expectedPiece, Piece.from(pieceLetter))
    }

    @Test
    fun `from should throw PieceException whe piece letter is invalid`() {
        assertThrows<PieceException> {
            Piece.from('x')
        }
    }

    @ParameterizedTest
    @CsvSource(
        "WHITE, KING, WHITE_KING",
        "WHITE, QUEEN, WHITE_QUEEN",
        "WHITE, ROOK, WHITE_ROOK",
        "WHITE, BISHOP, WHITE_BISHOP",
        "WHITE, KNIGHT, WHITE_KNIGHT",
        "WHITE, PAWN, WHITE_PAWN",
        "BLACK, KING, BLACK_KING",
        "BLACK, QUEEN, BLACK_QUEEN",
        "BLACK, ROOK, BLACK_ROOK",
        "BLACK, BISHOP, BLACK_BISHOP",
        "BLACK, KNIGHT, BLACK_KNIGHT",
        "BLACK, PAWN, BLACK_PAWN"
    )
    fun `from should return proper piece when given piece type and color`(
        color: Color,
        pieceType: PieceType,
        expectedPiece: Piece
    ) {
        Assertions.assertEquals(expectedPiece, Piece.from(pieceType, color))
    }

    @ParameterizedTest
    @CsvSource(
        "WHITE_KING, WHITE, true",
        "WHITE_QUEEN, WHITE, true",
        "WHITE_ROOK, WHITE, true",
        "WHITE_BISHOP, WHITE, true",
        "WHITE_KNIGHT, WHITE, true",
        "WHITE_PAWN, WHITE, true",
        "BLACK_KING, WHITE, false",
        "BLACK_QUEEN, WHITE, false",
        "BLACK_ROOK, WHITE, false",
        "BLACK_BISHOP, WHITE, false",
        "BLACK_KNIGHT, WHITE, false",
        "BLACK_PAWN, WHITE, false",
        "WHITE_KING, BLACK, false",
        "WHITE_QUEEN, BLACK, false",
        "WHITE_ROOK, BLACK, false",
        "WHITE_BISHOP, BLACK, false",
        "WHITE_KNIGHT, BLACK, false",
        "WHITE_PAWN, BLACK, false",
        "BLACK_KING, BLACK, true",
        "BLACK_QUEEN, BLACK, true",
        "BLACK_ROOK, BLACK, true",
        "BLACK_BISHOP, BLACK, true",
        "BLACK_KNIGHT, BLACK, true",
        "BLACK_PAWN, BLACK, true"
    )
    fun `isColorOf should return properly value`(piece: Piece, color: Color, expectedValue: Boolean) {
        Assertions.assertEquals(expectedValue, piece.isColorOf(color))
    }
}

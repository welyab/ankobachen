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

import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.PAWN
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK

class PieceException(
    message: String,
    cause: Throwable? = null
) : ChessException(
    message,
    cause
)

enum class Piece(
    val type: PieceType,
    val color: Color,
    val value: Char
) {
    WHITE_KING(KING, WHITE, Piece.WHITE_KING_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = true
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = true
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    WHITE_QUEEN(QUEEN, WHITE, Piece.WHITE_QUEEN_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = false
        override val isQueen = true
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = true
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    WHITE_ROOK(ROOK, WHITE, Piece.WHITE_ROOK_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = false
        override val isQueen = false
        override val isRook = true
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = true
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    WHITE_BISHOP(BISHOP, WHITE, Piece.WHITE_BISHOP_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = true
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = true
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    WHITE_KNIGHT(KNIGHT, WHITE, Piece.WHITE_KNIGHT_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = true
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = true
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    WHITE_PAWN(PAWN, WHITE, Piece.WHITE_PAWN_CHAR) {
        override val isWhite = true
        override val isBlack = false
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = true
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = true
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    BLACK_KING(KING, BLACK, Piece.BLACK_KING_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = true
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = true
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    BLACK_QUEEN(QUEEN, BLACK, Piece.BLACK_QUEEN_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = false
        override val isQueen = true
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = true
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    BLACK_ROOK(ROOK, BLACK, Piece.BLACK_ROOK_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = false
        override val isQueen = false
        override val isRook = true
        override val isBishop = false
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = true
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    BLACK_BISHOP(BISHOP, BLACK, Piece.BLACK_BISHOP_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = true
        override val isKnight = false
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = true
        override val isBlackKnight = false
        override val isBlackPawn = false
    },

    BLACK_KNIGHT(KNIGHT, BLACK, Piece.BLACK_KNIGHT_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = true
        override val isPawn = false
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = true
        override val isBlackPawn = false
    },

    BLACK_PAWN(PAWN, BLACK, Piece.BLACK_PAWN_CHAR) {
        override val isWhite = false
        override val isBlack = true
        override val isKing = false
        override val isQueen = false
        override val isRook = false
        override val isBishop = false
        override val isKnight = false
        override val isPawn = true
        override val isWhiteKing = false
        override val isWhiteQueen = false
        override val isWhiteRook = false
        override val isWhiteBishop = false
        override val isWhiteKnight = false
        override val isWhitePawn = false
        override val isBlackKing = false
        override val isBlackQueen = false
        override val isBlackRook = false
        override val isBlackBishop = false
        override val isBlackKnight = false
        override val isBlackPawn = true
    };

    val index: Int get() = ordinal

    abstract val isWhite: Boolean
    abstract val isBlack: Boolean

    abstract val isKing: Boolean
    abstract val isQueen: Boolean
    abstract val isRook: Boolean
    abstract val isBishop: Boolean
    abstract val isKnight: Boolean
    abstract val isPawn: Boolean

    abstract val isWhiteKing: Boolean
    abstract val isWhiteQueen: Boolean
    abstract val isWhiteRook: Boolean
    abstract val isWhiteBishop: Boolean
    abstract val isWhiteKnight: Boolean
    abstract val isWhitePawn: Boolean

    abstract val isBlackKing: Boolean
    abstract val isBlackQueen: Boolean
    abstract val isBlackRook: Boolean
    abstract val isBlackBishop: Boolean
    abstract val isBlackKnight: Boolean
    abstract val isBlackPawn: Boolean


    companion object {

        const val WHITE_KING_CHAR = 'K'
        const val WHITE_QUEEN_CHAR = 'Q'
        const val WHITE_ROOK_CHAR = 'R'
        const val WHITE_BISHOP_CHAR = 'B'
        const val WHITE_KNIGHT_CHAR = 'N'
        const val WHITE_PAWN_CHAR = 'P'
        const val BLACK_KING_CHAR = 'k'
        const val BLACK_QUEEN_CHAR = 'q'
        const val BLACK_ROOK_CHAR = 'r'
        const val BLACK_BISHOP_CHAR = 'b'
        const val BLACK_KNIGHT_CHAR = 'n'
        const val BLACK_PAWN_CHAR = 'p'

        private val pieces = values()

        fun from(index: Int): Piece {
            try {
                return pieces[index]
            } catch (e: IndexOutOfBoundsException) {
                throw PieceException(
                    "Invalid piece index: $index. Use one of [0, 11]",
                    e
                )
            }
        }

        fun from(piece: String): Piece {
            if (piece.length != 1) throw PieceException(
                "Invalid piece: $piece. Use one of K, Q, R, B, N, P, k, q, r, b, n, p"
            )
            return from(piece[0])
        }

        fun from(piece: Char): Piece {
            return when (piece) {
                WHITE_KING_CHAR -> WHITE_KING
                WHITE_QUEEN_CHAR -> WHITE_QUEEN
                WHITE_ROOK_CHAR -> WHITE_ROOK
                WHITE_BISHOP_CHAR -> WHITE_BISHOP
                WHITE_KNIGHT_CHAR -> WHITE_KNIGHT
                WHITE_PAWN_CHAR -> WHITE_PAWN
                BLACK_KING_CHAR -> BLACK_KING
                BLACK_QUEEN_CHAR -> BLACK_QUEEN
                BLACK_ROOK_CHAR -> BLACK_ROOK
                BLACK_BISHOP_CHAR -> BLACK_BISHOP
                BLACK_KNIGHT_CHAR -> BLACK_KNIGHT
                BLACK_PAWN_CHAR -> BLACK_PAWN
                else -> throw PieceException(
                    "Invalid piece: $piece. Use one of K, Q, R, B, N, P, k, q, r, b, n, p"
                )
            }
        }
    }
}

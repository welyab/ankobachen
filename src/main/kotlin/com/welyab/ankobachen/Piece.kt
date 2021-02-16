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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.welyab.ankobachen

import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.PAWN
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK

class PieceException(message: String, cause: Throwable? = null) : ChessException(message, cause)

enum class PieceType(val letter: Char) {
    KING(PieceType.KING_LETTER),
    QUEEN(PieceType.QUEEN_LETTER),
    ROOK(PieceType.ROOK_LETTER),
    BISHOP(PieceType.BISHOP_LETTER),
    KNIGHT(PieceType.KNIGHT_LETTER),
    PAWN(PieceType.PAWN_LETTER);

    val isKing get() = this == KING
    val isQueen get() = this == QUEEN
    val isRook get() = this == ROOK
    val isBishop get() = this == BISHOP
    val isKnight get() = this == KNIGHT
    val isPawn get() = this == PAWN

    override fun toString() =
        name.toLowerCase().capitalize()

    @Suppress("unused")
    companion object {

        const val KING_LETTER = 'K'
        const val QUEEN_LETTER = 'Q'
        const val ROOK_LETTER = 'R'
        const val BISHOP_LETTER = 'B'
        const val KNIGHT_LETTER = 'N'
        const val PAWN_LETTER = 'P'

        fun from(letter: Char) =
            when (letter) {
                KING_LETTER -> KING
                QUEEN_LETTER -> QUEEN
                ROOK_LETTER -> ROOK
                BISHOP_LETTER -> BISHOP
                KNIGHT_LETTER -> KNIGHT
                PAWN_LETTER -> PAWN
                else -> throw PieceException("Invalid piece type letter: $letter")
            }

        fun isPieceTypeLetter(letter: Char) =
            values().any { it.letter == letter }
    }
}

@Suppress("unused")
enum class Piece(
    val type: PieceType,
    val color: Color,
    override val letter: Char
) : SquareContent<Piece> {

    WHITE_KING(KING, WHITE, Piece.WHITE_KING_LETTER) {
        override val opposite: Piece get() = BLACK_KING
    },
    WHITE_QUEEN(QUEEN, WHITE, Piece.WHITE_QUEEN_LETTER) {
        override val opposite: Piece get() = BLACK_QUEEN
    },
    WHITE_ROOK(ROOK, WHITE, Piece.WHITE_ROOK_LETTER) {
        override val opposite: Piece get() = BLACK_ROOK
    },
    WHITE_BISHOP(BISHOP, WHITE, Piece.WHITE_BISHOP_LETTER) {
        override val opposite: Piece get() = BLACK_BISHOP
    },
    WHITE_KNIGHT(KNIGHT, WHITE, Piece.WHITE_KNIGHT_LETTER) {
        override val opposite: Piece get() = BLACK_KNIGHT
    },
    WHITE_PAWN(PAWN, WHITE, Piece.WHITE_PAWN_LETTER) {
        override val opposite: Piece get() = BLACK_PAWN
    },
    BLACK_KING(KING, BLACK, Piece.BLACK_KING_LETTER) {
        override val opposite: Piece get() = WHITE_KING
    },
    BLACK_QUEEN(QUEEN, BLACK, Piece.BLACK_QUEEN_LETTER) {
        override val opposite: Piece get() = WHITE_QUEEN
    },
    BLACK_ROOK(ROOK, BLACK, Piece.BLACK_ROOK_LETTER) {
        override val opposite: Piece get() = WHITE_ROOK
    },
    BLACK_BISHOP(BISHOP, BLACK, Piece.BLACK_BISHOP_LETTER) {
        override val opposite: Piece get() = WHITE_BISHOP
    },
    BLACK_KNIGHT(KNIGHT, BLACK, Piece.BLACK_KNIGHT_LETTER) {
        override val opposite: Piece get() = WHITE_KNIGHT
    },
    BLACK_PAWN(PAWN, BLACK, Piece.BLACK_PAWN_LETTER) {
        override val opposite: Piece get() = WHITE_PAWN
    };

    override val isKing = type.isKing
    override val isQueen = type.isQueen
    override val isRook = type.isRook
    override val isBishop = type.isBishop
    override val isKnight = type.isKnight
    override val isPawn = type.isPawn

    override val isWhiteKing get() = this == WHITE_KING
    override val isWhiteQueen get() = this == WHITE_QUEEN
    override val isWhiteRook get() = this == WHITE_ROOK
    override val isWhiteBishop get() = this == WHITE_BISHOP
    override val isWhiteKnight get() = this == WHITE_KNIGHT
    override val isWhitePawn get() = this == WHITE_PAWN

    override val isBlackKing get() = this == BLACK_KING
    override val isBlackQueen get() = this == BLACK_QUEEN
    override val isBlackRook get() = this == BLACK_ROOK
    override val isBlackBishop get() = this == BLACK_BISHOP
    override val isBlackKnight get() = this == BLACK_KNIGHT
    override val isBlackPawn get() = this == BLACK_PAWN

    override val isWhite = color.isWhite
    override val isBlack = color.isBlack

    override val isEmpty = false
    override val isNotEmpty = true

    abstract val opposite: Piece

    val king: Piece
        get() = when (color) {
            WHITE -> WHITE_KING
            BLACK -> BLACK_KING
        }

    val queen: Piece
        get() = when (color) {
            WHITE -> WHITE_QUEEN
            BLACK -> BLACK_QUEEN
        }

    val rook: Piece
        get() = when (color) {
            WHITE -> WHITE_ROOK
            BLACK -> BLACK_ROOK
        }

    val bishop: Piece
        get() = when (color) {
            WHITE -> WHITE_BISHOP
            BLACK -> BLACK_BISHOP
        }

    val knight: Piece
        get() = when (color) {
            WHITE -> WHITE_KNIGHT
            BLACK -> BLACK_KNIGHT
        }

    val pawn: Piece
        get() = when (color) {
            WHITE -> WHITE_PAWN
            BLACK -> BLACK_PAWN
        }

    val oppositeKing get() = opposite.king
    val oppositeQueen get() = opposite.queen
    val oppositeRook get() = opposite.rook
    val oppositeBishop get() = opposite.bishop
    val oppositeKnight get() = opposite.knight
    val oppositePawn get() = opposite.pawn

    override fun isColorOf(color: Color) = this.color == color

    override fun isPieceOf(piece: Piece) = this == piece

    override fun copy() = this

    override fun asPiece() = this

    override fun toString() = "$color $type"

    companion object {

        const val WHITE_KING_LETTER = 'K'
        const val WHITE_QUEEN_LETTER = 'Q'
        const val WHITE_ROOK_LETTER = 'R'
        const val WHITE_BISHOP_LETTER = 'B'
        const val WHITE_KNIGHT_LETTER = 'N'
        const val WHITE_PAWN_LETTER = 'P'

        const val BLACK_KING_LETTER = 'k'
        const val BLACK_QUEEN_LETTER = 'q'
        const val BLACK_ROOK_LETTER = 'r'
        const val BLACK_BISHOP_LETTER = 'b'
        const val BLACK_KNIGHT_LETTER = 'n'
        const val BLACK_PAWN_LETTER = 'p'

        fun from(letter: Char) = when (letter) {
            WHITE_KING_LETTER -> WHITE_KING
            WHITE_QUEEN_LETTER -> WHITE_QUEEN
            WHITE_ROOK_LETTER -> WHITE_ROOK
            WHITE_BISHOP_LETTER -> WHITE_BISHOP
            WHITE_KNIGHT_LETTER -> WHITE_KNIGHT
            WHITE_PAWN_LETTER -> WHITE_PAWN
            BLACK_KING_LETTER -> BLACK_KING
            BLACK_QUEEN_LETTER -> BLACK_QUEEN
            BLACK_ROOK_LETTER -> BLACK_ROOK
            BLACK_BISHOP_LETTER -> BLACK_BISHOP
            BLACK_KNIGHT_LETTER -> BLACK_KNIGHT
            BLACK_PAWN_LETTER -> BLACK_PAWN
            else -> throw PieceException("Invalid piece letter: $letter")
        }

        fun from(type: PieceType, color: Color): Piece =
            when (color) {
                WHITE -> when (type) {
                    KING -> WHITE_KING
                    QUEEN -> WHITE_QUEEN
                    ROOK -> WHITE_ROOK
                    BISHOP -> WHITE_BISHOP
                    KNIGHT -> WHITE_KNIGHT
                    PAWN -> WHITE_PAWN
                }
                BLACK -> when (type) {
                    KING -> BLACK_KING
                    QUEEN -> BLACK_QUEEN
                    ROOK -> BLACK_ROOK
                    BISHOP -> BLACK_BISHOP
                    KNIGHT -> BLACK_KNIGHT
                    PAWN -> BLACK_PAWN
                }
            }

        fun isPieceLetter(char: Char): Boolean {
            return when (char) {
                WHITE_KING_LETTER,
                WHITE_QUEEN_LETTER,
                WHITE_ROOK_LETTER,
                WHITE_BISHOP_LETTER,
                WHITE_KNIGHT_LETTER,
                WHITE_PAWN_LETTER,
                BLACK_KING_LETTER,
                BLACK_QUEEN_LETTER,
                BLACK_ROOK_LETTER,
                BLACK_BISHOP_LETTER,
                BLACK_KNIGHT_LETTER,
                BLACK_PAWN_LETTER -> true
                else -> false
            }
        }
    }
}

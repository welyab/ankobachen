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

import com.welyab.ankobachen.PieceSymbol.BLACK_BISHOP_SYMBOL
import com.welyab.ankobachen.PieceSymbol.BLACK_KING_SYMBOL
import com.welyab.ankobachen.PieceSymbol.BLACK_KNIGHT_SYMBOL
import com.welyab.ankobachen.PieceSymbol.BLACK_PAWN_SYMBOL
import com.welyab.ankobachen.PieceSymbol.BLACK_QUEEN_SYMBOL
import com.welyab.ankobachen.PieceSymbol.BLACK_ROOK_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_BISHOP_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_KING_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_KNIGHT_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_PAWN_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_QUEEN_SYMBOL
import com.welyab.ankobachen.PieceSymbol.WHITE_ROOK_SYMBOL

object PieceSymbol {
    const val BLACK_KING_SYMBOL = 'k'
    const val BLACK_QUEEN_SYMBOL = 'q'
    const val BLACK_ROOK_SYMBOL = 'r'
    const val BLACK_BISHOP_SYMBOL = 'b'
    const val BLACK_KNIGHT_SYMBOL = 'n'
    const val BLACK_PAWN_SYMBOL = 'p'

    const val WHITE_KING_SYMBOL = 'K'
    const val WHITE_QUEEN_SYMBOL = 'Q'
    const val WHITE_ROOK_SYMBOL = 'R'
    const val WHITE_BISHOP_SYMBOL = 'B'
    const val WHITE_KNIGHT_SYMBOL = 'N'
    const val WHITE_PAWN_SYMBOL = 'P'
}

enum class Piece(
    val type: PieceType,
    val color: Color,
    val symbol: Char,
) {
    BLACK_PAWN(
        type = PieceType.PAWN,
        color = Color.BLACK,
        symbol = BLACK_PAWN_SYMBOL
    ),
    BLACK_KNIGHT(
        type = PieceType.KNIGHT,
        color = Color.BLACK,
        symbol = BLACK_KNIGHT_SYMBOL
    ),
    BLACK_BISHOP(
        type = PieceType.BISHOP,
        color = Color.BLACK,
        symbol = BLACK_BISHOP_SYMBOL
    ),
    BLACK_ROOK(
        type = PieceType.ROOK,
        color = Color.BLACK,
        symbol = BLACK_ROOK_SYMBOL
    ),
    BLACK_QUEEN(
        type = PieceType.QUEEN,
        color = Color.BLACK,
        symbol = BLACK_QUEEN_SYMBOL
    ),
    BLACK_KING(
        type = PieceType.KING,
        color = Color.BLACK,
        symbol = BLACK_KING_SYMBOL
    ),
    WHITE_PAWN(
        type = PieceType.PAWN,
        color = Color.WHITE,
        symbol = WHITE_PAWN_SYMBOL
    ),
    WHITE_KNIGHT(
        type = PieceType.KNIGHT,
        color = Color.WHITE,
        symbol = WHITE_KNIGHT_SYMBOL
    ),
    WHITE_BISHOP(
        type = PieceType.BISHOP,
        color = Color.WHITE,
        symbol = WHITE_BISHOP_SYMBOL
    ),
    WHITE_ROOK(
        type = PieceType.ROOK,
        color = Color.WHITE,
        symbol = WHITE_ROOK_SYMBOL
    ),
    WHITE_QUEEN(
        type = PieceType.QUEEN,
        color = Color.WHITE,
        symbol = WHITE_QUEEN_SYMBOL
    ),
    WHITE_KING(
        type = PieceType.KING,
        color = Color.WHITE,
        symbol = WHITE_KING_SYMBOL
    );

    fun getIndex(): Int = ordinal

    companion object {

        private val piecesByTypeIndexAndColorIndex = Array(
            Color.entries.size
        ) { colorIndex ->
            Array(
                PieceType.entries.size
            ) { pieceTypeIndex ->
                Piece.entries.first {
                    it.type.getIndex() == pieceTypeIndex
                            && it.color.getIndex() == colorIndex
                }
            }
        }

        fun from(
            colorIndex: Int,
            pieceTypeIndex: Int,
        ): Piece = piecesByTypeIndexAndColorIndex[colorIndex][pieceTypeIndex]

        fun fromSymbol(
            symbol: Char
        ): Piece = when (symbol) {
            BLACK_PAWN.symbol -> BLACK_PAWN
            WHITE_PAWN.symbol -> WHITE_PAWN
            BLACK_KNIGHT.symbol -> BLACK_KNIGHT
            BLACK_BISHOP.symbol -> BLACK_BISHOP
            BLACK_ROOK.symbol -> BLACK_ROOK
            BLACK_QUEEN.symbol -> BLACK_QUEEN
            BLACK_KING.symbol -> BLACK_KING
            WHITE_KNIGHT.symbol -> WHITE_KNIGHT
            WHITE_BISHOP.symbol -> WHITE_BISHOP
            WHITE_ROOK.symbol -> WHITE_ROOK
            WHITE_QUEEN.symbol -> WHITE_QUEEN
            WHITE_KING.symbol -> WHITE_KING
            else -> throw IllegalArgumentException("unknown piece symbol: $symbol")
        }
    }
}

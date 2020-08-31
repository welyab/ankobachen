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
package com.welyab.ankobachen

interface SquareContent<out T : SquareContent<T>> : Copyable<T> {
    val letter: Char

    val isKing: Boolean
    val isQueen: Boolean
    val isRook: Boolean
    val isBishop: Boolean
    val isKnight: Boolean
    val isPawn: Boolean

    val isWhiteKing: Boolean
    val isWhiteQueen: Boolean
    val isWhiteRook: Boolean
    val isWhiteBishop: Boolean
    val isWhiteKnight: Boolean
    val isWhitePawn: Boolean

    val isBlackKing: Boolean
    val isBlackQueen: Boolean
    val isBlackRook: Boolean
    val isBlackBishop: Boolean
    val isBlackKnight: Boolean
    val isBlackPawn: Boolean

    val isWhite: Boolean
    val isBlack: Boolean

    val isEmpty: Boolean
    val isNotEmpty: Boolean

    fun isKingOfColor(color: Color) = isKing && isColorOf(color)
    fun isQueenOfColor(color: Color) = isQueen && isColorOf(color)
    fun isRookOfColor(color: Color) = isRook && isColorOf(color)
    fun isBishopOfColor(color: Color) = isBishop && isColorOf(color)
    fun isKnightOfColor(color: Color) = isKnight && isColorOf(color)
    fun isPawnOfColor(color: Color) = isPawn && isColorOf(color)

    fun isColorOf(color: Color): Boolean

    fun isPieceOf(piece: Piece): Boolean
    fun isNotPieceOf(piece: Piece): Boolean = !isPieceOf(piece)

    fun asPiece(): Piece
}

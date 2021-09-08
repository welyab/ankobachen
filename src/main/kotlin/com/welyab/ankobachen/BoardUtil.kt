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

class FenBoardProperties(fenString: FenString) {

    private val fenInfo = fenString.createParser().parse()

    fun getCastlingFlags() = or(
        fenInfo.castlingFlags.whiteShort?.bitboard ?: EMPTY,
        fenInfo.castlingFlags.whiteLong?.bitboard ?: EMPTY,
        fenInfo.castlingFlags.blackShort?.bitboard ?: EMPTY,
        fenInfo.castlingFlags.blackLong?.bitboard ?: EMPTY
    )

    fun getEpTargetSquare() = fenInfo.enPassantTarget?.bitboard ?: EMPTY

    fun getFullMoveCounter() = fenInfo.fullMoveCounter

    fun getHalfMoveClock() = fenInfo.halfMoveClock

    fun getColorToMove() = fenInfo.colorToMove

    fun getWhites() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isWhite }
        .map { it.square }
        .toBitBoard()

    fun getBlacks() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isBlack }
        .map { it.square }
        .toBitBoard()

    fun getKings() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isKing }
        .map { it.square }
        .toBitBoard()

    fun getQueens() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isQueen }
        .map { it.square }
        .toBitBoard()

    fun getRooks() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isRook }
        .map { it.square }
        .toBitBoard()

    fun getBishops() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isBishop }
        .map { it.square }
        .toBitBoard()

    fun getKnights() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isKnight }
        .map { it.square }
        .toBitBoard()

    fun getPawns() = fenInfo
        .pieceDisposition
        .asSequence()
        .filter { it.piece.isPawn }
        .map { it.square }
        .toBitBoard()
}

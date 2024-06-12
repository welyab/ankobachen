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

import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.Piece.*
import com.welyab.ankobachen.fen.FEN
import com.welyab.ankobachen.utils.BoardPrinter
import com.welyab.ankobachen.utils.toString

/**
 * The class `Board` keeps a game position at some point and generate movements for that position.
 * In a game playing, it also stores movement history to provide undo and redo features.
 *
 * * `Color` - black and white opponents
 * * `Piece` - each piece type for each color: black pawn, white pawn, black queen, white queen...
 * * `PieceType` - six chess piece types: king, queen, rook, bishop, knight and pawn
 * * `PlacedPiece` - a piece placed in a specific square: 64 squares and 12 pieces in 768 combinations
 *
 * ## Board representation
 *
 * The board is represented using bitboards. Bitboard is a technique where 64 bits integer numbers are used
 * piece locations and other information. Each piece type has its own bitboard and there is other two bitboards
 * to distinguish black and white pieces. It is a good representation because modern computer processors handle
 * 64 bit operations and chess has 64 squares.
 *
 * ```text
 * 64 bit unsigned integer board representation
 *
 *          +-- MSB - most significant bit
 *          |
 *          v
 * rank 8 | 0 0 0 0 0 0 0 0
 * rank 7 | 0 0 0 0 0 0 0 0
 * rank 6 | 0 0 0 0 0 0 0 0
 * rank 5 | 0 0 0 0 0 0 0 0
 * rank 4 | 0 0 0 0 0 0 0 0
 * rank 3 | 0 0 0 0 0 0 0 0
 * rank 2 | 0 0 0 0 0 0 0 0
 * rank 1 | 0 0 0 0 0 0 0 0 <-- LSB - least significant bit
 *        +----------------
 *  files  a b c d e f g h
 * ```
 *
 * Each piece type has its own bitboard. If there is a piece located in a square, then the bit associated
 * with that position is set to 1. Also, according to piece's color, then white or black bitboard has
 * the bit set to 1.
 */
class Board private constructor(
    blackPieces: ULong,
    whitePieces: ULong,
    pawns: ULong,
    knights: ULong,
    bishops: ULong,
    rooks: ULong,
    queens: ULong,
    kings: ULong,
    flags: ULong,
    moveCount: Int,
    halfMoveCount: Int,
    color: Color,
): PlacedPieceIterable {

    private var blackPieces: ULong = blackPieces
    private var whitePieces: ULong = whitePieces
    private var pawns: ULong = pawns
    private var knights: ULong = knights
    private var bishops: ULong = bishops
    private var rooks: ULong = rooks
    private var queens: ULong = queens
    private var kings: ULong = kings
    private var flags: ULong = flags
    private var moveCount: Int = moveCount
    private var halfMoveCount: Int = halfMoveCount
    private var color: Color = color

    constructor(fen: FEN) : this(
        blackPieces = fen.getBlackPiecesBitboard(),
        whitePieces = fen.getWhitePiecesBitboard(),
        pawns = fen.getPawnsBitboard(),
        knights = fen.getKnightsBitboard(),
        bishops = fen.getBishopsBitboard(),
        rooks = fen.getRooksBitboard(),
        queens = fen.getQueensBitboard(),
        kings = fen.getKingsBitboard(),
        flags = fen.getFlagsBitboard(),
        moveCount = fen.getFullMoveCount(),
        halfMoveCount = fen.getHalfMoveClock(),
        color = fen.getColorToMove(),
    )

    constructor() : this(FEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"))

    constructor(fen: String) : this(fen = FEN(value = fen))

    fun copy(): Board = Board(
        blackPieces = blackPieces,
        whitePieces = whitePieces,
        pawns = pawns,
        knights = knights,
        bishops = bishops,
        rooks = rooks,
        queens = queens,
        kings = kings,
        flags = flags,
        moveCount = moveCount,
        halfMoveCount = halfMoveCount,
        color = color,
    )

    fun isSquareEmpty(
        square: Square
    ): Boolean = (blackPieces and whitePieces)
        .isBitActive(square.getIndex())

    fun getPiece(
        square: Square
    ): Piece {
        if (blackPieces.isBitActive(square.getIndex())) {
            if (kings.isBitActive(square.getIndex())) return BLACK_KING
            if (queens.isBitActive(square.getIndex())) return BLACK_QUEEN
            if (rooks.isBitActive(square.getIndex())) return BLACK_ROOK
            if (bishops.isBitActive(square.getIndex())) return BLACK_BISHOP
            if (knights.isBitActive(square.getIndex())) return BLACK_KNIGHT
            if (pawns.isBitActive(square.getIndex())) return BLACK_PAWN
        } else if (whitePieces.isBitActive(square.getIndex())) {
            if (kings.isBitActive(square.getIndex())) return WHITE_KING
            if (queens.isBitActive(square.getIndex())) return WHITE_QUEEN
            if (rooks.isBitActive(square.getIndex())) return WHITE_ROOK
            if (bishops.isBitActive(square.getIndex())) return WHITE_BISHOP
            if (knights.isBitActive(square.getIndex())) return WHITE_KNIGHT
            if (pawns.isBitActive(square.getIndex())) return WHITE_PAWN
        }

        throw ChessException(
            message = "Square $square is empty"
        )
    }

    fun getPiecesColorBitboard(
        color: Color
    ): ULong = when (color) {
        BLACK -> blackPieces
        WHITE -> whitePieces
    }

    fun getBlackPiecesBitboard(): ULong = blackPieces

    fun getWhitePiecesBitboard(): ULong = whitePieces

    fun getPiecesBitboard(
        piece: Piece
    ): ULong = when (piece) {
        BLACK_KING -> blackPieces and kings
        BLACK_QUEEN -> blackPieces and queens
        BLACK_ROOK -> blackPieces and rooks
        BLACK_BISHOP -> blackPieces and bishops
        BLACK_KNIGHT -> blackPieces and knights
        BLACK_PAWN -> blackPieces and pawns
        WHITE_KING -> whitePieces and kings
        WHITE_QUEEN -> whitePieces and queens
        WHITE_ROOK -> whitePieces and rooks
        WHITE_BISHOP -> whitePieces and bishops
        WHITE_KNIGHT -> whitePieces and knights
        WHITE_PAWN -> whitePieces and pawns
    }

    fun getBlacksBitboards(): ULong = blackPieces

    fun getWhitesBitboards(): ULong = whitePieces

    fun getKingsBitboard(): ULong = kings

    fun getQueensBitboard(): ULong = queens

    fun getRooksBitboard(): ULong = rooks

    fun getBishopsBitboard(): ULong = bishops

    fun getKnightsBitboard(): ULong = knights

    fun getPawnsBitboard(): ULong = pawns

    private inner class PlacedPieceIterator : Iterator<PlacedPiece> {

        var piece = Piece.entries.first()
        var bitboard = getPiecesBitboard(piece)
        var nextPlacedPiece: PlacedPiece? = null

        fun getPlacedPiece(): PlacedPiece? {
            while (true) {
                val bitIndex = bitboard.getFirstActiveBitIndex()
                if (bitIndex == 64) {
                    if (piece.getIndex() >= Piece.entries.size - 1) {
                        break
                    }
                    piece = Piece.entries[piece.getIndex() + 1]
                    bitboard = getPiecesBitboard(piece)
                    continue
                }
                bitboard = bitboard.deactivateBit(bitIndex)
                return PlacedPiece.getFrom(
                    pieceIndex = piece.getIndex(),
                    squareIndex = bitIndex
                )
            }
            return null
        }

        override fun hasNext(): Boolean {
            if(nextPlacedPiece != null) return true
            nextPlacedPiece = getPlacedPiece()
            return nextPlacedPiece != null
        }

        override fun next(): PlacedPiece {
            if(!hasNext()) throw NoSuchElementException("no more elements")
            val next = nextPlacedPiece!!
            nextPlacedPiece = null
            return next
        }
    }

    fun getFEN(): FEN = TODO()

    override fun iterator(): Iterator<PlacedPiece> = PlacedPieceIterator()

    override fun toString(): String {
        return BoardPrinter.toString(this)
    }
}

fun main() {
    println(FEN("r1b2r1k/4qp1p/p1Nppb1Q/4nP2/1p2P3/2N5/PPP4P/2KR1BR1 b - - 5 18"))
}
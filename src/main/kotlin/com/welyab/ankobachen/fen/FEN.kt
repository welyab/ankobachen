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

import com.welyab.ankobachen.BitboardConstants
import com.welyab.ankobachen.Color
import com.welyab.ankobachen.FileSymbols
import com.welyab.ankobachen.Piece
import com.welyab.ankobachen.PieceType
import com.welyab.ankobachen.PlacedPiece
import com.welyab.ankobachen.PlacedPieceIterable
import com.welyab.ankobachen.RankNumbers
import com.welyab.ankobachen.RowColNumbers
import com.welyab.ankobachen.Square
import com.welyab.ankobachen.activateBit
import com.welyab.ankobachen.deactivateBit
import com.welyab.ankobachen.deactivateRow
import com.welyab.ankobachen.getFirstActiveBitIndex
import com.welyab.ankobachen.isBitActive
import com.welyab.ankobachen.utils.BoardPrinter
import com.welyab.ankobachen.utils.toString

/**
 * A chess FEN parser.
 *
 * FEN class converts pieces representation to bitboards and also have other utility methods.
 *
 * ## Usage
 *
 * ````kotlin
 * val fen = FEN("r4rkQ/pp2pp1p/3p2p1/6B1/7P/1P6/1RPKnPP1/q6R b - - 1 1")
 *
 * // outputs
 * // ┌───┬───┬───┬───┬───┬───┬───┬───┐
 * // │ r │   │   │   │   │ r │ k │ Q │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │ p │ p │   │   │ p │ p │   │ p │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │   │   │   │ p │   │   │ p │   │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │   │   │   │   │   │   │ B │   │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │   │   │   │   │   │   │   │ P │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │   │ P │   │   │   │   │   │   │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │   │ R │ P │ K │ n │ P │ P │   │
 * // ├───┼───┼───┼───┼───┼───┼───┼───┤
 * // │ q │   │   │   │   │   │   │ R │
 * // └───┴───┴───┴───┴───┴───┴───┴───┘
 * ```
 *
 * @author Welyab Paula @ https://github.com/welyab/
 */
class FEN(
    val value: String
) : PlacedPieceIterable {

    private val piecesColorsBitboards = Array(Color.entries.size) {
        BitboardConstants.EMPTY
    }
    private val piecesBitboards = Array(PieceType.entries.size) {
        BitboardConstants.EMPTY
    }
    private var flags: ULong = 0uL
    private var fullMoveCount: Int = 1
    private var halfMoveClock: Int = 0
    private var color: Color = Color.BLACK

    init {
        var charIndex = 0
        var rowNumber = 0
        var colNumber = 0

        fun skipSpaces() {
            while (charIndex < value.length && value[charIndex] == ' ') {
                charIndex++
            }
        }

        // reading piece placement
        while (true) {
            if (charIndex >= value.length) {
                throwInvalidPiecePlacement(
                    fen = value
                )
            }
            if (value[charIndex] == ' ') {
                break
            }
            if (value[charIndex] == '/') {
                if (colNumber != 8) {
                    throwInvalidPiecePlacement(
                        fen = value
                    )
                }
                charIndex++
                rowNumber++
                if (rowNumber > 7) {
                    throwInvalidPiecePlacement(
                        fen = value
                    )
                }
                colNumber = 0
                continue
            }
            if (value[charIndex] in '1'..'8') {
                colNumber += value[charIndex] - '0'
                if (colNumber > 8) {
                    throwInvalidPiecePlacement(
                        fen = value
                    )
                }
                charIndex++
                continue
            }
            val piece = try {
                Piece.fromSymbol(
                    symbol = value[charIndex]
                )
            } catch (ex: IllegalArgumentException) {
                throwInvalidCharacter(
                    fen = value,
                    char = value[charIndex],
                    charIndex = charIndex
                )
            }
            val squareIndex = Square.rowColToIndex(
                row = rowNumber,
                col = colNumber
            )
            piecesColorsBitboards[piece.color.getIndex()] = piecesColorsBitboards[piece.color.getIndex()]
                .activateBit(squareIndex)
            piecesBitboards[piece.type.getIndex()] = piecesBitboards[piece.type.getIndex()].activateBit(squareIndex)
            colNumber++
            charIndex++
        }

        if (rowNumber != 7 || colNumber != 8) {
            throwInvalidPiecePlacement(
                fen = value
            )
        }

        // reading side to move
        skipSpaces()
        color = try {
            Color.fromSymbol(value[charIndex]).also {
                charIndex++
            }
        } catch (ex: IllegalArgumentException) {
            throwInvalidCharacter(
                fen = value,
                char = value[charIndex],
                charIndex = charIndex,
                cause = ex
            )
        }

        // reading castling flags
        skipSpaces()
        if (charIndex >= value.length || value[charIndex] == '-') {
            charIndex++
        } else {
            if (value[charIndex] == Piece.WHITE_KING.symbol) {
                flags = flags.activateBit(Square.G1.getIndex())
                charIndex++
            }
            if (value[charIndex] == Piece.WHITE_QUEEN.symbol) {
                flags = flags.activateBit(Square.C1.getIndex())
                charIndex++
            }
            if (value[charIndex] == Piece.BLACK_KING.symbol) {
                flags = flags.activateBit(Square.G8.getIndex())
                charIndex++
            }
            if (value[charIndex] == Piece.BLACK_QUEEN.symbol) {
                flags = flags.activateBit(Square.C8.getIndex())
                charIndex++
            }

            if (charIndex < value.length && value[charIndex] != ' ') {
                throwInvalidCharacter(
                    fen = value,
                    char = value[charIndex],
                    charIndex = charIndex
                )
            }
        }

        // reading en passant target square
        skipSpaces()
        if (charIndex >= value.length || value[charIndex] == '-') {
            charIndex++
        } else {
            val file = value[charIndex]
            if (file !in FileSymbols.FILE_A..FileSymbols.FILE_H) {
                throwInvalidCharacter(
                    fen = value,
                    char = value[charIndex],
                    charIndex = charIndex,
                )
            }
            charIndex++
            if (charIndex >= value.length) {
                throw FENException(
                    fen = value,
                    message = "FEN string has invalid en passant target square information"
                )
            }
            val rank = value[charIndex] - '0'
            if (rank !in RankNumbers.RANK_1..RankNumbers.RANK_8) {
                throwInvalidCharacter(
                    fen = value,
                    char = value[charIndex],
                    charIndex = charIndex,
                )
            }
            charIndex++
            val enPassantTarget = Square.fromAlgebraicCoordinate(
                file = file,
                rank = rank
            )
            flags = flags.activateBit(enPassantTarget.getIndex())
        }

        // reading half move clock
        skipSpaces()
        if (charIndex >= value.length || value[charIndex] == '-') {
            charIndex++
        } else {
            val startIndex = charIndex
            while (charIndex < value.length && value[charIndex] != ' ') {
                charIndex++
            }
            halfMoveClock = value.substring(startIndex, charIndex).toInt()
        }

        // reading full move counter
        skipSpaces()
        if (charIndex >= value.length || value[charIndex] == '-') {
            charIndex++
        } else {
            val startIndex = charIndex
            while (charIndex < value.length && value[charIndex] != ' ') {
                charIndex++
            }
            fullMoveCount = value.substring(startIndex, charIndex).toInt()
        }
    }

    fun getBlackPiecesBitboard(): ULong = piecesColorsBitboards[Color.BLACK.getIndex()]

    fun getWhitePiecesBitboard(): ULong = piecesColorsBitboards[Color.WHITE.getIndex()]

    fun getPawnsBitboard(): ULong = piecesBitboards[PieceType.PAWN.getIndex()]

    fun getKnightsBitboard(): ULong = piecesBitboards[PieceType.KNIGHT.getIndex()]

    fun getBishopsBitboard(): ULong = piecesBitboards[PieceType.BISHOP.getIndex()]

    fun getRooksBitboard(): ULong = piecesBitboards[PieceType.ROOK.getIndex()]

    fun getQueensBitboard(): ULong = piecesBitboards[PieceType.QUEEN.getIndex()]

    fun getKingsBitboard(): ULong = piecesBitboards[PieceType.KING.getIndex()]

    fun getFlagsBitboard(): ULong = flags

    fun getFullMoveCount(): Int = fullMoveCount

    fun getHalfMoveClock(): Int = halfMoveClock

    fun getColorToMove(): Color = color

    fun hasEnPassantTargetSquare(): Boolean = flags
        .deactivateRow(RowColNumbers.ROW_0)
        .deactivateRow(RowColNumbers.ROW_7) != BitboardConstants.EMPTY

    fun getEnPassantTargetSquare(): Square =
        if (hasEnPassantTargetSquare())
            flags.deactivateRow(RowColNumbers.ROW_0)
                .deactivateRow(RowColNumbers.ROW_7)
                .getFirstActiveBitIndex()
                .let { Square.fromSquareIndex(it) }
        else throw FENException(
            "FEN \"${value}\" has no en passant target square (maybe you should use this.${this::getEnPassantTargetSquare.name}())."
        )

    fun hasWhiteKingSideCastling(): Boolean = flags.isBitActive(Square.G1.getIndex())

    fun hasWhiteQueenSideCastling(): Boolean = flags.isBitActive(Square.C1.getIndex())

    fun hasBlackKingSideCastling(): Boolean = flags.isBitActive(Square.G8.getIndex())

    fun hasBlackQueenSideCastling(): Boolean = flags.isBitActive(Square.C8.getIndex())

    private inner class PlacedPieceIterator : Iterator<PlacedPiece> {

        var currentPieceType: PieceType = PieceType.entries[0]
        var currentBitboard: ULong = piecesBitboards[currentPieceType.getIndex()]
        var nextPlacedPiece: PlacedPiece? = null

        fun nextPlacedPiece(): PlacedPiece? {
            while (true) {
                val bitIndex = currentBitboard.getFirstActiveBitIndex()
                if (bitIndex == 64) {
                    if (currentPieceType.getIndex() + 1 == PieceType.entries.size) {
                        return null
                    }
                    currentPieceType = PieceType.entries[currentPieceType.getIndex() + 1]
                    currentBitboard = piecesBitboards[currentPieceType.getIndex()]
                    continue
                }
                currentBitboard = currentBitboard.deactivateBit(bitIndex)
                val color = if (piecesColorsBitboards[Color.BLACK.getIndex()].isBitActive(bitIndex)) Color.BLACK
                else Color.WHITE
                return PlacedPiece.getFrom(
                    pieceIndex = Piece.from(
                        colorIndex = color.getIndex(),
                        pieceTypeIndex = currentPieceType.getIndex()
                    ).getIndex(),
                    squareIndex = bitIndex
                )
            }
        }

        override fun hasNext(): Boolean {
            if (nextPlacedPiece != null) return true
            nextPlacedPiece = nextPlacedPiece()
            return nextPlacedPiece != null
        }

        override fun next(): PlacedPiece {
            if (!hasNext()) throw NoSuchElementException("no more elements")
            val next = nextPlacedPiece!!
            nextPlacedPiece = null
            return next
        }
    }

    override fun iterator(): Iterator<PlacedPiece> = PlacedPieceIterator()

    override fun toString(): String {
        return BoardPrinter.toString(this)
    }

    companion object {

        private fun throwInvalidCharacter(
            fen: String,
            char: Char,
            charIndex: Int,
            cause: Throwable? = null
        ): Nothing = throw FENException(
            fen = fen,
            message = "Invalid character '$char' at position $charIndex",
            cause = cause
        )

        private fun throwInvalidPiecePlacement(
            fen: String,
        ): Nothing = throw FENException(
            fen = fen,
            message = "FEN has invalid piece placement"
        )
    }
}

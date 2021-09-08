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
import com.welyab.ankobachen.Piece.BLACK_BISHOP
import com.welyab.ankobachen.Piece.BLACK_KING
import com.welyab.ankobachen.Piece.BLACK_KNIGHT
import com.welyab.ankobachen.Piece.BLACK_PAWN
import com.welyab.ankobachen.Piece.BLACK_QUEEN
import com.welyab.ankobachen.Piece.BLACK_ROOK
import com.welyab.ankobachen.Piece.WHITE_BISHOP
import com.welyab.ankobachen.Piece.WHITE_KING
import com.welyab.ankobachen.Piece.WHITE_KNIGHT
import com.welyab.ankobachen.Piece.WHITE_PAWN
import com.welyab.ankobachen.Piece.WHITE_QUEEN
import com.welyab.ankobachen.Piece.WHITE_ROOK
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK

class BoardException(
    message: String = "",
    cause: Throwable? = null
) : ChessException(
    message,
    cause
)

@ExperimentalUnsignedTypes
class Board : Copyable<Board> {

    // ==================================================================================
    // ----------------------------------------------------------------------------------
    // Board properties
    // ----------------------------------------------------------------------------------
    // ==================================================================================

    private var whites: ULong = EMPTY
    private var blacks: ULong = EMPTY

    private var kings: ULong = EMPTY
    private var queens: ULong = EMPTY
    private var rooks: ULong = EMPTY
    private var bishops: ULong = EMPTY
    private var knights: ULong = EMPTY
    private var pawns: ULong = EMPTY

    private var epTargetSquare: ULong = EMPTY
    private var castlingFlags: ULong = EMPTY

    private var colorToMove: Color = WHITE
    private var plyCounter: Int = 0
    private var halfMoveClock: Int = 0
    private var fullMoveCounter: Int = 0

    // ==================================================================================
    // ----------------------------------------------------------------------------------
    // Board initializers
    // ----------------------------------------------------------------------------------
    // ==================================================================================

    constructor(initialPosition: Boolean = true) : this(if (initialPosition) INITIAL_FEN else EMPTY_FEN)

    constructor(fen: String) : this(
        try {
            FenBoardProperties(FenString(fen))
        } catch (e: Exception) {
            throw BoardException("Failed create board", e)
        }
    )

    private constructor(fenBoardProperties: FenBoardProperties) : this(
        whites = fenBoardProperties.getWhites(),
        blacks = fenBoardProperties.getBlacks(),
        kings = fenBoardProperties.getKings(),
        queens = fenBoardProperties.getQueens(),
        rooks = fenBoardProperties.getRooks(),
        bishops = fenBoardProperties.getBishops(),
        knights = fenBoardProperties.getKnights(),
        pawns = fenBoardProperties.getPawns(),
        colorToMove = fenBoardProperties.getColorToMove(),
        plyCounter = INITIAL_PLY_COUNTER,
        halfMoveClock = fenBoardProperties.getHalfMoveClock(),
        fullMoveCounter = fenBoardProperties.getFullMoveCounter(),
        epTargetSquare = fenBoardProperties.getEpTargetSquare(),
        castlingFlags = fenBoardProperties.getCastlingFlags(),
    )

    private constructor(
        whites: ULong,
        blacks: ULong,
        kings: ULong,
        queens: ULong,
        rooks: ULong,
        bishops: ULong,
        knights: ULong,
        pawns: ULong,
        colorToMove: Color,
        plyCounter: Int,
        halfMoveClock: Int,
        fullMoveCounter: Int,
        epTargetSquare: ULong,
        castlingFlags: ULong
    ) {
        this.whites = whites
        this.blacks = blacks
        this.kings = kings
        this.queens = queens
        this.rooks = rooks
        this.bishops = bishops
        this.knights = knights
        this.pawns = pawns
        this.colorToMove = colorToMove
        this.plyCounter = plyCounter
        this.halfMoveClock = halfMoveClock
        this.fullMoveCounter = fullMoveCounter
        this.epTargetSquare = epTargetSquare
        this.castlingFlags = castlingFlags
    }

    // ==================================================================================
    // ----------------------------------------------------------------------------------
    // Methods to get state of board
    // ----------------------------------------------------------------------------------
    // ==================================================================================

    fun isEmpty(square: Square) = isEmpty(square.bitboard)
    fun isNotEmpty(square: Square) = !isEmpty(square.bitboard)

    fun getPiece(square: Square) =
        if (isEmpty(square.bitboard)) throw BoardException("Can't get piece. Empty square: $square")
        else getPiece(square.bitboard)

    fun getMovements(valid: Boolean = true) = getMovements1(valid)

    fun getMovements(square: Square, valid: Boolean = true) =
        if (isEmpty(square.bitboard)) throw BoardException("Can't get movements. Empty square: $square")
        else getMovements(square.bitboard, valid)

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Board) return false
        return whites == other.whites
                && blacks == other.blacks
                && kings == other.kings
                && queens == other.queens
                && rooks == other.rooks
                && bishops == other.bishops
                && knights == other.knights
                && pawns == other.pawns
                && colorToMove == other.colorToMove
                && plyCounter == other.plyCounter
                && halfMoveClock == other.halfMoveClock
                && fullMoveCounter == other.fullMoveCounter
                && epTargetSquare == other.epTargetSquare
                && castlingFlags == other.castlingFlags
    }

    override fun hashCode(): Int {
        var result = 1
        result += whites.hashCode() * 31
        result += blacks.hashCode() * 31
        result += kings.hashCode() * 31
        result += queens.hashCode() * 31
        result += rooks.hashCode() * 31
        result += bishops.hashCode() * 31
        result += knights.hashCode() * 31
        result += pawns.hashCode() * 31
        result += colorToMove.hashCode() * 31
        result += plyCounter.hashCode() * 31
        result += halfMoveClock.hashCode() * 31
        result += fullMoveCounter.hashCode() * 31
        result += epTargetSquare.hashCode() * 31
        result += castlingFlags.hashCode() * 31
        return result
    }

    override fun copy() = Board(
        whites = this.whites,
        blacks = this.blacks,
        kings = this.kings,
        queens = this.queens,
        rooks = this.rooks,
        bishops = this.bishops,
        knights = this.knights,
        pawns = this.pawns,
        colorToMove = this.colorToMove,
        plyCounter = this.plyCounter,
        halfMoveClock = this.halfMoveClock,
        fullMoveCounter = this.fullMoveCounter,
        epTargetSquare = this.epTargetSquare,
        castlingFlags = this.castlingFlags
    )

    @Suppress("kotlin:S3776")
    fun getFen(): String = buildString {
        for (row in 0..7) {
            var emptyCount = 0
            for (column in 0..7) {
                val square = Square.from(row, column)
                if (isEmpty(square)) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) append(emptyCount)
                    emptyCount = 0
                    append(getPiece(square).value)
                }
            }
            if (emptyCount > 0) append(emptyCount)
            if (row != 7) append('/')
        }

        append(' ')
        append(colorToMove.value)

        val whiteKingIndex = kings.and(whites).index
        val blackKingIndex = kings.and(blacks).index
        val whiteCastlingIndexes = castlingFlags.and(RANK_1).indexes
        val blackCastlingIndexes = castlingFlags.and(RANK_8).indexes
        val flagsList = mutableListOf<Char>()
        if (whiteCastlingIndexes.any { it > whiteKingIndex }) flagsList += 'K'
        if (whiteCastlingIndexes.any { it < whiteKingIndex }) flagsList += 'Q'
        if (blackCastlingIndexes.any { it > blackKingIndex }) flagsList += 'k'
        if (blackCastlingIndexes.any { it < blackKingIndex }) flagsList += 'q'
        append(' ')
        if (flagsList.isNotEmpty()) flagsList.forEach { append(it) }
        else append('-')

        append(' ')
        if (epTargetSquare != EMPTY) append(Square.from(epTargetSquare.index).san)
        else append('-')

        append(' ')
        append(halfMoveClock)

        append(' ')
        append(fullMoveCounter)
    }

    fun toString(printInfo: Boolean) = buildString {
        if (printInfo) {
            append("FEN: ")
            append(getFen())
            append(String.format("%n"))
        }
        append(this@Board.toString())
    }

    override fun toString() = buildString {
        append("┌───┬───┬───┬───┬───┬───┬───┬───┐")
        append(String.format("%n"))
        val chars = mutableListOf<Char>()
        for (index in 0..63) {
            val square = Square.from(index)
            chars += if (isEmpty(square.bitboard)) ' '
            else getPiece(square.bitboard).value
            if ((index + 1) % 8 == 0) {
                append("│ %c │ %c │ %c │ %c │ %c │ %c │ %c │ %c │".format(*chars.toTypedArray()))
                append(String.format("%n"))
                if ((index / 8) < 7) {
                    append("├───┼───┼───┼───┼───┼───┼───┼───┤")
                    append(String.format("%n"))
                }
                chars.clear()
            }
        }
        append("└───┴───┴───┴───┴───┴───┴───┴───┘")
        append(String.format("%n"))
    }

    fun toBitboardString() = whites.or(blacks).toBitboardStringFormatted()

    // ==================================================================================
    // ----------------------------------------------------------------------------------
    // Public methods to change board state
    // ----------------------------------------------------------------------------------
    // ==================================================================================


    // ==================================================================================
    // ----------------------------------------------------------------------------------
    // Private methods with board logic
    // ----------------------------------------------------------------------------------
    // ==================================================================================

    private fun getMovements1(valid: Boolean): MovementBag {
        val movements = ArrayList<PieceMovements>(32)
        whites.or(blacks).indexes.forEach { squareIndex ->
            movements += getMovements(Square.from(squareIndex).bitboard, valid)
        }
        return MovementBag(movements)
    }

    private fun getMovements(
        squareBitboard: ULong,
        valid: Boolean
    ): PieceMovements {
        val piece = getPiece(squareBitboard)
        val ownPiecesBitboard = when (piece.color) {
            WHITE -> whites
            BLACK -> blacks
        }
        return when (piece) {
            WHITE_KING -> getKingMovements(piece, squareBitboard, ownPiecesBitboard, valid)
            WHITE_QUEEN -> getQueenMovements(piece, squareBitboard, valid)
            WHITE_ROOK -> getRookMovements(piece, squareBitboard, valid)
            WHITE_BISHOP -> getBishopMovements(piece, squareBitboard, valid)
            WHITE_KNIGHT -> getKnightMovements(piece, squareBitboard, ownPiecesBitboard, valid)
            WHITE_PAWN -> getPawnMovements(piece, squareBitboard, valid)
            BLACK_KING -> getKingMovements(piece, squareBitboard, ownPiecesBitboard, valid)
            BLACK_QUEEN -> getQueenMovements(piece, squareBitboard, valid)
            BLACK_ROOK -> getRookMovements(piece, squareBitboard, valid)
            BLACK_BISHOP -> getBishopMovements(piece, squareBitboard, valid)
            BLACK_KNIGHT -> getKnightMovements(piece, squareBitboard, ownPiecesBitboard, valid)
            BLACK_PAWN -> getPawnMovements(piece, squareBitboard, valid)
        }
    }

    private fun getKingMovements(
        piece: Piece,
        squareBitboard: ULong,
        ownPieces: ULong,
        valid: Boolean
    ): PieceMovements {
        val squareIndex = squareBitboard.index
        val mask = KING_MOVE_MASK[squareIndex]
        val destinationsBitboard = mask.and(ownPieces.inv())
        return PieceMovements(squareIndex, destinationsBitboard, emptyList())
    }

    private fun getQueenMovements(piece: Piece, squareBitboard: ULong, valid: Boolean): PieceMovements {
        TODO()
    }

    private fun getRookMovements(piece: Piece, squareBitboard: ULong, valid: Boolean): PieceMovements {
        TODO()
    }

    private fun getBishopMovements(piece: Piece, squareBitboard: ULong, valid: Boolean): PieceMovements {
        TODO()
    }

    private fun getSlidingPieceMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        movementTargets: ArrayList<MovementTarget>,
        moveGenFlags: Int
    ) {
        val targetSquares = getSlidingPieceTargetSquares(piece, fromSquare, ownPieces)
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets, moveGenFlags)
    }

    private fun getSlidingPieceTargetSquares(piece: Piece, fromSquare: Int, ownPieces: ULong): ULong {
        return when (piece.type) {
            ROOK -> getRookTargetSquares(fromSquare, ownPieces)
            BISHOP -> getBishopTargetSquares(fromSquare, ownPieces)
            QUEEN -> getQueenTargetSquares(fromSquare, ownPieces)
            else -> throw Error("no sliding piece: $piece")
        }
    }

    private fun getQueenTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val rookTargets = getRookTargetSquares(squareIndex, ownPieces)
        val bishopTargets = getBishopTargetSquares(squareIndex, ownPieces)
        return rookTargets or bishopTargets
    }

    private fun getRookTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = ROOK_MOVE_MASK[squareIndex].and(whites.or(blacks))
        val key = (blockers * ROOK_MAGICS[squareIndex]).shift(64 - ROOK_MAGIC_INDEX_BITS[squareIndex])
        return ROOK_MOVEMENT_DATABASE[squareIndex][key.toInt()] and ownPieces.inv()
    }

    private fun getBishopTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = BISHOP_MOVE_MASK[squareIndex].and(whites.and(blacks))
        val key = (blockers * BISHOP_MAGICS[squareIndex]).shift(64 - BISHOP_MAGIC_INDEX_BITS[squareIndex])
        return BISHOP_MOVEMENT_DATABASE[squareIndex][key.toInt()] and ownPieces.inv()
    }

    private fun getKnightMovements(
        piece: Piece,
        squareBitboard: ULong,
        ownPieces: ULong,
        valid: Boolean
    ): PieceMovements {
        val squareIndex = squareBitboard.index
        val mask = KNIGHT_MOVE_MASK[squareIndex]
        val destinationsBitboard = mask.and(ownPieces.inv())
        return PieceMovements(squareIndex, destinationsBitboard, emptyList())
    }

    private fun getPawnMovements(piece: Piece, squareBitboard: ULong, valid: Boolean): PieceMovements {
        TODO()
    }

    private fun isEmpty(squareBitboard: ULong) = whites.or(blacks).and(squareBitboard) == EMPTY

    private fun getPiece(squareBitboard: ULong): Piece {
        return when {
            whites.and(squareBitboard) != EMPTY -> when {
                kings.and(squareBitboard) != EMPTY -> WHITE_KING
                queens.and(squareBitboard) != EMPTY -> WHITE_QUEEN
                rooks.and(squareBitboard) != EMPTY -> WHITE_ROOK
                bishops.and(squareBitboard) != EMPTY -> WHITE_BISHOP
                knights.and(squareBitboard) != EMPTY -> WHITE_KNIGHT
                pawns.and(squareBitboard) != EMPTY -> WHITE_PAWN
                else -> throwUnbelievableError()
            }
            blacks.and(squareBitboard) != EMPTY -> when {
                kings.and(squareBitboard) != EMPTY -> BLACK_KING
                queens.and(squareBitboard) != EMPTY -> BLACK_QUEEN
                rooks.and(squareBitboard) != EMPTY -> BLACK_ROOK
                bishops.and(squareBitboard) != EMPTY -> BLACK_BISHOP
                knights.and(squareBitboard) != EMPTY -> BLACK_KNIGHT
                pawns.and(squareBitboard) != EMPTY -> BLACK_PAWN
                else -> throwUnbelievableError()
            }
            else -> throwUnbelievableError()
        }
    }

    private fun throwUnbelievableError(): Nothing = throw Error("Unbelievable error")

    companion object {
        const val INITIAL_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        const val EMPTY_FEN = "8/8/8/8/8/8/8/8 w - - 0 1"

        const val RANK_1 = 0xffUL
        const val RANK_8 = 0xff00000000000000UL

        const val INITIAL_PLY_COUNTER = 0

        private val KING_MOVE_MASK = ulongArrayOf(
            0x40c0000000000000uL, 0xa0e0000000000000uL, 0x5070000000000000uL, 0x2838000000000000uL,
            0x141c000000000000uL, 0x0a0e000000000000uL, 0x0507000000000000uL, 0x0203000000000000uL,
            0xc040c00000000000uL, 0xe0a0e00000000000uL, 0x7050700000000000uL, 0x3828380000000000uL,
            0x1c141c0000000000uL, 0x0e0a0e0000000000uL, 0x0705070000000000uL, 0x0302030000000000uL,
            0x00c040c000000000uL, 0x00e0a0e000000000uL, 0x0070507000000000uL, 0x0038283800000000uL,
            0x001c141c00000000uL, 0x000e0a0e00000000uL, 0x0007050700000000uL, 0x0003020300000000uL,
            0x0000c040c0000000uL, 0x0000e0a0e0000000uL, 0x0000705070000000uL, 0x0000382838000000uL,
            0x00001c141c000000uL, 0x00000e0a0e000000uL, 0x0000070507000000uL, 0x0000030203000000uL,
            0x000000c040c00000uL, 0x000000e0a0e00000uL, 0x0000007050700000uL, 0x0000003828380000uL,
            0x0000001c141c0000uL, 0x0000000e0a0e0000uL, 0x0000000705070000uL, 0x0000000302030000uL,
            0x00000000c040c000uL, 0x00000000e0a0e000uL, 0x0000000070507000uL, 0x0000000038283800uL,
            0x000000001c141c00uL, 0x000000000e0a0e00uL, 0x0000000007050700uL, 0x0000000003020300uL,
            0x0000000000c040c0uL, 0x0000000000e0a0e0uL, 0x0000000000705070uL, 0x0000000000382838uL,
            0x00000000001c141cuL, 0x00000000000e0a0euL, 0x0000000000070507uL, 0x0000000000030203uL,
            0x000000000000c040uL, 0x000000000000e0a0uL, 0x0000000000007050uL, 0x0000000000003828uL,
            0x0000000000001c14uL, 0x0000000000000e0auL, 0x0000000000000705uL, 0x0000000000000302uL
        )

        private val ROOK_MOVE_MASK = ulongArrayOf(
            0x7e80808080808000uL, 0x3e40404040404000uL, 0x5e20202020202000uL, 0x6e10101010101000uL,
            0x7608080808080800uL, 0x7a04040404040400uL, 0x7c02020202020200uL, 0x7e01010101010100uL,
            0x007e808080808000uL, 0x003e404040404000uL, 0x005e202020202000uL, 0x006e101010101000uL,
            0x0076080808080800uL, 0x007a040404040400uL, 0x007c020202020200uL, 0x007e010101010100uL,
            0x00807e8080808000uL, 0x00403e4040404000uL, 0x00205e2020202000uL, 0x00106e1010101000uL,
            0x0008760808080800uL, 0x00047a0404040400uL, 0x00027c0202020200uL, 0x00017e0101010100uL,
            0x0080807e80808000uL, 0x0040403e40404000uL, 0x0020205e20202000uL, 0x0010106e10101000uL,
            0x0008087608080800uL, 0x0004047a04040400uL, 0x0002027c02020200uL, 0x0001017e01010100uL,
            0x008080807e808000uL, 0x004040403e404000uL, 0x002020205e202000uL, 0x001010106e101000uL,
            0x0008080876080800uL, 0x000404047a040400uL, 0x000202027c020200uL, 0x000101017e010100uL,
            0x00808080807e8000uL, 0x00404040403e4000uL, 0x00202020205e2000uL, 0x00101010106e1000uL,
            0x0008080808760800uL, 0x00040404047a0400uL, 0x00020202027c0200uL, 0x00010101017e0100uL,
            0x0080808080807e00uL, 0x0040404040403e00uL, 0x0020202020205e00uL, 0x0010101010106e00uL,
            0x0008080808087600uL, 0x0004040404047a00uL, 0x0002020202027c00uL, 0x0001010101017e00uL,
            0x008080808080807euL, 0x004040404040403euL, 0x002020202020205euL, 0x001010101010106euL,
            0x0008080808080876uL, 0x000404040404047auL, 0x000202020202027cuL, 0x000101010101017euL
        )

        private val BISHOP_MOVE_MASK = ulongArrayOf(
            0x0040201008040200uL, 0x0020100804020000uL, 0x0050080402000000uL, 0x0028440200000000uL,
            0x0014224000000000uL, 0x000a102040000000uL, 0x0004081020400000uL, 0x0002040810204000uL,
            0x0000402010080400uL, 0x0000201008040200uL, 0x0000500804020000uL, 0x0000284402000000uL,
            0x0000142240000000uL, 0x00000a1020400000uL, 0x0000040810204000uL, 0x0000020408102000uL,
            0x0040004020100800uL, 0x0020002010080400uL, 0x0050005008040200uL, 0x0028002844020000uL,
            0x0014001422400000uL, 0x000a000a10204000uL, 0x0004000408102000uL, 0x0002000204081000uL,
            0x0020400040201000uL, 0x0010200020100800uL, 0x0008500050080400uL, 0x0044280028440200uL,
            0x0022140014224000uL, 0x00100a000a102000uL, 0x0008040004081000uL, 0x0004020002040800uL,
            0x0010204000402000uL, 0x0008102000201000uL, 0x0004085000500800uL, 0x0002442800284400uL,
            0x0040221400142200uL, 0x0020100a000a1000uL, 0x0010080400040800uL, 0x0008040200020400uL,
            0x0008102040004000uL, 0x0004081020002000uL, 0x0002040850005000uL, 0x0000024428002800uL,
            0x0000402214001400uL, 0x004020100a000a00uL, 0x0020100804000400uL, 0x0010080402000200uL,
            0x0004081020400000uL, 0x0002040810200000uL, 0x0000020408500000uL, 0x0000000244280000uL,
            0x0000004022140000uL, 0x00004020100a0000uL, 0x0040201008040000uL, 0x0020100804020000uL,
            0x0002040810204000uL, 0x0000020408102000uL, 0x0000000204085000uL, 0x0000000002442800uL,
            0x0000000040221400uL, 0x0000004020100a00uL, 0x0000402010080400uL, 0x0040201008040200uL
        )

        private val KNIGHT_MOVE_MASK = ulongArrayOf(
            0x0020400000000000uL, 0x0010a00000000000uL, 0x0088500000000000uL, 0x0044280000000000uL,
            0x0022140000000000uL, 0x00110a0000000000uL, 0x0008050000000000uL, 0x0004020000000000uL,
            0x2000204000000000uL, 0x100010a000000000uL, 0x8800885000000000uL, 0x4400442800000000uL,
            0x2200221400000000uL, 0x1100110a00000000uL, 0x0800080500000000uL, 0x0400040200000000uL,
            0x4020002040000000uL, 0xa0100010a0000000uL, 0x5088008850000000uL, 0x2844004428000000uL,
            0x1422002214000000uL, 0x0a1100110a000000uL, 0x0508000805000000uL, 0x0204000402000000uL,
            0x0040200020400000uL, 0x00a0100010a00000uL, 0x0050880088500000uL, 0x0028440044280000uL,
            0x0014220022140000uL, 0x000a1100110a0000uL, 0x0005080008050000uL, 0x0002040004020000uL,
            0x0000402000204000uL, 0x0000a0100010a000uL, 0x0000508800885000uL, 0x0000284400442800uL,
            0x0000142200221400uL, 0x00000a1100110a00uL, 0x0000050800080500uL, 0x0000020400040200uL,
            0x0000004020002040uL, 0x000000a0100010a0uL, 0x0000005088008850uL, 0x0000002844004428uL,
            0x0000001422002214uL, 0x0000000a1100110auL, 0x0000000508000805uL, 0x0000000204000402uL,
            0x0000000040200020uL, 0x00000000a0100010uL, 0x0000000050880088uL, 0x0000000028440044uL,
            0x0000000014220022uL, 0x000000000a110011uL, 0x0000000005080008uL, 0x0000000002040004uL,
            0x0000000000402000uL, 0x0000000000a01000uL, 0x0000000000508800uL, 0x0000000000284400uL,
            0x0000000000142200uL, 0x00000000000a1100uL, 0x0000000000050800uL, 0x0000000000020400uL
        )

        private val ROOK_MAGICS = ulongArrayOf(
            0x0040003402410286uL, 0x4086023048010084uL, 0x0041000208040001uL, 0x0022002004100902uL,
            0x40045000200d0129uL, 0x0000081020004101uL, 0x102b001022804001uL, 0x00044a1200802102uL,
            0x00300c0505a84200uL, 0x2000100102080400uL, 0x0001041020400801uL, 0x0008000400800880uL,
            0x00d0000804004040uL, 0x0020004012210300uL, 0x00201008400124c0uL, 0x0140082240800180uL,
            0x012a040040820001uL, 0x012008b042040001uL, 0x1206001004020008uL, 0x4005004800050010uL,
            0x0000420020120008uL, 0x000040a001030050uL, 0x0c10004020084008uL, 0x4080004020004002uL,
            0x5010008402000061uL, 0x2830022104004810uL, 0x0842006812001004uL, 0x0400041101000800uL,
            0x0010000800801081uL, 0x0000402001001100uL, 0x0140008040802004uL, 0x4800400020800084uL,
            0x0020008200040041uL, 0x0880018400020810uL, 0x2002000200100409uL, 0x6240080100041100uL,
            0x00e40c2100100100uL, 0x4005001100200440uL, 0x0000208200410a00uL, 0x2080004040002008uL,
            0x0004120018490084uL, 0x0000040002100108uL, 0x6109010008040002uL, 0x2804008008000480uL,
            0x0008008008100680uL, 0x0202020020441080uL, 0x1910084008200042uL, 0x0080004000200044uL,
            0x1505002041860900uL, 0x0402000108040200uL, 0x2802001002000408uL, 0x0802000810200600uL,
            0x4011000810010020uL, 0x0001002000104100uL, 0x0029002040010082uL, 0x0002002100420080uL,
            0x01000c4122820100uL, 0x2300108200040100uL, 0x2900010002082400uL, 0x02800c0008001281uL,
            0x0100100100082004uL, 0x0200084200102080uL, 0x0240001000402001uL, 0x0580005021400080uL
        )

        private val BISHOP_MAGICS = ulongArrayOf(
            0x00042c0802440084uL, 0x4000208801c10400uL, 0x0100008810012200uL, 0x6000002209112400uL,
            0x020c020008420200uL, 0x4d00540206011120uL, 0x0010008048080400uL, 0x204042481409200auL,
            0x0242441104010000uL, 0x00505009014c0080uL, 0x104028a808482000uL, 0x2018002120410210uL,
            0x0100004042020200uL, 0x0020590441107044uL, 0x11124422011002e4uL, 0x2080410450404040uL,
            0x0042424407001020uL, 0x0522048404140181uL, 0x0840100400402820uL, 0x2000420204100200uL,
            0x050000c010401200uL, 0x0000120309021000uL, 0x0002011920421820uL, 0x0102121040180480uL,
            0x600814a024010300uL, 0x1208210406210080uL, 0x0201080600902200uL, 0x0020009004050040uL,
            0x0400601108080080uL, 0x0d01008a44880800uL, 0x042401a014440404uL, 0x140108a000c02402uL,
            0x000a120010210101uL, 0x500206a000441000uL, 0x049003000082410duL, 0x0003001003004000uL,
            0x0001080044004090uL, 0x00480404580224a8uL, 0x00901420300400a2uL, 0x00042406202004c5uL,
            0x0001401100525002uL, 0x1820602508080400uL, 0x0120209410080800uL, 0x0002009402510000uL,
            0x4008008c01202300uL, 0x48a802100090200buL, 0x11a4410208482100uL, 0x080408c809480801uL,
            0x000c010c02220280uL, 0x10c1040402080440uL, 0x4000084110900600uL, 0x0010040420402000uL,
            0x0104082040406a05uL, 0x2200088801002401uL, 0x0001042820810a00uL, 0x040a28e111020200uL,
            0x0080240200842005uL, 0x0344040a52100002uL, 0x00010420452a2040uL, 0x0401104002080040uL,
            0x4628048500400086uL, 0x1004412401050400uL, 0x1010024801252042uL, 0x0088508100510200uL
        )

        private val ROOK_MAGIC_INDEX_BITS = intArrayOf(
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12
        )

        private val BISHOP_MAGIC_INDEX_BITS = intArrayOf(
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6
        )
    }
}

fun main() {
    val board = Board("rnbqkbnr/pppppppp/8/8/8/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1")
    val bag = board.getMovements(Square.B1)
    println(bag.encodedDestinations.toBitboardStringFormatted())
}

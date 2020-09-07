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

import com.welyab.ankobachen.BitboardUtil.getBishopMagicIndexBits
import com.welyab.ankobachen.BitboardUtil.getBishopMagics
import com.welyab.ankobachen.BitboardUtil.getBishopMoveMask
import com.welyab.ankobachen.BitboardUtil.getBishopMovementDatabase
import com.welyab.ankobachen.BitboardUtil.getBlackPawnCaptureMoveMask
import com.welyab.ankobachen.BitboardUtil.getBlackPawnDoubleMoveMask
import com.welyab.ankobachen.BitboardUtil.getBlackPawnSingleMoveMask
import com.welyab.ankobachen.BitboardUtil.getKingMoveMask
import com.welyab.ankobachen.BitboardUtil.getKnightMoveMask
import com.welyab.ankobachen.BitboardUtil.getRookMagicIndexBits
import com.welyab.ankobachen.BitboardUtil.getRookMagics
import com.welyab.ankobachen.BitboardUtil.getRookMoveMask
import com.welyab.ankobachen.BitboardUtil.getRookMovementDatabase
import com.welyab.ankobachen.BitboardUtil.getWhitePawnCaptureMoveMask
import com.welyab.ankobachen.BitboardUtil.getWhitePawnDoubleMoveMask
import com.welyab.ankobachen.BitboardUtil.getWhitePawnSingleMoveMask
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
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.PAWN
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK
import com.welyab.ankobachen.extensions.shift
import java.nio.file.Files
import java.nio.file.Paths
import java.util.EnumMap
import kotlin.math.absoluteValue

class BoardException(
    message: String = "",
    cause: Throwable? = null
) : ChessException(message, cause)

@ExperimentalUnsignedTypes
private data class MoveLog(
    val bits: Map<Piece, ULong>,
    val halfMoveCounter: Int,
    val epTargetSquare: ULong,
    val castlingFlags: ULong
)

@ExperimentalUnsignedTypes
private interface PieceBitBoard {
    fun getBits(): ULong
    fun getPiece(): Piece
    fun setBits(bits: ULong)
}

@Suppress(
    "MemberVisibilityCanBePrivate",
    "unused"
)
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Board : Copyable<Board> {

    private var whiteKing: ULong = 0uL
    private var whiteQueens: ULong = 0uL
    private var whiteRooks: ULong = 0uL
    private var whiteBishops: ULong = 0uL
    private var whiteKnights: ULong = 0uL
    private var whitePawns: ULong = 0uL
    private var blackKing: ULong = 0uL
    private var blackQueens: ULong = 0uL
    private var blackRooks: ULong = 0uL
    private var blackBishops: ULong = 0uL
    private var blackKnights: ULong = 0uL
    private var blackPawns: ULong = 0uL

    private var sideToMove: Color = WHITE
    private var plyCounter: Int = 0
    private var halfMoveClock: Int = 0
    private var fullMoveCounter: Int = 0
    private var epTargetSquare: ULong = 0uL
    private var castlingFlags = 0uL
    private val moveLog = ArrayList<MoveLog>()

    private val bitboardByPiece = EnumMap<Piece, PieceBitBoard>(Piece::class.java).apply {
        this[WHITE_KING] = object : PieceBitBoard {
            override fun getPiece() = WHITE_KING
            override fun getBits() = whiteKing
            override fun setBits(bits: ULong) {
                whiteKing = bits
            }
        }
        this[WHITE_QUEEN] = object : PieceBitBoard {
            override fun getPiece() = WHITE_QUEEN
            override fun getBits() = whiteQueens
            override fun setBits(bits: ULong) {
                whiteQueens = bits
            }
        }
        this[WHITE_ROOK] = object : PieceBitBoard {
            override fun getPiece() = WHITE_ROOK
            override fun getBits() = whiteRooks
            override fun setBits(bits: ULong) {
                whiteRooks = bits
            }
        }
        this[WHITE_BISHOP] = object : PieceBitBoard {
            override fun getPiece() = WHITE_BISHOP
            override fun getBits() = whiteBishops
            override fun setBits(bits: ULong) {
                whiteBishops = bits
            }
        }
        this[WHITE_KNIGHT] = object : PieceBitBoard {
            override fun getPiece() = WHITE_KNIGHT
            override fun getBits() = whiteKnights
            override fun setBits(bits: ULong) {
                whiteKnights = bits
            }
        }
        this[WHITE_PAWN] = object : PieceBitBoard {
            override fun getPiece() = WHITE_PAWN
            override fun getBits() = whitePawns
            override fun setBits(bits: ULong) {
                whitePawns = bits
            }
        }
        this[BLACK_KING] = object : PieceBitBoard {
            override fun getPiece() = BLACK_KING
            override fun getBits() = blackKing
            override fun setBits(bits: ULong) {
                blackKing = bits
            }
        }
        this[BLACK_QUEEN] = object : PieceBitBoard {
            override fun getPiece() = BLACK_QUEEN
            override fun getBits() = blackQueens
            override fun setBits(bits: ULong) {
                blackQueens = bits
            }
        }
        this[BLACK_ROOK] = object : PieceBitBoard {
            override fun getPiece() = BLACK_ROOK
            override fun getBits() = blackRooks
            override fun setBits(bits: ULong) {
                blackRooks = bits
            }
        }
        this[BLACK_BISHOP] = object : PieceBitBoard {
            override fun getPiece() = BLACK_BISHOP
            override fun getBits() = blackBishops
            override fun setBits(bits: ULong) {
                blackBishops = bits
            }
        }
        this[BLACK_KNIGHT] = object : PieceBitBoard {
            override fun getPiece() = BLACK_KNIGHT
            override fun getBits() = blackKnights
            override fun setBits(bits: ULong) {
                blackKnights = bits
            }
        }
        this[BLACK_PAWN] = object : PieceBitBoard {
            override fun getPiece() = BLACK_PAWN
            override fun getBits() = blackPawns
            override fun setBits(bits: ULong) {
                blackPawns = bits
            }
        }
    }

    constructor(fen: String) {
        setFen(FenString(fen))
    }

    constructor(initialize: Boolean = true) {
        if (initialize) setFen(FEN_INITIAL)
    }

    fun setFen(fen: String): Unit = setFen(FenString(fen))

    fun getPieceLocations(): List<PieceLocation> = ArrayList<PieceLocation>().apply {
        getPieceLocations(WHITE, this)
        getPieceLocations(BLACK, this)
    }

    fun getPieceLocations(color: Color = sideToMove): List<PieceLocation> = ArrayList<PieceLocation>().apply {
        getPieceLocations(color, this)
    }

    fun getMovements(position: Position): Movements = getMovements(position.squareIndex, ALL_FLAGS or ALL_MOVEMENTS)
    fun getMovements(color: Color = sideToMove): Movements = getMovements(color, ALL_FLAGS or ALL_MOVEMENTS)
    fun getRandomMovement(): Movement = getMovements(sideToMove, ALL_FLAGS or ALL_MOVEMENTS).getRandomMovement()
    fun forEachMovement(visitor: (Movement) -> Unit) {
        getMovements().forEachMovement { visitor.invoke(it) }
    }

    fun withinEachMovement(visitor: Board.() -> Unit) {
        forEachMovement { movement ->
            move(movement)
            this.visitor()
            undo()
        }
    }

    fun moveRandom(): Unit = move(getRandomMovement())
    fun move(movement: Movement) = move(movement.from, movement.to, movement.toPiece, movement.flags)
    fun move(from: Position, to: Position, toPiece: PieceType = QUEEN) {
        val movement = getMovements(from.squareIndex, ALL_FLAGS or ALL_MOVEMENTS)
            .asSequenceOfMovements()
            .filter { it.to == to.squareIndex }
            .filter { !it.flags.isPromotion || it.toPiece.type == toPiece }
            .firstOrNull()
            ?: throw BoardException("can't find valid move from $from to $to")
        move(movement)
    }

    fun isSquareAttacked(attackedPosition: Position, attackerColor: Color): Boolean =
        isSquareAttacked(attackedPosition.squareIndex, attackerColor)

    fun getAttackers(attackedPosition: Position, attackerColor: Color): List<PieceLocation> =
        getAttackers(attackedPosition.squareIndex, attackerColor)

    fun hasPreviousMove(): Boolean = moveLog.isNotEmpty()

    fun getFen(): String = buildString {
        // r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -
        val piecesMap = getPieceLocations().asSequence().map { it.position.squareIndex to it.piece }.toMap()
        var emptySquares = 0
        for (squareIndex in 0..63) {
            if (squareIndex > 0 && squareIndex % 8 == 0) {
                if (emptySquares != 0) append(emptySquares)
                emptySquares = 0
                append('/')
            }
            val piece = piecesMap[squareIndex]
            if (piece == null) emptySquares++
            else {
                if (emptySquares != 0) append(emptySquares)
                emptySquares = 0
                append(piece.letter)
            }
        }
        if (emptySquares != 0) append(emptySquares)
        append(' ')
        append(sideToMove.letter)
        val castlingFlagsList = ArrayList<Char>(4)
        var cf = castlingFlags
        while (cf != ZERO) {
            val rookIndex = cf.countLeadingZeroBits()
            val rook = getPiece(rookIndex)
            val rookPosition = Position.from(rookIndex)
            castlingFlagsList += when (rook.color) {
                WHITE -> rookPosition.file.toUpperCase()
                BLACK -> rookPosition.file.toLowerCase()
            }
            cf = cf and FULL.shift(rookIndex + 1)
        }
        append(' ')
        if (castlingFlagsList.isEmpty()) {
            append('-')
        } else {
            castlingFlagsList.forEach { append(it) }
        }
        append(' ')
        if (epTargetSquare == ZERO) {
            append('-')
        } else {
            append(Position.from(epTargetSquare.countLeadingZeroBits()).getSanNotation())
        }
        append(' ')
        append(halfMoveClock)
        append(' ')
        append(fullMoveCounter)
    }

    fun undo() {
        val log = moveLog.removeLast()
        for (entry in log.bits) {
            setBitBoard(entry.key, entry.value)
        }
        halfMoveClock = log.halfMoveCounter
        if (sideToMove.isWhite) fullMoveCounter--
        plyCounter--
        epTargetSquare = log.epTargetSquare
        castlingFlags = log.castlingFlags
        sideToMove = sideToMove.opposite
    }

    override fun toString(): String = BoardPrinter.toString(this)

    private fun setFen(fen: FenString) {
        val fenInfo = fen.getFenInfo()
        for (pieceLocation in fenInfo.piecesDisposition) {
            setBitBoardBit(pieceLocation.piece, pieceLocation.position.squareIndex, true)
        }
        sideToMove = fenInfo.sideToMove
        halfMoveClock = fenInfo.halfMoveClock
        fullMoveCounter = fenInfo.fullMoveCounter
        if (fenInfo.epTarget != null)
            epTargetSquare = getMaskedSquare(fenInfo.epTarget.squareIndex)

        plyCounter = 0

        castlingFlags = ZERO

        if (fenInfo.castlingFlags.whiteRookOne != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.whiteRookOne.squareIndex)
        }
        if (fenInfo.castlingFlags.whiteRookTwo != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.whiteRookTwo.squareIndex)
        }
        if (fenInfo.castlingFlags.blackRookOne != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.blackRookOne.squareIndex)
        }
        if (fenInfo.castlingFlags.blackRookTwo != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.blackRookTwo.squareIndex)
        }
    }

    private fun getMovements(
        squareIndex: Int,
        @Suppress("SameParameterValue") moveGenFlags: Int
    ): Movements = Movements(
        listOf(
            getMovements(
                piece = getPiece(squareIndex),
                squareIndex = squareIndex,
                moveGenFlags = moveGenFlags
            )
        )
    )

    private fun getMovements(color: Color, moveGenFlags: Int): Movements {
        val pieceMovements = ArrayList<PieceMovement>(24)
        for (piece in Piece.values()) {
            if (piece.color != color) continue
            val bitboardPiece = bitboardByPiece[piece]!!
            getMovements(piece, bitboardPiece.getBits(), pieceMovements, moveGenFlags)
            if (moveGenFlags and ALL_MOVEMENTS == 0 && pieceMovements.isNotEmpty()) break
        }
        return Movements(pieceMovements)
    }

    private fun getMovements(
        piece: Piece,
        pieceBitBoard: ULong,
        pieceMovements: MutableList<PieceMovement>,
        moveGenFlags: Int
    ) {
        var bb = pieceBitBoard
        while (bb != ZERO) {
            val fromSquare = bb.countLeadingZeroBits()
            val pieceMovement = getMovements(
                piece = piece,
                squareIndex = fromSquare,
                moveGenFlags = moveGenFlags
            )
            if (pieceMovement.isNotEmpty()) pieceMovements += pieceMovement
            if (moveGenFlags and ALL_MOVEMENTS == 0 && pieceMovements.isNotEmpty()) break
            bb = bb and FULL.shift(fromSquare + 1)
        }
    }

    private fun getMovements(
        piece: Piece,
        squareIndex: Int,
        moveGenFlags: Int
    ): PieceMovement {
        val occupied = getOccupiedBitBoard(piece.color)
        return when (piece.type) {
            KING -> getKingMovements(piece, squareIndex, occupied, moveGenFlags)
            QUEEN -> getQueenMovements(piece, squareIndex, occupied, moveGenFlags)
            ROOK -> getRookMovements(piece, squareIndex, occupied, moveGenFlags)
            BISHOP -> getBishopMovements(piece, squareIndex, occupied, moveGenFlags)
            KNIGHT -> getKnightMovements(piece, squareIndex, occupied, moveGenFlags)
            PAWN -> getPawnMovements(piece, squareIndex, occupied, moveGenFlags)
        }
    }

    private fun getKingMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val targetSquares = KING_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets, moveGenFlags)
        getCastlingMovements(
            kingFrom = piece,
            fromSquare = fromSquare,
            targets = movementTargets,
            moveGenFlags = moveGenFlags
        )
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getCastlingMovements(
        kingFrom: Piece,
        fromSquare: Int,
        targets: MutableList<MovementTarget>,
        moveGenFlags: Int
    ) {
        var cFlags = when (kingFrom.color) {
            WHITE -> castlingFlags and RANK_8.inv()
            BLACK -> castlingFlags and RANK_1.inv()
        }
        while (cFlags != ZERO) {
            val rookFrom = cFlags.countLeadingZeroBits()
            getCastlingMovements(
                king = kingFrom,
                kingFromSquare = fromSquare,
                kingFinalSquare = getKingCastlingFinalSquare(kingFrom.color, fromSquare, rookFrom),
                rookFromSquare = rookFrom,
                rookFinalSquare = getRookCastlingFinalSquare(kingFrom.color, fromSquare, rookFrom),
                targets = targets,
                moveGenFlags = moveGenFlags
            )
            if (moveGenFlags and ALL_MOVEMENTS == 0 && targets.isNotEmpty()) break
            cFlags = cFlags and FULL.shift(rookFrom + 1)
        }
    }

    private fun getKingCastlingFinalSquare(color: Color, kingFrom: Int, rookFrom: Int): Int {
        return when (color) {
            WHITE -> if (kingFrom < rookFrom) Position.G1.squareIndex
            else Position.C1.squareIndex
            BLACK -> if (kingFrom < rookFrom) Position.G8.squareIndex
            else Position.C8.squareIndex
        }
    }

    private fun getRookCastlingFinalSquare(color: Color, kingFrom: Int, rookFrom: Int): Int {
        return when (color) {
            WHITE -> if (kingFrom < rookFrom) Position.F1.squareIndex
            else Position.D1.squareIndex
            BLACK -> if (kingFrom < rookFrom) Position.F8.squareIndex
            else Position.D8.squareIndex
        }
    }

    private fun getCastlingMovements(
        king: Piece,
        kingFromSquare: Int,
        kingFinalSquare: Int,
        rookFromSquare: Int,
        rookFinalSquare: Int,
        targets: MutableList<MovementTarget>,
        moveGenFlags: Int
    ) {
        setBitBoardBit(king.rook, rookFromSquare, false)
        getCastlingMovements2(
            king,
            kingFromSquare,
            kingFinalSquare,
            rookFromSquare,
            rookFinalSquare,
            targets,
            moveGenFlags
        )
        setBitBoardBit(king.rook, rookFromSquare, true)
    }

    private fun getCastlingMovements2(
        king: Piece,
        kingFromSquare: Int,
        kingFinalSquare: Int,
        rookFromSquare: Int,
        rookFinalSquare: Int,
        targets: MutableList<MovementTarget>,
        moveGenFlags: Int
    ) {
        if (isSquareAttacked(kingFromSquare, king.color.opposite)) return
        val kingMoveDirection = if (kingFromSquare < kingFinalSquare) 1 else -1
        var kingPathSquare = kingFromSquare
        val occupied = getOccupiedBitBoard().and(
            (getMaskedSquare(rookFromSquare) or getMaskedSquare(kingFromSquare)).inv()
        )
        while (kingPathSquare != kingFinalSquare) {
            kingPathSquare += kingMoveDirection
            if (isSquareAttacked(kingPathSquare, king.color.opposite)) return
            if (occupied and getMaskedSquare(kingPathSquare) != ZERO) return
        }
        val rookMoveDirection = if (rookFromSquare < rookFinalSquare) 1 else -1
        var rookPathSquare = rookFromSquare
        while (rookPathSquare != rookFinalSquare) {
            rookPathSquare += rookMoveDirection
            if (occupied and getMaskedSquare(rookPathSquare) != ZERO) return
        }
        var flags = MovementFlags.CASTLING_MASK
        if (moveGenFlags and ALL_FLAGS != 0) {
            flags = flags or extractExtraMovementFlags(
                fromSquare = kingFromSquare,
                toSquare = rookFromSquare,
                toPiece = king,
                isCastling = true,
                isEnPassant = false,
                isCapture = false,
                isPromotion = false
            )
        }
        targets += MovementTarget(
            king,
            rookFromSquare,
            MovementFlags(flags)
        )
    }

    private fun getQueenMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets, moveGenFlags)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getRookMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets, moveGenFlags)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getBishopMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets, moveGenFlags)
        return PieceMovement(fromSquare, movementTargets)
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
            else -> throw BoardException("no sliding piece: $piece")
        }
    }

    private fun getQueenTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val rookTargets = getRookTargetSquares(squareIndex, ownPieces)
        val bishopTargets = getBishopTargetSquares(squareIndex, ownPieces)
        return rookTargets or bishopTargets
    }

    private fun getRookTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = ROOK_MOVE_MASK[squareIndex] and getOccupiedBitBoard()
        val key = (blockers * ROOK_MAGICS[squareIndex]).shift(64 - ROOK_MAGIC_INDEX_BITS[squareIndex])
        return ROOK_MOVEMENT_DATABASE[squareIndex][key.toInt()] and ownPieces.inv()
    }

    private fun getBishopTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = BISHOP_MOVE_MASK[squareIndex] and getOccupiedBitBoard()
        val key = (blockers * BISHOP_MAGICS[squareIndex]).shift(64 - BISHOP_MAGIC_INDEX_BITS[squareIndex])
        return BISHOP_MOVEMENT_DATABASE[squareIndex][key.toInt()] and ownPieces.inv()
    }

    private fun getKnightMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val targetSquares = KNIGHT_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets, moveGenFlags)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getPawnMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        return when (piece.color) {
            WHITE -> getWhitePawnMovements(piece, fromSquare, ownPieces, moveGenFlags)
            BLACK -> getBlackPawnMovements(piece, fromSquare, ownPieces, moveGenFlags)
        }
    }

    private fun getWhitePawnMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            WHITE_PAWN_SINGLE_MOVE_MASK[fromSquare],
            WHITE_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(
            piece,
            fromSquare,
            targetSquares,
            moveGenFlags
        )
    }

    private fun getBlackPawnMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            BLACK_PAWN_SINGLE_MOVE_MASK[fromSquare],
            BLACK_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(
            piece,
            fromSquare,
            targetSquares,
            moveGenFlags
        )
    }

    private fun getPawnTargetSquares(
        ownPieces: ULong,
        allPieces: ULong,
        captureMask: ULong,
        singleSquareMoveMask: ULong,
        doubleSquareMoveMask: ULong
    ): ULong {
        var targetSquares = captureMask and (allPieces or epTargetSquare) and ownPieces.inv()
        targetSquares = targetSquares or (singleSquareMoveMask xor allPieces).and(singleSquareMoveMask)
        if (doubleSquareMoveMask and allPieces == ZERO)
            targetSquares = targetSquares or (doubleSquareMoveMask xor allPieces).and(doubleSquareMoveMask)
        return targetSquares
    }

    private fun mountPawnMovements(
        pawn: Piece,
        fromSquare: Int,
        targetSquares: ULong,
        moveGenFlags: Int
    ): PieceMovement {
        var t = targetSquares
        val movementTargets = ArrayList<MovementTarget>()
        while (t != ZERO) {
            val toSquare = t.countLeadingZeroBits()
            val isEnpassant = (
                    (fromSquare - toSquare).absoluteValue == 9
                            || (fromSquare - toSquare).absoluteValue == 7
                    ) && isEmpty(toSquare)
            val isCapture = isEnpassant || isNotEmpty(toSquare)
            if (
                isMovementValid(
                    fromSquare,
                    toSquare,
                    pawn,
                    isEnpassant,
                    isCapture
                )
            ) {
                val isPromotion = ((RANK_1 or RANK_8) and getMaskedSquare(toSquare)) != ZERO
                for (targetPiece in getPawnTargetPieces(pawn, isPromotion)) {
                    var flags = 0uL
                    if (isEnpassant) flags = flags or MovementFlags.EN_PASSANT_MASK
                    if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                    if (isPromotion) flags = flags or MovementFlags.PROMOTION_MASK
                    if (moveGenFlags and ALL_FLAGS != 0) {
                        flags = flags or extractExtraMovementFlags(
                            fromSquare = fromSquare,
                            toSquare = toSquare,
                            toPiece = targetPiece,
                            isCastling = false,
                            isEnPassant = isEnpassant,
                            isCapture = isCapture,
                            isPromotion = isPromotion
                        )
                    }
                    movementTargets += MovementTarget(
                        targetPiece,
                        toSquare,
                        MovementFlags(flags)
                    )
                    if (moveGenFlags and ALL_MOVEMENTS == 0) break
                }
            }
            t = t and FULL.shift(toSquare + 1)
        }
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getPawnTargetPieces(piece: Piece, isPromotion: Boolean): List<Piece> {
        return if (isPromotion) when (piece.color) {
            WHITE -> WHITE_PAWN_REPLACEMENTS
            BLACK -> BLACK_PAWN_REPLACEMENTS
        } else when (piece.color) {
            WHITE -> WHITE_PAWN_NON_REPLACEMENT
            BLACK -> BLACK_PAWN_NON_REPLACEMENT
        }
    }

    private fun mountNonPawnMovements(
        piece: Piece,
        fromIndex: Int,
        targetSquares: ULong,
        movementTargets: ArrayList<MovementTarget>,
        moveGenFlags: Int
    ) {
        var s = targetSquares
        while (s != EMPTY) {
            val toIndex = s.countLeadingZeroBits()
            val isCapture = isNotEmpty(toIndex)
            if (
                isMovementValid(
                    fromIndex,
                    toIndex,
                    piece,
                    false,
                    isCapture
                )
            ) {
                var flags = ZERO
                if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                if (moveGenFlags and ALL_FLAGS != 0) {
                    flags = flags or extractExtraMovementFlags(
                        fromSquare = fromIndex,
                        toSquare = toIndex,
                        toPiece = piece,
                        isCastling = false,
                        isEnPassant = false,
                        isCapture = isCapture,
                        isPromotion = false
                    )
                }
                movementTargets += MovementTarget(
                    piece,
                    toIndex,
                    MovementFlags(flags)
                )
                if (moveGenFlags and ALL_FLAGS == 0) break
            }
            s = s and FULL.shift(toIndex + 1)
        }
    }

    private fun extractExtraMovementFlags(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isCastling: Boolean,
        isEnPassant: Boolean,
        isCapture: Boolean,
        isPromotion: Boolean
    ): ULong {
        move(
            fromSquare = fromSquare,
            toSquare = toSquare,
            toPiece = toPiece,
            isCastling = isCastling,
            isEnPassant = isEnPassant,
            isCapture = isCapture,
            isPromotion = isPromotion
        )
        val kingIndex = when (toPiece.color) {
            WHITE -> blackKing.countLeadingZeroBits()
            BLACK -> whiteKing.countLeadingZeroBits()
        }
        val attackers = getAttackersBits(kingIndex, toPiece.color)
        val attackersCount = attackers.countOneBits()
        var extraFlags = 0uL
        if (attackersCount > 0) {
            extraFlags = extraFlags or MovementFlags.CHECK_MASK
        }
        if (attackersCount == 2) {
            extraFlags = extraFlags or MovementFlags.DOUBLE_CHECK_MASK
        } else if (attackersCount == 1) {
            if (!toPiece.isKing) setBitBoardBit(toPiece, toSquare, false)
            if (attackers and getMaskedSquare(toSquare) == ZERO) {
                val rays = getQueenTargetSquares(kingIndex, getOccupiedBitBoard(toPiece.color.opposite))
                if (rays and getMaskedSquare(fromSquare) != ZERO) {
                    extraFlags = extraFlags or MovementFlags.DISCOVERY_CHECK_MASK
                }
            }
            if (!toPiece.isKing) setBitBoardBit(toPiece, toSquare, true)
        }

        val kingMovements = getMovements(
            piece = toPiece.oppositeKing,
            squareIndex = kingIndex,
            moveGenFlags = 0
        )
        if (attackersCount == 2 && kingMovements.isEmpty()) {
            extraFlags = extraFlags or MovementFlags.CHECKMATE_MASK
        } else if (
            kingMovements.isEmpty()
            && getMovements(
                color = toPiece.color.opposite,
                moveGenFlags = 0
            ).isEmpty()
        ) {
            extraFlags = if (attackersCount > 0) {
                extraFlags or MovementFlags.CHECKMATE_MASK
            } else {
                extraFlags or MovementFlags.STALEMATE_MASK
            }
        }

        undo()
        return extraFlags
    }

    private fun isMovementValid(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isEnPassant: Boolean,
        isCapture: Boolean
    ): Boolean {
        move(
            fromSquare = fromSquare,
            toSquare = toSquare,
            toPiece = toPiece,
            isCastling = false,
            isEnPassant = isEnPassant,
            isCapture = isCapture,
            isPromotion = false
        )
        val isSquareAttacked = when (toPiece.color) {
            WHITE -> isSquareAttacked(whiteKing.countLeadingZeroBits(), BLACK)
            BLACK -> isSquareAttacked(blackKing.countLeadingZeroBits(), WHITE)
        }
        undo()
        return !isSquareAttacked
    }

    private fun isSquareAttacked(fromSquare: Int, color: Color): Boolean {
        return getAttackersBits(fromSquare, color) != ZERO
    }

    private fun getAttackersBits(fromSquare: Int, color: Color): ULong {
        val occupied = getOccupiedBitBoard(color.opposite)
        val queenPositions = getPieceBitBoard(QUEEN, color)

        val rookPositions = getPieceBitBoard(ROOK, color)
        val rookRays = getRookTargetSquares(fromSquare, occupied)
        var attackers = rookRays and (rookPositions or queenPositions)

        val bishopPositions = getPieceBitBoard(BISHOP, color)
        val bishopRays = getBishopTargetSquares(fromSquare, occupied)
        attackers = attackers or (bishopRays and (bishopPositions or queenPositions))

        attackers = attackers or (KING_MOVE_MASK[fromSquare] and getPieceBitBoard(KING, color))
        attackers = attackers or (KNIGHT_MOVE_MASK[fromSquare] and getPieceBitBoard(KNIGHT, color))

        attackers = attackers or when (color) {
            WHITE -> BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(WHITE_PAWN)
            BLACK -> WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(BLACK_PAWN)
        }

        return attackers
    }

    private fun getAttackers(attackedSquare: Int, attackerColor: Color): List<PieceLocation> {
        val attackersBits = getAttackersBits(attackedSquare, attackerColor)
        val attackers = ArrayList<PieceLocation>()
        when (attackerColor) {
            WHITE -> {
                getPieceLocations(WHITE_KING, whiteKing and attackersBits, attackers)
                getPieceLocations(WHITE_QUEEN, whiteQueens and attackersBits, attackers)
                getPieceLocations(WHITE_ROOK, whiteRooks and attackersBits, attackers)
                getPieceLocations(WHITE_BISHOP, whiteBishops and attackersBits, attackers)
                getPieceLocations(WHITE_KNIGHT, whiteKnights and attackersBits, attackers)
                getPieceLocations(WHITE_PAWN, whitePawns and attackersBits, attackers)
            }
            BLACK -> {
                getPieceLocations(BLACK_KING, blackKing and attackersBits, attackers)
                getPieceLocations(BLACK_QUEEN, blackQueens and attackersBits, attackers)
                getPieceLocations(BLACK_ROOK, blackRooks and attackersBits, attackers)
                getPieceLocations(BLACK_BISHOP, blackBishops and attackersBits, attackers)
                getPieceLocations(BLACK_KNIGHT, blackKnights and attackersBits, attackers)
                getPieceLocations(BLACK_PAWN, blackPawns and attackersBits, attackers)
            }
        }
        return attackers
    }

    private fun isEmpty(fromSquare: Int): Boolean = getOccupiedBitBoard() and getMaskedSquare(fromSquare) == ZERO
    private fun isNotEmpty(fromSquare: Int) = !isEmpty(fromSquare)

    private fun getPiece(fromSquare: Int): Piece {
        val maskedSquare = getMaskedSquare(fromSquare)
        return when {
            whiteKing and maskedSquare != ZERO -> WHITE_KING
            whiteQueens and maskedSquare != ZERO -> WHITE_QUEEN
            whiteRooks and maskedSquare != ZERO -> WHITE_ROOK
            whiteBishops and maskedSquare != ZERO -> WHITE_BISHOP
            whiteKnights and maskedSquare != ZERO -> WHITE_KNIGHT
            whitePawns and maskedSquare != ZERO -> WHITE_PAWN
            blackKing and maskedSquare != ZERO -> BLACK_KING
            blackQueens and maskedSquare != ZERO -> BLACK_QUEEN
            blackRooks and maskedSquare != ZERO -> BLACK_ROOK
            blackBishops and maskedSquare != ZERO -> BLACK_BISHOP
            blackKnights and maskedSquare != ZERO -> BLACK_KNIGHT
            blackPawns and maskedSquare != ZERO -> BLACK_PAWN
            else -> throw BoardException("empty square: ${Position.from(fromSquare)}")
        }
    }

    private fun getPieceBitBoard(pieceType: PieceType, color: Color): ULong {
        return getPieceBitBoard(Piece.from(pieceType, color))
    }

    private fun getPieceBitBoard(piece: Piece): ULong {
        return bitboardByPiece[piece]!!.getBits()
    }

    private fun setBitBoardBit(piece: Piece, bitIndex: Int, value: Boolean) {
        var bitboard = getPieceBitBoard(piece)
        bitboard = if (value) bitboard or getMaskedSquare(bitIndex)
        else bitboard and getMaskedSquare(bitIndex).inv()
        setBitBoard(piece, bitboard)
    }

    private fun setBitBoard(piece: Piece, bitboard: ULong) {
        bitboardByPiece[piece]!!.setBits(bitboard)
    }

    private fun getPieceLocations(color: Color, locations: MutableList<PieceLocation>) {
        when (color) {
            WHITE -> {
                getPieceLocations(WHITE_KING, whiteKing, locations)
                getPieceLocations(WHITE_QUEEN, whiteQueens, locations)
                getPieceLocations(WHITE_ROOK, whiteRooks, locations)
                getPieceLocations(WHITE_BISHOP, whiteBishops, locations)
                getPieceLocations(WHITE_KNIGHT, whiteKnights, locations)
                getPieceLocations(WHITE_PAWN, whitePawns, locations)
            }
            BLACK -> {
                getPieceLocations(BLACK_KING, blackKing, locations)
                getPieceLocations(BLACK_QUEEN, blackQueens, locations)
                getPieceLocations(BLACK_ROOK, blackRooks, locations)
                getPieceLocations(BLACK_BISHOP, blackBishops, locations)
                getPieceLocations(BLACK_KNIGHT, blackKnights, locations)
                getPieceLocations(BLACK_PAWN, blackPawns, locations)
            }
        }
    }

    private fun getPieceLocations(piece: Piece, pieceBitboard: ULong, positions: MutableList<PieceLocation>) {
        var bbt = pieceBitboard
        while (bbt != ZERO) {
            val bitPosition = bbt.countLeadingZeroBits()
            positions += PieceLocation(piece, Position.from(bitPosition))
            bbt = bbt and FULL.shift(bitPosition + 1)
        }
    }

    private fun getMaskedSquare(fromSquare: Int): ULong = HIGHEST_BIT.shift(fromSquare)

    private fun getOccupiedBitBoard(): ULong {
        return getOccupiedBitBoard(WHITE) or getOccupiedBitBoard(BLACK)
    }

    private fun getOccupiedBitBoard(color: Color): ULong {
        return when (color) {
            WHITE -> whiteKing or whiteQueens or whiteRooks or whiteBishops or whiteKnights or whitePawns
            BLACK -> blackKing or blackQueens or blackRooks or blackBishops or blackKnights or blackPawns
        }
    }

    private fun move(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        flags: MovementFlags
    ) {
        move(
            fromSquare = fromSquare,
            toSquare = toSquare,
            toPiece = toPiece,
            isCastling = flags.isCastling,
            isEnPassant = flags.isEnPassant,
            isCapture = flags.isCapture,
            isPromotion = flags.isPromotion
        )
    }

    private fun move(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isCastling: Boolean,
        isEnPassant: Boolean,
        isCapture: Boolean,
        isPromotion: Boolean
    ) {
        val fromPiece = if (isPromotion) toPiece.pawn else toPiece
        val enPassantCapturedSquare = getEnPassantCapturedSquare(toPiece.color, toSquare)
        val capturedPiece = when {
            isEnPassant -> getPiece(enPassantCapturedSquare)
            isCapture -> getPiece(toSquare)
            else -> null
        }

        val map = HashMap<Piece, ULong>()
        map[fromPiece] = getPieceBitBoard(fromPiece)
        if (capturedPiece != null) map[capturedPiece] = getPieceBitBoard(capturedPiece)
        if (isCastling) map[fromPiece.rook] = getPieceBitBoard(fromPiece.rook)
        if (isPromotion) map[toPiece] = getPieceBitBoard(toPiece)
        moveLog += MoveLog(
            bits = map,
            halfMoveCounter = halfMoveClock,
            epTargetSquare = epTargetSquare,
            castlingFlags = castlingFlags
        )
        if (isCastling) {
            val kingDestination = getKingCastlingFinalSquare(fromPiece.color, fromSquare, toSquare)
            val rookDestination = getRookCastlingFinalSquare(fromPiece.color, fromSquare, toSquare)
            setBitBoardBit(fromPiece, fromSquare, false)
            setBitBoardBit(fromPiece.rook, toSquare, false)
            setBitBoardBit(fromPiece, kingDestination, true)
            setBitBoardBit(fromPiece.rook, rookDestination, true)
        } else {
            setBitBoardBit(fromPiece, fromSquare, false)
            setBitBoardBit(toPiece, toSquare, true)
            if (capturedPiece != null) {
                setBitBoardBit(
                    capturedPiece,
                    if (isEnPassant) enPassantCapturedSquare else toSquare,
                    false
                )
            }
        }
        if (fromPiece.isKing) {
            castlingFlags = when (fromPiece.color) {
                WHITE -> castlingFlags and RANK_1.inv()
                BLACK -> castlingFlags and RANK_8.inv()
            }
        } else if (fromPiece.isRook) {
            castlingFlags = castlingFlags and getMaskedSquare(fromSquare).inv()
        }
        if (capturedPiece != null && capturedPiece.isRook) {
            castlingFlags = castlingFlags and getMaskedSquare(toSquare).inv()
        }
        epTargetSquare =
            if (fromPiece.isPawn && (fromSquare - toSquare).absoluteValue == 16)
                when (fromPiece.color) {
                    WHITE -> WHITE_PAWN_SINGLE_MOVE_MASK[fromSquare]
                    BLACK -> BLACK_PAWN_SINGLE_MOVE_MASK[fromSquare]
                }
            else EMPTY

        halfMoveClock = if (fromPiece.isPawn || isCapture) halfMoveClock + 1 else 0
        if (sideToMove.isBlack) fullMoveCounter++
        plyCounter++
        sideToMove = sideToMove.opposite
    }

    private fun getEnPassantCapturedSquare(color: Color, toSquare: Int): Int {
        return when (color) {
            WHITE -> toSquare + 8
            BLACK -> toSquare - 8
        }
    }

    override fun copy(): Board {
        val copy = Board(false)

        copy.whiteKing = whiteKing
        copy.whiteQueens = whiteQueens
        copy.whiteRooks = whiteRooks
        copy.whiteBishops = whiteBishops
        copy.whiteKnights = whiteKnights
        copy.whitePawns = whitePawns
        copy.blackKing = blackKing
        copy.blackQueens = blackQueens
        copy.blackRooks = blackRooks
        copy.blackBishops = blackBishops
        copy.blackKnights = blackKnights
        copy.blackPawns = blackPawns

        copy.sideToMove = sideToMove
        copy.plyCounter = plyCounter
        copy.halfMoveClock = halfMoveClock
        copy.fullMoveCounter = fullMoveCounter
        copy.epTargetSquare = epTargetSquare
        copy.castlingFlags = castlingFlags

        copy.moveLog.addAll(moveLog)

        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (whiteKing != other.whiteKing) return false
        if (whiteQueens != other.whiteQueens) return false
        if (whiteRooks != other.whiteRooks) return false
        if (whiteBishops != other.whiteBishops) return false
        if (whiteKnights != other.whiteKnights) return false
        if (whitePawns != other.whitePawns) return false
        if (blackKing != other.blackKing) return false
        if (blackQueens != other.blackQueens) return false
        if (blackRooks != other.blackRooks) return false
        if (blackBishops != other.blackBishops) return false
        if (blackKnights != other.blackKnights) return false
        if (blackPawns != other.blackPawns) return false
        if (sideToMove != other.sideToMove) return false
        if (plyCounter != other.plyCounter) return false
        if (halfMoveClock != other.halfMoveClock) return false
        if (fullMoveCounter != other.fullMoveCounter) return false
        if (epTargetSquare != other.epTargetSquare) return false
        if (castlingFlags != other.castlingFlags) return false
        if (moveLog != other.moveLog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = whiteKing.hashCode()
        result = 31 * result + whiteQueens.hashCode()
        result = 31 * result + whiteRooks.hashCode()
        result = 31 * result + whiteBishops.hashCode()
        result = 31 * result + whiteKnights.hashCode()
        result = 31 * result + whitePawns.hashCode()
        result = 31 * result + blackKing.hashCode()
        result = 31 * result + blackQueens.hashCode()
        result = 31 * result + blackRooks.hashCode()
        result = 31 * result + blackBishops.hashCode()
        result = 31 * result + blackKnights.hashCode()
        result = 31 * result + blackPawns.hashCode()
        result = 31 * result + sideToMove.hashCode()
        result = 31 * result + plyCounter
        result = 31 * result + halfMoveClock
        result = 31 * result + fullMoveCounter
        result = 31 * result + epTargetSquare.hashCode()
        result = 31 * result + castlingFlags.hashCode()
        result = 31 * result + moveLog.hashCode()
        return result
    }

    companion object {
        @Suppress("SpellCheckingInspection")
        const val FEN_INITIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

        private const val ALL_MOVEMENTS = 1
        private const val ALL_FLAGS = 2

        private const val ZERO: ULong = 0u
        private const val EMPTY: ULong = ZERO
        private const val FULL: ULong = ULong.MAX_VALUE
        private const val HIGHEST_BIT = 0x8000000000000000uL

        private val KING_MOVE_MASK = getKingMoveMask().toULongArray()
        private val ROOK_MOVE_MASK = getRookMoveMask().toULongArray()
        private val BISHOP_MOVE_MASK = getBishopMoveMask().toULongArray()
        private val KNIGHT_MOVE_MASK = getKnightMoveMask().toULongArray()
        private val ROOK_MOVEMENT_DATABASE = getRookMovementDatabase()
        private val BISHOP_MOVEMENT_DATABASE = getBishopMovementDatabase()
        private val WHITE_PAWN_SINGLE_MOVE_MASK = getWhitePawnSingleMoveMask().toULongArray()
        private val BLACK_PAWN_SINGLE_MOVE_MASK = getBlackPawnSingleMoveMask().toULongArray()
        private val WHITE_PAWN_CAPTURE_MOVE_MASK = getWhitePawnCaptureMoveMask().toULongArray()
        private val BLACK_PAWN_CAPTURE_MOVE_MASK = getBlackPawnCaptureMoveMask().toULongArray()
        private val WHITE_PAWN_DOUBLE_MOVE_MASK = getWhitePawnDoubleMoveMask().toULongArray()
        private val BLACK_PAWN_DOUBLE_MOVE_MASK = getBlackPawnDoubleMoveMask().toULongArray()
        private val ROOK_MAGICS = getRookMagics().toULongArray()
        private val BISHOP_MAGICS = getBishopMagics().toULongArray()
        private val ROOK_MAGIC_INDEX_BITS = getRookMagicIndexBits().toIntArray()
        private val BISHOP_MAGIC_INDEX_BITS = getBishopMagicIndexBits().toIntArray()
        private val WHITE_PAWN_REPLACEMENTS = listOf(WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT)
        private val BLACK_PAWN_REPLACEMENTS = listOf(BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT)
        private val WHITE_PAWN_NON_REPLACEMENT = listOf(WHITE_PAWN)
        private val BLACK_PAWN_NON_REPLACEMENT = listOf(BLACK_PAWN)
        private val RANK_1 = 0x00000000000000ffuL
        private val RANK_8 = 0xff00000000000000uL
        private val LEFT_CASTLING_FINAL_POSITIONS = 0x3000000000000030uL
        private val RIGHT_CASTLING_FINAL_POSITIONS = 0x0600000000000006uL
    }
}

@ExperimentalStdlibApi
fun main() {
    Files.lines(Paths.get("C:\\Users\\wsp_e\\OneDrive\\Área de Trabalho\\fen.txt"))
        .filter { it.count { char -> char == '/' } == 7 }
        .forEach { println(it) }
}

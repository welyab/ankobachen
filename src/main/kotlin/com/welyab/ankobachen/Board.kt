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
import com.welyab.ankobachen.MovementFlags.Companion.LEFT_CASTLING_MASK
import com.welyab.ankobachen.MovementFlags.Companion.RIGHT_CASTLING_MASK
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
import com.welyab.ankobachen.old.NEWLINE
import kotlin.math.absoluteValue

class BoardException(
    message: String = "",
    cause: Throwable? = null
) : ChessException(message, cause)

@ExperimentalUnsignedTypes
private data class MoveLog(
    val fromPiece: Piece,
    val fromSquare: Int,
    val toPiece: Piece,
    val toSquare: Int,
    val isEnPassant: Boolean,
    val isLeftCastling: Boolean,
    val isRightCastling: Boolean,
    val halfMoveCounter: Int,
    val epTargetSquare: ULong,
    val castlingFlags: ULong,
    val capturedPiece: Piece?
)

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
    private var rookPositions: ULong = 0uL
    private var castlingFlags = 0uL
    private val moveLog = ArrayList<MoveLog>()

    constructor() : this(FEN_INITIAL)

    constructor(fen: String) {
        setFen(FenString(fen))
    }

    constructor(initialize: Boolean) {
        if (initialize) setFen(FEN_INITIAL)
    }

    fun setFen(fen: String): Unit = setFen(FenString(fen))

    fun getActivePieceLocations(): List<PieceLocation> = getPieceLocations2(sideToMove)
    fun getPieceLocations(): List<PieceLocation> = getPieceLocations2(BLACK) + getPieceLocations2(WHITE)
    fun getPieceLocations(color: Color): List<PieceLocation> = getPieceLocations2(color)

    fun getMovements(position: Position): Movements = getMovements(position.squareIndex)
    fun getMovements(color: Color): Movements = getMovements2(color)
    fun getMovements(): Movements = getMovements2(sideToMove)
    fun getMovementRandom(): Movement = getMovements2(sideToMove).getRandomMovement()

    fun moveRandom(): Unit = move(getMovementRandom())
    fun move(movement: Movement) = move(movement.from, movement.to, movement.toPiece, movement.flags)
    fun move(from: Position, to: Position, toPiece: PieceType = QUEEN) {
        val movement = getMovements(from.squareIndex)
            .asSequenceOfMovements()
            .filter { it.to == to.squareIndex }
            .filter { !it.flags.isPromotion || it.toPiece.type == toPiece }
            .firstOrNull()
            ?: throw BoardException("can't find valid move from $from to $to")
        move(movement)
    }

    fun isSquareAttacked(position: Position, attackerColor: Color): Boolean =
        isSquareAttacked(position.squareIndex, attackerColor)

    fun hasPreviousMove(): Boolean = moveLog.isNotEmpty()

    fun undo() {
        val log = moveLog.removeLast()

        if (log.isLeftCastling || log.isRightCastling) {
            val finalPositions = when (log.fromPiece.color) {
                WHITE -> if (log.isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> if (log.isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }

            val kingDestination = if (log.isLeftCastling) finalPositions.countLeadingZeroBits()
            else ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1

            val rookDestination = if (log.isLeftCastling) ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1
            else finalPositions.countLeadingZeroBits()

            setBitBoardBit(log.fromPiece, kingDestination, false)
            setBitBoardBit(log.fromPiece.getRook(), rookDestination, false)
            setBitBoardBit(log.fromPiece, log.fromSquare, true)
            setBitBoardBit(log.fromPiece.getRook(), log.toSquare, true)
        } else {
            setBitBoardBit(log.fromPiece, log.fromSquare, true)
            setBitBoardBit(log.toPiece, log.toSquare, false)
            if (log.isEnPassant) {
                val enPassantCaptured = getEnPassantCapturedSquare(sideToMove.opposite, log.toSquare)
                setBitBoardBit(log.capturedPiece!!, enPassantCaptured, true)
            } else if (log.capturedPiece != null) {
                setBitBoardBit(log.capturedPiece, log.toSquare, true)
            }
        }
        halfMoveClock = log.halfMoveCounter
        if (sideToMove.isWhite) fullMoveCounter--
        plyCounter--
        epTargetSquare = log.epTargetSquare
        castlingFlags = log.castlingFlags
        sideToMove = sideToMove.opposite
    }

    override fun toString(): String {
        val pieceLocations = getPieceLocations()
        return (0..7).asSequence()
            .map { row ->
                val rowPieces = pieceLocations
                    .asSequence()
                    .filter { it.position.row == row }
                    .sortedBy { it.position.column }
                    .toList()
                val rowLetters = (0..7).asSequence()
                    .map { column ->
                        rowPieces.asSequence()
                            .filter { it.position.column == column }
                            .map { it.piece.letter }
                            .firstOrNull()
                            ?: ' '
                    }
                    .toList()
                    .toTypedArray()
                "│ %c │ %c │ %c │ %c │ %c │ %c │ %c │ %c │".format(*rowLetters)
            }
            .reduce { l1, l2 -> "$l1${NEWLINE}├───┼───┼───┼───┼───┼───┼───┼───┤$NEWLINE$l2" }
            .let {
                "┌───┬───┬───┬───┬───┬───┬───┬───┐$NEWLINE$it${NEWLINE}└───┴───┴───┴───┴───┴───┴───┴───┘$NEWLINE"
            }
    }

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
        rookPositions = ZERO

        if (fenInfo.castlingFlags.leftWhiteRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.leftWhiteRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.leftWhiteRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.A1.squareIndex)
        }

        if (fenInfo.castlingFlags.rightWhiteRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.rightWhiteRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.rightWhiteRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.H1.squareIndex)
        }

        if (fenInfo.castlingFlags.leftBlackRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.leftBlackRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.leftBlackRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.A8.squareIndex)
        }

        if (fenInfo.castlingFlags.rightBlackRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.rightBlackRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.rightBlackRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.H8.squareIndex)
        }
    }

    private fun getMovements(squareIndex: Int): Movements = Movements(
        listOf(getMovements(getPiece(squareIndex), squareIndex))
    )

    private fun getMovements2(color: Color): Movements {
        val pieceMovements = ArrayList<PieceMovement>(24)
        when (color) {
            WHITE -> {
                getMovements(WHITE_KING, whiteKing, pieceMovements)
                getMovements(WHITE_QUEEN, whiteQueens, pieceMovements)
                getMovements(WHITE_ROOK, whiteRooks, pieceMovements)
                getMovements(WHITE_BISHOP, whiteBishops, pieceMovements)
                getMovements(WHITE_KNIGHT, whiteKnights, pieceMovements)
                getMovements(WHITE_PAWN, whitePawns, pieceMovements)
            }
            BLACK -> {
                getMovements(BLACK_KING, blackKing, pieceMovements)
                getMovements(BLACK_QUEEN, blackQueens, pieceMovements)
                getMovements(BLACK_ROOK, blackRooks, pieceMovements)
                getMovements(BLACK_BISHOP, blackBishops, pieceMovements)
                getMovements(BLACK_KNIGHT, blackKnights, pieceMovements)
                getMovements(BLACK_PAWN, blackPawns, pieceMovements)
            }
        }
        return Movements(pieceMovements)
    }

    private fun getMovements(piece: Piece, pieceBitBoard: ULong, pieceMovements: MutableList<PieceMovement>) {
        var bb = pieceBitBoard
        while (bb != ZERO) {
            val fromSquare = bb.countLeadingZeroBits()
            pieceMovements += getMovements(piece, fromSquare)
            bb = bb and FULL.shift(fromSquare + 1)
        }
    }

    private fun getMovements(piece: Piece, squareIndex: Int): PieceMovement {
        val occupied = getOccupiedBitBoard(piece.color)
        return when (piece.type) {
            KING -> getKingMovements(piece, squareIndex, occupied)
            QUEEN -> getQueenMovements(piece, squareIndex, occupied)
            ROOK -> getRookMovements(piece, squareIndex, occupied)
            BISHOP -> getBishopMovements(piece, squareIndex, occupied)
            KNIGHT -> getKnightMovements(piece, squareIndex, occupied)
            PAWN -> getPawnMovements(piece, squareIndex, occupied)
        }
    }

    private fun getKingMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = KING_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
        getCastlingMovements(piece, fromSquare, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getCastlingMovements(
        fromPiece: Piece,
        fromSquare: Int,
        targets: MutableList<MovementTarget>
    ) {
        val cFlags = when (fromPiece.color) {
            WHITE -> castlingFlags and RANK_8.inv()
            BLACK -> castlingFlags and RANK_1.inv()
        }
        val rPositions = when (fromPiece.color) {
            WHITE -> rookPositions and RANK_8.inv()
            BLACK -> rookPositions and RANK_1.inv()
        }
        val leftRookStartPosition = rPositions.countLeadingZeroBits()
        if (cFlags and getMaskedSquare(leftRookStartPosition) != ZERO) {
            val kingRookFinalPositions = when (fromPiece.color) {
                WHITE -> LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }


            getCastlingMovements(
                king = fromPiece,
                kingFromSquare = fromSquare,
                kingFinalSquare = kingRookFinalPositions.countLeadingZeroBits(),
                rookFromSquare = leftRookStartPosition,
                rookFinalSquare = ULong.SIZE_BITS - kingRookFinalPositions.countTrailingZeroBits() - 1,
                isLeftCastling = true,
                isRightCastling = false,
                targets = targets
            )
        }
        val rightRookStartPosition = ULong.SIZE_BITS - rPositions.countTrailingZeroBits() - 1
        if (cFlags and getMaskedSquare(rightRookStartPosition) != ZERO) {
            val kingRookFinalPositions = when (fromPiece.color) {
                WHITE -> RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }
            getCastlingMovements(
                king = fromPiece,
                kingFromSquare = fromSquare,
                kingFinalSquare = ULong.SIZE_BITS - kingRookFinalPositions.countTrailingZeroBits() - 1,
                rookFromSquare = rightRookStartPosition,
                rookFinalSquare = kingRookFinalPositions.countLeadingZeroBits(),
                isLeftCastling = false,
                isRightCastling = true,
                targets = targets
            )
        }
    }

    private fun getCastlingMovements(
        king: Piece,
        kingFromSquare: Int,
        kingFinalSquare: Int,
        rookFromSquare: Int,
        rookFinalSquare: Int,
        isLeftCastling: Boolean,
        isRightCastling: Boolean,
        targets: MutableList<MovementTarget>
    ) {
        if (isSquareAttacked(kingFromSquare, king.color.opposite)) return
        val kingMoveDirection = if (kingFromSquare < kingFinalSquare) 1 else -1
        var kingPathSquare = kingFromSquare
        val occupied = getOccupiedBitBoard() and (getPieceBitBoard(king.getRook()) or getPieceBitBoard(king)).inv()
        do {
            kingPathSquare += kingMoveDirection
            if (isSquareAttacked(kingPathSquare, king.color.opposite)) return
            if (occupied and getMaskedSquare(kingPathSquare) != ZERO) return
        } while (kingPathSquare != kingFinalSquare)
        val rookMoveDirection = if (rookFromSquare < rookFinalSquare) 1 else -1
        var rookPathSquare = rookFromSquare
        do {
            rookPathSquare += rookMoveDirection
            if (occupied and getMaskedSquare(rookPathSquare) != ZERO) return
        } while (rookPathSquare != rookFinalSquare)
        var flags = MovementFlags.CASTLING_MASK
        if (isLeftCastling) flags = flags or LEFT_CASTLING_MASK
        if (isRightCastling) flags = flags or RIGHT_CASTLING_MASK
        targets += MovementTarget(
            king,
            rookFromSquare,
            MovementFlags(flags)
        )
    }

    private fun getQueenMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getRookMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getBishopMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getSlidingPieceMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        movementTargets: ArrayList<MovementTarget>
    ) {
        val targetSquares = getSlidingPieceTargetSquares(piece, fromSquare, ownPieces)
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
    }

    private fun getSlidingPieceTargetSquares(piece: Piece, fromSquare: Int, ownPieces: ULong): ULong {
        return when (piece.type) {
            ROOK -> getRookTargetSquares(fromSquare, ownPieces)
            BISHOP -> getBishopTargetSquares(fromSquare, ownPieces)
            QUEEN -> {
                val rookTargets = getRookTargetSquares(fromSquare, ownPieces)
                val bishopTargets = getBishopTargetSquares(fromSquare, ownPieces)
                rookTargets or bishopTargets
            }
            else -> throw BoardException("no sliding piece: $piece")
        }
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

    private fun getKnightMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = KNIGHT_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getPawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        return when (piece.color) {
            WHITE -> getWhitePawnMovements(piece, fromSquare, ownPieces)
            BLACK -> getBlackPawnMovements(piece, fromSquare, ownPieces)
        }
    }

    private fun getWhitePawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            WHITE_PAWN_SINGLE_MOVE_MASK[fromSquare],
            WHITE_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(piece, fromSquare, targetSquares)
    }

    private fun getBlackPawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            BLACK_PAWN_SINGLE_MOVE_MASK[fromSquare],
            BLACK_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(piece, fromSquare, targetSquares)
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

    private fun mountPawnMovements(pawn: Piece, fromSquare: Int, targetSquares: ULong): PieceMovement {
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
                var flags = 0uL
                if (isEnpassant) flags = flags or MovementFlags.EN_PASSANT_MASK
                if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                if (isPromotion) flags = flags or MovementFlags.PROMOTION_MASK
                for (targetPiece in getPawnTargetPieces(pawn, isPromotion)) {
                    movementTargets += MovementTarget(
                        targetPiece,
                        toSquare,
                        MovementFlags(flags)
                    )
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
        }
        else listOf(piece)
    }

    private fun mountNonPawnMovements(
        piece: Piece,
        fromIndex: Int,
        targetSquares: ULong,
        movementTargets: ArrayList<MovementTarget>
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
                movementTargets += MovementTarget(
                    piece,
                    toIndex,
                    MovementFlags(flags)
                )
            }
            s = s and FULL.shift(toIndex + 1)
        }
    }

    /**
     * A movement is valid if, after it, own king is not in check
     */
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
            isLeftCastling = false,
            isRightCastling = false,
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
        val occupied = getOccupiedBitBoard(color.opposite)
        val queenPositions = getPieceBitBoard(QUEEN, color)

        val rookPositions = getPieceBitBoard(ROOK, color)
        val rookRays = getRookTargetSquares(fromSquare, occupied)
        if (rookRays and (rookPositions or queenPositions) != ZERO) return true

        val bishopPositions = getPieceBitBoard(BISHOP, color)
        val bishopRays = getBishopTargetSquares(fromSquare, occupied)
        if (bishopRays and (bishopPositions or queenPositions) != ZERO) return true

        if (
            KING_MOVE_MASK[fromSquare] and getPieceBitBoard(KING, color) != ZERO
            || KNIGHT_MOVE_MASK[fromSquare] and getPieceBitBoard(KNIGHT, color) != ZERO
        ) {
            return true
        }

        return when (color) {
            WHITE -> BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(WHITE_PAWN) != ZERO
            BLACK -> WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(BLACK_PAWN) != ZERO
        }
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
        return when (piece) {
            WHITE_KING -> whiteKing
            WHITE_QUEEN -> whiteQueens
            WHITE_ROOK -> whiteRooks
            WHITE_BISHOP -> whiteBishops
            WHITE_KNIGHT -> whiteKnights
            WHITE_PAWN -> whitePawns
            BLACK_KING -> blackKing
            BLACK_QUEEN -> blackQueens
            BLACK_ROOK -> blackRooks
            BLACK_BISHOP -> blackBishops
            BLACK_KNIGHT -> blackKnights
            BLACK_PAWN -> blackPawns
        }
    }

    private fun setBitBoardBit(piece: Piece, bitIndex: Int, value: Boolean) {
        var bitboard = getPieceBitBoard(piece)
        bitboard = if (value) bitboard or getMaskedSquare(bitIndex)
        else bitboard and getMaskedSquare(bitIndex).inv()
        setBitBoard(piece, bitboard)
    }

    private fun setBitBoard(piece: Piece, bitboard: ULong) {
        when (piece) {
            WHITE_KING -> whiteKing = bitboard
            WHITE_QUEEN -> whiteQueens = bitboard
            WHITE_ROOK -> whiteRooks = bitboard
            WHITE_BISHOP -> whiteBishops = bitboard
            WHITE_KNIGHT -> whiteKnights = bitboard
            WHITE_PAWN -> whitePawns = bitboard
            BLACK_KING -> blackKing = bitboard
            BLACK_QUEEN -> blackQueens = bitboard
            BLACK_ROOK -> blackRooks = bitboard
            BLACK_BISHOP -> blackBishops = bitboard
            BLACK_KNIGHT -> blackKnights = bitboard
            BLACK_PAWN -> blackPawns = bitboard
        }
    }

    private fun getPieceLocations2(color: Color): List<PieceLocation> {
        val list = ArrayList<PieceLocation>()
        when (color) {
            WHITE -> {
                list += getPieceLocations(WHITE_KING, whiteKing)
                list += getPieceLocations(WHITE_QUEEN, whiteQueens)
                list += getPieceLocations(WHITE_ROOK, whiteRooks)
                list += getPieceLocations(WHITE_BISHOP, whiteBishops)
                list += getPieceLocations(WHITE_KNIGHT, whiteKnights)
                list += getPieceLocations(WHITE_PAWN, whitePawns)
            }
            BLACK -> {
                list += getPieceLocations(BLACK_KING, blackKing)
                list += getPieceLocations(BLACK_QUEEN, blackQueens)
                list += getPieceLocations(BLACK_ROOK, blackRooks)
                list += getPieceLocations(BLACK_BISHOP, blackBishops)
                list += getPieceLocations(BLACK_KNIGHT, blackKnights)
                list += getPieceLocations(BLACK_PAWN, blackPawns)
            }
        }
        return list.sorted()
    }

    private fun getPieceLocations(piece: Piece, pieceBitboard: ULong): List<PieceLocation> {
        var bbt = pieceBitboard
        val positions = ArrayList<PieceLocation>()
        while (bbt != ZERO) {
            val bitPosition = bbt.countLeadingZeroBits()
            positions += PieceLocation(piece, Position.from(bitPosition))
            bbt = bbt and FULL.shift(bitPosition + 1)
        }
        return positions
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

    private fun move(fromSquare: Int, toSquare: Int, toPiece: Piece, flags: MovementFlags) {
        move(
            fromSquare,
            toSquare,
            toPiece,
            flags.isLeftCastling,
            flags.isRightCastling,
            flags.isEnPassant,
            flags.isCapture,
            flags.isPromotion
        )
    }

    private fun move(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isLeftCastling: Boolean,
        isRightCastling: Boolean,
        isEnPassant: Boolean,
        isCapture: Boolean,
        isPromotion: Boolean
    ) {
        val fromPiece = if (isPromotion) toPiece.getPawn() else toPiece
        val enPassantCapturedSquare = getEnPassantCapturedSquare(toPiece.color, toSquare)
        val capturedPiece = when {
            isEnPassant -> getPiece(enPassantCapturedSquare)
            isCapture -> getPiece(toSquare)
            else -> null
        }

        moveLog += MoveLog(
            fromPiece,
            fromSquare,
            toPiece,
            toSquare,
            isEnPassant,
            isLeftCastling,
            isRightCastling,
            halfMoveClock,
            epTargetSquare,
            castlingFlags,
            capturedPiece
        )

        if (isLeftCastling || isRightCastling) {
            val finalPositions = when (fromPiece.color) {
                WHITE -> if (isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> if (isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }

            val kingDestination = if (isLeftCastling) finalPositions.countLeadingZeroBits()
            else ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1

            val rookDestination = if (isLeftCastling) ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1
            else finalPositions.countLeadingZeroBits()

            setBitBoardBit(fromPiece, kingDestination, true)
            setBitBoardBit(fromPiece.getRook(), rookDestination, true)
            setBitBoardBit(fromPiece, fromSquare, false)
            setBitBoardBit(fromPiece.getRook(), toSquare, false)
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

        halfMoveClock = if (fromPiece.isPawn || isCapture) halfMoveClock + 1
        else 0

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
        copy.rookPositions = rookPositions
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
        if (rookPositions != other.rookPositions) return false
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
        result = 31 * result + rookPositions.hashCode()
        result = 31 * result + castlingFlags.hashCode()
        result = 31 * result + moveLog.hashCode()
        return result
    }

    companion object {
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
        private val RANK_1 = 0x00000000000000ffuL
        private val RANK_8 = 0xff00000000000000uL
        private val LEFT_CASTLING_FINAL_POSITIONS = 0x3000000000000030uL
        private val RIGHT_CASTLING_FINAL_POSITIONS = 0x0600000000000006uL
    }
}

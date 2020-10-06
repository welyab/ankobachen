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

import com.welyab.ankobachen.BitboardUtil.getColumnMasks
import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.Piece.*
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.PAWN
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK
import com.welyab.ankobachen.Position.Companion.rowColumnToSquareIndex
import com.welyab.ankobachen.extensions.shift
import java.util.EnumMap
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val BONUS_FOR_BISHOP_PAIR = 20
private val BONUS_FOR_ROOK_ON_SEMI_OPEN_FILE = 8
private val BONUS_FOR_ROOK_ON_OPEN_FILE = 12
private val BONUS_FOR_QUEEN_ON_SAME_DIAGONAL_OF_BISHOP = 9
private val PENALTY_FOR_BISHOP_BLOCKED_BY_OWN_PARN = 5
private val PENALTY_FOR_PAWNS_ON_SAME_FILE = 7

private val kingValues = intArrayOf(
    //@formatter:off
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -20, -30, -30, -40, -40, -30, -30, -20,
    -10, -20, -20, -20, -20, -20, -20, -10,
    20,   20,   0,   0,   0,   0,  20,  20,
    20,   30,  10,   0,   0,  10,  30,  20
    //@formatter:on
)

private val queenValues = intArrayOf(
    //@formatter:off
    -20, -10, -10, -5, -5, -10, -10, -20,
    -10,   0,   0,  0,  0,   0,   0, -10,
    -10,   0,   5,  5,  5,   5,   0, -10,
     -5,   0,   5,  5,  5,   5,   0,  -5,
      0,   0,   5,  5,  5,   5,   0,  -5,
    -10,   5,   5,  5,  5,   5,   0, -10,
    -10,   0,   5,  0,  0,   0,   0, -10,
    -20, -10, -10, -5, -5, -10, -10, -20
    //@formatter:on
)

private val rookValues = intArrayOf(
    //@formatter:off
     0,  0,  0,  0,  0,  0,  0,  0,
     5, 10, 10, 10, 10, 10, 10,  5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
     0,  0,  0,  5,  5,  0,  0,  0
    //@formatter:on
)

private val bishopValues = intArrayOf(
    //@formatter:off
    -20, -10, -10, -10, -10, -10, -10, -20,
    -10,   0,   0,   0,   0,   0,   0, -10,
    -10,   0,   5,  10,  10,   5,   0, -10,
    -10,   5,   5,  10,  10,   5,   5, -10,
    -10,   0,  10,  10,  10,  10,   0, -10,
    -10,  10,  10,  10,  10,  10,  10, -10,
    -10,   5,   0,   0,   0,   0,   5, -10,
    -20, -10, -10, -10, -10, -10, -10, -20
    //@formatter:on
)

private val knightValues = intArrayOf(
    //@formatter:off
    -50, -40, -30, -30, -30, -30, -40, -50,
    -40, -20,   0,   0,   0,   0, -20, -40,
    -30,   0,  10,  15,  15,  10,   0, -30,
    -30,   5,  15,  20,  20,  15,   5, -30,
    -30,   0,  15,  20,  20,  15,   0, -30,
    -30,   5,  10,  15,  15,  10,   5, -30,
    -40, -20,   0,   5,   5,   0, -20, -40,
    -50, -40, -30, -30, -30, -30, -40, -50
    //@formatter:on
)

private val pawnValues = intArrayOf(
    //@formatter:off
     0,  0,   0,   0,   0,   0,  0,  0,
    50, 50,  50,  50,  50,  50, 50, 50,
    10, 10,  20,  30,  30,  20, 10, 10,
     5,  5,  10,  25,  25,  10,  5,  5,
     0,  0,   0,  20,  20,   0,  0,  0,
     5, -5, -10,   0,   0, -10, -5,  5,
     5, 10,  10, -20, -20,  10, 10,  5,
     0,  0,   0,   0,   0,   0,  0,  0
    //@formatter:on
)

private val pieceValues = EnumMap<Piece, Int>(Piece::class.java).apply {
    this[WHITE_KING] = 1
    this[WHITE_QUEEN] = 900
    this[WHITE_ROOK] = 500
    this[WHITE_BISHOP] = 340
    this[WHITE_KNIGHT] = 325
    this[WHITE_PAWN] = 100
    this[BLACK_KING] = -1
    this[BLACK_QUEEN] = -900
    this[BLACK_ROOK] = -500
    this[BLACK_BISHOP] = -340
    this[BLACK_KNIGHT] = -325
    this[BLACK_PAWN] = -100
}

private fun Piece.getStaticValue(): Int = pieceValues[this]!!

private val valuesMap = EnumMap<PieceType, IntArray>(PieceType::class.java).apply {
    this[KING] = kingValues
    this[QUEEN] = queenValues
    this[ROOK] = rookValues
    this[BISHOP] = bishopValues
    this[KNIGHT] = knightValues
    this[PAWN] = pawnValues
}

private val MIN_INFINITY = Int.MIN_VALUE
private val MAX_INFINITY = Int.MAX_VALUE

private val MIN_SCORE = -1000000000
private val MAX_SCORE = 1000000000

private const val FULL: ULong = ULong.MAX_VALUE

private fun ULong.forEach(action: (Int) -> Unit) {
    var value = this
    while (value != 0uL) {
        val squareIndex = value.countLeadingZeroBits()
        action.invoke(squareIndex)
        value = value and FULL.shift(squareIndex + 1)
    }
}

private fun List<PieceLocation>.toArray(): Array<Piece?> {
    val array = arrayOfNulls<Piece?>(64)
    forEach { array[it.position.squareIndex] = it.piece }
    return array
}

@ExperimentalStdlibApi
private fun getQueenStructureScore(board: Board, pieceLocations: List<PieceLocation>): Int {
    var score = 0
    for (pl in pieceLocations) {
        if (!pl.piece.isQueen) break
        val queenRays = BitboardUtil.getRookRays()[pl.position.squareIndex].or(
            BitboardUtil.getBishopRays()[pl.position.squareIndex]
        )
        when (pl.piece.color) {
            WHITE -> {
                if (board.getBitBoard(WHITE_BISHOP) and queenRays != 0uL) score += BONUS_FOR_QUEEN_ON_SAME_DIAGONAL_OF_BISHOP
            }
            BLACK -> {
                if (board.getBitBoard(BLACK_BISHOP) and queenRays != 0uL) score -= BONUS_FOR_QUEEN_ON_SAME_DIAGONAL_OF_BISHOP
            }
        }
    }
    return score
}

@ExperimentalStdlibApi
private fun getRookStructureScore(board: Board, pieceLocations: List<PieceLocation>): Int {
    var score = 0
    for (pl in pieceLocations) {
        if (!pl.piece.isRook) continue
        val columnMask = getColumnMasks()[pl.position.column]
        val whitePawns = board.getBitBoard(WHITE_PAWN)
        val blackPawns = board.getBitBoard(BLACK_PAWN)
        val kingPosition = board.getKingPosition(pl.piece.color.opposite)
        when (pl.piece.color) {
            WHITE -> {
                if (whitePawns.and(columnMask) == 0uL) score += BONUS_FOR_ROOK_ON_SEMI_OPEN_FILE
                if (whitePawns.or(blackPawns).and(columnMask) == 0uL) score += BONUS_FOR_ROOK_ON_OPEN_FILE
                score += 7 - (kingPosition.row - pl.position.row).absoluteValue
                score += 7 - (kingPosition.column - pl.position.column).absoluteValue
            }
            BLACK -> {
                if (blackPawns.and(columnMask) == 0uL) score -= BONUS_FOR_ROOK_ON_SEMI_OPEN_FILE
                if (blackPawns.or(whitePawns).and(columnMask) == 0uL) score -= BONUS_FOR_ROOK_ON_OPEN_FILE
                score -= 7 - (kingPosition.row - pl.position.row).absoluteValue
                score -= 7 - (kingPosition.column - pl.position.column).absoluteValue
            }
        }
    }
    return score
}

@ExperimentalStdlibApi
private fun getBishopStructureScore(board: Board, pieceLocations: List<PieceLocation>): Int {
    var score = 0
    var isWhiteBishopInWhiteSquaresPresent = false
    var isWhiteBishopInBlackSquaresPresent = false
    var isBlackBishopInWhiteSquaresPresent = false
    var isBlackBishopInBlackSquaresPresent = false
    for (pl in pieceLocations) {
        if (!pl.piece.isBishop) continue
        val bishopAdjacency = BitboardUtil.getBishopAdjacency()[pl.position.squareIndex]
        when (pl.piece.color) {
            WHITE -> {
                if (pl.position.squareIndex % 2 == 0) isWhiteBishopInWhiteSquaresPresent = true
                else isWhiteBishopInBlackSquaresPresent = true
                val pawnsBitboard = board.getBitboard(WHITE_PAWN)
                val blockingPawns = (bishopAdjacency and pawnsBitboard).countOneBits()
                score -= blockingPawns * PENALTY_FOR_BISHOP_BLOCKED_BY_OWN_PARN
            }
            BLACK -> {
                if (pl.position.squareIndex % 2 == 0) isBlackBishopInWhiteSquaresPresent = true
                else isBlackBishopInBlackSquaresPresent = true
                val pawnsBitboard = board.getBitboard(BLACK_PAWN)
                val blockingPawns = (bishopAdjacency and pawnsBitboard).countOneBits()
                score += blockingPawns * PENALTY_FOR_BISHOP_BLOCKED_BY_OWN_PARN
            }
        }
    }
    if (isWhiteBishopInWhiteSquaresPresent && isWhiteBishopInBlackSquaresPresent) score += BONUS_FOR_BISHOP_PAIR
    if (isBlackBishopInWhiteSquaresPresent && isBlackBishopInBlackSquaresPresent) score -= BONUS_FOR_BISHOP_PAIR
    return score
}

@ExperimentalStdlibApi
private fun getPawnStructureScore(board: Board, pieceLocations: List<PieceLocation>): Int {
    var score = 0
    val whitePawns = board.getBitBoard(WHITE_PAWN)
    val blackPawns = board.getBitBoard(BLACK_PAWN)
    for (column in 0..7) {
        val mask = getColumnMasks()[column]
        val whitePawnsOnSameFile = mask.and(whitePawns).countOneBits()
        if (whitePawnsOnSameFile > 1) score -= whitePawnsOnSameFile * PENALTY_FOR_PAWNS_ON_SAME_FILE
        val blackPawnsOnSameFile = mask.and(blackPawns).countOneBits()
        if (blackPawnsOnSameFile > 1) score += blackPawnsOnSameFile * PENALTY_FOR_PAWNS_ON_SAME_FILE
    }
    for (pl in pieceLocations) {
        if (!pl.piece.isPawn) continue
        var isolatedPawnMask = 0uL
        if (pl.position.column - 1 >= 0) isolatedPawnMask =
            isolatedPawnMask or getColumnMasks()[pl.position.column - 1]
        if (pl.position.column + 1 <= 7) isolatedPawnMask =
            isolatedPawnMask or getColumnMasks()[pl.position.column + 1]
        when (pl.piece.color) {
            WHITE -> {
                val mask = BitboardUtil.getWhitePassedPawnMask()[pl.position.squareIndex]
                if (mask and blackPawns == 0uL) score += 7 - pl.position.row
                if(isolatedPawnMask and whitePawns == 0uL) score -= 3
            }
            BLACK -> {
                val mask = BitboardUtil.getBlackPassedPawnMask()[pl.position.squareIndex]
                if (mask and whitePawns == 0uL) score -= pl.position.row
                if(isolatedPawnMask and blackPawns == 0uL) score += 3
            }
        }
    }
    return score
}

@ExperimentalStdlibApi
private fun getBoardValue(board: Board): Int {
    val pieces = board.getPieceLocations()
    var score = 0
    score += pieces
        .asSequence()
        .filterNotNull()
        .map { pl ->
            val pieceValue = pieceValues[pl.piece] ?: 0
            val positionalValue = when (pl.piece.color) {
                WHITE -> valuesMap[pl.piece.type]!![pl.position.squareIndex]
                BLACK -> -valuesMap[pl.piece.type]!![
                        rowColumnToSquareIndex(
                            row = 7 - Position.squareIndexToRow(pl.position.squareIndex),
                            column = Position.squareIndexToColumn(pl.position.squareIndex)
                        )
                ]
            }
            pieceValue + positionalValue
        }
        .sum()
    score += getQueenStructureScore(board, pieces)
    score += getRookStructureScore(board, pieces)
    score += getBishopStructureScore(board, pieces)
    score += getPawnStructureScore(board, pieces)
    return score
}

private fun sortMovements(previousMovement: Movement?, movements: List<Movement>): List<Movement> {
    return movements.sortedBy {
        when {
            previousMovement?.to == it.to -> 0
            it.flags.isCheckmate -> 100
            it.flags.isStalemate -> 200
            it.flags.isPromotion -> 300
            it.flags.isDoubleCheck -> 400
            it.flags.isCapture -> 500
            else -> 999
        }
    }
}

private class Variant(val score: Int, val movements: List<Movement>) {

    private fun movementsToString(): String = movements
        .asSequence()
        .map { "${it.fromPosition}->${it.toPosition}(${it.flags})" }
        .joinToString()

    override fun toString(): String = "score=$score, moves=${movementsToString()}"
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
private class Minimax(
    private val fen: String,
    private val depth: Int
) {

    var checkedNodes = 0
        private set

    private var board: Board = Board()

    fun find(): Variant {
        checkedNodes = 0
        board = Board(fen)
        return walk(
            depth,
            MIN_INFINITY,
            MAX_INFINITY,
            null
        )
    }

    private fun calculateBoardScore(cDepth: Int, previousMovement: Movement?): Boolean {
        return cDepth == 0 || previousMovement?.isFinalMovement ?: false
    }

    private fun getBoardScore(cDepth: Int, previousMovement: Movement?): Int {
        if (previousMovement != null && previousMovement.isFinalMovement)
            return when {
                previousMovement.flags.isStalemate -> 0
                else -> when (board.getSideToMove()) {
                    WHITE -> MIN_SCORE - cDepth
                    BLACK -> MAX_SCORE + cDepth
                }
            }
        return getBoardValue(board) + depth
    }

    private fun walk(
        cDepth: Int,
        alpha: Int,
        beta: Int,
        previousMovement: Movement?
    ): Variant {
        checkedNodes++

        if (calculateBoardScore(cDepth, previousMovement)) {
            return Variant(getBoardScore(cDepth, previousMovement), emptyList())
        }

        val movements = board.getMovements(pseudoValid = true).toListOfMovements()
        val sortedMoves = sortMovements(previousMovement, movements)
        when (board.getSideToMove()) {
            WHITE -> {
                var best: Variant? = null
                var cAlpha = alpha
                for (pseudoValidMove in sortedMoves) {
                    if (!board.isMovementValid(pseudoValidMove)) continue
                    val movement = board.getExtraFlags(pseudoValidMove)
                    val deeper = board.withinMovement(movement) {
                        walk(cDepth - 1, cAlpha, beta, movement)
                    }
                    best = getBest(best, movement, deeper)
                    cAlpha = max(cAlpha, deeper.score)
                    if (cAlpha >= beta) break
                }
                return best!!
            }
            BLACK -> {
                var best: Variant? = null
                var cBeta = beta
                for (pseudoValidMove in sortedMoves) {
                    if (!board.isMovementValid(pseudoValidMove)) continue
                    val movement = board.getExtraFlags(pseudoValidMove)
                    val deeper = board.withinMovement(movement) {
                        walk(cDepth - 1, alpha, cBeta, movement)
                    }
                    best = getBest(best, movement, deeper)
                    cBeta = min(cBeta, deeper.score)
                    if (alpha >= cBeta) break
                }
                return best!!
            }
        }
    }

    private fun createVariant(score: Int, movement: Movement, movements: List<Movement>): Variant {
        val list = ArrayList<Movement>(movements.size + 1)
        list += movement
        list += movements
        return Variant(score, list)
    }

    private fun getBest(
        currentBest: Variant?,
        movement: Movement,
        deeperVariant: Variant
    ): Variant {
        if (currentBest == null) {
            return createVariant(deeperVariant.score, movement, deeperVariant.movements)
        }
        val foundBetter = when (board.getSideToMove()) {
            WHITE -> deeperVariant.score > currentBest.score
            BLACK -> deeperVariant.score < currentBest.score
        }
        return if (foundBetter) createVariant(deeperVariant.score, movement, deeperVariant.movements)
        else currentBest
    }
}

class Searcher {

    private val lines = arrayOfNulls<Any>(MAX_LINES_ANALYSIS)

    fun setFen(fen: String) {
    }

    fun setTotalLines(value: Int) {
        var x = value
        x = max(MIN_LINES_ANALYSIS, x)
        x = min(MAX_LINES_ANALYSIS, x)
        val diff = x - getTotalLines()
        if (x > 0) addLines(x)
        else if (x < 0) removeLines(x)
    }

    private fun addLines(amount: Int) {
        System.currentTimeMillis()
    }

    private fun removeLines(amount: Int) {
    }

    fun getTotalLines(): Int {
        return max(
            MIN_LINES_ANALYSIS,
            lines.asSequence().filterNotNull().count()
        )
    }

    companion object {
        val MIN_LINES_ANALYSIS = 1
        val MAX_LINES_ANALYSIS = 10
    }
}

@ExperimentalTime
@ExperimentalStdlibApi
fun main1() {
    val board = Board("k7/3p4/8/8/8/8/2PP4/KRRR4 w - - 0 1")
    val score = getRookStructureScore(board, board.getPieceLocations())
    println("score = $score")
}

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    Board()
    measureTimedValue {
        val minimax = Minimax(
            fen = "4Q3/p2P2k1/1p4p1/8/7p/1B6/P6P/7K b - - 0 49",
            depth = 7
        )
        minimax.find().run {
            println("checked nodes = ${minimax.checkedNodes}")
            println("score=$score")
            movements.forEach {
                println(it)
            }
        }
    }.run {
        println("${duration.inSeconds} secs")
    }
}

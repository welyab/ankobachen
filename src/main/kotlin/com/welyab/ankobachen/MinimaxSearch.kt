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

import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.PAWN
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK
import com.welyab.ankobachen.Position.Companion.rowColumnToSquareIndex
import com.welyab.ankobachen.Position.Companion.squareIndexToColumn
import com.welyab.ankobachen.Position.Companion.squareIndexToRow
import jdk.nashorn.internal.objects.NativeArray
import java.util.EnumMap
import kotlin.math.max
import kotlin.math.min

private val kingValues = intArrayOf(
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -20, -30, -30, -40, -40, -30, -30, -20,
    -10, -20, -20, -20, -20, -20, -20, -10,
    20, 20, 0, 0, 0, 0, 20, 20,
    20, 30, 10, 0, 0, 10, 30, 20
)

private val queenValues = intArrayOf(
    -20, -10, -10, -5, -5, -10, -10, -20,
    -10, 0, 0, 0, 0, 0, 0, -10,
    -10, 0, 5, 5, 5, 5, 0, -10,
    -5, 0, 5, 5, 5, 5, 0, -5,
    0, 0, 5, 5, 5, 5, 0, -5,
    -10, 5, 5, 5, 5, 5, 0, -10,
    -10, 0, 5, 0, 0, 0, 0, -10,
    -20, -10, -10, -5, -5, -10, -10, -20
)

private val rookValues = intArrayOf(
    0, 0, 0, 0, 0, 0, 0, 0,
    5, 10, 10, 10, 10, 10, 10, 5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    0, 0, 0, 5, 5, 0, 0, 0
)

private val bishopValues = intArrayOf(
    -20, -10, -10, -10, -10, -10, -10, -20,
    -10, 0, 0, 0, 0, 0, 0, -10,
    -10, 0, 5, 10, 10, 5, 0, -10,
    -10, 5, 5, 10, 10, 5, 5, -10,
    -10, 0, 10, 10, 10, 10, 0, -10,
    -10, 10, 10, 10, 10, 10, 10, -10,
    -10, 5, 0, 0, 0, 0, 5, -10,
    -20, -10, -10, -10, -10, -10, -10, -20
)

private val knightValues = intArrayOf(
    -50, -40, -30, -30, -30, -30, -40, -50,
    -40, -20, 0, 0, 0, 0, -20, -40,
    -30, 0, 10, 15, 15, 10, 0, -30,
    -30, 5, 15, 20, 20, 15, 5, -30,
    -30, 0, 15, 20, 20, 15, 0, -30,
    -30, 5, 10, 15, 15, 10, 5, -30,
    -40, -20, 0, 5, 5, 0, -20, -40,
    -50, -40, -30, -30, -30, -30, -40, -50
)

private val pawnValues = intArrayOf(
    0, 0, 0, 0, 0, 0, 0, 0,
    50, 50, 50, 50, 50, 50, 50, 50,
    10, 10, 20, 30, 30, 20, 10, 10,
    5, 5, 10, 25, 25, 10, 5, 5,
    0, 0, 0, 20, 20, 0, 0, 0,
    5, -5, -10, 0, 0, -10, -5, 5,
    5, 10, 10, -20, -20, 10, 10, 5,
    0, 0, 0, 0, 0, 0, 0, 0
)

private val pieceValues = EnumMap<Piece, Int>(Piece::class.java).apply {
    this[Piece.WHITE_KING] = 999
    this[Piece.WHITE_QUEEN] = 9
    this[Piece.WHITE_ROOK] = 5
    this[Piece.WHITE_BISHOP] = 3
    this[Piece.WHITE_KNIGHT] = 3
    this[Piece.WHITE_PAWN] = 1
    this[Piece.BLACK_KING] = -999
    this[Piece.BLACK_QUEEN] = 9
    this[Piece.BLACK_ROOK] = 5
    this[Piece.BLACK_BISHOP] = 3
    this[Piece.BLACK_KNIGHT] = 3
    this[Piece.BLACK_PAWN] = 1
}

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

private fun getBoardValue(pieces: List<PieceLocation>): Int {
    return pieces
        .asSequence()
        .map { pieceLocation ->
            when (pieceLocation.piece.color) {
                WHITE -> valuesMap[pieceLocation.piece.type]!![pieceLocation.position.squareIndex]
                BLACK -> -valuesMap[pieceLocation.piece.type]!![
                        rowColumnToSquareIndex(
                            row = 7 - squareIndexToRow(pieceLocation.position.squareIndex),
                            column = squareIndexToColumn(pieceLocation.position.squareIndex)
                        )
                ]
            }
        }
        .sum()
}

private fun sortMovements(movements: List<Movement>): List<Movement> {
    return movements
}

private class Variant(val score: Int, val movements: List<Movement>) {

    private fun movementsToString(): String = movements
        .asSequence()
        .map { "${it.fromPosition}->${it.toPosition}(${it.flags})" }
        .joinToString()

    override fun toString(): String = "score=$score, moves=${movementsToString()}"
}

@ExperimentalStdlibApi
private class Minimax(
    private val fen: String,
    private val depth: Int
) {

    fun find(): Variant {
        val board = Board(fen)
        return walk(
            board,
            depth,
            MIN_INFINITY,
            MAX_INFINITY,
            null
        )
    }

    private fun walk(
        board: Board,
        cDepth: Int,
        alpha: Int,
        beta: Int,
        previousMovement: Movement?
    ): Variant {
        if (previousMovement != null && previousMovement.isFinalMovement) return when {
            previousMovement.flags.isStalemate -> Variant(0, emptyList())
            else -> when (board.getSideToMove()) {
                WHITE -> Variant(MIN_SCORE - cDepth, emptyList())
                BLACK -> Variant(MAX_SCORE + cDepth, emptyList())
            }
        } else if (cDepth == 0) return Variant(
            getBoardValue(board.getPieceLocations()) + depth,
            emptyList()
        )
        when (board.getSideToMove()) {
            WHITE -> {
                var best: Variant? = null
                var cAlpha = alpha
                for (movement in board) {
                    val deeper = board.withinMovement(movement) {
                        walk(this, cDepth - 1, cAlpha, beta, movement)
                    }
                    best = getBest(board.getSideToMove(), best, movement, deeper)
                    cAlpha = max(cAlpha, deeper.score)
                    if (cAlpha >= beta) break
                }
                return best!!
            }
            BLACK -> {
                var best: Variant? = null
                var cBeta = beta
                for (movement in board) {
                    val deeper = board.withinMovement(movement) {
                        walk(this, cDepth - 1, alpha, cBeta, movement)
                    }
                    best = getBest(board.getSideToMove(), best, movement, deeper)
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
        sideToMove: Color,
        currentBest: Variant?,
        movement: Movement,
        deeperVariant: Variant
    ): Variant {
        if (currentBest == null) {
            return createVariant(deeperVariant.score, movement, deeperVariant.movements)
        }
        val foundBetter = when (sideToMove) {
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

@ExperimentalStdlibApi
fun main() {
    Minimax(
        fen = "4b1k1/2r2p2/1q1pnPpQ/7p/p3P2P/pN5B/P1P5/1K1R2R1 w - - 1 0",
        depth = 5
    ).find().run {
        println("score=$score")
        movements.forEach {
            println(it)
        }
    }
}

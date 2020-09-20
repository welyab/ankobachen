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
import java.util.EnumMap
import kotlin.math.max
import kotlin.math.min

private val kingValues = doubleArrayOf(
    -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0,
    -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0,
    -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0,
    -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0,
    -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0,
    -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0,
    2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0,
    2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0
)

private val queenValues = doubleArrayOf(
    -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0,
    -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0,
    -1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0,
    -0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5,
    0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5,
    -1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0,
    -1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0,
    -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0
)

private val rookValues = doubleArrayOf(
    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5,
    -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5,
    -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5,
    -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5,
    -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5,
    -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5,
    0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0
)

private val bishopValues = doubleArrayOf(
    -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0,
    -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0,
    -1.0, 0.0, 0.5, 1.0, 1.0, 0.5, 0.0, -1.0,
    -1.0, 0.5, 0.5, 1.0, 1.0, 0.5, 0.5, -1.0,
    -1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.0,
    -1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0,
    -1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, -1.0,
    -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0
)

private val knightValues = doubleArrayOf(
    -5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0,
    -4.0, -2.0, 0.0, 0.0, 0.0, 0.0, -2.0, -4.0,
    -3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0,
    -3.0, 0.5, 1.5, 2.0, 2.0, 1.5, 0.5, -3.0,
    -3.0, 0.0, 1.5, 2.0, 2.0, 1.5, 0.0, -3.0,
    -3.0, 0.5, 1.0, 1.5, 1.5, 1.0, 0.5, -3.0,
    -4.0, -2.0, 0.0, 0.5, 0.5, 0.0, -2.0, -4.0,
    -5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0
)

private val pawnValues = doubleArrayOf(
    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0,
    1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0,
    0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5,
    0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0,
    0.5, -0.5, -1.0, 0.0, 0.0, -1.0, -0.5, 0.5,
    0.5, 1.0, 1.0, -2.0, -2.0, 1.0, 1.0, 0.5,
    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
)

private val valuesMap = EnumMap<PieceType, DoubleArray>(PieceType::class.java).apply {
    this[KING] = kingValues
    this[QUEEN] = queenValues
    this[ROOK] = rookValues
    this[BISHOP] = bishopValues
    this[KNIGHT] = knightValues
    this[PAWN] = pawnValues
}

private fun getBoardValue(pieces: List<PieceLocation>): Double {
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

private var evaluatedPositions = 0

private fun sortMovements(movements: List<Movement>): List<Movement> {
    return movements.sortedBy {
        when {
            it.flags.isCheckmate -> 0
            it.flags.isPromotion -> 1
            it.flags.isCapture -> 2
            it.flags.isEnPassant -> 3
            it.flags.isStalemate -> 99999999
            else -> 999999
        }
    }
}

@ExperimentalStdlibApi
private fun minmax(
    board: Board,
    currentDepth: Int,
    maxDepth: Int,
    maxScore: Int,
    minScore: Int
): Pair<Double, List<Movement>> {
    if (currentDepth == maxDepth) {
        evaluatedPositions++
        return getBoardValue(board.getPieceLocations()) to emptyList()
    }
    val movements = sortMovements(board.getMovements().asSequenceOfMovements().toList())
    var currentScore = when (board.getSideToMove()) {
        WHITE -> maxScore
        BLACK -> minScore
    }
    val path = ArrayList<Movement>(maxDepth)
    for (index in movements.indices) {
        val movement = movements[index]
        board.move(movement)
        val currentMinMaxScores = when (board.getSideToMove()) {
            WHITE -> Pair(currentScore, minScore)
            WHITE -> Pair(maxScore, currentDepth)
        }
        val deeperPath = minmax(
            board,
            currentDepth + 1,
            maxDepth,
            currentMinMaxScores.first,
            currentMinMaxScores.second
        )
        board.undo()
        val deeperScore = deeperPath.first
        if (
            path.isEmpty()
            || (board.getSideToMove().isWhite && deeperScore > bestPath.first)
            || (board.getSideToMove().isBlack && deeperScore < bestPath.first)
        ) {
            val path = ArrayList<Movement>(deeperPath.second.size + 1)
            path += movement
            path += deeperPath.second
            bestPath = deeperScore to path
        } else {
            val factor = (1 - 0.1 * (currentDepth - 1))
            val cutIndex = min((movements.size * factor).toInt(), 6)
            if (index >= cutIndex) {
                break
            }
        }
    }
    if (bestPath == null) {
        val score = when (board.getSideToMove()) {
            WHITE -> Double.MIN_VALUE
            BLACK -> Double.MAX_VALUE
        }
        bestPath = score to emptyList()
    }
    return bestPath
}

@ExperimentalStdlibApi
fun main() {
    val fen = "r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w KQkq - 1 0"
    val board = Board(fen)
    val movements = minmax(
        board,
        0,
        8
    )
    println("total positions: $evaluatedPositions")
    println("score: ${movements.first}")
    movements.second.forEach { println(it) }
}

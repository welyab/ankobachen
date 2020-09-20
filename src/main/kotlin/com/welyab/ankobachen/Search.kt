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

private val valuesMap = EnumMap<PieceType, IntArray>(PieceType::class.java).apply {
    this[KING] = kingValues
    this[QUEEN] = queenValues
    this[ROOK] = rookValues
    this[BISHOP] = bishopValues
    this[KNIGHT] = knightValues
    this[PAWN] = pawnValues
}

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
    maxDepth: Int
): Pair<Int, List<Movement>> {
    if (currentDepth == maxDepth) {
        return getBoardValue(board.getPieceLocations()) to emptyList()
    }
    val movements = sortMovements(board.getMovements().asSequenceOfMovements().toList())
    var bestPath: Pair<Int, List<Movement>>? = null
    for (index in movements.indices) {
        val movement = movements[index]
        board.move(movement)
        evaluatedPositions++
        val deeperPath = minmax(
            board,
            currentDepth + 1,
            maxDepth
        )
        board.undo()
        if (
            bestPath == null
            || (board.getSideToMove().isWhite && deeperPath.first > bestPath.first)
            || (board.getSideToMove().isBlack && deeperPath.first < bestPath.first)
        ) {
            val path = ArrayList<Movement>(deeperPath.second.size + 1)
            path += movement
            path += deeperPath.second
            bestPath = deeperPath.first to path
        } else {
            val factor = (1 - 0.1 * (currentDepth - 1))
            val cutIndex = min((movements.size * factor).toInt(), 6)
            if (index >= cutIndex) {
                println("factor: %.2f".format(factor))
                break
            }
        }
    }
    if (bestPath == null) {
        val score = when (board.getSideToMove()) {
            WHITE -> Int.MIN_VALUE
            BLACK -> Int.MAX_VALUE
        }
        bestPath = score to emptyList()
    }
    return bestPath
}

@ExperimentalStdlibApi
fun main1() {
    val board = Board()
    val score = getBoardValue(board.getPieceLocations())
    println("score: $score")
}

@ExperimentalStdlibApi
fun main() {
    val fen = "1n2kb1r/p4ppp/4q3/4p1B1/4P3/8/PPP2PPP/2KR4 w k - 0 2"
    val board = Board(fen)
    val movements = minmax(
        board,
        0,
        4
    )
    println("total positions: $evaluatedPositions")
    println("score: ${movements.first}")
    movements.second.forEach { println(it) }
}

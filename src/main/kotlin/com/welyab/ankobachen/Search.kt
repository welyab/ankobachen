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

@ExperimentalStdlibApi
private fun minmax(board: Board, depth: Int, movement: Movement? = null): Pair<Int, Movement?> {
    if (depth == 0) {
        return getBoardValue(board.getPieceLocations()) to movement!!
    }
    val movements = board.getMovements()
    var bestMovement: Pair<Int, Movement?>? = null
    val movingSide = board.getSideToMove()
    for (movement in movements) {
        board.move(movement)
        val value = minmax(board, depth - 1, movement)
        board.undo()

        bestMovement = if (bestMovement == null) value
        else getBestMovement(bestMovement, Pair(value.first, movement), movingSide)
    }

    if(bestMovement == null) {
        return when(movingSide) {
            WHITE -> Pair(Int.MIN_VALUE, null)
            BLACK -> Pair(Int.MAX_VALUE, null)
        }
    }

    return bestMovement
}

private fun getBestMovement(
    movement1: Pair<Int, Movement?>,
    movement2: Pair<Int, Movement?>,
    movingSide: Color
): Pair<Int, Movement?> {
    return when (movingSide) {
        WHITE -> if (movement1.first > movement2.first) movement1
        else movement2
        BLACK -> if (movement1.first < movement2.first) movement1
        else movement2
    }
}

@ExperimentalStdlibApi
fun main() {
    val fen = "r1b2bnr/p3k2p/2Q3p1/2pNpp2/4p3/2P3P1/PP3PBP/R1B1K1NR b KQ - 1 14"
    val board = Board(fen)
    val movement = minmax(board, 4)
    println("movement = $movement")
}

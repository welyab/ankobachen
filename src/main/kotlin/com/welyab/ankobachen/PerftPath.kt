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
@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.welyab.ankobachen

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

enum class PerftValue {
    NODES,
    CAPTURES,
    EN_PASSANTS,
    CASTLINGS,
    PROMOTIONS,
    CHECKS,
    DISCOVERIES,
    DOUBLES,
    CHECKMATES,
    STALEMATES
}

@Suppress("MemberVisibilityCanBePrivate")
class PerftResult private constructor(val fen: String, private val results: Map<Int, MovementMetadata>) {

    fun getMaxDepth() = results.size

    fun getPerftValues(depth: Int) = results[depth]?.let {
        mapOf(
            PerftValue.NODES to it.nodesCount,
            PerftValue.CAPTURES to it.captureCount,
            PerftValue.EN_PASSANTS to it.enPassantCount,
            PerftValue.CASTLINGS to it.castlingCount,
            PerftValue.PROMOTIONS to it.promotionCount,
            PerftValue.CHECKS to it.checkCount,
            PerftValue.DISCOVERIES to it.discoveryCheckCount,
            PerftValue.DOUBLES to it.doubleCheckCount,
            PerftValue.CHECKMATES to it.checkmateCount,
            PerftValue.STALEMATES to it.stalemateCount
        )
    }!!

    fun getPerftValue(depth: Int, perftValue: PerftValue) = results[depth]?.let {
        when (perftValue) {
            PerftValue.NODES -> it.nodesCount
            PerftValue.CAPTURES -> it.captureCount
            PerftValue.EN_PASSANTS -> it.enPassantCount
            PerftValue.CASTLINGS -> it.castlingCount
            PerftValue.PROMOTIONS -> it.promotionCount
            PerftValue.CHECKS -> it.checkCount
            PerftValue.DISCOVERIES -> it.discoveryCheckCount
            PerftValue.DOUBLES -> it.doubleCheckCount
            PerftValue.CHECKMATES -> it.checkmateCount
            PerftValue.STALEMATES -> it.stalemateCount
        }
    }!!

    class Builder constructor(
        private val fen: String,
        private val map: HashMap<Int, MovementMetadata.Builder> = HashMap()
    ) {
        fun add(depth: Int, movementMetadata: MovementMetadata): Builder {
            map.computeIfAbsent(depth) { _ ->
                MovementMetadata.builder()
            }.addMetadata(movementMetadata)
            return this
        }

        fun add(perftResult: PerftResult): Builder {
            perftResult.results.forEach { e ->
                add(e.key, e.value)
            }
            return this
        }

        fun builder() = PerftResult(
            fen,
            map.mapValues { it.value.build() }
        )
    }

    override fun toString() = buildString {
        val headers = ArrayList<String>()
        headers += " DEPTH "
        PerftValue.values().forEach { headers += " ${it.name} " }
        val values = ArrayList<ArrayList<String>>()
        for (depth in 1..getMaxDepth()) {
            val list = ArrayList<String>()
            values += list
            list += " $depth "
            for (perftValue in PerftValue.values()) {
                val value = getPerftValue(depth, perftValue).toString()
                list += " $value "
            }
        }
        val rows: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
        rows += headers
        fun ArrayList<ArrayList<String>>.columnLength(columnIndex: Int) =
            asSequence().map { it[columnIndex].length }.max()!!
        values.forEach { rows += it }

        for (i in 0 until headers.size) {
            if (i == 0) append('┌') else append('┬')
            append("".padStart(rows.columnLength(i), '─'))
        }
        append("┐").append('\n')

        for (rowIndex in 0 until rows.size) {
            rows[rowIndex].forEachIndexed { column, value ->
                append('│').append(rows[rowIndex][column].padStart(rows.columnLength(column), ' '))
            }
            append('│').append('\n')

            if (rowIndex != rows.lastIndex) {
                for (i in 0 until headers.size) {
                    if (i == 0) append('├') else append('┼')
                    append("".padStart(rows.columnLength(i), '─'))
                }
                append("┤").append('\n')
            } else {
                for (i in 0 until headers.size) {
                    if (i == 0) append('└') else append('┴')
                    append("".padStart(rows.columnLength(i), '─'))
                }
                append("┘").append('\n')
            }
        }
    }

    companion object {
        fun builder(fen: String) = Builder(fen)
    }
}

@ExperimentalStdlibApi
private class Walker constructor(
    private val fen: String,
    private val board: Board,
    private val currentDepth: Int,
    private val maxDepth: Int
) : RecursiveTask<PerftResult>() {

    override fun compute(): PerftResult {
        if (maxDepth - currentDepth + 1 == 2) {
            val builder = PerftResult.builder(fen)
            walk(board, currentDepth, maxDepth, builder)
            return builder.builder()
        }

        val movements = board.getMovements()
        val builder = PerftResult.builder(fen)
        builder.add(currentDepth, movements.metadata)
        val walkers = ArrayList<Walker>()
        for (movement in movements) {
            board.move(movement)
            walkers += Walker(fen, board.copy(), currentDepth + 1, maxDepth)
            board.undo()
        }
        ForkJoinTask.invokeAll(walkers)
            .stream()
            .map { it.join() }
            .forEach { builder.add(it) }
        return builder.builder()
    }

    private fun walk(board: Board, currentDepth: Int, maxDepth: Int, builder: PerftResult.Builder) {
        val movements = board.getMovements()
        builder.add(currentDepth, movements.metadata)
        for (movement in movements) {
            board.move(movement)
            if (currentDepth + 1 <= maxDepth) {
                walk(board, currentDepth + 1, maxDepth, builder)
            }
            board.undo()
        }
    }
}

@ExperimentalStdlibApi
class PerftCalculator(
    val fen: String = "",
    val depth: Int = 2
) {
    private var perftResult: PerftResult? = null

    fun getPerftResult(): PerftResult {
        if (perftResult == null) {
            val walker = Walker(fen, Board(fen), 1, depth)
            perftResult = ForkJoinPool.commonPool().invoke(walker)
        }
        return perftResult!!
    }

    fun execute() {
        if (perftResult != null) return
        val board = if (fen.isBlank()) Board() else Board(fen)
        val walker = Walker(fen, board, 1, depth)
        perftResult = ForkJoinPool.commonPool().invoke(walker)
    }
}

@ExperimentalStdlibApi
class PathEnumerator(
    val fen: String,
    val enumerationDepth: Int,
    val depth: Int
) {

    fun enumerate() {
        val board = Board(fen)
        enumerate(board, 1, ArrayList())
    }

    private fun enumerate(board: Board, currentDepth: Int, path: ArrayList<Movement>) {
        if (currentDepth > enumerationDepth) {
            path.asSequence()
                .map { "${it.fromPosition}${it.toPosition}" }
                .reduce { s1, s2 -> "$s1 $s2" }
                .apply { print(this) }
            if (currentDepth <= depth) {
                totalizeMovements(board, currentDepth).apply {
                    print(" $this")
                }
            }
            println()
        } else {
            board.getMovements().forEachMovement { pieceMovement ->
                board.move(pieceMovement)
                path += pieceMovement
                enumerate(board, currentDepth + 1, path)
                path.removeAt(path.lastIndex)
                board.undo()
            }
        }
    }

    private fun totalizeMovements(board: Board, currentDepth: Int): Long {
        var sum = 0L
        board.getMovements()
            .apply {
                sum += metadata.nodesCount
            }
            .forEachMovement {
                if (currentDepth + 1 <= depth) {
                    board.move(it)
                    sum += totalizeMovements(board, currentDepth + 1)
                    board.undo()
                }
            }
        return sum
    }
}

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    Board()
    measureTimedValue {
        PerftCalculator("7N/8/8/2p5/1pp5/brpp4/1pprp3/qnkbK3 w - - 1 8", 9)
            .getPerftResult()
            .apply {
                println("FEN: ${this.fen}")
                println(this)
            }
    }.apply {
        println("${this.duration.inSeconds}")
    }
}

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

    fun getPeftValue(depth: Int, perftValue: PerftValue) = results[depth]?.let {
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
                val value = getPeftValue(depth, perftValue).toString()
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
    private val board: Board,
    private val currentDepth: Int,
    private val maxDepth: Int
) : RecursiveTask<PerftResult>() {

    override fun compute(): PerftResult {
        if (maxDepth - currentDepth + 1 == 2) {
            val builder = PerftResult.builder("fen")
            walk(board, currentDepth, maxDepth, builder)
            return builder.builder()
        }

        val movements = board.getMovements()
        val builder = PerftResult.builder("fen")
        builder.add(currentDepth, movements.metadata)
        val walkers = ArrayList<Walker>()
        for (movement in movements) {
            board.move(movement)
            walkers += Walker(board.copy(), currentDepth + 1, maxDepth)
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
//        if (perftResult == null) execute()
//        return perftResult!!
        if (perftResult == null) {
            val walker = Walker(Board(fen), 1, depth)
            perftResult = ForkJoinPool.commonPool().invoke(walker)
        }
        return perftResult!!
    }

    fun execute() {
        if (perftResult != null) return
        val board = if (fen.isBlank()) Board() else Board(fen)
        val builder = PerftResult.builder(fen)
        val path = ArrayList<Movement>()
        walker(board, 1, builder, path)
        perftResult = builder.builder()
    }

    private fun walker(
        board: Board,
        currentDepth: Int,
        builder: PerftResult.Builder,
        path: MutableList<Movement>
    ) {
        val movements = board.getMovements()
        builder.add(currentDepth, movements.metadata)
        for (movement in movements) {
            board.move(movement)
            path += movement
            if (currentDepth + 1 <= depth) {
                walker(board, currentDepth + 1, builder, path)
            }
            path.removeLast()
            board.undo()
        }
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

// [175] nrnk1rbb/p1p2ppp/3pq3/Qp2p3/1P1P4/8/P1P1PPPP/NRN1KRBB w fb - 2 9, 28, 873, 25683, 791823, 23868737, 747991356
// [262] nrbkn2r/pppp1pqp/4p1p1/8/3P2P1/P3B3/P1P1PP1P/NR1KNBQR w HBhb - 1 9, 32, 808, 25578, 676525, 22094260, 609377239 -- falha
// [271] nrknqrbb/1p2ppp1/2pp4/Q6p/P2P3P/8/1PP1PPP1/NRKN1RBB w FBfb - 0 9, 34, 513, 16111, 303908, 9569590, 206509331
// [327] nr1qkr1b/ppp1pp1p/4bn2/3p2p1/4P3/1Q6/PPPP1PPP/NRB1KRNB w FBfb - 4 9, 33, 939, 30923, 942138, 30995969, 991509814
// [377] nrk1brnq/pp1p1pp1/7p/b1p1p3/1P6/6P1/P1PPPPQP/NRKBBRN1 w FBfb - 2 9, 29, 675, 20352, 492124, 15316285, 389051744
// [494] qrnk1bbr/1pnp1ppp/p1p1p3/8/3Q4/1P1N3P/P1PPPPP1/1RNK1BBR w HBhb - 0 9, 43, 1106, 42898, 1123080, 41695761, 1113836402
// [562] br1k1brq/ppppp2p/1n1n1pp1/8/P1P5/3P2P1/1P2PP1P/BRNKNBRQ w GBgb - 0 9, 28, 811, 23550, 664880, 19913758, 565143976
// [563] 1r1knrqb/n1pppppp/p1b5/1p6/8/3N1P2/PPPPP1PP/BRNK1RQB w fb - 3 9, 29, 753, 23210, 620019, 20044474, 558383603
// [687] qrkn1rbb/pp2pppp/2p5/3p4/P2Qn1P1/1P6/2PPPP1P/1RKNNRBB w FBfb - 0 9, 38, 943, 35335, 868165, 31909835, 798405123
// [754] brk1nbrq/1ppppn1p/6p1/p4p2/P5P1/5R2/1PPPPP1P/BRKNNB1Q w Bgb - 0 9, 29, 922, 27709, 879527, 27463717, 888881062
// [913] 1rkbrqnn/p1pp1ppp/1p6/8/P2Pp3/8/1PPKPPQP/BR1BR1NN w eb - 0 9, 28, 916, 24892, 817624, 22840279, 759318058
// [929] brkbrnqn/ppp2p2/4p3/P2p2pp/6P1/5P2/1PPPP2P/BRKBRNQN w EBeb - 0 9, 25, 548, 14563, 348259, 9688526, 247750144

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    val bbb = Board()
    measureTimedValue {
        val board = Board("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1")
        val walker = Walker(board, 1, 6)
        val result = ForkJoinPool.commonPool().invoke(walker)
        println(result)
    }.duration.inSeconds.apply { println("$this seconds") }

//    measureTimedValue {
//        PerftCalculator(
//            fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",
//            depth = 6
//        ).getPerftResult()
//            .apply {
//                println(this)
//            }
//    }.duration.inSeconds.apply { println("$this seconds") }
}

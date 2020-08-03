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

import com.welyab.ankobachen.BitboardUtil.toULong
import com.welyab.ankobachen.extensions.getNumericValue
import com.welyab.ankobachen.extensions.toBinaryString
import com.welyab.ankobachen.extensions.toBinaryStringTable
import com.welyab.ankobachen.extensions.toHexString
import com.welyab.ankobachen.old.BOARD_SIZE
import java.lang.Exception
import kotlin.ULong
import kotlin.time.ExperimentalTime
import kotlin.ULong as Blockers
import kotlin.ULong as Movements

object BitboardUtil {

    val EMPTY = ULong.MIN_VALUE
    val FULL = ULong.MAX_VALUE

    val COLUMNS = listOf(
        0x8080808080808080uL,
        0x4040404040404040uL,
        0x2020202020202020uL,
        0x1010101010101010uL,
        0x0808080808080808uL,
        0x0404040404040404uL,
        0x0202020202020202uL,
        0x0101010101010101uL
    )

    val ROWS = listOf(
        0xff00000000000000uL,
        0x00ff000000000000uL,
        0x0000ff0000000000uL,
        0x000000ff00000000uL,
        0x00000000ff000000uL,
        0x0000000000ff0000uL,
        0x000000000000ff00uL,
        0x00000000000000ffuL
    )

    val KING_MOVE_MASK = listOf(
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

    val ROOK_MOVE_MASK = listOf(
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

    val BISHOP_MOVE_MASK = listOf(
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

    val KNIGHT_MOVE_MASK = listOf(
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

    fun generateRookMovementMasks(): List<ULong> {
        val masks = ArrayList<ULong>()
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val value = ROWS[row] xor COLUMNS[col]
                masks += removeEdges(row, col, value)
            }
        }
        return masks
    }

    fun generateKingMovementMasks(): List<ULong> {
        val masks = ArrayList<ULong>()
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val map = createBoard()
                (row - 1).takeIf { it >= 0 }?.let { map[it][col] = 1 }
                (row + 1).takeIf { it <= 7 }?.let { map[it][col] = 1 }
                (col - 1).takeIf { it >= 0 }?.let { map[row][it] = 1 }
                (col + 1).takeIf { it <= 7 }?.let { map[row][it] = 1 }
                Pair(row - 1, col - 1).takeIf { it.first >= 0 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row - 1, col + 1).takeIf { it.first >= 0 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 1, col - 1).takeIf { it.first <= 7 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 1, col + 1).takeIf { it.first <= 7 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                masks += map.toULong()
            }
        }
        return masks
    }

    fun generateKnightMovementMasks(): List<ULong> {
        val masks = ArrayList<ULong>()
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val map = createBoard()
                Pair(row - 2, col - 1).takeIf { it.first >= 0 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row - 2, col + 1).takeIf { it.first >= 0 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 2, col - 1).takeIf { it.first <= 7 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 2, col + 1).takeIf { it.first <= 7 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                Pair(row - 1, col - 2).takeIf { it.first >= 0 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 1, col - 2).takeIf { it.first <= 7 && it.second >= 0 }?.let { map[it.first][it.second] = 1 }
                Pair(row - 1, col + 2).takeIf { it.first >= 0 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                Pair(row + 1, col + 2).takeIf { it.first <= 7 && it.second <= 7 }?.let { map[it.first][it.second] = 1 }
                masks += map.toULong()
            }
        }
        return masks
    }

    private fun createBoard(value: ULong = 0uL): Array<Array<Int>> {
        val map = arrayOf(
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        )

        value.toBinaryString().forEachIndexed { index, bitChar ->
            val position = Position.from(index)
            map[position.row][position.column] = bitChar.getNumericValue()
        }

        return map
    }

    private fun Array<Array<Int>>.toULong() =
        asSequence()
            .flatMap { it.asSequence() }.map { it.toString() }
            .joinToString(separator = "")
            .toULong(2)


    fun generateBishopMovementMasks(): List<ULong> {
        val masks = ArrayList<ULong>()
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val map = createBoard()
                for (i in 1..7) {
                    val up = row - i
                    val down = row + i
                    val left = col - i
                    val right = col + i
                    // UP LEFT
                    if (up >= 0 && left >= 0) {
                        map[up][left] = 1
                    }
                    // UP RIGHT
                    if (up >= 0 && right < 8) {
                        map[up][right] = 1
                    }
                    // DOWN LEFT
                    if (down < 8 && left >= 0) {
                        map[down][left] = 1
                    }
                    // DOWN RIGHT
                    if (down < 8 && right < 8) {
                        map[down][right] = 1
                    }
                }
                masks += removeEdges(row, col, map.toULong())
            }
        }

        return masks
    }

    private fun removeEdges(row: Int, col: Int, value: ULong): ULong {
        var temp = value

        if (row == 0) {
            temp = temp and ROWS.last().inv()
        } else if (row == BOARD_SIZE - 1) {
            temp = temp and ROWS.first().inv()
        } else {
            temp = temp and ROWS.last().inv()
            temp = temp and ROWS.first().inv()
        }

        if (col == 0) {
            temp = temp and COLUMNS.last().inv()
        } else if (col == BOARD_SIZE - 1) {
            temp = temp and COLUMNS.first().inv()
        } else {
            temp = temp and COLUMNS.last().inv()
            temp = temp and COLUMNS.first().inv()
        }

        return temp
    }

    private fun getPermutations(bits: ULong): List<ULong> {
        return HashSet<String>()
            .apply {
                permute(bits.toBinaryString().toCharArray(), 0, this)
            }
            .asSequence()
            .map { it.toULong(2) }
            .toList()
    }

    private fun permute(chars: CharArray, currentIndex: Int, set: MutableSet<String>) {
        set += chars.joinToString(separator = "")
        for (index in currentIndex until chars.size) {
            if (chars[index] == '0') continue
            chars[index] = '0'
            permute(chars, index + 1, set)
            chars[index] = '1'
        }
    }

    fun generateBishopMovementDatabase(): Map<Int, Map<Blockers, Movements>> {
        val db = HashMap<Int, HashMap<Blockers, Movements>>()
        BISHOP_MOVE_MASK.forEachIndexed { squareIndex, movementMask ->
            getPermutations(movementMask).forEach { maskedBlockers ->
                val position = Position.from(squareIndex)
                val blockers = createBoard(maskedBlockers)
                val movements = createBoard(0uL)
                // up left
                for (i in 1..7) {
                    val row = position.row - i
                    val column = position.column - i
                    if (row < 0 || column < 0) break
                    movements[row][column] = 1
                    if (blockers[row][column] == 1) break
                }
                // up right
                for (i in 1..7) {
                    val row = position.row - i
                    val column = position.column + i
                    if (row < 0 || column > 7) break
                    movements[row][column] = 1
                    if (blockers[row][column] == 1) break
                }
                // down left
                for (i in 1..7) {
                    val row = position.row + i
                    val column = position.column - i
                    if (row > 7 || column < 0) break
                    movements[row][column] = 1
                    if (blockers[row][column] == 1) break
                }
                // down right
                for (i in 1..7) {
                    val row = position.row + i
                    val column = position.column + i
                    if (row > 7 || column > 7) break
                    movements[row][column] = 1
                    if (blockers[row][column] == 1) break
                }
                db.computeIfAbsent(squareIndex) { HashMap() }[blockers.toULong()] = movements.toULong()
            }
        }
        return db
    }

    fun generateRookMovementDatabase(): Map<Int, Map<Blockers, Movements>> {
        val db = HashMap<Int, HashMap<Blockers, Movements>>()
        ROOK_MOVE_MASK.forEachIndexed { squareIndex, movementMask ->
            getPermutations(movementMask).forEach { maskedBlockers ->
                val position = Position.from(squareIndex)
                val blockers = createBoard(maskedBlockers)
                val movements = createBoard(0uL)

                // up
                for (i in 1..7) {
                    val x = position.row - i
                    if (x < 0) break
                    movements[x][position.column] = 1
                    if (blockers[x][position.column] == 1) break
                }

                // down
                for (i in 1..7) {
                    val x = position.row + i
                    if (x >= 8) break
                    movements[x][position.column] = 1
                    if (blockers[x][position.column] == 1) break
                }

                // left
                for (i in 1..7) {
                    val x = position.column - i
                    if (x < 0) break
                    movements[position.row][x] = 1
                    if (blockers[position.row][x] == 1) break
                }

                // right
                for (i in 1..7) {
                    val x = position.column + i
                    if (x >= 8) break
                    movements[position.row][x] = 1
                    if (blockers[position.row][x] == 1) break
                }

                db.computeIfAbsent(squareIndex) { HashMap() }[blockers.toULong()] = movements.toULong()
            }
        }
        return db
    }

    fun generateWhitePawnSingleMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 0 || row == 7) {
                    list += 0uL
                } else {
                    val map = createBoard()
                    map[row - 1][col] = 1
                    list += map.toULong()
                }
            }
        }
        return list
    }

    fun generateWhitePawnCaptureMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 0 || row == 7) {
                    list += 0uL
                } else {
                    val map = createBoard()
                    (col - 1).takeIf { it >= 0 }?.let { map[row - 1][it] = 1 }
                    (col + 1).takeIf { it <= 7 }?.let { map[row - 1][it] = 1 }
                    list += map.toULong()
                }
            }
        }
        return list
    }

    fun generateWhitePawnDoubleMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 6) {
                    val map = createBoard()
                    map[row - 1][col] = 1
                    map[row - 2][col] = 1
                    list += map.toULong()
                } else {
                    list += 0uL
                }
            }
        }
        return list
    }

    fun generateBlackPawnSingleMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 0 || row == 7) {
                    list += 0uL
                } else {
                    val map = createBoard()
                    map[row + 1][col] = 1
                    list += map.toULong()
                }
            }
        }
        return list
    }

    fun generateBlackPawnCaptureMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 0 || row == 7) {
                    list += 0uL
                } else {
                    val map = createBoard()
                    (col - 1).takeIf { it >= 0 }?.let { map[row + 1][it] = 1 }
                    (col + 1).takeIf { it <= 7 }?.let { map[row + 1][it] = 1 }
                    list += map.toULong()
                }
            }
        }
        return list
    }

    fun generateBlackPawnDoubleMovementMask(): List<ULong> {
        val list = ArrayList<ULong>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (row == 1) {
                    val map = createBoard()
                    map[row + 1][col] = 1
                    map[row + 2][col] = 1
                    list += map.toULong()
                } else {
                    list += 0uL
                }
            }
        }
        return list
    }
}

private fun HashMap<Int, ULong>.getKey(value: ULong): Int =
    asSequence()
        .filter { it.value == value }
        .map { it.key }
        .firstOrNull()
        ?: throw Exception("key not found")

@ExperimentalTime
fun main() {
    // map[square index][blockers][movements]
    val map = BitboardUtil.generateBishopMovementDatabase()

    val movePatterns = map.values.flatMap { it.values }.distinct().sorted().toList()
    val movePatternByIndex = HashMap<Int, ULong>()
    movePatterns.forEachIndexed { index, value ->
        movePatternByIndex[index] = value
    }

    println("wide totals = ${map.map { it.value.size }.asSequence().sum()}")
    println("unique totals = ${movePatterns.size}")

    map.asSequence()
        .sortedBy { it.key }
        .forEach { e ->
            e.value.asSequence()
                .sortedBy { it.key }
                .forEach {
                    println(
                        "square = %s, blockers = %s, targets = %s, index = %s"
                            .format(
                                e.key,
                                it.key.toHexString(),
                                it.value.toHexString(),
                                movePatternByIndex.getKey(it.value)
                            )
                    )
                }
        }
}

/**
 * Copyright 2024 Welyab da Silva Paula
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.welyab.ankobachen

import com.welyab.ankobachen.BitboardConstants.EMPTY
import com.welyab.ankobachen.BitboardConstants.FIRST_BIT_SET
import com.welyab.ankobachen.BitboardConstants.ROW_0_SET
import com.welyab.ankobachen.BitboardConstants.ROW_1_SET
import com.welyab.ankobachen.BitboardConstants.ROW_2_SET
import com.welyab.ankobachen.BitboardConstants.ROW_3_SET
import com.welyab.ankobachen.BitboardConstants.ROW_4_SET
import com.welyab.ankobachen.BitboardConstants.ROW_5_SET
import com.welyab.ankobachen.BitboardConstants.ROW_6_SET
import com.welyab.ankobachen.BitboardConstants.ROW_7_SET

object BitboardConstants {
    const val EMPTY: ULong = 0uL
    const val FIRST_BIT_SET: ULong = 0b1000000000000000000000000000000000000000000000000000000000000000uL

    const val ROW_0_SET = 0b1111111100000000000000000000000000000000000000000000000000000000uL
    const val ROW_1_SET = 0b0000000011111111000000000000000000000000000000000000000000000000uL
    const val ROW_2_SET = 0b0000000000000000111111110000000000000000000000000000000000000000uL
    const val ROW_3_SET = 0b0000000000000000000000001111111100000000000000000000000000000000uL
    const val ROW_4_SET = 0b0000000000000000000000000000000011111111000000000000000000000000uL
    const val ROW_5_SET = 0b0000000000000000000000000000000000000000111111110000000000000000uL
    const val ROW_6_SET = 0b0000000000000000000000000000000000000000000000001111111100000000uL
    const val ROW_7_SET = 0b0000000000000000000000000000000000000000000000000000000011111111uL
}

private val rowsSetByRowNumber = arrayOf(
    ROW_0_SET,
    ROW_1_SET,
    ROW_2_SET,
    ROW_3_SET,
    ROW_4_SET,
    ROW_5_SET,
    ROW_6_SET,
    ROW_7_SET,
)

fun ULong.activateBit(index: Int): ULong = this.or(FIRST_BIT_SET.shr(index))
fun ULong.deactivateBit(index: Int): ULong = this.and(FIRST_BIT_SET.shr(index).inv())

fun ULong.isBitActive(index: Int): Boolean = this.and(FIRST_BIT_SET.shr(index)) != EMPTY

fun ULong.deactivateRow(
    row: Int
): ULong = try {
    this and rowsSetByRowNumber[row].inv()
} catch (ex: ArrayIndexOutOfBoundsException) {
    throw ChessException()
}

fun ULong.getFirstActiveBitIndex(): Int = this.countLeadingZeroBits()

fun ULong.toBitStringTable(): String = buildString {
    append(this@toBitStringTable.toString(2))
}

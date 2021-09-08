/*
 * Copyright (C) 2021 Welyab da Silva Paula
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

const val EMPTY = 0UL
const val FULL = ULong.MAX_VALUE

fun ULong.shiftRight(bits: Int) = if (bits >= 63) EMPTY else shr(bits)

val ULong.index get() = countLeadingZeroBits()

fun Iterable<ULong>.toBitboard() = reduceOrNull { acc, value -> acc or value } ?: EMPTY

fun Sequence<Square>.toBitBoard(): ULong = map { it.bitboard }.asIterable().toBitboard()

fun or(vararg values: ULong) = values.toBitboard()

fun ULong.toBitboardString() = toString(2).padStart(64, '0')

fun ULong.toBitboardStringFormatted() = buildString {
    toBitboardString().forEachIndexed { index, c ->
        append(c)
        if ((index + 1) % 8 == 0) append(String.format("%n"))
        else append(' ')
    }
}

private fun localGetOneIndexes(value: ULong): List<Int> {
    var localValue = value
    val indexes = mutableListOf<Int>()
    while (localValue != EMPTY) {
        val index = localValue.countLeadingZeroBits()
        indexes += index
        localValue = localValue.and(FULL.shiftRight(index + 1))
    }
    return indexes
}

val ULong.indexes: List<Int> get() = localGetOneIndexes(this)

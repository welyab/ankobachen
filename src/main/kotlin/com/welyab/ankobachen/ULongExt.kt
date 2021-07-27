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

package com.welyab.ankobachen.extensions

import kotlin.math.absoluteValue
import kotlin.math.sign

val ZERO_UL: ULong get() = 0uL
val FULL_UL: ULong get() = ULong.MAX_VALUE
private const val HIGHEST_BIT = 0x8000000000000000uL

fun ULong.toBinaryString() =
    this.toString(2).padStart(ULong.SIZE_BITS, '0')

private fun String.withSpaces() =
    asSequence().joinToString(separator = " ")

fun ULong.toBinaryStringTable() = toBinaryString().let {
    buildString {
        for (startIndex in 0 until 64 step 8) {
            append(it.substring(startIndex until (startIndex + 8)).withSpaces()).append('\n')
        }
    }
}

fun ULong.toHexString() = toString(16).padStart(16, '0')

fun ULong.shift(bits: Int) =
    when {
        bits.absoluteValue >= ULong.SIZE_BITS -> 0uL
        bits.sign == 1 -> this shr bits
        else -> this shl bits.absoluteValue
    }

@ExperimentalUnsignedTypes
fun ULong.isBitSet(bitIndex: Int): Boolean {
    return this.and(HIGHEST_BIT.shift(bitIndex)) != 0uL
}

inline fun ULong.forEachSetBit(action: (index: Int) -> Unit) {
    var value = this
    while (value != ZERO_UL) {
        val bitIndex = value.countLeadingZeroBits()
        action.invoke(bitIndex)
        value = value.and(FULL_UL.shift(bitIndex + 1))
    }
}

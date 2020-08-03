package com.welyab.ankobachen.extensions

import kotlin.math.absoluteValue
import kotlin.math.sign

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

inline fun ULong.shift(bits: Int) =
    when {
        bits.absoluteValue >= ULong.SIZE_BITS -> 0uL
        bits.sign == 1 -> this shr bits
        else -> this shl bits.absoluteValue
    }


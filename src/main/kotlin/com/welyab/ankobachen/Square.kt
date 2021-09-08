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

class SquareException(
    message: String = "",
    cause: Throwable? = null
) : ChessException(
    message,
    cause
)

enum class Square(
    val file: Char,
    val rank: Int,
    val row: Int,
    val column: Int,
    val index: Int,
    val bitboard: ULong
) {

    // @formatter:off
    A8('a', 8, 0, 0, 0, 0x8000000000000000UL),
    B8('b', 8, 0, 1, 1, 0x4000000000000000UL),
    C8('c', 8, 0, 2, 2, 0x2000000000000000UL),
    D8('d', 8, 0, 3, 3, 0x1000000000000000UL),
    E8('e', 8, 0, 4, 4, 0x800000000000000UL),
    F8('f', 8, 0, 5, 5, 0x400000000000000UL),
    G8('g', 8, 0, 6, 6, 0x200000000000000UL),
    H8('h', 8, 0, 7, 7, 0x100000000000000UL),
    A7('a', 7, 1, 0, 8, 0x80000000000000UL),
    B7('b', 7, 1, 1, 9, 0x40000000000000UL),
    C7('c', 7, 1, 2, 10, 0x20000000000000UL),
    D7('d', 7, 1, 3, 11, 0x10000000000000UL),
    E7('e', 7, 1, 4, 12, 0x8000000000000UL),
    F7('f', 7, 1, 5, 13, 0x4000000000000UL),
    G7('g', 7, 1, 6, 14, 0x2000000000000UL),
    H7('h', 7, 1, 7, 15, 0x1000000000000UL),
    A6('a', 6, 2, 0, 16, 0x800000000000UL),
    B6('b', 6, 2, 1, 17, 0x400000000000UL),
    C6('c', 6, 2, 2, 18, 0x200000000000UL),
    D6('d', 6, 2, 3, 19, 0x100000000000UL),
    E6('e', 6, 2, 4, 20, 0x80000000000UL),
    F6('f', 6, 2, 5, 21, 0x40000000000UL),
    G6('g', 6, 2, 6, 22, 0x20000000000UL),
    H6('h', 6, 2, 7, 23, 0x10000000000UL),
    A5('a', 5, 3, 0, 24, 0x8000000000UL),
    B5('b', 5, 3, 1, 25, 0x4000000000UL),
    C5('c', 5, 3, 2, 26, 0x2000000000UL),
    D5('d', 5, 3, 3, 27, 0x1000000000UL),
    E5('e', 5, 3, 4, 28, 0x800000000UL),
    F5('f', 5, 3, 5, 29, 0x400000000UL),
    G5('g', 5, 3, 6, 30, 0x200000000UL),
    H5('h', 5, 3, 7, 31, 0x100000000UL),
    A4('a', 4, 4, 0, 32, 0x80000000UL),
    B4('b', 4, 4, 1, 33, 0x40000000UL),
    C4('c', 4, 4, 2, 34, 0x20000000UL),
    D4('d', 4, 4, 3, 35, 0x10000000UL),
    E4('e', 4, 4, 4, 36, 0x8000000UL),
    F4('f', 4, 4, 5, 37, 0x4000000UL),
    G4('g', 4, 4, 6, 38, 0x2000000UL),
    H4('h', 4, 4, 7, 39, 0x1000000UL),
    A3('a', 3, 5, 0, 40, 0x800000UL),
    B3('b', 3, 5, 1, 41, 0x400000UL),
    C3('c', 3, 5, 2, 42, 0x200000UL),
    D3('d', 3, 5, 3, 43, 0x100000UL),
    E3('e', 3, 5, 4, 44, 0x80000UL),
    F3('f', 3, 5, 5, 45, 0x40000UL),
    G3('g', 3, 5, 6, 46, 0x20000UL),
    H3('h', 3, 5, 7, 47, 0x10000UL),
    A2('a', 2, 6, 0, 48, 0x8000UL),
    B2('b', 2, 6, 1, 49, 0x4000UL),
    C2('c', 2, 6, 2, 50, 0x2000UL),
    D2('d', 2, 6, 3, 51, 0x1000UL),
    E2('e', 2, 6, 4, 52, 0x800UL),
    F2('f', 2, 6, 5, 53, 0x400UL),
    G2('g', 2, 6, 6, 54, 0x200UL),
    H2('h', 2, 6, 7, 55, 0x100UL),
    A1('a', 1, 7, 0, 56, 0x80UL),
    B1('b', 1, 7, 1, 57, 0x40UL),
    C1('c', 1, 7, 2, 58, 0x20UL),
    D1('d', 1, 7, 3, 59, 0x10UL),
    E1('e', 1, 7, 4, 60, 0x8UL),
    F1('f', 1, 7, 5, 61, 0x4UL),
    G1('g', 1, 7, 6, 62, 0x2UL),
    H1('h', 1, 7, 7, 63, 0x1UL);
    // @formatter:on

    val san: String get() = "$file$rank"

    companion object {

        private fun Int.isValidIndex() = this in 0..63
        private fun Char.isValidFile() = this in 'a'..'h'
        private fun Char.isValidRank() = this in '1'..'8'
        private fun Int.isValidRank() = this in 1..8
        private fun Int.isValidRow() = this in 0..7
        private fun Int.isValidColumn() = this in 0..7

        fun from(index: Int): Square {
            if (!index.isValidIndex()) throw SquareException(
                "Invalid square index: $index. Use a value in the range [0, 63]"
            )
            return values()[index]
        }

        fun from(san: String): Square {
            if (san.length != 2
                || !san[0].isValidFile()
                || !san[1].isValidRank()
            ) throw SquareException(
                "Invalid square notation: $san. Use algebraic notation like a1, a8, f5, h2, etc, " +
                        "with file and rank in the range [a, h] and [1, 8] respectively"
            )
            val row: Int = 8 - (san[1] - '0')
            val column: Int = san[0] - 'a'
            return _from(row, column)
        }

        fun from(file: Char, rank: Char): Square {
            if (!file.isValidFile() || !rank.isValidRank()) throw SquareException(
                "Invalid file/rank: $file/$rank. Use values in range [a, h] and [1, 8], respectively"
            )
            val row: Int = 8 - (rank - '0')
            val column: Int = file - 'a'
            return _from(row, column)
        }

        fun from(file: Char, rank: Int): Square {
            if (!file.isValidFile() || !rank.isValidRank()) throw SquareException(
                "Invalid file/rank: $file/$rank. Use values in range [a, h] and [1, 8], respectively"
            )
            val row: Int = 8 - rank
            val column: Int = file - 'a'
            return _from(row, column)
        }

        fun from(row: Int, column: Int): Square {
            if (!row.isValidRow() || !column.isValidColumn()) throw SquareException(
                "Invalid row/column: $row/$column. Use values in range [0, 7]"
            )
            return _from(row, column)
        }

        private fun _from(row: Int, column: Int): Square {
            return values()[row * 8 + column]
        }
    }
}

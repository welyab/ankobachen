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

import com.welyab.ankobachen.FileSymbols.FILE_A
import com.welyab.ankobachen.FileSymbols.FILE_B
import com.welyab.ankobachen.FileSymbols.FILE_C
import com.welyab.ankobachen.FileSymbols.FILE_D
import com.welyab.ankobachen.FileSymbols.FILE_E
import com.welyab.ankobachen.FileSymbols.FILE_F
import com.welyab.ankobachen.FileSymbols.FILE_G
import com.welyab.ankobachen.FileSymbols.FILE_H
import com.welyab.ankobachen.RankNumbers.RANK_1
import com.welyab.ankobachen.RankNumbers.RANK_2
import com.welyab.ankobachen.RankNumbers.RANK_3
import com.welyab.ankobachen.RankNumbers.RANK_4
import com.welyab.ankobachen.RankNumbers.RANK_5
import com.welyab.ankobachen.RankNumbers.RANK_6
import com.welyab.ankobachen.RankNumbers.RANK_7
import com.welyab.ankobachen.RankNumbers.RANK_8
import com.welyab.ankobachen.RowColNumbers.COL_0
import com.welyab.ankobachen.RowColNumbers.COL_1
import com.welyab.ankobachen.RowColNumbers.COL_2
import com.welyab.ankobachen.RowColNumbers.COL_3
import com.welyab.ankobachen.RowColNumbers.COL_4
import com.welyab.ankobachen.RowColNumbers.COL_5
import com.welyab.ankobachen.RowColNumbers.COL_6
import com.welyab.ankobachen.RowColNumbers.COL_7
import com.welyab.ankobachen.RowColNumbers.ROW_0
import com.welyab.ankobachen.RowColNumbers.ROW_1
import com.welyab.ankobachen.RowColNumbers.ROW_2
import com.welyab.ankobachen.RowColNumbers.ROW_3
import com.welyab.ankobachen.RowColNumbers.ROW_4
import com.welyab.ankobachen.RowColNumbers.ROW_5
import com.welyab.ankobachen.RowColNumbers.ROW_6
import com.welyab.ankobachen.RowColNumbers.ROW_7

object RankNumbers {
    const val RANK_1 = 1
    const val RANK_2 = 2
    const val RANK_3 = 3
    const val RANK_4 = 4
    const val RANK_5 = 5
    const val RANK_6 = 6
    const val RANK_7 = 7
    const val RANK_8 = 8
}

object FileSymbols {
    const val FILE_A = 'a'
    const val FILE_B = 'b'
    const val FILE_C = 'c'
    const val FILE_D = 'd'
    const val FILE_E = 'e'
    const val FILE_F = 'f'
    const val FILE_G = 'g'
    const val FILE_H = 'h'
}

object RowColNumbers {
    const val ROW_0 = 0
    const val ROW_1 = 1
    const val ROW_2 = 2
    const val ROW_3 = 3
    const val ROW_4 = 4
    const val ROW_5 = 5
    const val ROW_6 = 6
    const val ROW_7 = 7

    const val COL_0 = 0
    const val COL_1 = 1
    const val COL_2 = 2
    const val COL_3 = 3
    const val COL_4 = 4
    const val COL_5 = 5
    const val COL_6 = 6
    const val COL_7 = 7
}

operator fun Square.rangeTo(other: Square): SquareRange = SquareRange(this, other)

class SquareRange(
    override val endInclusive: Square,
    override val start: Square
) : ClosedRange<Square>, Iterable<Square> {

    override fun iterator(): Iterator<Square> {
        return object : Iterator<Square> {
            val baseIterator = (start.getIndex()..endInclusive.getIndex()).iterator()

            override fun hasNext(): Boolean {
                return baseIterator.hasNext()
            }

            override fun next(): Square {
                return Square.entries[baseIterator.next()]
            }
        }
    }
}

enum class Square(
    val row: Int,
    val col: Int,
    val file: Char,
    val rank: Int,
) {

    // @formatter:off
    A8(row = ROW_0, col = COL_0, file = FILE_A, rank = RANK_8), B8(row = ROW_0, col = COL_1, file = FILE_B, rank = RANK_8), C8(row = ROW_0, col = COL_2, file = FILE_C, rank = RANK_8), D8(row = ROW_0, col = COL_3, file = FILE_D, rank = RANK_8), E8(row = ROW_0, col = COL_4, file = FILE_E, rank = RANK_8), F8(row = ROW_0, col = COL_5, file = FILE_F, rank = RANK_8), G8(row = ROW_0, col = COL_6, file = FILE_G, rank = RANK_8), H8(row = ROW_0, col = COL_7, file = FILE_H, rank = RANK_8),
    A7(row = ROW_1, col = COL_0, file = FILE_A, rank = RANK_7), B7(row = ROW_1, col = COL_1, file = FILE_B, rank = RANK_7), C7(row = ROW_1, col = COL_2, file = FILE_C, rank = RANK_7), D7(row = ROW_1, col = COL_3, file = FILE_D, rank = RANK_7), E7(row = ROW_1, col = COL_4, file = FILE_E, rank = RANK_7), F7(row = ROW_1, col = COL_5, file = FILE_F, rank = RANK_7), G7(row = ROW_1, col = COL_6, file = FILE_G, rank = RANK_7), H7(row = ROW_1, col = COL_7, file = FILE_H, rank = RANK_7),
    A6(row = ROW_2, col = COL_0, file = FILE_A, rank = RANK_6), B6(row = ROW_2, col = COL_1, file = FILE_B, rank = RANK_6), C6(row = ROW_2, col = COL_2, file = FILE_C, rank = RANK_6), D6(row = ROW_2, col = COL_3, file = FILE_D, rank = RANK_6), E6(row = ROW_2, col = COL_4, file = FILE_E, rank = RANK_6), F6(row = ROW_2, col = COL_5, file = FILE_F, rank = RANK_6), G6(row = ROW_2, col = COL_6, file = FILE_G, rank = RANK_6), H6(row = ROW_2, col = COL_7, file = FILE_H, rank = RANK_6),
    A5(row = ROW_3, col = COL_0, file = FILE_A, rank = RANK_5), B5(row = ROW_3, col = COL_1, file = FILE_B, rank = RANK_5), C5(row = ROW_3, col = COL_2, file = FILE_C, rank = RANK_5), D5(row = ROW_3, col = COL_3, file = FILE_D, rank = RANK_5), E5(row = ROW_3, col = COL_4, file = FILE_E, rank = RANK_5), F5(row = ROW_3, col = COL_5, file = FILE_F, rank = RANK_5), G5(row = ROW_3, col = COL_6, file = FILE_G, rank = RANK_5), H5(row = ROW_3, col = COL_7, file = FILE_H, rank = RANK_5),
    A4(row = ROW_4, col = COL_0, file = FILE_A, rank = RANK_4), B4(row = ROW_4, col = COL_1, file = FILE_B, rank = RANK_4), C4(row = ROW_4, col = COL_2, file = FILE_C, rank = RANK_4), D4(row = ROW_4, col = COL_3, file = FILE_D, rank = RANK_4), E4(row = ROW_4, col = COL_4, file = FILE_E, rank = RANK_4), F4(row = ROW_4, col = COL_5, file = FILE_F, rank = RANK_4), G4(row = ROW_4, col = COL_6, file = FILE_G, rank = RANK_4), H4(row = ROW_4, col = COL_7, file = FILE_H, rank = RANK_4),
    A3(row = ROW_5, col = COL_0, file = FILE_A, rank = RANK_3), B3(row = ROW_5, col = COL_1, file = FILE_B, rank = RANK_3), C3(row = ROW_5, col = COL_2, file = FILE_C, rank = RANK_3), D3(row = ROW_5, col = COL_3, file = FILE_D, rank = RANK_3), E3(row = ROW_5, col = COL_4, file = FILE_E, rank = RANK_3), F3(row = ROW_5, col = COL_5, file = FILE_F, rank = RANK_3), G3(row = ROW_5, col = COL_6, file = FILE_G, rank = RANK_3), H3(row = ROW_5, col = COL_7, file = FILE_H, rank = RANK_3),
    A2(row = ROW_6, col = COL_0, file = FILE_A, rank = RANK_2), B2(row = ROW_6, col = COL_1, file = FILE_B, rank = RANK_2), C2(row = ROW_6, col = COL_2, file = FILE_C, rank = RANK_2), D2(row = ROW_6, col = COL_3, file = FILE_D, rank = RANK_2), E2(row = ROW_6, col = COL_4, file = FILE_E, rank = RANK_2), F2(row = ROW_6, col = COL_5, file = FILE_F, rank = RANK_2), G2(row = ROW_6, col = COL_6, file = FILE_G, rank = RANK_2), H2(row = ROW_6, col = COL_7, file = FILE_H, rank = RANK_2),
    A1(row = ROW_7, col = COL_0, file = FILE_A, rank = RANK_1), B1(row = ROW_7, col = COL_1, file = FILE_B, rank = RANK_1), C1(row = ROW_7, col = COL_2, file = FILE_C, rank = RANK_1), D1(row = ROW_7, col = COL_3, file = FILE_D, rank = RANK_1), E1(row = ROW_7, col = COL_4, file = FILE_E, rank = RANK_1), F1(row = ROW_7, col = COL_5, file = FILE_F, rank = RANK_1), G1(row = ROW_7, col = COL_6, file = FILE_G, rank = RANK_1), H1(row = ROW_7, col = COL_7, file = FILE_H, rank = RANK_1);
    // @formatter:on

    fun getIndex(): Int = ordinal

    companion object {

        private val squaresByIndex = arrayOf(
            // @formatter:off
            A8, B8, C8, D8, E8, F8, G8, H8,
            A7, B7, C7, D7, E7, F7, G7, H7,
            A6, B6, C6, D6, E6, F6, G6, H6,
            A5, B5, C5, D5, E5, F5, G5, H5,
            A4, B4, C4, D4, E4, F4, G4, H4,
            A3, B3, C3, D3, E3, F3, G3, H3,
            A2, B2, C2, D2, E2, F2, G2, H2,
            A1, B1, C1, D1, E1, F1, G1, H1,
            // @formatter:on
        )

        private val squaresByAlgebraicCoordinate = arrayOf(
            // @formatter:off
            /*              ranks 1   2   3   4   5   6   7   8 */
            /* file a */ arrayOf(A1, A2, A3, A4, A5, A6, A7, A8),
            /* file b */ arrayOf(B1, B2, B3, B4, B5, B6, B7, B8),
            /* file c */ arrayOf(C1, C2, C3, C4, C5, C6, C7, C8),
            /* file d */ arrayOf(D1, D2, D3, D4, D5, D6, D7, D8),
            /* file e */ arrayOf(E1, E2, E3, E4, E5, E6, E7, E8),
            /* file f */ arrayOf(F1, F2, F3, F4, F5, F6, F7, F8),
            /* file g */ arrayOf(G1, G2, G3, G4, G5, G6, G7, G8),
            /* file h */ arrayOf(H1, H2, H3, H4, H5, H6, H7, H8),
            // @formatter:on
        )

        fun rowToRank(row: Int) {
        }

        fun fromAlgebraicCoordinate(
            file: Char,
            rank: Int
        ): Square = try {
            squaresByAlgebraicCoordinate[file - 'a'][rank - 1]
        } catch (ex: ArrayIndexOutOfBoundsException) {
            throw ChessException(
                "",
                ex
            )
        }

        fun rowColToIndex(
            row: Int,
            col: Int
        ): Int = row * 8 + col

        fun fromSquareIndex(
            squareIndex: Int
        ): Square = try {
            squaresByIndex[squareIndex]
        } catch (ex: ArrayIndexOutOfBoundsException) {
            throw ChessException(
                "",
                ex
            )
        }
    }
}

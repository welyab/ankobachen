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

class MoveMetadata {

    companion object {
        //@formatter:off
        const val ALL_FLAGS: Long            = 0b10000000000000000000000000000000

        const val CAPTURE_FLAG: Long         = 0b01000000000000000000000000000000
        const val EN_PASSANT_FLAG: Long      = 0b00100000000000000000000000000000
        const val CASTLING_FLAG: Long        = 0b00010000000000000000000000000000
        const val PROMOTION_FLAG: Long       = 0b00001000000000000000000000000000
        const val CHECK_FLAG: Long           = 0b00000100000000000000000000000000
        const val DISCOVERY_CHECK_FLAG: Long = 0b00000010000000000000000000000000
        const val DOUBLE_CHECK_FLAG: Long    = 0b00000001000000000000000000000000
        const val CHECKMATE_FLAG: Long       = 0b00000000100000000000000000000000
        const val STALEMATE_FLAG: Long       = 0b00000000010000000000000000000000

        const val PSEUDO_MOVE_FLAG: Long     = 0b00000000001000000000000000000000
        const val FROM_PIECE_MASK: Long      = 0b00000000000000000000000011110000
        const val TO_PIECE_MASK: Long        = 0b00000000000000000000000000001111

        const val ALL_FLAGS_STRING            = "all_flags"
        const val MAIN_FLAGS_STRING           = "main_flags"
        const val CAPTURE_FLAG_STRING         = "capture"
        const val EN_PASSANT_FLAG_STRING      = "en_passant"
        const val CASTLING_FLAG_STRING        = "castling"
        const val PROMOTION_FLAG_STRING       = "promotion"
        const val CHECK_FLAG_STRING           = "check"
        const val DISCOVERY_CHECK_FLAG_STRING = "discovery_check"
        const val DOUBLE_CHECK_FLAG_STRING    = "double_check"
        const val CHECKMATE_FLAG_STRING       = "checkmate"
        const val STALEMATE_FLAG_STRING       = "stalemate"
        const val PSEUDO_MOVE_FLAG_STRING     = "pseudo_move"
        //@formatter:on

        fun decodeFromPiece(metadata: Long): Piece {
            return Piece.from(metadata.and(FROM_PIECE_MASK).shr(4).toInt())
        }

        fun decodeToPiece(metadata: Long): Piece {
            return Piece.from(metadata.and(TO_PIECE_MASK).toInt())
        }

        fun decodeToString(value: Long) = value.let {
            val decoded = mutableListOf<String>()
            decoded += if (value.and(ALL_FLAGS) != 0L) ALL_FLAGS_STRING
            else MAIN_FLAGS_STRING
            if (value.and(CAPTURE_FLAG) != 0L) decoded += CAPTURE_FLAG_STRING
            if (value.and(EN_PASSANT_FLAG) != 0L) decoded += EN_PASSANT_FLAG_STRING
            if (value.and(CASTLING_FLAG) != 0L) decoded += CASTLING_FLAG_STRING
            if (value.and(PROMOTION_FLAG) != 0L) decoded += PROMOTION_FLAG_STRING
            if (value.and(CHECK_FLAG) != 0L) decoded += CHECK_FLAG_STRING
            if (value.and(DISCOVERY_CHECK_FLAG) != 0L) decoded += DISCOVERY_CHECK_FLAG_STRING
            if (value.and(DOUBLE_CHECK_FLAG) != 0L) decoded += DOUBLE_CHECK_FLAG_STRING
            if (value.and(CHECKMATE_FLAG) != 0L) decoded += CHECKMATE_FLAG_STRING
            if (value.and(STALEMATE_FLAG) != 0L) decoded += STALEMATE_FLAG_STRING
            if (value.and(PSEUDO_MOVE_FLAG) != 0L) decoded += PSEUDO_MOVE_FLAG_STRING
            decoded += "from_piece=${decodeToPiece(value)}"
            decoded += "to_piece=${decodeToPiece(value)}"
            decoded.joinToString()
        }
    }
}

class MovementBag(
    val movements: List<PieceMovements>
)

operator fun MovementBag.plus(other: MovementBag): MovementBag {
    TODO()
}

class PieceMovement {

}

class PieceMovements(
    val fromIndex: Int,
    val toIndexes: List<Int>,
    val metadatas: List<Long>
) {

    val fromSquare: Square
        get() = Square.from(fromIndex)

    fun getToSquare(toIndex: Int): Square = Square.from(toIndexes[toIndex])

    fun getMetadata(index: Int) = metadatas[index]

    override fun toString() = buildString {
        toIndexes.indices.forEachIndexed { index, toIndex ->
            append("${index + 1}# ")
            append(fromSquare)
            append(" -> ")
            append(getToSquare(toIndex))
            append(" ")
            append(MoveMetadata.decodeToString(getMetadata(toIndex)))
            append("%n".format())
        }
    }
}

fun main() {
    println("")
}

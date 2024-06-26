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

package com.welyab.ankobachen.utils

import com.welyab.ankobachen.PlacedPieceIterable

class BoardPrinter private constructor() {
    companion object
}

fun BoardPrinter.Companion.toString(placedPieceIterable: PlacedPieceIterable): String {
    val newLine = System.lineSeparator()
    val pieceLocations = placedPieceIterable.iterator().asSequence().toList()
    return (0..7)
        .asSequence()
        .map { row ->
            val rowPieces = pieceLocations
                .asSequence()
                .filter { it.square.row == row }
                .sortedBy { it.square.col }
                .toList()
            val rowLetters = (0..7)
                .asSequence()
                .map { column ->
                    rowPieces.asSequence()
                        .filter { it.square.col == column }
                        .map { it.piece.symbol }
                        .firstOrNull()
                        ?: ' '
                }
                .toList()
                .toTypedArray()
            "│ %c │ %c │ %c │ %c │ %c │ %c │ %c │ %c │".format(*rowLetters)
        }
        .reduce { l1, l2 -> "$l1${newLine}├───┼───┼───┼───┼───┼───┼───┼───┤${newLine}$l2" }
        .let {
            "┌───┬───┬───┬───┬───┬───┬───┬───┐${newLine}$it${newLine}└───┴───┴───┴───┴───┴───┴───┴───┘${newLine}"
        }
}

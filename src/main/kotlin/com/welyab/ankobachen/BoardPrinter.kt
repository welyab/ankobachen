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

@ExperimentalStdlibApi
object BoardPrinter {

    fun toString(board: Board) : String {
        val pieceLocations = board.getPieceLocations()
        return (0..7)
            .asSequence()
            .map { row ->
                val rowPieces = pieceLocations
                    .asSequence()
                    .filter { it.position.row == row }
                    .sortedBy { it.position.column }
                    .toList()
                val rowLetters = (0..7).asSequence()
                    .map { column ->
                        rowPieces.asSequence()
                            .filter { it.position.column == column }
                            .map { it.piece.letter }
                            .firstOrNull()
                            ?: ' '
                    }
                    .toList()
                    .toTypedArray()
                "│ %c │ %c │ %c │ %c │ %c │ %c │ %c │ %c │".format(*rowLetters)
            }
            .reduce { l1, l2 -> "$l1${com.welyab.ankobachen.old.NEWLINE}├───┼───┼───┼───┼───┼───┼───┼───┤${com.welyab.ankobachen.old.NEWLINE}$l2" }
            .let {
                "┌───┬───┬───┬───┬───┬───┬───┬───┐${com.welyab.ankobachen.old.NEWLINE}$it${com.welyab.ankobachen.old.NEWLINE}└───┴───┴───┴───┴───┴───┴───┴───┘${com.welyab.ankobachen.old.NEWLINE}"
            }
    }

    override fun toString(): String = this.javaClass.simpleName
}
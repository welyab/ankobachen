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

data class PieceLocation(val piece: Piece, val position: Position) : Comparable<PieceLocation> {

    override fun compareTo(other: PieceLocation): Int {
        val rowDiff = position.row - other.position.row
        if (rowDiff != 0) return -rowDiff
        val colDiff = position.column - other.position.column
        if (colDiff != 0) return colDiff
        return piece.compareTo(other.piece)
    }

    override fun toString() = "$piece at $position"
}

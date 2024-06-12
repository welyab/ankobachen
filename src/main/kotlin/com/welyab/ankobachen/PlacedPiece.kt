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

class PlacedPiece private constructor(
    val square: Square,
    val piece: Piece,
) {
    override fun toString(): String {
        return "${PlacedPiece::class.simpleName}[$square, $piece]"
    }

    companion object {
        private val placedPieces = Array(Square.entries.size) { squareIndex ->
            Square.fromSquareIndex(
                squareIndex = squareIndex
            ).let { square ->
                Array(
                    Piece.entries.size
                ) { pieceIndex ->
                    PlacedPiece(
                        square,
                        Piece.entries[pieceIndex],
                    )
                }
            }
        }

        fun getFrom(
            piece: Piece,
            square: Square,
        ): PlacedPiece = placedPieces[square.getIndex()][piece.getIndex()]

        fun getFrom(
            pieceIndex: Int,
            squareIndex: Int
        ): PlacedPiece = try {
            placedPieces[squareIndex][pieceIndex]
        } catch (ex: ArrayIndexOutOfBoundsException) {
            throw ChessException(
                message = buildString {
                    append("There is no placed with index $pieceIndex at square $squareIndex.")
                    append(" ")
                    append(
                        "Valid values are piece index in 0..${
                            Piece.entries.size - 1
                        } and square index in 0..${
                            Square.entries.size - 1
                        }"
                    )
                },
                cause = ex
            )
        }
    }
}

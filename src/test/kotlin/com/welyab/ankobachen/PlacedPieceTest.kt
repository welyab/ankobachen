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

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream
import kotlin.test.assertEquals

class PlacedPieceTest {

    @ParameterizedTest
    @MethodSource(
        value = ["pieceSquareSource"]
    )
    fun `placed piece should refer properly piece and square`(
        piece: Piece,
        square: Square
    ) {
        val placedPiece = PlacedPiece.getFrom(piece, square)
        assertEquals(piece, placedPiece.piece)
        assertEquals(square, placedPiece.square)
    }

    companion object {

        @JvmStatic
        fun pieceSquareSource(): Stream<Arguments> =
            Square.entries
                .asSequence()
                .flatMap { square -> Piece.entries.asSequence().map { piece -> Arguments.of(piece, square) } }
                .asStream()
    }
}

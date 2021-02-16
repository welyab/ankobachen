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

import java.awt.Image
import javax.imageio.ImageIO

object Resources {

    private val piecesIcons: Map<Piece, Image> by lazy {
        mapOf(
            Piece.BLACK_BISHOP to "/util/images/black_bishop.png",
            Piece.BLACK_KING to "/util/images/black_king.png",
            Piece.BLACK_KNIGHT to "/util/images/black_knight.png",
            Piece.BLACK_PAWN to "/util/images/black_pawn.png",
            Piece.BLACK_QUEEN to "/util/images/black_queen.png",
            Piece.BLACK_ROOK to "/util/images/black_rook.png",
            Piece.WHITE_BISHOP to "/util/images/white_bishop.png",
            Piece.WHITE_KING to "/util/images/white_king.png",
            Piece.WHITE_KNIGHT to "/util/images/white_knight.png",
            Piece.WHITE_PAWN to "/util/images/white_pawn.png",
            Piece.WHITE_QUEEN to "/util/images/white_queen.png",
            Piece.WHITE_ROOK to "/util/images/white_rook.png"
        ).asSequence().map { e ->
            val image = ImageIO.read(javaClass.getResource(e.value))
            e.key to image
        }.toMap()
    }

    fun getPieceIcon(piece: Piece): Image = piecesIcons[piece] ?: error("not image icon for $piece")
}
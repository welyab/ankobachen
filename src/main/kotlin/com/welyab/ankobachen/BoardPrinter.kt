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

import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.Position.Companion.rowColumnToSquareIndex
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

private fun Color.getAwtColor() = when (this) {
    WHITE -> java.awt.Color.WHITE
    BLACK -> java.awt.Color.LIGHT_GRAY
}

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object BoardPrinter {

    private val NEWLINE = String.format("%n")

    fun to(board: Board): BufferedImage {
        val pieces = arrayOfNulls<Piece>(64)
            .apply {
                board.getPieceLocations()
                    .forEach { this[it.position.squareIndex] = it.piece }
            }
        val boardLength = 400
        val squareLength = boardLength / 8.0
        val bImage = BufferedImage(boardLength, boardLength, BufferedImage.TYPE_INT_RGB)
        val g = bImage.graphics as Graphics2D
        for (row in 0..7) {
            for (column in 0..7) {
                val color = if (row % 2 == 0) if (column % 2 == 0) BLACK else WHITE
                else if (column % 2 == 0) WHITE else BLACK
                val squareIndex = rowColumnToSquareIndex(row, column)
                val squareShape = Rectangle2D.Double(
                    column * squareLength,
                    row * squareLength,
                    squareLength,
                    squareLength
                )
                g.color = color.getAwtColor()
                g.fill(squareShape)
                val piece = pieces[squareIndex]
                if (piece != null) {
                    val icon = Resources.getPieceIcon(piece)
                    g.drawImage(
                        icon,
                        (column * squareLength).toInt(),
                        (row * squareLength).toInt(),
                        squareLength.toInt(),
                        squareLength.toInt(),
                        null
                    )
                }
            }
        }
        return bImage
    }

    fun toString(board: Board): String {
        val pieceLocations = board.getPieceLocations()
        return (0..7)
            .asSequence()
            .map { row ->
                val rowPieces = pieceLocations
                    .asSequence()
                    .filter { it.position.row == row }
                    .sortedBy { it.position.column }
                    .toList()
                val rowLetters = (0..7)
                    .asSequence()
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
            .reduce { l1, l2 -> "$l1${NEWLINE}├───┼───┼───┼───┼───┼───┼───┼───┤${NEWLINE}$l2" }
            .let {
                "┌───┬───┬───┬───┬───┬───┬───┬───┐${NEWLINE}$it${NEWLINE}└───┴───┴───┴───┴───┴───┴───┴───┘${NEWLINE}"
            }
    }

    override fun toString(): String = this.javaClass.simpleName
}

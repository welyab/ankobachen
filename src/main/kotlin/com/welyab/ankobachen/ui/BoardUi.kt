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
package com.welyab.ankobachen.ui

import com.welyab.ankobachen.Board
import com.welyab.ankobachen.Color
import com.welyab.ankobachen.Color.BLACK
import com.welyab.ankobachen.Color.WHITE
import com.welyab.ankobachen.Piece
import com.welyab.ankobachen.Position
import com.welyab.ankobachen.Resources
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.BasicStroke.CAP_ROUND
import java.awt.BasicStroke.JOIN_ROUND
import java.awt.Color as AwtColor
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import java.awt.RenderingHints
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.Stroke
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel
import javax.swing.SwingUtilities.invokeLater
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class BoardUi : JPanel {

    private val board = Board()

    private var renderingHints: RenderingHints = DEFAULT_RENDERING_HINTS
    private var boardBuffer: Image? = null

    private var pressedRightPoint: Point? = null
    private var draggedRightPoint: Point? = null

    private var leftClickedPoint: Point? = null
    private var pressedLeftPoint: Point? = null
    private var draggedLeftPoint: Point? = null

    private var selectedSquare: Position? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val squareWidth
        get() = width / 8.0

    @Suppress("MemberVisibilityCanBePrivate")
    val squareHeight
        get() = height / 8.0

    private var selectedSquareColor: AwtColor = DEFAULT_SELECTED_SQUARE_COLOR

    private var whiteSquareColor: AwtColor = DEFAULT_WHITE_SQUARE_COLOR
    private var blackSquareColor: AwtColor = DEFAULT_BLACK_SQUARE_COLOR
    private var markedSquareColor: AwtColor = DEFAULT_MARKED_SQUARE_COLOR

    private var buildingArrow: Arrow? = null
    private val arrows: MutableList<Arrow> = ArrayList()
    private var arrowColor: AwtColor = DEFAULT_ARROW_COLOR
    private var arrowStroke: Stroke = DEFAULT_ARROW_STROKE

    private var holdPiece: Piece? = null

    private val markedSquares: MutableList<Position> = ArrayList()

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor() : super() {
        LocalMouseAdapter().apply {
            addMouseListener(this)
            addMouseMotionListener(this)
            addMouseWheelListener(this)
        }
    }

    private fun Position.getSquareColor(): Color = SQUARE_COLOR_MAP[squareIndex]

    private fun Position.getPoint2D(): Point2D {
        return Point2D.Double(column * squareWidth, row * squareHeight)
    }

    private fun Position.getRectangle2D(): Rectangle2D {
        return getPoint2D().let {
            Rectangle2D.Double(
                it.x, it.y,
                squareWidth, squareHeight
            )
        }
    }

    override fun paint(g: Graphics) {
        g as Graphics2D
        g.setRenderingHints(renderingHints)
        g.paintBoardSquares()
        g.paintSquareMarks()
        g.paintPieces()
        g.paintArrows()
    }

    private fun Graphics2D.paintBoardSquares() {
        getBoardBuffer()?.let { drawImage(it, 0, 0, null) }
    }

    private fun Graphics2D.paintPieces() {
        val savedTransform = transform
        board.getPieceLocations()
            .asSequence()
            .forEach {
                val alphaComposite = if (it.position == pressedLeftPoint?.toPosition() && holdPiece != null) {
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)
                } else {
                    null
                }
                val paintPoint = it.position.getPoint2D()
                val transform = AffineTransform()
                transform.translate(paintPoint.x, paintPoint.y)
                it.piece.paintIcon(
                    g = this,
                    transform = transform,
                    alphaComposite = alphaComposite
                )
            }

        val hPiece = holdPiece
        val dPoint = draggedLeftPoint
        if (hPiece != null && dPoint != null) {
            val transform = AffineTransform()
            transform.translate(
                dPoint.x.toDouble() - (squareWidth * DEFAULT_HOLD_PIECE_IMG_SCALE) / 2,
                dPoint.y.toDouble() - (squareHeight * DEFAULT_HOLD_PIECE_IMG_SCALE) / 2
            )
            transform.scale(DEFAULT_HOLD_PIECE_IMG_SCALE, DEFAULT_HOLD_PIECE_IMG_SCALE)
            hPiece.paintIcon(this, transform)
        }

        this.transform = savedTransform
    }

    private fun Piece.paintIcon(
        g: Graphics2D,
        transform: AffineTransform,
        alphaComposite: AlphaComposite? = null
    ) {
        val icon = Resources.getPieceIcon(this)
        val scaled = icon.getScaledInstance(
            squareWidth.toInt(),
            squareHeight.toInt(),
            Image.SCALE_SMOOTH
        )
        val saveTransform = g.transform
        val saveComposite = g.composite
        alphaComposite?.let { g.composite = it }
        g.transform = transform
        g.drawImage(scaled, 0, 0, null)
        g.transform = saveTransform
        g.composite = saveComposite
    }

    private fun Graphics2D.paintSquareMarks() {
        this.color = markedSquareColor
        markedSquares.forEach {
            fill(it.getRectangle2D())
        }
        this.color = selectedSquareColor
        selectedSquare?.apply { fill(this.getRectangle2D()) }
    }

    private fun Graphics2D.paintArrows() {
        arrows.forEach { it.paint(this) }
        buildingArrow?.paint(this)
    }

    private fun getBoardBuffer(): Image? {
        var buffer = boardBuffer
        if (
            buffer == null
            || buffer.getWidth(null) != width
            || buffer.getWidth(null) != height
        ) {
            buffer = createImage(width, height)
            boardBuffer = if (buffer == null) null
            else {
                val g = buffer.graphics as Graphics2D
                g.color = whiteSquareColor
                g.fillRect(0, 0, width, height)
                g.color = blackSquareColor
                Position.values()
                    .asSequence()
                    .filter { it.getSquareColor().isBlack }
                    .map { it.getRectangle2D() }
                    .forEach { g.fill(it) }
                buffer
            }
        }
        return boardBuffer
    }

    private fun Point.toPosition(): Position? = pointToPosition(x.toDouble(), y.toDouble())

    private fun pointToPosition(x: Double, y: Double): Position? {
        val row = (y / squareHeight).toInt()
        val column = (x / squareWidth).toInt()
        return if (row in 0..7 && column in 0..7) Position.from(row, column)
        else null
    }

    private fun addSquareMark(pressedPoint: Point?, releasedPoint: Point?) {
        if (pressedPoint == null || releasedPoint == null) return
        val fromPosition = pressedPoint.toPosition() ?: return
        val toPosition = releasedPoint.toPosition() ?: return
        if (fromPosition != toPosition) return
        if (markedSquares.remove(fromPosition)) return
        markedSquares += fromPosition
    }

    private fun normalizedRowColumn(value: Int): Int {
        return when {
            value < 0 -> 0
            value > 7 -> 7
            else -> value
        }
    }

    private fun getPossibleArrow(from: Position, to: Position): Arrow {
        if (
            (from.row - to.row).absoluteValue == 1 && (from.column - to.column).absoluteValue == 2
            || (from.row - to.row).absoluteValue == 2 && (from.column - to.column).absoluteValue == 1
        ) {
            return Arrow(from, to)
        }
        val rowDirection = (to.row - from.row).sign
        val columnDirection = (to.column - from.column).sign
        var shift = max(
            (from.row - to.row).absoluteValue,
            (from.column - to.column).absoluteValue
        )
        (from.row + shift * rowDirection).run {
            if (this < 0) shift -= this.absoluteValue
            else if (this > 7) shift -= this - 7
        }
        (from.column + shift * columnDirection).run {
            if (this < 0) shift -= this.absoluteValue
            else if (this > 7) shift -= this - 7
        }
        val cornerRow = from.row + shift * rowDirection
        val cornerColumn = from.column + shift * columnDirection
        val row = if ((from.row - to.row).absoluteValue < (cornerRow - to.row).absoluteValue) from.row
        else cornerRow
        val column = if ((from.column - to.column).absoluteValue < (cornerColumn - to.column).absoluteValue) from.column
        else cornerColumn
        return Arrow(from, Position.from(row, column))
    }

    private fun updateBuildingArrow(fromPoint: Point?, toPoint: Point?) {
        if (fromPoint == null || toPoint == null) return
        val fromPosition = fromPoint.toPosition() ?: return
        val toPosition = toPoint.toPosition() ?: return
        if (fromPosition == toPosition) {
            buildingArrow = null
            return
        }
//        buildingArrow = Arrow(fromPosition, toPosition)
        buildingArrow = getPossibleArrow(fromPosition, toPosition)
    }

    private fun addArrow(fromPoint: Point?, toPoint: Point?) {
        if (fromPoint == null || toPoint == null) return
        val fromPosition = fromPoint.toPosition() ?: return
        val toPosition = toPoint.toPosition() ?: return
        if (fromPosition == toPosition) return
//        val arrow = Arrow(fromPosition, toPosition)
        val arrow = getPossibleArrow(fromPosition, toPosition)
        if (arrows.removeIf { it.from == fromPosition && it.to == toPosition }) return
        arrows += arrow
    }

    private fun clearBuildingArrow() {
        buildingArrow = null
    }

    private fun clearArrows() {
        arrows.clear()
    }

    private fun clearSquareMarks() {
        markedSquares.clear()
    }

    private fun holdPiece(point: Point) {
        val position = point.toPosition() ?: return
        if (board.isEmpty(position)) return
        holdPiece = board.getPiece(position)
    }

    private fun dropPiece(point: Point) {
        holdPiece = null
    }

    private fun selectSquare(point: Point?) {
        if (point == null) return
        val position = point.toPosition()
        if (position != null && board.isEmpty(position)) {
            selectedSquare = null
            return
        }
        if (selectedSquare != null && selectedSquare == position) {
            selectedSquare = null
            return
        }
        selectedSquare = position
    }

    private inner class Arrow(val from: Position, val to: Position) {

        fun paint(g: Graphics2D) {
            val fromRec = from.getRectangle2D()
            val toRec = to.getRectangle2D()
            val line = Line2D.Double(fromRec.centerX, fromRec.centerY, toRec.centerX, toRec.centerY)
            g.color = arrowColor
            g.stroke = arrowStroke
            g.draw(line)
        }
    }

    private inner class LocalMouseAdapter : CustomMouseAdapter() {

        override fun rightMousePressed(e: MouseEvent) {
            pressedRightPoint = e.point
            draggedRightPoint = e.point
            repaint()
        }

        override fun rightMouseReleased(e: MouseEvent) {
            clearBuildingArrow()
            addArrow(pressedRightPoint, e.point)
            addSquareMark(pressedRightPoint, e.point)
            pressedRightPoint = null
            draggedRightPoint = null
            repaint()
        }

        override fun rightMouseDragged(e: MouseEvent) {
            updateBuildingArrow(pressedRightPoint, e.point)
            pressedRightPoint = pressedRightPoint ?: e.point
            draggedRightPoint = e.point
            repaint()
        }

        override fun leftMouseClicked(e: MouseEvent) {
            clearSquareMarks()
            clearArrows()
            selectSquare(e.point)
            repaint()
        }

        override fun leftMousePressed(e: MouseEvent) {
            holdPiece(e.point)
            pressedLeftPoint = e.point
            draggedLeftPoint = e.point
            repaint()
        }

        override fun leftMouseDragged(e: MouseEvent) {
            draggedLeftPoint = e.point
            repaint()
        }

        override fun leftMouseReleased(e: MouseEvent) {
            dropPiece(e.point)
            pressedLeftPoint = null
            draggedLeftPoint = null
            repaint()
        }
    }

    companion object {

        private val DEFAULT_RENDERING_HINTS = RenderingHints(
            mapOf<RenderingHints.Key, Any>(
                KEY_ANTIALIASING to VALUE_ANTIALIAS_ON
            )
        )

        private val DEFAULT_WHITE_SQUARE_COLOR = AwtColor(238, 238, 210)
        private val DEFAULT_BLACK_SQUARE_COLOR = AwtColor(118, 150, 86)
        private val DEFAULT_MARKED_SQUARE_COLOR = AwtColor(255, 0, 0, 125)
        private val DEFAULT_ARROW_COLOR = AwtColor(0, 148, 255, 180)
        private val DEFAULT_ARROW_STROKE = BasicStroke(12.0f, CAP_ROUND, JOIN_ROUND)
        private val DEFAULT_SELECTED_SQUARE_COLOR = AwtColor(0, 0, 0, 70)

        private const val DEFAULT_HOLD_PIECE_IMG_SCALE = 1.4

        private val SQUARE_COLOR_MAP = listOf(
            BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK,
            BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK,
            BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK,
            BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK
        )
    }
}

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
fun main() {
    val frame = JFrame("BoardUi - Test")
    frame.contentPane = BoardUi()
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.setSize(600, 600)
    invokeLater { frame.isVisible = true }
}

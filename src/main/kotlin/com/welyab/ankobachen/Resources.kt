package com.welyab.ankobachen

import java.awt.Image
import java.awt.Toolkit
import javax.imageio.ImageIO
import javax.swing.ImageIcon

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
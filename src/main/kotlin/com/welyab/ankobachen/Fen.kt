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

import java.lang.Exception

private const val WHITE_KING_SIDE_CASTLING_FLAG = 'K'
private const val WHITE_QUEEN_SIDE_CASTLING_FLAG = 'Q'
private const val BLACK_KING_SIDE_CASTLING_FLAG = 'k'
private const val BLACK_QUEEN_SIDE_CASTLING_FLAG = 'q'

private const val DEFAULT_HALF_MOVE_CLOCK = 0
private const val DEFAULT_FULL_MOVE_COUNTER = 1

class FenException(
    fen: String,
    message: String = "",
    cause: Throwable? = null
) : ChessException(
    formatMessage(fen, message),
    cause
) {
    companion object {
        private fun formatMessage(fen: String, message: String): String {
            var formatted = "Can't parse fen \"$fen\""
            if (message.isNotEmpty()) {
                formatted += ". $message"
            }
            return formatted
        }
    }
}

data class FenCastlingFlags(
    val whiteShort: Square? = null,
    val whiteLong: Square? = null,
    val blackShort: Square? = null,
    val blackLong: Square? = null

)

class FenInfo(
    val pieceDisposition: List<PieceSquare>,
    val colorToMove: Color,
    val castlingFlags: FenCastlingFlags,
    val enPassantTarget: Square?,
    val halfMoveClock: Int,
    val fullMoveCounter: Int
)

class FenString(val value: String) {
    fun createParser() = FenParser(this)
}

class FenParser(val fen: String) {

    constructor(fen: FenString) : this(fen.value)

    private var fenInfo: FenInfo? = null

    fun parse(): FenInfo {
        try {
            return parseInternal()
        } catch (e: Exception) {
            if (e is FenException) throw e
            throw FenException(fen = fen, cause = e)
        }
    }

    private fun parseInternal(): FenInfo {
        var fenInfoCache = fenInfo
        if (fenInfoCache != null) return fenInfoCache

        val parts = fen.split(" ")

        val pieceDisposition = parsePieceDisposition(parts.getOrNull(0))

        fenInfoCache = FenInfo(
            pieceDisposition = pieceDisposition,
            colorToMove = parseColorToMove(parts.getOrNull(1)),
            castlingFlags = parseCastlingFlags(parts.getOrNull(2), pieceDisposition),
            enPassantTarget = parseEnPassantTarget(parts.getOrNull(3)),
            halfMoveClock = parseHalfMoveClock(parts.getOrNull(4)),
            fullMoveCounter = parseFullMoveCounter(parts.getOrNull(5)),
        )

        fenInfo = fenInfoCache
        return fenInfoCache
    }

    private fun parsePieceDisposition(fenPieceDisposition: String?): List<PieceSquare> {
        if (fenPieceDisposition == null) throw FenException(
            "The piece disposition is missing"
        )
        var row = 0
        var column = 0
        val pieces = mutableListOf<PieceSquare>()
        for (c in fenPieceDisposition) {
            if (c == '/') {
                row++
                if (column != 8) throw FenException(fen, "Invalid piece disposition: $fenPieceDisposition")
                column = 0
                continue
            }
            if (c in '1'..'8') {
                column += c.digitToInt()
                continue
            }
            val piece = try {
                Piece.from(c)
            } catch (e: PieceException) {
                throw FenException(
                    fen,
                    "Invalid piece disposition: $fenPieceDisposition",
                    e
                )
            }
            pieces += PieceSquare(piece, Square.from(row, column))
            column++
        }
        row++
        if (row != 8 || column != 8) throw FenException(
            fen,
            "Invalid piece disposition: $fenPieceDisposition"
        )
        return pieces
    }

    private fun parseColorToMove(fenColorToMove: String?): Color {
        if (fenColorToMove == null) throw FenException(
            fen,
            "The color to move \"$fenColorToMove\" is missing or is invalid"
        )
        return try {
            Color.from(fenColorToMove)
        } catch (e: ColorException) {
            throw FenException(
                fen,
                "Invalid color: \"$fenColorToMove\""
            )
        }
    }

    private fun parseCastlingFlags(
        fenCastlingFlags: String?,
        pieceDisposition: List<PieceSquare>
    ): FenCastlingFlags {
        if (fenCastlingFlags.isNullOrEmpty() || fenCastlingFlags == "-") return FenCastlingFlags()

        var whiteShort: Square? = null
        var whiteLong: Square? = null
        var blackShort: Square? = null
        var blackLong: Square? = null

        val whiteKingSquare = pieceDisposition.firstOrNull {
            it.square.row == 7 && it.piece.isWhiteKing
        }?.square
        val blackKingSquare = pieceDisposition.firstOrNull {
            it.square.row == 0 && it.piece.isBlackKing
        }?.square

        var index = 0
        if (index < fenCastlingFlags.length && fenCastlingFlags[index] == WHITE_KING_SIDE_CASTLING_FLAG) {
            index++
            if (whiteKingSquare != null) {
                whiteShort = pieceDisposition
                    .asSequence()
                    .filter { it.piece.isWhiteRook }
                    .filter { it.square.row == whiteKingSquare.row }
                    .filter { it.square.column > whiteKingSquare.column }
                    .map { it.square }
                    .toList()
                    .takeIf { it.size == 1 }
                    ?.get(0)
            }
        }
        if (index < fenCastlingFlags.length && fenCastlingFlags[index] == WHITE_QUEEN_SIDE_CASTLING_FLAG) {
            index++
            if (whiteKingSquare != null) {
                whiteLong = pieceDisposition
                    .asSequence()
                    .filter { it.piece.isWhiteRook }
                    .filter { it.square.row == whiteKingSquare.row }
                    .filter { it.square.column < whiteKingSquare.column }
                    .map { it.square }
                    .toList()
                    .takeIf { it.size == 1 }
                    ?.get(0)
            }
        }
        if (index < fenCastlingFlags.length && fenCastlingFlags[index] == BLACK_KING_SIDE_CASTLING_FLAG) {
            index++
            if (blackKingSquare != null) {
                blackShort = pieceDisposition
                    .asSequence()
                    .filter { it.piece.isBlackRook }
                    .filter { it.square.row == blackKingSquare.row }
                    .filter { it.square.column > blackKingSquare.column }
                    .map { it.square }
                    .toList()
                    .takeIf { it.size == 1 }
                    ?.get(0)
            }
        }
        if (index < fenCastlingFlags.length && fenCastlingFlags[index] == BLACK_QUEEN_SIDE_CASTLING_FLAG) {
            index++
            if (blackKingSquare != null) {
                blackLong = pieceDisposition
                    .asSequence()
                    .filter { it.piece.isBlackRook }
                    .filter { it.square.row == blackKingSquare.row }
                    .filter { it.square.column < blackKingSquare.column }
                    .map { it.square }
                    .toList()
                    .takeIf { it.size == 1 }
                    ?.get(0)
            }
        }
        if (index < fenCastlingFlags.length) throw FenException(
            fen,
            "Invalid castling flags: \"$fenCastlingFlags\""
        )
        return FenCastlingFlags(
            whiteShort,
            whiteLong,
            blackShort,
            blackLong
        )
    }

    private fun parseEnPassantTarget(fenPassantTarget: String?): Square? {
        if (fenPassantTarget == null || fenPassantTarget == "-") return null
        if (fenPassantTarget.length > 2) throw FenException(
            fen,
            "Invalid en passant target: $fenPassantTarget. Use algebraic notation"
        )
        return try {
            Square.from(fenPassantTarget[0], fenPassantTarget[1])
        } catch (e: SquareException) {
            throw FenException(
                fen,
                "Invalid en passant target: $fenPassantTarget. Use algebraic notation"
            )
        }
    }

    private fun parseHalfMoveClock(fenHalfMoveClock: String?): Int {
        if (fenHalfMoveClock == null || fenHalfMoveClock == "-") return DEFAULT_HALF_MOVE_CLOCK
        return try {
            fenHalfMoveClock.toInt()
                .takeIf { it >= 0 }
                ?: throw FenException(
                    fen,
                    "Half move clock \"$fenHalfMoveClock\" should be positive"
                )
        } catch (e: NumberFormatException) {
            throw FenException(fen, "Invalid half move clock \"$fenHalfMoveClock\"", e)
        }
    }

    private fun parseFullMoveCounter(fenFullMoveCounter: String?): Int {
        if (fenFullMoveCounter == null || fenFullMoveCounter == "-") return DEFAULT_FULL_MOVE_COUNTER
        return try {
            fenFullMoveCounter.toInt()
                .takeIf { it >= 1 }
                ?: throw FenException(
                    fen,
                    "Full move counter \"$fenFullMoveCounter\" should be greater than 0"
                )
        } catch (e: NumberFormatException) {
            throw FenException(fen, "Invalid full move counter \"$fenFullMoveCounter\"", e)
        }
    }
}

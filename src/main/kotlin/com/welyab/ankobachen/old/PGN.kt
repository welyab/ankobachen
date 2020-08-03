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
package com.welyab.ankobachen.old

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.LinkedList
import java.util.Stack

const val SUFFIX_GOOD_MOVE = "!"
const val SUFFIX_POOR_MOVE = "?"
const val SUFFIX_VERY_GOOD_MOVE = "!!"
const val SUFFIX_VERY_POOR_MOVE = "??"
const val SUFFIX_SPECULATIVE_MOVE = "!?"
const val SUFFIX_QUESTIONABLE_MOVE = "?!"

const val GAME_RESULT_WHITE_WIN = "1-0"
const val GAME_RESULT_BLACK_WIN = "0-1"
const val GAME_RESULT_DRAW = "1/2-1/2"
const val GAME_RESULT_UNKNOWN = "*"

const val TAG_NAME_EVENT = "Event"
const val TAG_NAME_SITE = "Site"
const val TAG_NAME_DATE = "Date"
const val TAG_NAME_ROUND = "Round"
const val TAG_NAME_WHITE = "White"
const val TAG_NAME_BLACK = "Black"
const val TAG_NAME_RESULT = "Result"
const val TAG_NAME_SETUP = "SetUp"
const val TAG_NAME_FEN = "FEN"

class PgnException(message: String, cause: Throwable? = null) : ChessException(message, cause)

enum class SevenTagRoster(val tagName: String) {

    EVENT(TAG_NAME_EVENT),
    SITE(TAG_NAME_SITE),
    DATE(TAG_NAME_DATE),
    ROUND(TAG_NAME_ROUND),
    WHITE(TAG_NAME_WHITE),
    BLACK(TAG_NAME_BLACK),
    RESULT(TAG_NAME_RESULT);
}

enum class SuffixAnnotation(val value: String) {

    GOOD_MOVE("!"),
    POOR_MOVE("?"),
    VERY_GOOD_MOVE("!!"),
    VERY_POOR_MOVE("??"),
    SPECULATIVE_MOVE("!?"),
    QUESTIONABLE_MOVE("?!");

    companion object {
        fun fromValue(value: String) =
            when (value) {
                SUFFIX_GOOD_MOVE -> GOOD_MOVE
                SUFFIX_POOR_MOVE -> POOR_MOVE
                SUFFIX_VERY_GOOD_MOVE -> VERY_GOOD_MOVE
                SUFFIX_VERY_POOR_MOVE -> VERY_POOR_MOVE
                SUFFIX_SPECULATIVE_MOVE -> SPECULATIVE_MOVE
                SUFFIX_QUESTIONABLE_MOVE -> QUESTIONABLE_MOVE
                else -> throw IllegalArgumentException("Invalid sufix: $value")
            }
    }
}

enum class GameResult(val value: String) {

    WHITE_WIN(GAME_RESULT_WHITE_WIN),
    BLACK_WIN(GAME_RESULT_BLACK_WIN),
    DRAW(GAME_RESULT_DRAW),
    UNKNOWN(GAME_RESULT_UNKNOWN);

    override fun toString() = value

    companion object {
        fun from(result: String) = when (result) {
            GAME_RESULT_WHITE_WIN -> WHITE_WIN
            GAME_RESULT_BLACK_WIN -> BLACK_WIN
            GAME_RESULT_DRAW -> DRAW
            GAME_RESULT_UNKNOWN -> UNKNOWN
            else -> throw IllegalArgumentException("Invalid game result: $result")
        }
    }
}

private fun Char.isNewLineChar() = this == '\r' || this == '\n'

private class Context {

    private var gameBuilder: PgnGameBuilder? = null
    private var moveNumber: Int? = null
    private var sideToMove: Color? = null
    private var game: PgnGame? = null

    fun getGameBuilder() = gameBuilder!!

    fun resetGameBuilder() {
        gameBuilder = PgnGameBuilder()
    }

    fun getCurrentMoveNumber() = moveNumber!!

    fun setCurrentMoveNumber(moveNumber: Int) {
        this.moveNumber = moveNumber
    }

    fun setCurrentSideToMove(sideToMove: Color) {
        this.sideToMove = sideToMove
    }

    fun getCurrentSideToMove() = sideToMove!!

    fun getCurrentParsedGame() = game!!

    fun setCurrentParsedGame(game: PgnGame) {
        this.game = game
    }
}

data class Tag(val name: String, val value: String) {
    override fun hashCode() = name.hashCode()
    override fun equals(other: Any?) = name == other
    override fun toString() = "Tag($name = $value)"
}

private class CharStream(private val charSource: BufferedReader) : Iterator<Char> {

    private var prev: Char? = null
    private val queue = LinkedList<Char>()

    override fun hasNext(): Boolean {
        if (queue.isNotEmpty()) return true
        val c = charSource.read()
        if (c < 0) return false
        queue.addLast(c.toChar())
        return true
    }

    override fun next(): Char {
        if (!hasNext()) throw NoSuchElementException("no more characters")
        val c = queue.removeFirst()!!
        prev = c
        return c
    }

    fun back() {
        if (prev == null) throw IllegalStateException("no previous character to back")
        queue.offerFirst(prev)
        prev = null
    }

    fun nextWhile(predicate: (Char) -> Boolean) = buildString {
        this@CharStream
            .asSequence()
            .takeWhile {
                if (predicate.invoke(it)) {
                    true
                } else {
                    back()
                    false
                }
            }
            .forEach { append(it) }
    }

    fun nextWhileInLine(predicate: (Char) -> Boolean): String {
        val value = nextWhile { !it.isNewLineChar() && predicate.invoke(it) }
        nextWhile { it.isNewLineChar() }
        return value
    }
}

private enum class TokenType {
    TAG_NAME,
    TAG_VALUE,
    MOVE_NUMBER,
    SINGLE_DOT,
    TRIPLE_DOT,
    MOVE,
    COMMENT,
    NAG,
    GAME_RESULT,
    OPEN_PARENTHESIS,
    CLOSE_PARENTHESIS,
    WHITE_SPACES,
    NEW_LINE,
    EOF
}

private data class Token(val value: String, val type: TokenType) {

    private fun String.removeNewLines() =
        value.replace("\n", "\\n")
            .replace("\r", "\\r")

    override fun toString() = "Token(value='${value.removeNewLines()}', type=$type)"
}

private class Tokenizer(charSource: BufferedReader) {

    private var prev: Token? = null
    private var prevBackup: Token? = null
    private val stream = CharStream(charSource)

    fun skipWhitespaces() {
        stream.nextWhile { it.isWhitespace() }
    }

    fun nextToken(vararg expectedTypes: TokenType) =
        nextToken().apply {
            if (type !in expectedTypes) throw Exception(
                "Unexpected token type. Found $this, expecting one of ${expectedTypes.contentToString()}"
            )
        }

    fun nextToken(): Token {
        if (prev != null) {
            val token = prev!!
            prev = null
            return token
        }

        if (!stream.hasNext()) return Token("", TokenType.EOF)

        val c = stream.next()
        stream.back()

        var token: Token? = null

        if (c.isWhitespace() && !c.isNewLineChar()) {
            token = Token(
                stream.nextWhileInLine { it.isWhitespace() },
                TokenType.WHITE_SPACES
            )
        } else if (c.isNewLineChar()) {
            var prev: Char? = null
            val value = stream.nextWhile {
                when {
                    !it.isNewLineChar() -> false
                    prev == null -> {
                        prev = it
                        true
                    }
                    prev == '\r' && it == '\n' -> {
                        prev = it
                        true
                    }
                    else -> false
                }
            }
            token = Token(value, TokenType.NEW_LINE)
        } else if (c == '[') {
            stream.next()
            token = Token(stream.nextWhile { !it.isWhitespace() }, TokenType.TAG_NAME)
        } else if (c == '(') {
            stream.next()
            token = Token(c.toString(), TokenType.OPEN_PARENTHESIS)
        } else if (c == ')') {
            stream.next()
            token = Token(c.toString(), TokenType.CLOSE_PARENTHESIS)
        } else if (c == '"') {
            stream.next()
            val value = stream.nextWhile { it != '"' }
            stream.next()
            stream.next()
            token = Token(value, TokenType.TAG_VALUE)
        } else if (c == '$') {
            stream.next()
            val value = stream.nextWhile { it.isDigit() }
            token = Token(value, TokenType.NAG)
        } else if (c.isDigit()) {
            val value1 = stream.nextWhile { it.isDigit() }
            val value2 = stream.next()
            stream.back()
            token = if (value2 == '-' || value2 == '/') {
                val value3 = stream.nextWhileInLine { !it.isWhitespace() }
                Token(value1 + value3, TokenType.GAME_RESULT)
            } else {
                Token(value1, TokenType.MOVE_NUMBER)
            }
        } else if (c == '*') {
            stream.next()
            token = Token(c.toString(), TokenType.GAME_RESULT)
        } else if (c == '.') {
            val value = stream.nextWhile { it == '.' }
            token = when (value) {
                "." -> Token(c.toString(), TokenType.SINGLE_DOT)
                "..." -> Token(c.toString(), TokenType.TRIPLE_DOT)
                else -> throw Exception("Unexpected $value")
            }
        } else if (c == '{') {
            var stop = false
            val value = stream.nextWhile {
                when {
                    stop -> false
                    it == '}' -> {
                        stop = true
                        true
                    }
                    else -> true
                }
            }
            token = Token(value.trim(), TokenType.COMMENT)
        } else {
            token = Token(
                stream.nextWhile { !it.isWhitespace() && it != ')' && it != '{' },
                TokenType.MOVE
            )
        }

        prevBackup = token
        return token!!
    }

    fun back() {
        if (prevBackup == null) throw Exception("No previous token to back")
        prev = prevBackup
        prevBackup = null
    }
}

//class PgnMove(
//    val origin: LocalizedPiece,
//    val target: LocalizedPiece,
//    val isCheck: Boolean,
//    val isCheckmate: Boolean,
//    val isPromotion: Boolean,
//    val isCapture: Boolean,
//    val isCastling: Boolean,
//    val isEnPassant: Boolean,
//    val isGoodMove: Boolean,
//    val isPoorMove: Boolean,
//    val isVeryGoodMove: Boolean,
//    val isVeryPoorMove: Boolean,
//    val isSpeculativeMove: Boolean,
//    val isQuestionableMove: Boolean
//) {
//}

class PgnMove(
    val number: Int,
    val sideToMove: Color,
    val move: String,
    val beforeComment: List<String> = emptyList(),
    val afterComment: List<String> = emptyList(),
    val variants: List<List<PgnMove>> = emptyList()
) {
    override fun toString() = buildString {
        append(number)
        when (sideToMove) {
            Color.WHITE -> append(".")
            Color.BLACK -> append("...")
        }
        append(" ")
//        if (beforeComment.isNotBlank()) append("{${beforeComment}} ")
        append(move)
//        if (afterComment.isNotBlank()) append(" {${afterComment}}")

        for (variant in variants) {
            if (variant.isNotEmpty()) {
                append(" (")
                variant.forEachIndexed { index, value ->
                    if (index > 0) append(" ")
                    append(value)
                }
                append(')')
            }
        }
    }
}

private data class ParsedMove(
    val origin: LocalizedPiece,
    val target: LocalizedPiece,
    val isCheck: Boolean,
    val isCastling: Boolean,
    val isEnPassant: Boolean,
    val isCheckmate: Boolean,
    val isPromotion: Boolean,
    val isCapture: Boolean,
    val isGoodMove: Boolean,
    val isPoorMove: Boolean,
    val isVeryGoodMove: Boolean,
    val isVeryPoorMove: Boolean,
    val isSpeculativeMove: Boolean,
    val isQuestionableMove: Boolean
)

class PgnGame(
    private val tags: Map<String, Tag>,
    private val moves: List<PgnMove>,
    private val result: GameResult
) {
    fun hasTag(tagName: String) = tags.containsKey(tagName)
    fun getTag(tagName: String): Tag {
        if (!hasTag(tagName)) {
            throw PgnException("Not tag present: $tagName")
        }
        return tags.get(tagName)!!
    }

    fun getMoves() = moves

    fun getTag(tag: SevenTagRoster) = getTag(tag.tagName)

    fun getEventTag() = getTag(SevenTagRoster.EVENT.tagName)
    fun getSiteTag() = getTag(SevenTagRoster.SITE.tagName)
    fun getDateTag() = getTag(SevenTagRoster.DATE.tagName)
    fun getRoundTag() = getTag(SevenTagRoster.ROUND.tagName)
    fun getWhiteTag() = getTag(SevenTagRoster.WHITE.tagName)
    fun getBlackTag() = getTag(SevenTagRoster.BLACK.tagName)
    fun getResultTag() = getTag(SevenTagRoster.RESULT.tagName)

    fun getResult() = result

    fun toBoard(): Board {
        val board = if (hasTag(TAG_NAME_SETUP) && getTag(TAG_NAME_SETUP).value.toInt() != 1) Board()
        else Board(getTag(TAG_NAME_FEN).value)
        moves.forEach {
            try {
                val parsedMove = parseMove(board, it.move)
                board.move(
                    parsedMove.origin.position,
                    parsedMove.target.position,
                    parsedMove.target.piece.type
                )
            } catch(e: Exception) {
                moves.apply { println(this) }
                getTag(TAG_NAME_SITE).value.apply { println(this) }
                println(board)
                throw e
            }
        }
        return board
    }

    private fun parseMove(board: Board, san: String): ParsedMove {
        var isCheck = false
        var isCheckmate = false
        var isPromotion = false
        var isCapture = false
        var isCastling = false
        var isEnPassant = false
        var originPieceType = PieceType.PAWN
        var targetPieceType = PieceType.PAWN
        var originPosition: Position? = null
        var targetPosition: Position? = null
        var isGoodMove = false
        var isPoorMove = false
        var isVeryGoodMove = false
        var isVeryPoorMove = false
        var isSpeculativeMove = false
        var isQuestionableMove = false

        var originRow: Int? = null
        var originColumn: Int? = null

        var index = san.length - 1

        if (san[index] == '!') {
            index--
            when (san[index]) {
                '!' -> {
                    isVeryGoodMove = true
                    index--
                }
                '?' -> {
                    isSpeculativeMove = true
                    index--
                }
                else -> isGoodMove = true
            }
        }

        if (san[index] == '?') {
            index--
            when (san[index]) {
                '?' -> {
                    isVeryPoorMove = true
                    index--
                }
                '!' -> {
                    isQuestionableMove = true
                    index--
                }
                else -> isPoorMove = true
            }
        }

        if (san[index] == '#') {
            isCheckmate = true
            index--
        } else if (san[index] == '+') {
            isCheck = true
            index--
        }

        if (san.startsWith("O-O-O")) {
            originPosition = board.getBoardState().getKingPosition(board.getSideToMove())
            targetPosition = board.getBoardState().getLeftRookPosition(board.getSideToMove())
            isCastling = true
            originPieceType = PieceType.KING
            targetPieceType = PieceType.KING
        } else if (san.startsWith("O-O")) {
            originPosition = board.getBoardState().getKingPosition(board.getSideToMove())
            targetPosition = board.getBoardState().getRightRookPosition(board.getSideToMove())
            isCastling = true
            originPieceType = PieceType.KING
            targetPieceType = PieceType.KING
        } else {
            if (PieceType.isPieceTypeLetter(san[index])) {
                targetPieceType = PieceType.from(san[index])
                isPromotion = true
                index -= 2
            }

            targetPosition = Position.from("${san[index - 1]}${san[index]}")
            index -= 2

            if (index >= 0 && san[index] == 'x') {
                index--
                isCapture = true
            }

            if (index >= 0 && san[index] in '1'..'8') {
                originRow = Position.rankToRow(san[index] - '0')
                index--
            }
            if (index >= 0 && san[index] in 'a'..'h') {
                originColumn = Position.fileToColumn(san[index])
                index--
            }

            if (index >= 0) {
                originPieceType = PieceType.from(san[index])
                targetPieceType = originPieceType
            }

            originPosition = board
                .getPiecesCanReach(targetPosition!!, board.getSideToMove())
                .asSequence()
                .filter { originRow == null || it.position.row == originRow }
                .filter { originColumn == null || it.position.column == originColumn }
                .filter { it.piece.type == originPieceType }
                .map { it.position }
                .firstOrNull()

            if (originPosition == null) throw Exception("invalid move: $san")

            isEnPassant = originPieceType.isPawn && isCapture && board.isEmpty(targetPosition)
        }

        return ParsedMove(
            origin = LocalizedPiece(
                piece = Piece.from(originPieceType, board.getSideToMove()),
                position = originPosition!!
            ),
            target = LocalizedPiece(
                piece = Piece.from(targetPieceType, board.getSideToMove()),
                position = targetPosition!!
            ),
            isCheck = isCheck,
            isCastling = isCastling,
            isEnPassant = isEnPassant,
            isCheckmate = isCheckmate,
            isPromotion = isPromotion,
            isCapture = isCapture,
            isGoodMove = isGoodMove,
            isPoorMove = isPoorMove,
            isVeryGoodMove = isVeryGoodMove,
            isVeryPoorMove = isVeryPoorMove,
            isSpeculativeMove = isSpeculativeMove,
            isQuestionableMove = isQuestionableMove
        )
    }

    override fun toString() = buildString {
        if (moves.isNotEmpty()) {
            moves.forEachIndexed { index, pgnMove ->
                if (index > 0) append(' ')
                append(pgnMove)
            }
        }
    }
}

private class PgnGameBuilder {

    private val tags = ArrayList<Tag>()
    private val variants = Stack<ArrayList<Move>>()
    private var beforeComment: ArrayList<String> = ArrayList()
    private var gameResult: GameResult = GameResult.UNKNOWN

    init {
        variants.push(ArrayList())
    }

    fun addMove(
        number: Int,
        sideToMove: Color,
        move: String
    ): PgnGameBuilder {
        val move = Move(
            number = number,
            sideToMove = sideToMove,
            move = move
        )
        if (beforeComment.isNotEmpty()) {
            move.beforeComment.addAll(beforeComment)
            beforeComment.clear()
        }
        variants.peek() += move
        return this
    }

    fun addComment(comment: String) {
        if (variants.peek().isNotEmpty()) {
            variants.peek().last().afterComment.add(comment)
        } else {
            beforeComment.add(comment)
        }
    }

    fun openVariant() {
        val lastMove = variants.last().last()
        val newVariant = ArrayList<Move>()
        lastMove.variants.add(newVariant)
        variants.push(newVariant)
    }

    fun closeVariant() {
        variants.pop()
    }

    fun addTag(tag: Tag): PgnGameBuilder {
        tags += tag
        return this
    }

    fun setGameResult(gameResult: GameResult): PgnGameBuilder {
        this.gameResult = gameResult
        return this
    }

    fun build() = PgnGame(
        tags = tags.asSequence().associateBy({ it.name }, { it }),
        moves = variants.first().map { it.toPgnMove() },
        result = gameResult
    )

    private data class Move(
        var number: Int,
        var sideToMove: Color,
        var move: String,
        var beforeComment: ArrayList<String> = ArrayList(),
        var afterComment: ArrayList<String> = ArrayList(),
        var variants: ArrayList<ArrayList<Move>> = ArrayList()
    )

    private fun convert(movements: ArrayList<ArrayList<Move>>): List<List<PgnMove>> {
        if (movements.isEmpty()) return emptyList()
        return movements.map { movements ->
            movements.map {
                it.toPgnMove()
            }
        }
    }

    private fun Move.toPgnMove() = PgnMove(
        number = this.number,
        sideToMove = this.sideToMove,
        move = this.move,
        beforeComment = this.beforeComment,
        afterComment = this.afterComment,
        variants = convert(this.variants)
    )
}

private enum class ParserState {
    START {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            tokenizer.skipWhitespaces()
            return GAME
        }
    },
    GAME {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            context.resetGameBuilder()
            return TAGS
        }
    },
    TAGS {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val token = tokenizer.nextToken(TokenType.TAG_NAME, TokenType.NEW_LINE)
            if (token.type == TokenType.NEW_LINE) {
                tokenizer.skipWhitespaces()
                return MOVEMENTS
            }
            val tagName = token.value
            tokenizer.nextToken(TokenType.WHITE_SPACES)
            val tagValue = tokenizer.nextToken(TokenType.TAG_VALUE).value
            tokenizer.nextToken(TokenType.NEW_LINE)
            context.getGameBuilder().addTag(Tag(tagName, tagValue))
            return TAGS
        }
    },
    MOVEMENTS {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val token = tokenizer.nextToken(
                TokenType.MOVE_NUMBER,
                TokenType.GAME_RESULT,
                TokenType.MOVE,
                TokenType.NAG,
                TokenType.OPEN_PARENTHESIS,
                TokenType.CLOSE_PARENTHESIS,
                TokenType.COMMENT
            )
            tokenizer.back()
            return when (token.type) {
                TokenType.MOVE_NUMBER -> MOVE_NUMBER
                TokenType.GAME_RESULT -> GAME_RESULT
                TokenType.MOVE -> MOVE
                TokenType.NAG -> NAG
                TokenType.OPEN_PARENTHESIS -> OPEN_VARIANT
                TokenType.CLOSE_PARENTHESIS -> CLOSE_VARIANT
                TokenType.COMMENT -> COMMENT
                else -> throw PgnException("Unexpected token ${token.type} in $MOVEMENTS state")
            }
        }
    },
    MOVE_NUMBER {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val token = tokenizer.nextToken(TokenType.MOVE_NUMBER)
            context.setCurrentMoveNumber(token.value.toInt())
            val dotsToken = tokenizer.nextToken(TokenType.SINGLE_DOT, TokenType.TRIPLE_DOT)
            context.setCurrentSideToMove(
                when (dotsToken.type) {
                    TokenType.SINGLE_DOT -> Color.WHITE
                    TokenType.TRIPLE_DOT -> Color.BLACK
                    else -> throw PgnException("unexpected error")
                }
            )
            tokenizer.skipWhitespaces()
            return MOVEMENTS
        }
    },
    MOVE {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val moveToken = tokenizer.nextToken(TokenType.MOVE)
            context.getGameBuilder().addMove(
                number = context.getCurrentMoveNumber(),
                sideToMove = context.getCurrentSideToMove(),
                move = moveToken.value
            )
            context.setCurrentSideToMove(context.getCurrentSideToMove().opposite)
            tokenizer.skipWhitespaces()
            return MOVEMENTS
        }
    },
    COMMENT {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val commentToken = tokenizer.nextToken(TokenType.COMMENT)
            context.getGameBuilder().addComment(commentToken.value)
            tokenizer.skipWhitespaces()
            return MOVEMENTS
        }
    },
    OPEN_VARIANT {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            tokenizer.nextToken(TokenType.OPEN_PARENTHESIS)
            context.getGameBuilder().openVariant()
            tokenizer.skipWhitespaces()
            return MOVEMENTS
        }
    },
    NAG {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            TODO("Not yet implemented")
        }
    },
    CLOSE_VARIANT {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            tokenizer.nextToken(TokenType.CLOSE_PARENTHESIS)
            context.getGameBuilder().closeVariant()
            tokenizer.skipWhitespaces()
            return MOVEMENTS
        }
    },
    GAME_RESULT {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val resultToken = tokenizer.nextToken(TokenType.GAME_RESULT)
            context.getGameBuilder().setGameResult(GameResult.from(resultToken.value))
            val game = context.getGameBuilder().build()
            context.setCurrentParsedGame(game)
            tokenizer.skipWhitespaces()
            return GAME_END
        }
    },
    GAME_END {
        override fun process(tokenizer: Tokenizer, context: Context): ParserState {
            val token = tokenizer.nextToken(TokenType.TAG_NAME, TokenType.EOF)
            if (token.type == TokenType.TAG_NAME) {
                tokenizer.back()
                return GAME
            }
            return END
        }
    },
    END {
        override fun process(
            tokenizer: Tokenizer,
            context: Context
        ): ParserState {
            throw UnsupportedOperationException("parsing end")
        }
    };

    abstract fun process(
        tokenizer: Tokenizer,
        context: Context
    ): ParserState
}

private class PgnParser(charSource: BufferedReader) : Iterator<PgnGame> {

    private val tokenizer = Tokenizer(charSource)
    private var state = ParserState.START
    private var nextGame: PgnGame? = null
    private val context = Context()

    override fun hasNext(): Boolean {
        if (nextGame != null) return true

        while (state != ParserState.END) {
            state = state.process(tokenizer, context)
            if (state == ParserState.GAME_END) {
                nextGame = context.getCurrentParsedGame()
                break
            }
        }

        return nextGame != null
    }

    override fun next(): PgnGame {
        if (!hasNext()) throw NoSuchElementException("no more games")

        val next = nextGame!!
        nextGame = null
        return next
    }
}

class PgnReader(private val charSource: BufferedReader) : AutoCloseable, Iterator<PgnGame> {

    constructor(pgn: String) : this(BufferedReader(StringReader(pgn)))

    constructor(path: Path) : this(BufferedReader(FileReader(path.toFile())))

    constructor(
        inputStream: InputStream,
        charset: Charset = StandardCharsets.US_ASCII
    ) : this(
        BufferedReader(
            InputStreamReader(
                inputStream,
                charset
            )
        )
    )

    private val pgnParser = PgnParser(charSource)

    override fun hasNext() = pgnParser.hasNext()

    override fun next() = pgnParser.next()

    override fun close() {
        charSource.close()
    }
}

fun main() {
    var v = 1
    while(v-- > 0) println("funfou")
    val input = BZip2CompressorInputStream(FileInputStream("d:/lichess_db_chess960_rated_2020-03.pgn.bz2"), true)
    val reader = PgnReader(
        """
            
            [Event "Rated Chess960 tournament https://lichess.org/tournament/GawNofZ6"]
            [Site "https://lichess.org/B7t01Oa5"]
            [Date "2020.03.01"]
            [Round "-"]
            [White "kouyache20"]
            [Black "plich"]
            [Result "1-0"]
            [UTCDate "2020.03.01"]
            [UTCTime "00:12:52"]
            [WhiteElo "1792"]
            [BlackElo "1710"]
            [WhiteRatingDiff "+68"]
            [BlackRatingDiff "-4"]
            [TimeControl "300+0"]
            [Termination "Normal"]
            [FEN "bbqrknrn/pppppppp/8/8/8/8/PPPPPPPP/BBQRKNRN w KQkq - 0 1"]
            [SetUp "1"]
            [Variant "Chess960"]

            1. e4 { [%eval -0.16] [%clk 0:05:00] } 1... Nhg6 { [%eval 0.06] [%clk 0:05:00] } 2. d4 { [%eval 0.18] [%clk 0:04:58] } 2... b6 { [%eval 0.4] [%clk 0:04:57] } 3. f3?! { [%eval -0.46] [%clk 0:04:54] } 3... c5 { [%eval -0.11] [%clk 0:04:54] } 4. d5 { [%eval -0.1] [%clk 0:04:52] } 4... Bf4 { [%eval -0.37] [%clk 0:04:46] } 5. Nd2? { [%eval -2.09] [%clk 0:04:45] } 5... Be3? { [%eval 0.02] [%clk 0:04:34] } 6. Rf1? { [%eval -2.42] [%clk 0:04:43] } 6... Qc7? { [%eval -0.91] [%clk 0:04:30] } 7. Ke2 { [%eval -0.93] [%clk 0:04:41] } 7... Bxd2 { [%eval -0.84] [%clk 0:04:26] } 8. Qxd2 { [%eval -0.72] [%clk 0:04:39] } 8... Qxh2 { [%eval -0.87] [%clk 0:04:24] } 9. Rf2? { [%eval -3.12] [%clk 0:04:36] } 9... Nf4+ { [%eval -3.11] [%clk 0:03:52] } 10. Ke3 { [%eval -2.64] [%clk 0:04:34] } 10... N4g6? { [%eval -0.76] [%clk 0:03:43] } 11. g3 { [%eval -0.49] [%clk 0:04:32] } 11... Qh6+ { [%eval -0.72] [%clk 0:03:40] } 12. Ke2 { [%eval -0.63] [%clk 0:04:30] } 12... e6? { [%eval 1.73] [%clk 0:03:34] } 13. Qxh6 { [%eval 1.69] [%clk 0:04:28] } 13... gxh6 { [%eval 1.62] [%clk 0:03:32] } 14. c4?! { [%eval 1.06] [%clk 0:04:27] } 14... exd5? { [%eval 2.46] [%clk 0:03:31] } 15. cxd5?! { [%eval 1.51] [%clk 0:04:24] } 15... d6 { [%eval 1.92] [%clk 0:03:27] } 16. b3 { [%eval 1.5] [%clk 0:04:20] } 16... O-O-O? { [%eval 2.54] [%clk 0:03:23] } 17. a4?! { [%eval 1.73] [%clk 0:04:17] } 17... Bb7?! { [%eval 2.35] [%clk 0:03:21] } 18. Bf6? { [%eval 0.76] [%clk 0:04:14] } 18... Re8 { [%eval 1.0] [%clk 0:03:17] } 19. Kd2? { [%eval -1.0] [%clk 0:04:10] } 19... Nd7 { [%eval -0.94] [%clk 0:03:12] } 20. Bc3 { [%eval -0.93] [%clk 0:04:04] } 20... a5?! { [%eval 0.01] [%clk 0:03:09] } 21. Bd3? { [%eval -1.07] [%clk 0:04:02] } 21... Nde5? { [%eval 0.85] [%clk 0:03:02] } 22. Be2 { [%eval 0.76] [%clk 0:03:55] } 22... h5 { [%eval 0.92] [%clk 0:02:56] } 23. Rh2 { [%eval 0.75] [%clk 0:03:48] } 23... h4 { [%eval 1.0] [%clk 0:02:45] } 24. gxh4? { [%eval -0.91] [%clk 0:03:45] } 24... Nf4 { [%eval -0.87] [%clk 0:02:38] } 25. Ke3?! { [%eval -1.67] [%clk 0:03:38] } 25... Nxe2 { [%eval -1.34] [%clk 0:02:15] } 26. Kxe2 { [%eval -1.38] [%clk 0:03:36] } 26... Ba6+?! { [%eval -0.59] [%clk 0:02:14] } 27. Ke3 { [%eval -1.01] [%clk 0:03:33] } 27... Rg6? { [%eval 0.0] [%clk 0:02:06] } 28. Bxe5 { [%eval 0.0] [%clk 0:03:27] } 28... dxe5 { [%eval -0.19] [%clk 0:02:05] } 29. Nf2 { [%eval -0.04] [%clk 0:03:26] } 29... Reg8? { [%eval 2.69] [%clk 0:02:02] } 30. Ng4 { [%eval 2.77] [%clk 0:03:24] } 30... f6? { [%eval 3.9] [%clk 0:01:57] } 31. d6?? { [%eval -0.93] [%clk 0:03:21] } 31... Kd7?? { [%eval 3.05] [%clk 0:01:55] } 32. h5 { [%eval 3.25] [%clk 0:03:16] } 32... Rxg4 { [%eval 3.26] [%clk 0:01:46] } 33. fxg4 { [%eval 2.92] [%clk 0:03:14] } 33... Rxg4 { [%eval 3.09] [%clk 0:01:45] } 34. Rf2 { [%eval 3.1] [%clk 0:03:09] } 34... Bb7 { [%eval 2.86] [%clk 0:01:32] } 35. Rxf6 { [%eval 2.92] [%clk 0:03:05] } 35... Rxe4+ { [%eval 2.2] [%clk 0:01:31] } 36. Kf2 { [%eval 2.07] [%clk 0:03:02] } 36... Rh4?? { [%eval 13.88] [%clk 0:01:18] } 37. Rf7+ { [%eval 13.17] [%clk 0:03:00] } 37... Kc6?! { [%eval #1] [%clk 0:01:16] } 38. Rc7# { [%clk 0:02:56] } 1-0

        """.trimIndent()
    )
    while (reader.hasNext()) {
        println(reader.next().toBoard())
    }
}

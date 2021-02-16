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

import com.welyab.ankobachen.GameReaderAction.END
import com.welyab.ankobachen.GameReaderAction.PREPARING_READING
import com.welyab.ankobachen.GameReaderAction.READING_NAG
import com.welyab.ankobachen.TokenType.BLACK_DOTS
import com.welyab.ankobachen.TokenType.CLOSE_VARIANT
import com.welyab.ankobachen.TokenType.COMMENT
import com.welyab.ankobachen.TokenType.EOF
import com.welyab.ankobachen.TokenType.GAME_RESULT
import com.welyab.ankobachen.TokenType.LINE_COMMENT
import com.welyab.ankobachen.TokenType.MOVE
import com.welyab.ankobachen.TokenType.MOVEMENT_NUMBER
import com.welyab.ankobachen.TokenType.NAG
import com.welyab.ankobachen.TokenType.NEW_LINE
import com.welyab.ankobachen.TokenType.OPEN_VARIANT
import com.welyab.ankobachen.TokenType.TAG_NAME
import com.welyab.ankobachen.TokenType.TAG_VALUE
import com.welyab.ankobachen.TokenType.WHITE_DOT
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.EnumMap
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

private const val OPEN_BRACKET_CHAR = '['
private const val OPEN_BRACE_CHAR = '{'
private const val CLOSE_BRACE_CHAR = '}'
private const val OPEN_PARENTHESIS_CHAR = '('
private const val CLOSE_PARENTHESIS_CHAR = ')'
private const val QUOTATION_CHAR = '"'
private const val WHITESPACE_CHAR = ' '
private const val SEMICOLON_CHAR = ';'
private const val ASTERISK_CHAR = '*'
private const val DOLLAR_SIGN_CHAR = '$'
private const val DOT_CHAR = '.'
private const val CR_CHAR = '\r'
private const val LF_CHAR = '\n'

private const val GAME_TERMINAL_WHITE_WIN_VALUE = "1-0"
private const val GAME_TERMINAL_BLACK_WIN_VALUE = "0-1"
private const val GAME_TERMINAL_DRAW_VALUE = "1/2-1/2"
private const val GAME_TERMINAL_UNKNOWN_VALUE = "*"

private const val WHITE_DOT_VALUE = "."
private const val BLACK_DOTS_VALUE = "..."

private const val SAN_SUFFIX_GOOD_MOVE = "!"
private const val SAN_SUFFIX_POOR_MOVE = "?"
private const val SAN_SUFFIX_VERY_GOOD_MOVE = "!!"
private const val SAN_SUFFIX_VERY_POOR_MOVE = "??"
private const val SAN_SUFFIX_SPECULATIVE_MOVE = "!?"
private const val SAN_SUFFIX_QUESTIONABLE_MOVE = "?!"

private fun Char.isNewLine() = this == CR_CHAR || this == LF_CHAR

private val MOVE_CHAR_FILTER: (c: Char) -> Boolean = {
    it in 'a'..'h'
            || it in '0'..'9'
            || it == 'K' || it == 'Q' || it == 'R' || it == 'B' || it == 'N'
            || it == 'x'
            || it == '='
            || it == '+'
            || it == '-'
            || it == '?'
            || it == '!'
}

private enum class TokenType {
    TAG_NAME,
    TAG_VALUE,
    MOVEMENT_NUMBER,
    MOVE,
    COMMENT,
    LINE_COMMENT,
    NEW_LINE,
    WHITE_DOT,
    BLACK_DOTS,
    OPEN_VARIANT,
    CLOSE_VARIANT,
    GAME_RESULT,
    NAG,
    EOF
}

private data class Token(val value: String, val type: TokenType) {
    override fun toString(): String =
        "Token[value=$value, type=$type]"
            .replace("\r", "\\r")
            .replace("\n", "\\n")
}

private val EOF_TOKEN = Token("", EOF)
private val NEW_LINE_TOKEN = Token("$CR_CHAR$LF_CHAR", NEW_LINE)

data class PgnTag(val name: String, val value: String)

class PgnGame

interface PgnReaderListener {

    fun onGame(game: PgnGame)
}

enum class GameResult(val value: String) {
    WHITE_WIN(GAME_TERMINAL_WHITE_WIN_VALUE),
    BLACK_WIN(GAME_TERMINAL_BLACK_WIN_VALUE),
    DRAW(GAME_TERMINAL_DRAW_VALUE),
    UNKNOWN(GAME_TERMINAL_UNKNOWN_VALUE);

    companion object {
        fun fromValue(value: String): GameResult {
            return when (value) {
                GAME_TERMINAL_WHITE_WIN_VALUE -> WHITE_WIN
                GAME_TERMINAL_BLACK_WIN_VALUE -> BLACK_WIN
                GAME_TERMINAL_DRAW_VALUE -> DRAW
                GAME_TERMINAL_UNKNOWN_VALUE -> UNKNOWN
                else -> throw IllegalArgumentException(value)
            }
        }
    }
}

private class CharStream(private val input: BufferedReader) : Iterator<Char>, AutoCloseable {

    private var index: Int = 0
    private var previousBufferSize = 0
    private var previousBuffer = CharArray(DEFAULT_BUFFER_SIZE)
    private var bufferSize = 0
    private var buffer = CharArray(DEFAULT_BUFFER_SIZE)

    fun next(breaker: (Char) -> Boolean): String = buildString {
        while (hasNext()) {
            val c = next()
            if (!breaker.invoke(c)) {
                if (hasNext()) back()
                break
            }
            append(c)
        }
    }

    private fun noMoreCharsToRewind(): Nothing = throw IllegalStateException("no more chars to rewind")

    fun back() {
        val prevIndex = index - 1
        if (prevIndex < 0 && previousBufferSize == 0) noMoreCharsToRewind()
        val normalizedIndex = normalizeIndex(prevIndex)
        if (normalizedIndex < 0) noMoreCharsToRewind()
        index = prevIndex
    }

    override fun hasNext(): Boolean {
        if (index < bufferSize) return true
        previousBufferSize = bufferSize
        buffer.copyInto(previousBuffer, 0, 0, bufferSize)
        bufferSize = input.read(buffer)
        index = 0
        return index < bufferSize
    }

    override fun next(): Char {
        if (!hasNext()) throw NoSuchElementException("not more chars to read")
        val idx = index
        index++
        return if (idx < 0) previousBuffer[normalizeIndex(idx)]
        else buffer[idx]
    }

    private fun normalizeIndex(index: Int): Int {
        return if (index >= 0) index
        else previousBufferSize + index
    }

    override fun close() {
        input.close()
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}

private class TokenReader(private val charStream: CharStream) : Iterator<Token>, AutoCloseable {

    private var backup: Token? = null
    private var previousToken: Token? = null
    private var token: Token? = null

    fun skip(breaker: (Token) -> Boolean) {
        while (hasNext()) {
            if (!breaker.invoke(next())) {
                if (hasNext()) back()
                break
            }
        }
    }

    fun skipNewLines() {
        skip { it.type == NEW_LINE }
    }

    override fun hasNext(): Boolean {
        if (token != null || backup != null) return true
        if (!charStream.hasNext()) return false
        token = next1()
        return true
    }

    fun back() {
        if (previousToken == null) throw IllegalStateException("no previous token to rewind")
        backup = previousToken
        previousToken = null
    }

    fun next(vararg expectedTypes: TokenType): Token {
        if (!hasNext()) expectedButNoMoreTokens()
        val token = next()
        checkExpected(token, *expectedTypes)
        return token
    }

    private fun checkExpected(token: Token, vararg expectedTypes: TokenType) {
        for (expected in expectedTypes) {
            if (token.type == expected) {
                return
            }
        }
        throw IllegalStateException(
            "expecting one of ${expectedTypes.contentDeepToString()} but found $token"
        )
    }

    private fun expectedButNoMoreTokens(): Nothing = TODO()

    override fun next(): Token {
        if (!hasNext()) throw NoSuchElementException("no more tokens to read")
        return if (backup != null) {
            previousToken = backup
            backup = null
            previousToken!!
        } else {
            previousToken = token
            token = null
            previousToken!!
        }
    }

    private fun next1(): Token {
        charStream.next { it == WHITESPACE_CHAR }
        val c = charStream.next()
        charStream.back()
        if (c == OPEN_BRACKET_CHAR) {
            charStream.next()
            val tagName = charStream.next { it != WHITESPACE_CHAR }
            return Token(tagName, TAG_NAME)
        }
        if (c == QUOTATION_CHAR) {
            charStream.next()
            val tagValue = charStream.next { it != QUOTATION_CHAR }
            charStream.next()
            charStream.next()
            return Token(tagValue, TAG_VALUE)
        }
        if (c == DOT_CHAR) {
            return when (val dots = charStream.next { it == DOT_CHAR }) {
                WHITE_DOT_VALUE -> Token(dots, WHITE_DOT)
                BLACK_DOTS_VALUE -> Token(dots, WHITE_DOT)
                else -> TODO()
            }
        }
        if (c == CR_CHAR || c == LF_CHAR) {
            charStream.next()
            if (c == CR_CHAR && charStream.hasNext() && charStream.next() != LF_CHAR) {
                charStream.back()
            }
            return NEW_LINE_TOKEN
        }
        if (c.isDigit() || c == ASTERISK_CHAR) {
            val tagValue = charStream.next {
                it.isDigit() || it == '-' || it == '/' || it == ASTERISK_CHAR
            }
            return when (tagValue) {
                GAME_TERMINAL_WHITE_WIN_VALUE,
                GAME_TERMINAL_BLACK_WIN_VALUE,
                GAME_TERMINAL_DRAW_VALUE,
                GAME_TERMINAL_UNKNOWN_VALUE -> Token(tagValue, GAME_RESULT)
                else -> Token(tagValue, MOVEMENT_NUMBER)
            }
        }
        if (c == OPEN_BRACE_CHAR) {
            charStream.next()
            val comment = charStream.next { it != CLOSE_BRACE_CHAR }
            charStream.next()
            return Token(comment.trim(), COMMENT)
        }
        if (c == SEMICOLON_CHAR) {
            charStream.next()
            val comment = charStream.next { !it.isNewLine() }
            return Token(comment.trim(), COMMENT)
        }
        if (c == OPEN_PARENTHESIS_CHAR) {
            charStream.next()
            return Token(OPEN_PARENTHESIS_CHAR.toString(), OPEN_VARIANT)
        }
        if (c == CLOSE_PARENTHESIS_CHAR) {
            charStream.next()
            return Token(CLOSE_PARENTHESIS_CHAR.toString(), CLOSE_VARIANT)
        }
        if (c == DOLLAR_SIGN_CHAR) {
            charStream.next()
            val number = charStream.next { it.isDigit() }
            return Token(number, NAG)
        }
        val move = charStream.next(MOVE_CHAR_FILTER)
        return Token(move, MOVE)
    }

    override fun close() {
        charStream.close()
    }
}

private enum class GameReaderAction {

    PREPARING_READING {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            tokenReader.skipNewLines()
            return READING_TAGS
        }
    },

    READING_TAGS {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val tagName = tokenReader.next(TAG_NAME).value
            val tagValue = tokenReader.next(TAG_VALUE).value
            ctx.addTag(tagName, tagValue)
            tokenReader.skipNewLines()
            return next(
                tokenReader,
                TAG_NAME,
                MOVEMENT_NUMBER,
                GAME_RESULT,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    READING_COMMENT {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val token = tokenReader.next(COMMENT, LINE_COMMENT)
            ctx.addComment(
                token.value,
                token.type == LINE_COMMENT
            )
            tokenReader.skipNewLines()
            return next(
                tokenReader,
                MOVEMENT_NUMBER,
                MOVE,
                NAG,
                OPEN_VARIANT,
                GAME_RESULT,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    READING_MOVEMENT_NUMBER {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val movementNumber = tokenReader.next(MOVEMENT_NUMBER).value
            ctx.setCurrentMoveNumber(movementNumber.toInt())
            return READING_MOVEMENT_NUMBER_DOTS
        }
    },

    READING_MOVEMENT_NUMBER_DOTS {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            tokenReader.next(WHITE_DOT, BLACK_DOTS)
            return READING_MOVE
        }
    },

    READING_MOVE {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val token = tokenReader.next(MOVE)
            ctx.addMove(token.value)
            tokenReader.skipNewLines()
            return next(
                tokenReader,
                MOVEMENT_NUMBER,
                MOVE,
                NAG,
                OPEN_VARIANT,
                CLOSE_VARIANT,
                GAME_RESULT,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    READING_OPEN_VARIANT {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            tokenReader.next(OPEN_VARIANT)
            ctx.openVariant()
            tokenReader.skipNewLines()
            return next(
                tokenReader,
                MOVEMENT_NUMBER,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    READING_CLOSE_VARIANT {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            tokenReader.next(CLOSE_VARIANT)
            ctx.closeVariant()
            tokenReader.skipNewLines()
            return next(
                tokenReader,
                MOVEMENT_NUMBER,
                GAME_RESULT,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    READING_GAME_RESULT {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val token = tokenReader.next(GAME_RESULT)
            val gameTermination = GameResult.fromValue(token.value)
            ctx.setGameResult(gameTermination)
            return END
        }
    },

    READING_NAG {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            val token = tokenReader.next(NAG)
            ctx.addNag(token.value.toInt())
            return next(
                tokenReader,
                MOVEMENT_NUMBER,
                MOVE,
                OPEN_VARIANT,
                CLOSE_VARIANT,
                GAME_RESULT,
                COMMENT,
                LINE_COMMENT
            )
        }
    },

    END {
        override fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction {
            throw IllegalStateException("this state is can`t acting")
        }
    };

    abstract fun act(tokenReader: TokenReader, ctx: ReaderContext): GameReaderAction

    companion object {
        private val TOKEN_MAP = EnumMap<TokenType, GameReaderAction>(TokenType::class.java).apply {
            this[TAG_NAME] = READING_TAGS
            this[MOVEMENT_NUMBER] = READING_MOVEMENT_NUMBER
            this[MOVE] = READING_MOVE
            this[NAG] = READING_NAG
            this[COMMENT] = READING_COMMENT
            this[LINE_COMMENT] = READING_COMMENT
            this[WHITE_DOT] = READING_MOVEMENT_NUMBER_DOTS
            this[BLACK_DOTS] = READING_MOVEMENT_NUMBER_DOTS
            this[OPEN_VARIANT] = READING_OPEN_VARIANT
            this[CLOSE_VARIANT] = READING_CLOSE_VARIANT
            this[GAME_RESULT] = READING_GAME_RESULT
        }

        private fun next(tokenReader: TokenReader, vararg expectedTypes: TokenType): GameReaderAction {
            val token = tokenReader.next(*expectedTypes)
            tokenReader.back()
            return TOKEN_MAP[token.type] ?: throw IllegalStateException(
                "The token ${token.type} is not mapped to an action"
            )
        }
    }
}

private typealias MoveList = MutableList<MutableMap<String, Any>>

private class ReaderContext {

    private val tags: MutableList<PgnTag> = ArrayList()
    private val moves: MutableList<MutableList<MutableMap<String, Any>>> = ArrayList(1)
    private var gameResult: GameResult = GameResult.UNKNOWN
    private var commentsBefore = ArrayList<String>()

    init {
        moves.add(ArrayList(30))
    }

    fun addTag(tagName: String, tagValue: String) {
        tags += PgnTag(tagName, tagValue)
    }

    fun setGameResult(gameResult: GameResult) {
        this.gameResult = gameResult
    }

    fun setCurrentMoveNumber(moveNumber: Int) {
        val move = HashMap<String, Any>()
        move[KEY_MOVE_NUMBER] = moveNumber
        val variant = moves.last()
        variant.add(move)

        if (commentsBefore.isNotEmpty()) {
            val comments = ArrayList(commentsBefore)
            move[KEY_COMMENT_BEFORE_LIST] = comments
            commentsBefore.clear()
        }
    }

    fun addComment(comment: String, isLineComment: Boolean) {
        val variant = moves.last()
        if (variant.isEmpty()) {
            commentsBefore.add(comment)
            return
        }
        val move = variant.last() as MutableMap<String, ArrayList<String>>
        val comments = move.getOrPut(KEY_COMMENT_AFTER_LIST, { ArrayList() })
        comments.add(comment)
    }

    fun addMove(move: String) {
        val move = HashMap<String, Any>()
        move[KEY_MOVE] = move
        val variant = moves.last()
        variant.add(move)

        if (commentsBefore.isNotEmpty()) {
            val comments = ArrayList(commentsBefore)
            move[KEY_COMMENT_BEFORE_LIST] = comments
            commentsBefore.clear()
        }
    }

    fun addNag(nag: Int) {
        @Suppress("UNCHECKED_CAST")
        val map = moves.last().last() as MutableMap<String, MutableList<Int>>
        map.getOrPut(KEY_NAG_LIST, { ArrayList() }).add(nag)
    }

    fun getVariantsDepth(): Int {
        return moves.size
    }

    fun openVariant() {
        val newVariant = ArrayList<MutableMap<String, Any>>()
        val variant = moves.last()

        @Suppress("UNCHECKED_CAST")
        val move = variant.last() as MutableMap<String, ArrayList<Any>>
        move.getOrPut(KEY_VARIANT_LIST, { ArrayList<Any>() }).add(newVariant)
        moves.add(newVariant)
    }

    fun closeVariant() {
        moves.removeLast()
    }

    override fun toString(): String = buildString {
        tags.forEach { append("${it.name}: ${it.value}").append("\n") }
        append(moves)
    }

    companion object {
        val KEY_MOVE_NUMBER = "MOVE_NUMBER"
        val KEY_MOVE = "MOVE"
        val KEY_VARIANT_LIST = "VARIANT_LIST"
        val KEY_NAG_LIST = "NAG_LIST"
        val KEY_COMMENT_BEFORE_LIST = "COMMENT_BEFORE"
        val KEY_COMMENT_AFTER_LIST = "COMMENT_AFTER"
    }
}

private class GameReader(private val tokenReader: TokenReader) {

    private var state = PREPARING_READING

    fun read(): PgnGame {
        val ctx = ReaderContext()
        var counter = 0
        do {
            state = state.act(tokenReader, ctx)
            if (counter++ > 10000) break
        } while (state != END)
        println(ctx)
        return createPgnGame(ctx)
    }

    private fun createPgnGame(ctx: ReaderContext): PgnGame {
        return PgnGame()
    }
}

class PgnReader(private val input: InputStream) : AutoCloseable {

    private val listeners: MutableList<PgnReaderListener> = ArrayList()

    constructor(input: InputStream, listener: PgnReaderListener) : this(input) {
        addListener(listener)
    }

    fun start() {
    }

    fun stop() {
    }

    fun nextGame(): PgnGame {
        TODO()
    }

    fun addListener(listener: PgnReaderListener) {
        listeners += listener
    }

    override fun close() {
        input.close()
    }
}

fun main1() {
    Locale.setDefault(Locale.US)
    val path = Paths.get("C:\\Users\\welyab\\sources\\chess\\lichess_db_standard_rated_2017-04.pgn.bz2")
    Files.newInputStream(path).use { zipInput ->
        BZip2CompressorInputStream(zipInput, true).use {
            val charStream = CharStream(BufferedReader(InputStreamReader(it)))
            val tokenReader = TokenReader(charStream)

            var counter = 0
            var counterInterval = 0
            var startTime = System.currentTimeMillis()
            val timer = Timer()
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
                        startTime = System.currentTimeMillis()
                        val speed = counterInterval / elapsed
                        counterInterval = 0
                        println("total games = $counter, ${"%.2f".format(speed)} games/sec")
                    }
                },
                1000L,
                1000L
            )

            val terminations = HashMap<String, Int>()
            while (tokenReader.hasNext()) {
                val token = tokenReader.next()
                if (token.type == GAME_RESULT) {
                    terminations[token.value] = (terminations[token.value] ?: 0) + 1
                    counter++
                    counterInterval++
                }
            }
            timer.cancel()
            println("total games = $counter")
            terminations.forEach { value ->
                println("${value.key} -> ${value.value}")
            }
            println("fim")
        }
    }
}

fun main() {
    val path =
        Paths.get("C:\\Users\\welyab\\sources\\chess\\lichess_study_welyab-vs-anjobachen_by_welyab_2020.10.05.pgn")
    TokenReader(CharStream(Files.newBufferedReader(path))).use { tokenReader ->
        val gameReader = GameReader(tokenReader)
        val pgnGame = gameReader.read()
        println(pgnGame)
    }
}

fun main3() {
    val stream = CharStream(StringReader("welyab").buffered())
    println(stream.next())
    println(stream.next())
    println(stream.next())
    println(stream.next())
    println(stream.next())
    println(stream.next())
    println(stream.next())
}

fun main4() {
    val path = Paths.get("C:\\Users\\welyab\\sources\\chess\\lichess_db_standard_rated_2017-04.pgn")
    TokenReader(CharStream(Files.newBufferedReader(path))).use { reader ->
        println(reader.next())
        println(reader.next())
        reader.back()
        println(reader.next())
        println(reader.next())
    }
}

class Solution {
    fun cost(size: Int, locations: List<Int>) {
    }
}

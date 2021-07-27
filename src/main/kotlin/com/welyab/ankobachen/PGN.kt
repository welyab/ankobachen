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

import com.welyab.ankobachen.ParserState.END
import com.welyab.ankobachen.ParserState.GAME_END
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList

class PgnGame {
}

private enum class TokenType {
    NEW_LINE
}

private data class Token(val value: String, val type: TokenType)

private class CharStream(val reader: Reader) : AutoCloseable {

    override fun close() {
        reader.close()
    }
}

private class PgnTokenizer(reader: Reader) : AutoCloseable {

    private val chars = CharStream(reader)
    private val buffer = LinkedList<Token>()

    private fun readToken(): Token {
        TODO("Not yet implemented")
    }

    fun hasNext(): Boolean {
        TODO("Not yet implemented")
    }

    fun next(): Token {
        TODO("Not yet implemented")
    }

    fun rewind() {
        TODO("Not yet implemented")
    }

    fun skipWhile(test: (token: Token) -> Boolean) {
        while (hasNext()) {
            if (!test.invoke(next())) break
        }
        if (hasNext()) rewind()
    }

    override fun close() {
        chars.close()
    }
}

private class ParserContext {

    fun toGame(): PgnGame {
        TODO()
    }
}

private enum class ParserState {

    START {
        override fun execute(tokenizer: PgnTokenizer, ctx: ParserContext): ParserState {
            tokenizer.skipWhile { it.type == TokenType.NEW_LINE }
            return GAME_START
        }
    },

    GAME_START {
        override fun execute(tokenizer: PgnTokenizer, ctx: ParserContext): ParserState {
            TODO("Not yet implemented")
        }
    },

    GAME_END {
        override fun execute(tokenizer: PgnTokenizer, ctx: ParserContext): ParserState {
            TODO("Not yet implemented")
        }
    },

    END {
        override fun execute(tokenizer: PgnTokenizer, ctx: ParserContext): ParserState {
            TODO("Not yet implemented")
        }
    };

    abstract fun execute(tokenizer: PgnTokenizer, ctx: ParserContext): ParserState
}

private class PgnParser(reader: Reader) : AutoCloseable, Iterator<PgnGame> {

    private var state = ParserState.START
    private val tokenizer = PgnTokenizer(reader)
    private var game: PgnGame? = null


    private fun readGame() {
        if (game != null) return
        if (state == END) return
        val ctx = ParserContext()
        do {
            state = state.execute(tokenizer, ctx)
        } while (state !in FINAL_STATES)
        if (state == END) return
        game = ctx.toGame()
    }

    override fun hasNext(): Boolean {
        if (game != null) return true
        readGame()
        return game != null
    }

    override fun next(): PgnGame {
        if (!hasNext()) NoSuchElementException("no more games")
        val g = game!!
        game = null
        return g
    }

    override fun close() {
        tokenizer.close()
    }

    companion object {
        private val FINAL_STATES = listOf(GAME_END, END)
    }
}

class PgnReader(reader: Reader) : AutoCloseable, Iterator<PgnGame> {

    private val parser = PgnParser(reader)

    override fun hasNext() = parser.hasNext()

    override fun next() = parser.next()

    override fun close() {
        parser.close()
    }
}

fun main() {
    val file = "C:/Users/welyab/sources/chess/lichess_welyab_2021-04-11.pgn"
    PgnReader(Files.newBufferedReader(Paths.get(file))).use { parser ->
        while (parser.hasNext()) {
            val game = parser.next()
            println(game)
        }
    }
}

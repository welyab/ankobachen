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

class ColorException(message: String) : ChessException(message)

enum class Color(val value: Char) {

    WHITE(Color.WHITE_COLOR_LETTER_CHAR) {
        override val opposite: Color
            get() = BLACK

        override val isWhite: Boolean
            get() = true

        override val isBlack: Boolean
            get() = false
    },

    BLACK(Color.BLACK_COLOR_LETTER_CHAR) {
        override val opposite: Color
            get() = WHITE

        override val isWhite: Boolean
            get() = false

        override val isBlack: Boolean
            get() = true
    };

    abstract val opposite: Color

    abstract val isWhite: Boolean
    abstract val isBlack: Boolean

    companion object {

        const val WHITE_COLOR_LETTER_CHAR = 'w'
        const val BLACK_COLOR_LETTER_CHAR = 'b'
        const val WHITE_COLOR_LETTER_STRING = "w"
        const val BLACK_COLOR_LETTER_STRING = "b"

        fun from(color: Char): Color = when (color) {
            WHITE_COLOR_LETTER_CHAR -> WHITE
            BLACK_COLOR_LETTER_CHAR -> BLACK
            else -> throw ColorException(
                "Invalid color. Use '$WHITE_COLOR_LETTER_CHAR' or '$BLACK_COLOR_LETTER_CHAR'"
            )
        }

        fun from(color: String): Color = when (color) {
            WHITE_COLOR_LETTER_STRING -> WHITE
            BLACK_COLOR_LETTER_STRING -> BLACK
            else -> throw ColorException(
                "Invalid color. Use \"$WHITE_COLOR_LETTER_STRING\" or \"$BLACK_COLOR_LETTER_STRING\""
            )
        }
    }
}

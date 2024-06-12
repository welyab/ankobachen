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

import com.welyab.ankobachen.ColorSymbol.BLACK_SYMBOL
import com.welyab.ankobachen.ColorSymbol.WHITE_SYMBOL

object ColorSymbol {
    const val BLACK_SYMBOL = 'b'
    const val WHITE_SYMBOL = 'w'
}

enum class Color(
    val symbol: Char,
) {
    BLACK(
        symbol = BLACK_SYMBOL,
    ),
    WHITE(
        symbol = WHITE_SYMBOL
    );

    fun getIndex(): Int = ordinal

    companion object {
        fun fromSymbol(
            symbol: Char
        ): Color = when (symbol) {
            BLACK.symbol -> BLACK
            WHITE.symbol -> WHITE
            else -> throw IllegalArgumentException("unknown color symbol: $symbol")
        }
    }
}

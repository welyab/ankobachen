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

object MovementFlags {
    //@formatter:off
    const val CAPTURE_FLAG         = 0b100000000
    const val EN_PASSANT_FLAG      = 0b010000000
    const val CASTLING_FLAG        = 0b001000000
    const val PROMOTION_FLAG       = 0b000100000
    const val CHECK_FLAG           = 0b000010000
    const val DISCOVERY_CHECK_FLAG = 0b000001000
    const val DOUBLE_CHECK_FLAG    = 0b000000100
    const val CHECKMATE_FLAG       = 0b000000010
    const val STALEMATE_FLAG       = 0b000000001
    //@formatter:on
}

class MovementBag(
    val movements: List<PieceMovements>
)

class PieceMovements(
    val originSquareIndex: Int,
    val encodedDestinations: ULong,
    val movementsMetadata: List<Int>
) {
}

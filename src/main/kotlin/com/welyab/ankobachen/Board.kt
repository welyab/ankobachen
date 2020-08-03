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
import com.welyab.ankobachen.Piece.BLACK_BISHOP
import com.welyab.ankobachen.Piece.BLACK_KING
import com.welyab.ankobachen.Piece.BLACK_KNIGHT
import com.welyab.ankobachen.Piece.BLACK_PAWN
import com.welyab.ankobachen.Piece.BLACK_QUEEN
import com.welyab.ankobachen.Piece.BLACK_ROOK
import com.welyab.ankobachen.Piece.WHITE_BISHOP
import com.welyab.ankobachen.Piece.WHITE_KING
import com.welyab.ankobachen.Piece.WHITE_KNIGHT
import com.welyab.ankobachen.Piece.WHITE_PAWN
import com.welyab.ankobachen.Piece.WHITE_QUEEN
import com.welyab.ankobachen.Piece.WHITE_ROOK
import com.welyab.ankobachen.PieceType.BISHOP
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK
import com.welyab.ankobachen.extensions.shift
import com.welyab.ankobachen.old.NEWLINE
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
val KING_MOVE_MASK = ulongArrayOf(
    0x40c0000000000000uL, 0xa0e0000000000000uL, 0x5070000000000000uL, 0x2838000000000000uL,
    0x141c000000000000uL, 0x0a0e000000000000uL, 0x0507000000000000uL, 0x0203000000000000uL,
    0xc040c00000000000uL, 0xe0a0e00000000000uL, 0x7050700000000000uL, 0x3828380000000000uL,
    0x1c141c0000000000uL, 0x0e0a0e0000000000uL, 0x0705070000000000uL, 0x0302030000000000uL,
    0x00c040c000000000uL, 0x00e0a0e000000000uL, 0x0070507000000000uL, 0x0038283800000000uL,
    0x001c141c00000000uL, 0x000e0a0e00000000uL, 0x0007050700000000uL, 0x0003020300000000uL,
    0x0000c040c0000000uL, 0x0000e0a0e0000000uL, 0x0000705070000000uL, 0x0000382838000000uL,
    0x00001c141c000000uL, 0x00000e0a0e000000uL, 0x0000070507000000uL, 0x0000030203000000uL,
    0x000000c040c00000uL, 0x000000e0a0e00000uL, 0x0000007050700000uL, 0x0000003828380000uL,
    0x0000001c141c0000uL, 0x0000000e0a0e0000uL, 0x0000000705070000uL, 0x0000000302030000uL,
    0x00000000c040c000uL, 0x00000000e0a0e000uL, 0x0000000070507000uL, 0x0000000038283800uL,
    0x000000001c141c00uL, 0x000000000e0a0e00uL, 0x0000000007050700uL, 0x0000000003020300uL,
    0x0000000000c040c0uL, 0x0000000000e0a0e0uL, 0x0000000000705070uL, 0x0000000000382838uL,
    0x00000000001c141cuL, 0x00000000000e0a0euL, 0x0000000000070507uL, 0x0000000000030203uL,
    0x000000000000c040uL, 0x000000000000e0a0uL, 0x0000000000007050uL, 0x0000000000003828uL,
    0x0000000000001c14uL, 0x0000000000000e0auL, 0x0000000000000705uL, 0x0000000000000302uL
)

@ExperimentalUnsignedTypes
val ROOK_MOVE_MASK = ulongArrayOf(
    0x7e80808080808000uL, 0x3e40404040404000uL, 0x5e20202020202000uL, 0x6e10101010101000uL,
    0x7608080808080800uL, 0x7a04040404040400uL, 0x7c02020202020200uL, 0x7e01010101010100uL,
    0x007e808080808000uL, 0x003e404040404000uL, 0x005e202020202000uL, 0x006e101010101000uL,
    0x0076080808080800uL, 0x007a040404040400uL, 0x007c020202020200uL, 0x007e010101010100uL,
    0x00807e8080808000uL, 0x00403e4040404000uL, 0x00205e2020202000uL, 0x00106e1010101000uL,
    0x0008760808080800uL, 0x00047a0404040400uL, 0x00027c0202020200uL, 0x00017e0101010100uL,
    0x0080807e80808000uL, 0x0040403e40404000uL, 0x0020205e20202000uL, 0x0010106e10101000uL,
    0x0008087608080800uL, 0x0004047a04040400uL, 0x0002027c02020200uL, 0x0001017e01010100uL,
    0x008080807e808000uL, 0x004040403e404000uL, 0x002020205e202000uL, 0x001010106e101000uL,
    0x0008080876080800uL, 0x000404047a040400uL, 0x000202027c020200uL, 0x000101017e010100uL,
    0x00808080807e8000uL, 0x00404040403e4000uL, 0x00202020205e2000uL, 0x00101010106e1000uL,
    0x0008080808760800uL, 0x00040404047a0400uL, 0x00020202027c0200uL, 0x00010101017e0100uL,
    0x0080808080807e00uL, 0x0040404040403e00uL, 0x0020202020205e00uL, 0x0010101010106e00uL,
    0x0008080808087600uL, 0x0004040404047a00uL, 0x0002020202027c00uL, 0x0001010101017e00uL,
    0x008080808080807euL, 0x004040404040403euL, 0x002020202020205euL, 0x001010101010106euL,
    0x0008080808080876uL, 0x000404040404047auL, 0x000202020202027cuL, 0x000101010101017euL
)

@ExperimentalUnsignedTypes
val BISHOP_MOVE_MASK = ulongArrayOf(
    0x0040201008040200uL, 0x0020100804020000uL, 0x0050080402000000uL, 0x0028440200000000uL,
    0x0014224000000000uL, 0x000a102040000000uL, 0x0004081020400000uL, 0x0002040810204000uL,
    0x0000402010080400uL, 0x0000201008040200uL, 0x0000500804020000uL, 0x0000284402000000uL,
    0x0000142240000000uL, 0x00000a1020400000uL, 0x0000040810204000uL, 0x0000020408102000uL,
    0x0040004020100800uL, 0x0020002010080400uL, 0x0050005008040200uL, 0x0028002844020000uL,
    0x0014001422400000uL, 0x000a000a10204000uL, 0x0004000408102000uL, 0x0002000204081000uL,
    0x0020400040201000uL, 0x0010200020100800uL, 0x0008500050080400uL, 0x0044280028440200uL,
    0x0022140014224000uL, 0x00100a000a102000uL, 0x0008040004081000uL, 0x0004020002040800uL,
    0x0010204000402000uL, 0x0008102000201000uL, 0x0004085000500800uL, 0x0002442800284400uL,
    0x0040221400142200uL, 0x0020100a000a1000uL, 0x0010080400040800uL, 0x0008040200020400uL,
    0x0008102040004000uL, 0x0004081020002000uL, 0x0002040850005000uL, 0x0000024428002800uL,
    0x0000402214001400uL, 0x004020100a000a00uL, 0x0020100804000400uL, 0x0010080402000200uL,
    0x0004081020400000uL, 0x0002040810200000uL, 0x0000020408500000uL, 0x0000000244280000uL,
    0x0000004022140000uL, 0x00004020100a0000uL, 0x0040201008040000uL, 0x0020100804020000uL,
    0x0002040810204000uL, 0x0000020408102000uL, 0x0000000204085000uL, 0x0000000002442800uL,
    0x0000000040221400uL, 0x0000004020100a00uL, 0x0000402010080400uL, 0x0040201008040200uL
)

val ROOK_MOVEMENT_DATABASE = BitboardUtil.generateRookMovementDatabase()
val BISHOP_MOVEMENT_DATABASE = BitboardUtil.generateBishopMovementDatabase()

@ExperimentalUnsignedTypes
val KNIGHT_MOVE_MASK = ulongArrayOf(
    0x0020400000000000uL, 0x0010a00000000000uL, 0x0088500000000000uL, 0x0044280000000000uL,
    0x0022140000000000uL, 0x00110a0000000000uL, 0x0008050000000000uL, 0x0004020000000000uL,
    0x2000204000000000uL, 0x100010a000000000uL, 0x8800885000000000uL, 0x4400442800000000uL,
    0x2200221400000000uL, 0x1100110a00000000uL, 0x0800080500000000uL, 0x0400040200000000uL,
    0x4020002040000000uL, 0xa0100010a0000000uL, 0x5088008850000000uL, 0x2844004428000000uL,
    0x1422002214000000uL, 0x0a1100110a000000uL, 0x0508000805000000uL, 0x0204000402000000uL,
    0x0040200020400000uL, 0x00a0100010a00000uL, 0x0050880088500000uL, 0x0028440044280000uL,
    0x0014220022140000uL, 0x000a1100110a0000uL, 0x0005080008050000uL, 0x0002040004020000uL,
    0x0000402000204000uL, 0x0000a0100010a000uL, 0x0000508800885000uL, 0x0000284400442800uL,
    0x0000142200221400uL, 0x00000a1100110a00uL, 0x0000050800080500uL, 0x0000020400040200uL,
    0x0000004020002040uL, 0x000000a0100010a0uL, 0x0000005088008850uL, 0x0000002844004428uL,
    0x0000001422002214uL, 0x0000000a1100110auL, 0x0000000508000805uL, 0x0000000204000402uL,
    0x0000000040200020uL, 0x00000000a0100010uL, 0x0000000050880088uL, 0x0000000028440044uL,
    0x0000000014220022uL, 0x000000000a110011uL, 0x0000000005080008uL, 0x0000000002040004uL,
    0x0000000000402000uL, 0x0000000000a01000uL, 0x0000000000508800uL, 0x0000000000284400uL,
    0x0000000000142200uL, 0x00000000000a1100uL, 0x0000000000050800uL, 0x0000000000020400uL
)

@ExperimentalUnsignedTypes
val WHITE_PAWN_SINGLE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x8000000000000000uL, 0x4000000000000000uL, 0x2000000000000000uL, 0x1000000000000000uL,
    0x0800000000000000uL, 0x0400000000000000uL, 0x0200000000000000uL, 0x0100000000000000uL,
    0x0080000000000000uL, 0x0040000000000000uL, 0x0020000000000000uL, 0x0010000000000000uL,
    0x0008000000000000uL, 0x0004000000000000uL, 0x0002000000000000uL, 0x0001000000000000uL,
    0x0000800000000000uL, 0x0000400000000000uL, 0x0000200000000000uL, 0x0000100000000000uL,
    0x0000080000000000uL, 0x0000040000000000uL, 0x0000020000000000uL, 0x0000010000000000uL,
    0x0000008000000000uL, 0x0000004000000000uL, 0x0000002000000000uL, 0x0000001000000000uL,
    0x0000000800000000uL, 0x0000000400000000uL, 0x0000000200000000uL, 0x0000000100000000uL,
    0x0000000080000000uL, 0x0000000040000000uL, 0x0000000020000000uL, 0x0000000010000000uL,
    0x0000000008000000uL, 0x0000000004000000uL, 0x0000000002000000uL, 0x0000000001000000uL,
    0x0000000000800000uL, 0x0000000000400000uL, 0x0000000000200000uL, 0x0000000000100000uL,
    0x0000000000080000uL, 0x0000000000040000uL, 0x0000000000020000uL, 0x0000000000010000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val BLACK_PAWN_SINGLE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000800000000000uL, 0x0000400000000000uL, 0x0000200000000000uL, 0x0000100000000000uL,
    0x0000080000000000uL, 0x0000040000000000uL, 0x0000020000000000uL, 0x0000010000000000uL,
    0x0000008000000000uL, 0x0000004000000000uL, 0x0000002000000000uL, 0x0000001000000000uL,
    0x0000000800000000uL, 0x0000000400000000uL, 0x0000000200000000uL, 0x0000000100000000uL,
    0x0000000080000000uL, 0x0000000040000000uL, 0x0000000020000000uL, 0x0000000010000000uL,
    0x0000000008000000uL, 0x0000000004000000uL, 0x0000000002000000uL, 0x0000000001000000uL,
    0x0000000000800000uL, 0x0000000000400000uL, 0x0000000000200000uL, 0x0000000000100000uL,
    0x0000000000080000uL, 0x0000000000040000uL, 0x0000000000020000uL, 0x0000000000010000uL,
    0x0000000000008000uL, 0x0000000000004000uL, 0x0000000000002000uL, 0x0000000000001000uL,
    0x0000000000000800uL, 0x0000000000000400uL, 0x0000000000000200uL, 0x0000000000000100uL,
    0x0000000000000080uL, 0x0000000000000040uL, 0x0000000000000020uL, 0x0000000000000010uL,
    0x0000000000000008uL, 0x0000000000000004uL, 0x0000000000000002uL, 0x0000000000000001uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val WHITE_PAWN_CAPTURE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x4000000000000000uL, 0xa000000000000000uL, 0x5000000000000000uL, 0x2800000000000000uL,
    0x1400000000000000uL, 0x0a00000000000000uL, 0x0500000000000000uL, 0x0200000000000000uL,
    0x0040000000000000uL, 0x00a0000000000000uL, 0x0050000000000000uL, 0x0028000000000000uL,
    0x0014000000000000uL, 0x000a000000000000uL, 0x0005000000000000uL, 0x0002000000000000uL,
    0x0000400000000000uL, 0x0000a00000000000uL, 0x0000500000000000uL, 0x0000280000000000uL,
    0x0000140000000000uL, 0x00000a0000000000uL, 0x0000050000000000uL, 0x0000020000000000uL,
    0x0000004000000000uL, 0x000000a000000000uL, 0x0000005000000000uL, 0x0000002800000000uL,
    0x0000001400000000uL, 0x0000000a00000000uL, 0x0000000500000000uL, 0x0000000200000000uL,
    0x0000000040000000uL, 0x00000000a0000000uL, 0x0000000050000000uL, 0x0000000028000000uL,
    0x0000000014000000uL, 0x000000000a000000uL, 0x0000000005000000uL, 0x0000000002000000uL,
    0x0000000000400000uL, 0x0000000000a00000uL, 0x0000000000500000uL, 0x0000000000280000uL,
    0x0000000000140000uL, 0x00000000000a0000uL, 0x0000000000050000uL, 0x0000000000020000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val BLACK_PAWN_CAPTURE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000400000000000uL, 0x0000a00000000000uL, 0x0000500000000000uL, 0x0000280000000000uL,
    0x0000140000000000uL, 0x00000a0000000000uL, 0x0000050000000000uL, 0x0000020000000000uL,
    0x0000004000000000uL, 0x000000a000000000uL, 0x0000005000000000uL, 0x0000002800000000uL,
    0x0000001400000000uL, 0x0000000a00000000uL, 0x0000000500000000uL, 0x0000000200000000uL,
    0x0000000040000000uL, 0x00000000a0000000uL, 0x0000000050000000uL, 0x0000000028000000uL,
    0x0000000014000000uL, 0x000000000a000000uL, 0x0000000005000000uL, 0x0000000002000000uL,
    0x0000000000400000uL, 0x0000000000a00000uL, 0x0000000000500000uL, 0x0000000000280000uL,
    0x0000000000140000uL, 0x00000000000a0000uL, 0x0000000000050000uL, 0x0000000000020000uL,
    0x0000000000004000uL, 0x000000000000a000uL, 0x0000000000005000uL, 0x0000000000002800uL,
    0x0000000000001400uL, 0x0000000000000a00uL, 0x0000000000000500uL, 0x0000000000000200uL,
    0x0000000000000040uL, 0x00000000000000a0uL, 0x0000000000000050uL, 0x0000000000000028uL,
    0x0000000000000014uL, 0x000000000000000auL, 0x0000000000000005uL, 0x0000000000000002uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val WHITE_PAWN_DOUBLE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000080800000uL, 0x0000000040400000uL, 0x0000000020200000uL, 0x0000000010100000uL,
    0x0000000008080000uL, 0x0000000004040000uL, 0x0000000002020000uL, 0x0000000001010000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val BLACK_PAWN_DOUBLE_MOVE_MASK = ulongArrayOf(
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000808000000000uL, 0x0000404000000000uL, 0x0000202000000000uL, 0x0000101000000000uL,
    0x0000080800000000uL, 0x0000040400000000uL, 0x0000020200000000uL, 0x0000010100000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL,
    0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL, 0x0000000000000000uL
)

@ExperimentalUnsignedTypes
val WHITE_EN_PASSANT_TARGET_SQUARE_MASK = listOf(
    0x0000800000000000uL,
    0x0000400000000000uL,
    0x0000200000000000uL,
    0x0000100000000000uL,
    0x0000080000000000uL,
    0x0000040000000000uL,
    0x0000020000000000uL,
    0x0000010000000000uL
)

@ExperimentalUnsignedTypes
val BLACK_EN_PASSANT_TARGET_SQUARE_MASK = listOf(
    0x0000000000800000uL,
    0x0000000000400000uL,
    0x0000000000200000uL,
    0x0000000000100000uL,
    0x0000000000080000uL,
    0x0000000000040000uL,
    0x0000000000020000uL,
    0x0000000000010000uL
)

private val WHITE_PAWN_REPLACEMENTS = listOf(WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT)
private val BLACK_PAWN_REPLACEMENTS = listOf(BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT)

private val ROWS = listOf(
    0xff00000000000000uL,
    0x00ff000000000000uL,
    0x0000ff0000000000uL,
    0x000000ff00000000uL,
    0x00000000ff000000uL,
    0x0000000000ff0000uL,
    0x000000000000ff00uL,
    0x00000000000000ffuL
)

private val RANK_1 = ROWS[7]
private val RANK_8 = ROWS[0]

private val COLUMNS = listOf(
    0x8080808080808080uL,
    0x4040404040404040uL,
    0x2020202020202020uL,
    0x1010101010101010uL,
    0x0808080808080808uL,
    0x0404040404040404uL,
    0x0202020202020202uL,
    0x0101010101010101uL
)

class BoardException(message: String = "", cause: Throwable? = null) : ChessException(message, cause)

private val WHITE_KING_LEFT_CASTLING_FINAL_POSITION: ULong =
    0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100000u
private val WHITE_KING_RIGHT_CASTLING_FINAL_POSITION: ULong =
    0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000010u
private val BLACK_KING_LEFT_CASTLING_FINAL_POSITION: ULong =
    0b00100000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
private val BLACK_KING_RIGHT_CASTLING_FINAL_POSITION: ULong =
    0b00000010_00000000_00000000_00000000_00000000_00000000_00000000_00000000u

private val WHITE_ROOK_LEFT_CASTLING_FINAL_POSITION: ULong =
    0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00010000u
private val WHITE_ROOK_RIGHT_CASTLING_FINAL_POSITION: ULong =
    0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000100u
private val BLACK_ROOK_LEFT_CASTLING_FINAL_POSITION: ULong =
    0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
private val BLACK_ROOK_RIGHT_CASTLING_FINAL_POSITION: ULong =
    0b00000100_00000000_00000000_00000000_00000000_00000000_00000000_00000000u

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Board {

    // @formatter:off
    private var whiteKing: ULong    = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00001000u
    private var whiteQueens: ULong  = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00010000u
    private var whiteRooks: ULong   = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_10000001u
    private var whiteBishops: ULong = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100100u
    private var whiteKnights: ULong = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_01000010u
    private var whitePawns: ULong   = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000u

    private var blackKing: ULong    = 0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    private var blackQueens: ULong  = 0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    private var blackRooks: ULong   = 0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    private var blackBishops: ULong = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    private var blackKnights: ULong = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_00000000u
    private var blackPawns: ULong   = 0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000u
    // @formatter:on

    private var sideToMove: Color = WHITE
    private var plyCounter: Int = 0
    private var halfMoveClock: Int = 0
    private var fullMoveCounter: Int = 0
    private var epTargetSquare: ULong = 0uL
    private var whiteCastlingFlags = 0uL
    private var blackCastlingFlags = 0uL

    fun getActivePieceLocations(): List<PieceLocation> = getPieceLocations2(sideToMove)
    fun getPieceLocations(): List<PieceLocation> = getPieceLocations2(BLACK) + getPieceLocations2(WHITE)
    fun getPieceLocations(color: Color): List<PieceLocation> = getPieceLocations2(color)

    fun getActivePieceMovements(): Movements = getMovements2(sideToMove)
    fun getMovements(position: Position): Movements = getMovements(position.squareIndex)
    fun getMovements(color: Color): Movements = getMovements2(color)
    fun getMovements(): Movements = getMovements2(WHITE) + getMovements2(BLACK)
    fun getMovementRandom(): Movement = getActivePieceMovements().getRandomMovement()

    fun moveRandom(): Unit = move(getMovementRandom())
    fun move(movement: Movement): Unit = move(movement.from, movement.to, movement.toPiece, movement.flags)
    fun move(from: Position, to: Position, toPiece: PieceType = QUEEN) {
        val movement = getMovements(from.squareIndex)
            .asSequenceOfMovements()
            .filter { !it.flags.isPromotion || it.toPiece.type == toPiece }
            .firstOrNull()
            ?: throw BoardException("can't find valid move from $from to $to")
        move(movement)
    }

    fun undo(): Unit = TODO()

    override fun toString(): String {
        val pieceLocations = getPieceLocations()
        return (0..7).asSequence()
            .map { row ->
                val rowPieces = pieceLocations
                    .asSequence()
                    .filter { it.position.row == row }
                    .sortedBy { it.position.column }
                    .toList()
                val rowLetters = (0..7).asSequence()
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
            .reduce { l1, l2 -> "$l1${NEWLINE}├───┼───┼───┼───┼───┼───┼───┼───┤$NEWLINE$l2" }
            .let {
                "┌───┬───┬───┬───┬───┬───┬───┬───┐$NEWLINE$it${NEWLINE}└───┴───┴───┴───┴───┴───┴───┴───┘$NEWLINE"
            }
    }

    private fun getMovements(squareIndex: Int): Movements = getMovements(getPiece(squareIndex), squareIndex)

    private fun getMovements2(color: Color): Movements {
        var movements = Movements(emptyList())
        when (color) {
            WHITE -> {
                movements = movements + getMovements(WHITE_KING, whiteKing)
                movements = movements + getMovements(WHITE_QUEEN, whiteQueens)
                movements = movements + getMovements(WHITE_ROOK, whiteRooks)
                movements = movements + getMovements(WHITE_BISHOP, whiteBishops)
                movements = movements + getMovements(WHITE_KNIGHT, whiteKnights)
                movements = movements + getMovements(WHITE_PAWN, whitePawns)
            }
            BLACK -> {
                movements = movements + getMovements(BLACK_KING, blackKing)
                movements = movements + getMovements(BLACK_QUEEN, blackQueens)
                movements = movements + getMovements(BLACK_ROOK, blackRooks)
                movements = movements + getMovements(BLACK_BISHOP, blackBishops)
                movements = movements + getMovements(BLACK_KNIGHT, blackKnights)
                movements = movements + getMovements(BLACK_PAWN, blackPawns)
            }
        }
        return movements
    }

    private fun getMovements(piece: Piece, pieceBitBoard: ULong): Movements {
        var bb = pieceBitBoard
        var movements = Movements(emptyList())
        while (bb != ZERO) {
            val squareIndex = bb.countLeadingZeroBits()
            movements = movements + getMovements(piece, squareIndex)
            bb = bb and FULL.shift(squareIndex + 1)
        }
        return movements
    }

    private fun getMovements(piece: Piece, squareIndex: Int): Movements {
        val occupied = getOccupiedBitBoard(piece.color)
        return when (piece) {
            WHITE_KING -> getKingMovements(piece, squareIndex, occupied)
            WHITE_QUEEN -> getQueenMovements(piece, squareIndex, occupied)
            WHITE_ROOK -> getRookMovements(piece, squareIndex, occupied)
            WHITE_BISHOP -> getBishopMovements(piece, squareIndex, occupied)
            WHITE_KNIGHT -> getKnightMovements(piece, squareIndex, occupied)
            WHITE_PAWN -> getPawnMovements(piece, squareIndex, occupied)
            BLACK_KING -> getKingMovements(piece, squareIndex, occupied)
            BLACK_QUEEN -> getQueenMovements(piece, squareIndex, occupied)
            BLACK_ROOK -> getRookMovements(piece, squareIndex, occupied)
            BLACK_BISHOP -> getBishopMovements(piece, squareIndex, occupied)
            BLACK_KNIGHT -> getKnightMovements(piece, squareIndex, occupied)
            BLACK_PAWN -> getPawnMovements(piece, squareIndex, occupied)
        }
    }

    private fun getKingMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        val targetSquares = KING_MOVE_MASK[squareIndex] and ownPieces.inv()
        return mountNonPawnMovements(piece, squareIndex, targetSquares)
    }

    private fun getQueenMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        return getSlidingPieceMovements(piece, squareIndex, ownPieces)
    }

    private fun getRookMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        return getSlidingPieceMovements(piece, squareIndex, ownPieces)
    }

    private fun getBishopMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        return getSlidingPieceMovements(piece, squareIndex, ownPieces)
    }

    private fun getSlidingPieceMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        val targetSquares = getSlidingPieceTargetSquares(piece, squareIndex, ownPieces)
        return mountNonPawnMovements(piece, squareIndex, targetSquares)
    }

    private fun getSlidingPieceTargetSquares(piece: Piece, squareIndex: Int, ownPieces: ULong): ULong {
        return when (piece.type) {
            PieceType.ROOK -> getRookTargetSquares(squareIndex, ownPieces)
            PieceType.BISHOP -> getBishopTargetSquares(squareIndex, ownPieces)
            QUEEN -> {
                val rookTargets = getRookTargetSquares(squareIndex, ownPieces)
                val bishopTargets = getBishopTargetSquares(squareIndex, ownPieces)
                rookTargets or bishopTargets
            }
            else -> throw BoardException("no sliding piece: $piece")
        }
    }

    private fun getRookTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = ROOK_MOVE_MASK[squareIndex] and getOccupiedBitBoard()
        return ROOK_MOVEMENT_DATABASE[squareIndex]!![blockers]!! and ownPieces.inv()
    }

    private fun getBishopTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = BISHOP_MOVE_MASK[squareIndex] and getOccupiedBitBoard()
        return BISHOP_MOVEMENT_DATABASE[squareIndex]!![blockers]!! and ownPieces.inv()
    }

    private fun getKnightMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        val targetSquares = KNIGHT_MOVE_MASK[squareIndex] and ownPieces.inv()
        return mountNonPawnMovements(piece, squareIndex, targetSquares)
    }

    private fun getPawnMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        return when (piece.color) {
            WHITE -> getWhitePawnMovements(piece, squareIndex, ownPieces)
            BLACK -> getBlackPawnMovements(piece, squareIndex, ownPieces)
        }
    }

    private fun getWhitePawnMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            WHITE_PAWN_CAPTURE_MOVE_MASK[squareIndex],
            WHITE_PAWN_SINGLE_MOVE_MASK[squareIndex],
            WHITE_PAWN_DOUBLE_MOVE_MASK[squareIndex]
        )
        return mountPawnMovements(piece, squareIndex, targetSquares)
    }

    private fun getBlackPawnMovements(piece: Piece, squareIndex: Int, ownPieces: ULong): Movements {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            BLACK_PAWN_CAPTURE_MOVE_MASK[squareIndex],
            BLACK_PAWN_SINGLE_MOVE_MASK[squareIndex],
            BLACK_PAWN_DOUBLE_MOVE_MASK[squareIndex]
        )
        return mountPawnMovements(piece, squareIndex, targetSquares)
    }

    private fun getPawnTargetSquares(
        ownPieces: ULong,
        allPieces: ULong,
        captureMask: ULong,
        singleSquareMoveMask: ULong,
        doubleSquareMoveMask: ULong
    ): ULong {
        var targetSquares = captureMask and (allPieces or epTargetSquare) and ownPieces.inv()
        targetSquares = targetSquares or singleSquareMoveMask and allPieces.inv()
        if (doubleSquareMoveMask and allPieces == ZERO)
            targetSquares = targetSquares or (singleSquareMoveMask xor doubleSquareMoveMask)
        return targetSquares
    }

    private fun mountPawnMovements(pawn: Piece, fromSquare: Int, targetSquares: ULong): Movements {
        var t = targetSquares
        val movementTargets = ArrayList<MovementTarget>()
        while (t != ZERO) {
            val toSquare = t.countLeadingZeroBits()
            val enPassantCapturedSquare = getEnPassantCapturedSquare(pawn.color, toSquare)
            val isEnpassant = isEmpty(enPassantCapturedSquare)
            val isCapture = isEmpty(toSquare)
            if (
                isMovementValid(
                    fromSquare,
                    toSquare,
                    pawn,
                    isEnpassant,
                    isCapture
                )
            ) {
                val isPromotion = (RANK_1 and RANK_8 and getMaskedSquare(toSquare)) != ZERO
                var flags = 0uL
                if (isEnpassant) flags = flags or MovementFlags.EN_PASSANT_MASK
                if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                for (targetPiece in getPawnTargetPieces(pawn, isPromotion)) {
                    movementTargets += MovementTarget(
                        targetPiece,
                        toSquare,
                        MovementFlags(flags)
                    )
                }
            }
            t = t and FULL.shift(toSquare + 1)
        }
        return Movements(listOf(PieceMovement(fromSquare, movementTargets)))
    }

    private fun getPawnTargetPieces(piece: Piece, isPromotion: Boolean): List<Piece> {
        return if (isPromotion) when (piece.color) {
            WHITE -> WHITE_PAWN_REPLACEMENTS
            BLACK -> BLACK_PAWN_REPLACEMENTS
        }
        else listOf(piece)
    }

    private fun mountNonPawnMovements(piece: Piece, fromIndex: Int, targetSquares: ULong): Movements {
        var s = targetSquares
        val targets = ArrayList<MovementTarget>()
        while (s != EMPTY) {
            val toIndex = s.countLeadingZeroBits()
            var isCapture = isEmpty(toIndex)
            if (
                isMovementValid(
                    fromIndex,
                    toIndex,
                    piece,
                    false,
                    isCapture
                )
            ) {
                var flags = ZERO
                if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                targets += MovementTarget(
                    piece,
                    toIndex,
                    MovementFlags(flags)
                )
            }
            s = s and FULL.shift(toIndex + 1)
        }
        return Movements(
            listOf(
                PieceMovement(
                    fromIndex,
                    targets
                )
            )
        )
    }

    /**
     * A movement is valid if, after it, own king is not in check
     */
    private fun isMovementValid(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isEnPassant: Boolean,
        isCapture: Boolean
    ): Boolean {
        move(
            fromSquare,
            toSquare,
            toPiece,
            isEnPassant,
            isCapture,
            false
        )
        val isValid = when (toPiece.color) {
            WHITE -> isSquareAttacked(whiteKing.countLeadingZeroBits(), BLACK)
            BLACK -> isSquareAttacked(blackKing.countLeadingZeroBits(), WHITE)
        }
        undo()
        return isValid
    }

    private fun isSquareAttacked(squareIndex: Int, color: Color): Boolean {
        val occupied = getOccupiedBitBoard(color.opposite)
        val queenPositions = getPieceBitBoard(QUEEN, color)
        val rookPositions = getPieceBitBoard(ROOK, color)
        val rookRays = getRookTargetSquares(squareIndex, occupied)
        if (rookRays and (rookPositions and queenPositions) != ZERO) return true

        val bishopPositions = getPieceBitBoard(BISHOP, color)
        val bishopRays = getBishopTargetSquares(squareIndex, occupied)
        if (bishopRays and (bishopPositions and queenPositions) != ZERO) return true

        return false
    }

    private fun isEmpty(squareIndex: Int): Boolean = getOccupiedBitBoard() and getSingleMaskBit(squareIndex) != ZERO

    private fun getPiece(squareIndex: Int): Piece {
        val maskedSquare = getMaskedSquare(squareIndex)
        return when {
            whiteKing and maskedSquare != ZERO -> WHITE_KING
            whiteQueens and maskedSquare != ZERO -> WHITE_QUEEN
            whiteRooks and maskedSquare != ZERO -> WHITE_ROOK
            whiteBishops and maskedSquare != ZERO -> WHITE_BISHOP
            whiteKnights and maskedSquare != ZERO -> WHITE_KNIGHT
            whitePawns and maskedSquare != ZERO -> WHITE_PAWN
            blackKing and maskedSquare != ZERO -> BLACK_KING
            blackQueens and maskedSquare != ZERO -> BLACK_QUEEN
            blackRooks and maskedSquare != ZERO -> BLACK_ROOK
            blackBishops and maskedSquare != ZERO -> BLACK_BISHOP
            blackKnights and maskedSquare != ZERO -> BLACK_KNIGHT
            blackPawns and maskedSquare != ZERO -> BLACK_PAWN
            else -> throw BoardException("empty square: ${Position.from(squareIndex)}")
        }
    }

    private fun getPieceBitBoard(pieceType: PieceType, color: Color): ULong {
        return getPieceBitBoard(Piece.from(pieceType, color))
    }

    private fun getPieceBitBoard(piece: Piece): ULong {
        return when (piece) {
            WHITE_KING -> whiteKing
            WHITE_QUEEN -> whiteQueens
            WHITE_ROOK -> whiteRooks
            WHITE_BISHOP -> whiteBishops
            WHITE_KNIGHT -> whiteKnights
            WHITE_PAWN -> whitePawns
            BLACK_KING -> blackKing
            BLACK_QUEEN -> blackQueens
            BLACK_ROOK -> blackRooks
            BLACK_BISHOP -> blackBishops
            BLACK_KNIGHT -> blackKnights
            BLACK_PAWN -> blackPawns
        }
    }

    private fun setBitBoardBit(piece: Piece, bitIndex: Int, value: Boolean) {
        var bitboard = getPieceBitBoard(piece)
        bitboard = if (value) bitboard or getSingleMaskBit(bitIndex)
        else bitboard and getSingleMaskBit(bitIndex).inv()
        setBitBoard(piece, bitboard)
    }

    private fun setBitBoard(piece: Piece, bitboard: ULong) {
        when (piece) {
            WHITE_KING -> whiteKing = bitboard
            WHITE_QUEEN -> whiteQueens = bitboard
            WHITE_ROOK -> whiteRooks = bitboard
            WHITE_BISHOP -> whiteBishops = bitboard
            WHITE_KNIGHT -> whiteKnights = bitboard
            WHITE_PAWN -> whitePawns = bitboard
            BLACK_KING -> blackKing = bitboard
            BLACK_QUEEN -> blackQueens = bitboard
            BLACK_ROOK -> blackRooks = bitboard
            BLACK_BISHOP -> blackBishops = bitboard
            BLACK_KNIGHT -> blackKnights = bitboard
            BLACK_PAWN -> blackPawns = bitboard
        }
    }

    private fun getPieceLocations2(color: Color): List<PieceLocation> {
        val list = ArrayList<PieceLocation>()
        when (color) {
            WHITE -> {
                list += getPieceLocations(WHITE_KING, whiteKing)
                list += getPieceLocations(WHITE_QUEEN, whiteQueens)
                list += getPieceLocations(WHITE_ROOK, whiteRooks)
                list += getPieceLocations(WHITE_BISHOP, whiteBishops)
                list += getPieceLocations(WHITE_KNIGHT, whiteKnights)
                list += getPieceLocations(WHITE_PAWN, whitePawns)
            }
            BLACK -> {
                list += getPieceLocations(BLACK_KING, blackKing)
                list += getPieceLocations(BLACK_QUEEN, blackQueens)
                list += getPieceLocations(BLACK_ROOK, blackRooks)
                list += getPieceLocations(BLACK_BISHOP, blackBishops)
                list += getPieceLocations(BLACK_KNIGHT, blackKnights)
                list += getPieceLocations(BLACK_PAWN, blackPawns)
            }
        }
        return list.sorted()
    }

    private fun getPieceLocations(piece: Piece, pieceBitboard: ULong): List<PieceLocation> {
        var bbt = pieceBitboard
        val positions = ArrayList<PieceLocation>()
        while (bbt != ZERO) {
            val bitPosition = bbt.countLeadingZeroBits()
            positions += PieceLocation(piece, Position.from(bitPosition))
            bbt = bbt and FULL.shift(bitPosition + 1)
        }
        return positions
    }

    private fun getMaskedSquare(squareIndex: Int): ULong = HIGEST_BIT.shift(squareIndex)

    private fun getOccupiedBitBoard(): ULong {
        return getOccupiedBitBoard(WHITE) or getOccupiedBitBoard(BLACK)
    }

    private fun getOccupiedBitBoard(color: Color): ULong {
        return when (color) {
            WHITE -> whiteKing or whiteQueens or whiteRooks or whiteBishops or whiteKnights or whitePawns
            BLACK -> blackKing or blackQueens or blackRooks or blackBishops or blackKnights or blackPawns
        }
    }

    private fun getSingleMaskBit(squareIndex: Int): ULong = HIGEST_BIT.shift(squareIndex)

    private fun move(fromSquare: Int, toSquare: Int, toPiece: Piece, flags: MovementFlags) {
        move(
            fromSquare,
            toSquare,
            toPiece,
            flags.isEnPassant,
            flags.isCapture,
            flags.isPromotion
        )
    }

    private fun move(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isEnPassant: Boolean,
        isCapture: Boolean,
        isPromotion: Boolean
    ) {
        val originPiece = if (isPromotion) toPiece.getPawn() else toPiece
        val capturedPiece = when {
            isEnPassant -> getPiece(getEnPassantCapturedSquare(toPiece.color, toSquare))
            isCapture -> getPiece(toSquare)
            else -> null
        }

        setBitBoardBit(originPiece, fromSquare, false)
        setBitBoardBit(toPiece, toSquare, true)
        if (isEnPassant && capturedPiece != null)
            setBitBoardBit(capturedPiece, getEnPassantCapturedSquare(toPiece.color, toSquare), false)

        sideToMove = sideToMove.opposite
    }

    private fun getEnPassantCapturedSquare(color: Color, toSquare: Int): Int {
        return when (color) {
            WHITE -> toSquare + 8
            BLACK -> toSquare - 8
        }
    }

    companion object {
        private const val ZERO: ULong = 0u
        private const val EMPTY: ULong = ZERO
        private const val FULL: ULong = ULong.MAX_VALUE

        // 0b1000000 ... 0000000uL
        private const val HIGEST_BIT = 0x8000000000000000uL
    }
}

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    val board = Board()
    println("intial board")
    println(board)
    (1..30).forEach {
        val movement = board.getMovementRandom()
        println(movement)
        board.move(movement)
        println(board)
    }
}

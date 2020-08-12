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
import com.welyab.ankobachen.MovementFlags.Companion.LEFT_CASTLING_MASK
import com.welyab.ankobachen.MovementFlags.Companion.RIGHT_CASTLING_MASK
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
import com.welyab.ankobachen.PieceType.KING
import com.welyab.ankobachen.PieceType.KNIGHT
import com.welyab.ankobachen.PieceType.QUEEN
import com.welyab.ankobachen.PieceType.ROOK
import com.welyab.ankobachen.extensions.shift
import com.welyab.ankobachen.old.NEWLINE
import java.util.stream.IntStream
import kotlin.math.absoluteValue

@ExperimentalUnsignedTypes
private val KING_MOVE_MASK = ulongArrayOf(
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
private val ROOK_MOVE_MASK = ulongArrayOf(
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
private val BISHOP_MOVE_MASK = ulongArrayOf(
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

private val ROOK_MOVEMENT_DATABASE = BitboardUtil.generateRookMovementDatabase()
private val BISHOP_MOVEMENT_DATABASE = BitboardUtil.generateBishopMovementDatabase()

@ExperimentalUnsignedTypes
private val KNIGHT_MOVE_MASK = ulongArrayOf(
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
private val WHITE_PAWN_SINGLE_MOVE_MASK = ulongArrayOf(
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
private val BLACK_PAWN_SINGLE_MOVE_MASK = ulongArrayOf(
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
private val WHITE_PAWN_CAPTURE_MOVE_MASK = ulongArrayOf(
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
private val BLACK_PAWN_CAPTURE_MOVE_MASK = ulongArrayOf(
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
private val WHITE_PAWN_DOUBLE_MOVE_MASK = ulongArrayOf(
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
private val BLACK_PAWN_DOUBLE_MOVE_MASK = ulongArrayOf(
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
private val WHITE_EN_PASSANT_TARGET_SQUARE_MASK = listOf(
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
private val BLACK_EN_PASSANT_TARGET_SQUARE_MASK = listOf(
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

@ExperimentalUnsignedTypes
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

@ExperimentalUnsignedTypes
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

private val LEFT_CASTLING_FINAL_POSITIONS = 0b00110000_00000000_00000000_00000000_00000000_00000000_00000000_00110000uL
private val RIGHT_CASTLING_FINAL_POSITIONS = 0b00000110_00000000_00000000_00000000_00000000_00000000_00000000_00000110uL

class BoardException(message: String = "", cause: Throwable? = null) : ChessException(message, cause)

private data class MoveLog(
    val fromPiece: Piece,
    val fromSquare: Int,
    val toPiece: Piece,
    val toSquare: Int,
    val isEnPassant: Boolean,
    val isLeftCastling: Boolean,
    val isRightCastling: Boolean,
    val halfMoveCounter: Int,
    val epTargetSquare: ULong,
    val castlingFlags: ULong,
    val capturedPiece: Piece?
)

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Board : Copyable<Board> {

    private var whiteKing: ULong = 0uL
    private var whiteQueens: ULong = 0uL
    private var whiteRooks: ULong = 0uL
    private var whiteBishops: ULong = 0uL
    private var whiteKnights: ULong = 0uL
    private var whitePawns: ULong = 0uL
    private var blackKing: ULong = 0uL
    private var blackQueens: ULong = 0uL
    private var blackRooks: ULong = 0uL
    private var blackBishops: ULong = 0uL
    private var blackKnights: ULong = 0uL
    private var blackPawns: ULong = 0uL

    private var sideToMove: Color = WHITE
    private var plyCounter: Int = 0
    private var halfMoveClock: Int = 0
    private var fullMoveCounter: Int = 0
    private var epTargetSquare: ULong = 0uL
    private var rookPositions: ULong = 0uL
    private var castlingFlags = 0uL
    private val moveLog = ArrayList<MoveLog>()

    constructor() : this(FEN_INITIAL)

    constructor(fen: String) {
        setFen(FenString(fen))
    }

    constructor(initialize: Boolean) {
        if (initialize) setFen(FEN_INITIAL)
    }

    fun setFen(fen: String): Unit = setFen(FenString(fen))

    fun getActivePieceLocations(): List<PieceLocation> = getPieceLocations2(sideToMove)
    fun getPieceLocations(): List<PieceLocation> = getPieceLocations2(BLACK) + getPieceLocations2(WHITE)
    fun getPieceLocations(color: Color): List<PieceLocation> = getPieceLocations2(color)

    fun getMovements(position: Position): Movements = getMovements(position.squareIndex)
    fun getMovements(color: Color): Movements = getMovements2(color)
    fun getMovements(): Movements = getMovements2(sideToMove)
    fun getMovementRandom(): Movement = getMovements2(sideToMove).getRandomMovement()

    fun moveRandom(): Unit = move(getMovementRandom())
    fun move(movement: Movement) = move(movement.from, movement.to, movement.toPiece, movement.flags)
    fun move(from: Position, to: Position, toPiece: PieceType = QUEEN) {
        val movement = getMovements(from.squareIndex)
            .asSequenceOfMovements()
            .filter { it.to == to.squareIndex }
            .filter { !it.flags.isPromotion || it.toPiece.type == toPiece }
            .firstOrNull()
            ?: throw BoardException("can't find valid move from $from to $to")
        move(movement)
    }

    fun hasPreviousMove(): Boolean = moveLog.isNotEmpty()

    fun undo() {
        val log = moveLog.removeLast()

        if (log.isLeftCastling || log.isRightCastling) {
            val finalPositions = when (log.fromPiece.color) {
                WHITE -> if (log.isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> if (log.isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }

            val kingDestination = if (log.isLeftCastling) finalPositions.countLeadingZeroBits()
            else ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1

            val rookDestination = if (log.isLeftCastling) ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1
            else finalPositions.countLeadingZeroBits()

            setBitBoardBit(log.fromPiece, kingDestination, false)
            setBitBoardBit(log.fromPiece.getRook(), rookDestination, false)
            setBitBoardBit(log.fromPiece, log.fromSquare, true)
            setBitBoardBit(log.fromPiece.getRook(), log.toSquare, true)
        } else {
            setBitBoardBit(log.fromPiece, log.fromSquare, true)
            setBitBoardBit(log.toPiece, log.toSquare, false)
            if (log.isEnPassant) {
                val enPassantCaptured = getEnPassantCapturedSquare(sideToMove.opposite, log.toSquare)
                setBitBoardBit(log.capturedPiece!!, enPassantCaptured, true)
            } else if (log.capturedPiece != null) {
                setBitBoardBit(log.capturedPiece, log.toSquare, true)
            }
        }
        halfMoveClock = log.halfMoveCounter
        if (sideToMove.isWhite) fullMoveCounter--
        plyCounter--
        epTargetSquare = log.epTargetSquare
        castlingFlags = log.castlingFlags
        sideToMove = sideToMove.opposite
    }

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

    private fun setFen(fen: FenString) {
        val fenInfo = fen.getFenInfo()
        for (pieceLocation in fenInfo.piecesDisposition) {
            setBitBoardBit(pieceLocation.piece, pieceLocation.position.squareIndex, true)
        }
        sideToMove = fenInfo.sideToMove
        halfMoveClock = fenInfo.halfMoveClock
        fullMoveCounter = fenInfo.fullMoveCounter
        if (fenInfo.epTarget != null)
            epTargetSquare = getSingleMaskBit(fenInfo.epTarget.squareIndex)

        plyCounter = 0

        castlingFlags = ZERO
        rookPositions = ZERO

        if (fenInfo.castlingFlags.leftWhiteRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.leftWhiteRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.leftWhiteRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.A1.squareIndex)
        }

        if (fenInfo.castlingFlags.rightWhiteRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.rightWhiteRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.rightWhiteRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.H1.squareIndex)
        }

        if (fenInfo.castlingFlags.leftBlackRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.leftBlackRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.leftBlackRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.A8.squareIndex)
        }

        if (fenInfo.castlingFlags.rightBlackRook != null) {
            castlingFlags = castlingFlags or getMaskedSquare(fenInfo.castlingFlags.rightBlackRook.squareIndex)
            rookPositions = rookPositions or getMaskedSquare(fenInfo.castlingFlags.rightBlackRook.squareIndex)
        } else {
            rookPositions = rookPositions or getMaskedSquare(Position.H8.squareIndex)
        }
    }

    private fun getMovements(squareIndex: Int): Movements = Movements(
        listOf(getMovements(getPiece(squareIndex), squareIndex))
    )

    private fun getMovements2(color: Color): Movements {
        val pieceMovements = ArrayList<PieceMovement>(24)
        when (color) {
            WHITE -> {
                getMovements(WHITE_KING, whiteKing, pieceMovements)
                getMovements(WHITE_QUEEN, whiteQueens, pieceMovements)
                getMovements(WHITE_ROOK, whiteRooks, pieceMovements)
                getMovements(WHITE_BISHOP, whiteBishops, pieceMovements)
                getMovements(WHITE_KNIGHT, whiteKnights, pieceMovements)
                getMovements(WHITE_PAWN, whitePawns, pieceMovements)
            }
            BLACK -> {
                getMovements(BLACK_KING, blackKing, pieceMovements)
                getMovements(BLACK_QUEEN, blackQueens, pieceMovements)
                getMovements(BLACK_ROOK, blackRooks, pieceMovements)
                getMovements(BLACK_BISHOP, blackBishops, pieceMovements)
                getMovements(BLACK_KNIGHT, blackKnights, pieceMovements)
                getMovements(BLACK_PAWN, blackPawns, pieceMovements)
            }
        }
        return Movements(pieceMovements)
    }

    private fun getMovements(piece: Piece, pieceBitBoard: ULong, pieceMovements: MutableList<PieceMovement>) {
        var bb = pieceBitBoard
        while (bb != ZERO) {
            val fromSquare = bb.countLeadingZeroBits()
            pieceMovements += getMovements(piece, fromSquare)
            bb = bb and FULL.shift(fromSquare + 1)
        }
    }

    private fun getMovements(piece: Piece, squareIndex: Int): PieceMovement {
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

    private inline fun getKingMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = KING_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
        getCastlingMovements(piece, fromSquare, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private inline fun getCastlingMovements(
        fromPiece: Piece,
        fromSquare: Int,
        targets: MutableList<MovementTarget>
    ) {
        val cFlags = when (fromPiece.color) {
            WHITE -> castlingFlags and RANK_8.inv()
            BLACK -> castlingFlags and RANK_1.inv()
        }
        val rPositions = when (fromPiece.color) {
            WHITE -> rookPositions and RANK_8.inv()
            BLACK -> rookPositions and RANK_1.inv()
        }
        val leftRookStartPosition = rPositions.countLeadingZeroBits()
        if (cFlags and getMaskedSquare(leftRookStartPosition) != ZERO) {
            val kingRookFinalPositions = when (fromPiece.color) {
                WHITE -> LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }
            getCastlingMovements(
                fromPiece,
                fromSquare,
                kingRookFinalPositions.countLeadingZeroBits(),
                leftRookStartPosition,
                ULong.SIZE_BITS - kingRookFinalPositions.countTrailingZeroBits() - 1,
                true,
                false,
                targets
            )
        }
        val rightRookStartPosition = ULong.SIZE_BITS - rPositions.countTrailingZeroBits() - 1
        if (cFlags and getMaskedSquare(rightRookStartPosition) != ZERO) {
            val kingRookFinalPositions = when (fromPiece.color) {
                WHITE -> RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }
            getCastlingMovements(
                fromPiece,
                fromSquare,
                ULong.SIZE_BITS - kingRookFinalPositions.countTrailingZeroBits() - 1,
                rightRookStartPosition,
                kingRookFinalPositions.countLeadingZeroBits(),
                false,
                true,
                targets
            )
        }
    }

    private inline fun getCastlingMovements(
        king: Piece,
        kingFromSquare: Int,
        kingFinalSquare: Int,
        rookFromSquare: Int,
        rookFinalSquare: Int,
        isLeftCastling: Boolean,
        isRightCastling: Boolean,
        targets: MutableList<MovementTarget>
    ) {
        if (isSquareAttacked(kingFromSquare, king.color.opposite)) return
        val kingMoveDirection = if (kingFromSquare < kingFinalSquare) 1 else -1
        var kingPathSquare = kingFromSquare
        val occupied = getOccupiedBitBoard() and (getPieceBitBoard(king.getRook()) or getPieceBitBoard(king)).inv()
        do {
            kingPathSquare += kingMoveDirection
            if (isSquareAttacked(kingPathSquare, king.color.opposite)) return
            if (occupied and getMaskedSquare(kingPathSquare) != ZERO) return
        } while (kingPathSquare != kingFinalSquare)
        val rookMoveDirection = if (rookFromSquare < rookFinalSquare) 1 else -1
        var rookPathSquare = rookFromSquare
        do {
            rookPathSquare += rookMoveDirection
            if (occupied and getSingleMaskBit(rookPathSquare) != ZERO) return
        } while (rookPathSquare != rookFinalSquare)
        var flags = MovementFlags.CASTLING_MASK
        if (isLeftCastling) flags = flags or LEFT_CASTLING_MASK
        if (isRightCastling) flags = flags or RIGHT_CASTLING_MASK
        targets += MovementTarget(
            king,
            rookFromSquare,
            MovementFlags(flags)
        )
    }

    private inline fun getQueenMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private inline fun getRookMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private inline fun getBishopMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val movementTargets = ArrayList<MovementTarget>()
        getSlidingPieceMovements(piece, fromSquare, ownPieces, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getSlidingPieceMovements(
        piece: Piece,
        fromSquare: Int,
        ownPieces: ULong,
        movementTargets: ArrayList<MovementTarget>
    ) {
        val targetSquares = getSlidingPieceTargetSquares(piece, fromSquare, ownPieces)
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
    }

    private fun getSlidingPieceTargetSquares(piece: Piece, fromSquare: Int, ownPieces: ULong): ULong {
        return when (piece.type) {
            ROOK -> getRookTargetSquares(fromSquare, ownPieces)
            BISHOP -> getBishopTargetSquares(fromSquare, ownPieces)
            QUEEN -> {
                val rookTargets = getRookTargetSquares(fromSquare, ownPieces)
                val bishopTargets = getBishopTargetSquares(fromSquare, ownPieces)
                rookTargets or bishopTargets
            }
            else -> throw BoardException("no sliding piece: $piece")
        }
    }

    private fun getRookTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = ROOK_MOVE_MASK[squareIndex] and (getOccupiedBitBoard())
        return ROOK_MOVEMENT_DATABASE[squareIndex]!![blockers]!! and ownPieces.inv()
    }

    private fun getBishopTargetSquares(squareIndex: Int, ownPieces: ULong): ULong {
        val blockers = BISHOP_MOVE_MASK[squareIndex] and (getOccupiedBitBoard())
        return BISHOP_MOVEMENT_DATABASE[squareIndex]!![blockers]!! and ownPieces.inv()
    }

    private inline fun getKnightMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = KNIGHT_MOVE_MASK[fromSquare] and ownPieces.inv()
        val movementTargets = ArrayList<MovementTarget>()
        mountNonPawnMovements(piece, fromSquare, targetSquares, movementTargets)
        return PieceMovement(fromSquare, movementTargets)
    }

    private inline fun getPawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        return when (piece.color) {
            WHITE -> getWhitePawnMovements(piece, fromSquare, ownPieces)
            BLACK -> getBlackPawnMovements(piece, fromSquare, ownPieces)
        }
    }

    private fun getWhitePawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            WHITE_PAWN_SINGLE_MOVE_MASK[fromSquare],
            WHITE_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(piece, fromSquare, targetSquares)
    }

    private fun getBlackPawnMovements(piece: Piece, fromSquare: Int, ownPieces: ULong): PieceMovement {
        val targetSquares = getPawnTargetSquares(
            ownPieces,
            getOccupiedBitBoard(),
            BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare],
            BLACK_PAWN_SINGLE_MOVE_MASK[fromSquare],
            BLACK_PAWN_DOUBLE_MOVE_MASK[fromSquare]
        )
        return mountPawnMovements(piece, fromSquare, targetSquares)
    }

    private fun getPawnTargetSquares(
        ownPieces: ULong,
        allPieces: ULong,
        captureMask: ULong,
        singleSquareMoveMask: ULong,
        doubleSquareMoveMask: ULong
    ): ULong {
        var targetSquares = captureMask and (allPieces or epTargetSquare) and ownPieces.inv()
        targetSquares = targetSquares or (singleSquareMoveMask xor allPieces).and(singleSquareMoveMask)
        if (doubleSquareMoveMask and allPieces == ZERO)
            targetSquares = targetSquares or (doubleSquareMoveMask xor allPieces).and(doubleSquareMoveMask)
        return targetSquares
    }

    private fun mountPawnMovements(pawn: Piece, fromSquare: Int, targetSquares: ULong): PieceMovement {
        var t = targetSquares
        val movementTargets = ArrayList<MovementTarget>()
        while (t != ZERO) {
            val toSquare = t.countLeadingZeroBits()
            val isEnpassant = (
                    (fromSquare - toSquare).absoluteValue == 9
                            || (fromSquare - toSquare).absoluteValue == 7
                    ) && isEmpty(toSquare)
            val isCapture = isEnpassant || isNotEmpty(toSquare)
            if (
                isMovementValid(
                    fromSquare,
                    toSquare,
                    pawn,
                    isEnpassant,
                    isCapture
                )
            ) {
                val isPromotion = ((RANK_1 or RANK_8) and getMaskedSquare(toSquare)) != ZERO
                var flags = 0uL
                if (isEnpassant) flags = flags or MovementFlags.EN_PASSANT_MASK
                if (isCapture) flags = flags or MovementFlags.CAPTURE_MASK
                if (isPromotion) flags = flags or MovementFlags.PROMOTION_MASK
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
        return PieceMovement(fromSquare, movementTargets)
    }

    private fun getPawnTargetPieces(piece: Piece, isPromotion: Boolean): List<Piece> {
        return if (isPromotion) when (piece.color) {
            WHITE -> WHITE_PAWN_REPLACEMENTS
            BLACK -> BLACK_PAWN_REPLACEMENTS
        }
        else listOf(piece)
    }

    private fun mountNonPawnMovements(
        piece: Piece,
        fromIndex: Int,
        targetSquares: ULong,
        movementTargets: ArrayList<MovementTarget>
    ) {
        var s = targetSquares
        while (s != EMPTY) {
            val toIndex = s.countLeadingZeroBits()
            var isCapture = isNotEmpty(toIndex)
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
                movementTargets += MovementTarget(
                    piece,
                    toIndex,
                    MovementFlags(flags)
                )
            }
            s = s and FULL.shift(toIndex + 1)
        }
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
            false,
            false,
            isEnPassant,
            isCapture,
            false
        )

        val isSquareAttacked = when (toPiece.color) {
            WHITE -> isSquareAttacked(whiteKing.countLeadingZeroBits(), BLACK)
            BLACK -> isSquareAttacked(blackKing.countLeadingZeroBits(), WHITE)
        }
        undo()
        return !isSquareAttacked
    }

    private fun isSquareAttacked(fromSquare: Int, color: Color): Boolean {
        val occupied = getOccupiedBitBoard(color.opposite)
        val queenPositions = getPieceBitBoard(QUEEN, color)

        val rookPositions = getPieceBitBoard(ROOK, color)
        val rookRays = getRookTargetSquares(fromSquare, occupied)
        if (rookRays and (rookPositions or queenPositions) != ZERO) return true

        val bishopPositions = getPieceBitBoard(BISHOP, color)
        val bishopRays = getBishopTargetSquares(fromSquare, occupied)
        if (bishopRays and (bishopPositions or queenPositions) != ZERO) return true

        if (
            KING_MOVE_MASK[fromSquare] and getPieceBitBoard(KING, color) != ZERO
            || KNIGHT_MOVE_MASK[fromSquare] and getPieceBitBoard(KNIGHT, color) != ZERO
        ) {
            return true
        }

        return when (color) {
            WHITE -> BLACK_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(WHITE_PAWN) != ZERO
            BLACK -> WHITE_PAWN_CAPTURE_MOVE_MASK[fromSquare] and getPieceBitBoard(BLACK_PAWN) != ZERO
        }
    }

    private fun isEmpty(fromSquare: Int): Boolean = getOccupiedBitBoard() and getSingleMaskBit(fromSquare) == ZERO
    private fun isNotEmpty(fromSquare: Int) = !isEmpty(fromSquare)

    private fun getPiece(fromSquare: Int): Piece {
        val maskedSquare = getMaskedSquare(fromSquare)
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
            else -> throw BoardException("empty square: ${Position.from(fromSquare)}")
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

    private fun getMaskedSquare(fromSquare: Int): ULong = HIGEST_BIT.shift(fromSquare)

    private fun getOccupiedBitBoard(): ULong {
        return getOccupiedBitBoard(WHITE) or getOccupiedBitBoard(BLACK)
    }

    private fun getOccupiedBitBoard(color: Color): ULong {
        return when (color) {
            WHITE -> whiteKing or whiteQueens or whiteRooks or whiteBishops or whiteKnights or whitePawns
            BLACK -> blackKing or blackQueens or blackRooks or blackBishops or blackKnights or blackPawns
        }
    }

    private fun getSingleMaskBit(fromSquare: Int): ULong = HIGEST_BIT.shift(fromSquare)

    private fun move(fromSquare: Int, toSquare: Int, toPiece: Piece, flags: MovementFlags) {
        move(
            fromSquare,
            toSquare,
            toPiece,
            flags.isLeftCastling,
            flags.isRightCastling,
            flags.isEnPassant,
            flags.isCapture,
            flags.isPromotion
        )
    }

    private fun move(
        fromSquare: Int,
        toSquare: Int,
        toPiece: Piece,
        isLeftCastling: Boolean,
        isRightCastling: Boolean,
        isEnPassant: Boolean,
        isCapture: Boolean,
        isPromotion: Boolean
    ) {
        val fromPiece = if (isPromotion) toPiece.getPawn() else toPiece
        val enPassantCapturedSquare = getEnPassantCapturedSquare(toPiece.color, toSquare)
        val capturedPiece = when {
            isEnPassant -> getPiece(enPassantCapturedSquare)
            isCapture -> getPiece(toSquare)
            else -> null
        }

        moveLog += MoveLog(
            fromPiece,
            fromSquare,
            toPiece,
            toSquare,
            isEnPassant,
            isLeftCastling,
            isRightCastling,
            halfMoveClock,
            epTargetSquare,
            castlingFlags,
            capturedPiece
        )

        if (isLeftCastling || isRightCastling) {
            val finalPositions = when (fromPiece.color) {
                WHITE -> if (isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_8.inv()
                BLACK -> if (isLeftCastling) LEFT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
                else RIGHT_CASTLING_FINAL_POSITIONS and RANK_1.inv()
            }

            val kingDestination = if (isLeftCastling) finalPositions.countLeadingZeroBits()
            else ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1

            val rookDestination = if (isLeftCastling) ULong.SIZE_BITS - finalPositions.countTrailingZeroBits() - 1
            else finalPositions.countLeadingZeroBits()

            setBitBoardBit(fromPiece, kingDestination, true)
            setBitBoardBit(fromPiece.getRook(), rookDestination, true)
            setBitBoardBit(fromPiece, fromSquare, false)
            setBitBoardBit(fromPiece.getRook(), toSquare, false)
        } else {
            setBitBoardBit(fromPiece, fromSquare, false)
            setBitBoardBit(toPiece, toSquare, true)
            if (capturedPiece != null) {
                setBitBoardBit(
                    capturedPiece,
                    if (isEnPassant) enPassantCapturedSquare else toSquare,
                    false
                )
            }
        }

        if (fromPiece.isKing) {
            castlingFlags = when (fromPiece.color) {
                WHITE -> castlingFlags and RANK_1.inv()
                BLACK -> castlingFlags and RANK_8.inv()
            }
        } else if (fromPiece.isRook) {
            castlingFlags = castlingFlags and getMaskedSquare(fromSquare).inv()
        }
        if (capturedPiece != null && capturedPiece.isRook) {
            castlingFlags = castlingFlags and getMaskedSquare(toSquare).inv()
        }

        epTargetSquare =
            if (fromPiece.isPawn && (fromSquare - toSquare).absoluteValue == 16)
                when (fromPiece.color) {
                    WHITE -> WHITE_PAWN_SINGLE_MOVE_MASK[fromSquare]
                    BLACK -> BLACK_PAWN_SINGLE_MOVE_MASK[fromSquare]
                }
            else EMPTY

        halfMoveClock = if (fromPiece.isPawn || isCapture) halfMoveClock + 1
        else 0

        if (sideToMove.isBlack) fullMoveCounter++

        plyCounter++
        sideToMove = sideToMove.opposite
    }

    private fun getEnPassantCapturedSquare(color: Color, toSquare: Int): Int {
        return when (color) {
            WHITE -> toSquare + 8
            BLACK -> toSquare - 8
        }
    }

    override fun copy(): Board {
        val copy = Board(false)

        copy.whiteKing = whiteKing
        copy.whiteQueens = whiteQueens
        copy.whiteRooks = whiteRooks
        copy.whiteBishops = whiteBishops
        copy.whiteKnights = whiteKnights
        copy.whitePawns = whitePawns
        copy.blackKing = blackKing
        copy.blackQueens = blackQueens
        copy.blackRooks = blackRooks
        copy.blackBishops = blackBishops
        copy.blackKnights = blackKnights
        copy.blackPawns = blackPawns

        copy.sideToMove = sideToMove
        copy.plyCounter = plyCounter
        copy.halfMoveClock = halfMoveClock
        copy.fullMoveCounter = fullMoveCounter
        copy.epTargetSquare = epTargetSquare
        copy.rookPositions = rookPositions
        copy.castlingFlags = castlingFlags

        copy.moveLog.addAll(moveLog)

        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (whiteKing != other.whiteKing) return false
        if (whiteQueens != other.whiteQueens) return false
        if (whiteRooks != other.whiteRooks) return false
        if (whiteBishops != other.whiteBishops) return false
        if (whiteKnights != other.whiteKnights) return false
        if (whitePawns != other.whitePawns) return false
        if (blackKing != other.blackKing) return false
        if (blackQueens != other.blackQueens) return false
        if (blackRooks != other.blackRooks) return false
        if (blackBishops != other.blackBishops) return false
        if (blackKnights != other.blackKnights) return false
        if (blackPawns != other.blackPawns) return false
        if (sideToMove != other.sideToMove) return false
        if (plyCounter != other.plyCounter) return false
        if (halfMoveClock != other.halfMoveClock) return false
        if (fullMoveCounter != other.fullMoveCounter) return false
        if (epTargetSquare != other.epTargetSquare) return false
        if (rookPositions != other.rookPositions) return false
        if (castlingFlags != other.castlingFlags) return false
        if (moveLog != other.moveLog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = whiteKing.hashCode()
        result = 31 * result + whiteQueens.hashCode()
        result = 31 * result + whiteRooks.hashCode()
        result = 31 * result + whiteBishops.hashCode()
        result = 31 * result + whiteKnights.hashCode()
        result = 31 * result + whitePawns.hashCode()
        result = 31 * result + blackKing.hashCode()
        result = 31 * result + blackQueens.hashCode()
        result = 31 * result + blackRooks.hashCode()
        result = 31 * result + blackBishops.hashCode()
        result = 31 * result + blackKnights.hashCode()
        result = 31 * result + blackPawns.hashCode()
        result = 31 * result + sideToMove.hashCode()
        result = 31 * result + plyCounter
        result = 31 * result + halfMoveClock
        result = 31 * result + fullMoveCounter
        result = 31 * result + epTargetSquare.hashCode()
        result = 31 * result + rookPositions.hashCode()
        result = 31 * result + castlingFlags.hashCode()
        result = 31 * result + moveLog.hashCode()
        return result
    }

    companion object {
        private const val ZERO: ULong = 0u
        private const val EMPTY: ULong = ZERO
        private const val FULL: ULong = ULong.MAX_VALUE

        // 0b1000000 ... 0000000uL
        private const val HIGEST_BIT = 0x8000000000000000uL
    }
}

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
fun main() {
    val board = Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -")
    println(board)
    board.move(Position.E2, Position.A6)
    println(board)
    val movements = board.getMovements(Position.E8)
    movements.forEachMovement {
        println(it)
    }
    println(board)
//    board.move(Position.E8, Position.A8)
//    println(board)
}

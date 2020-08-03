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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConverter
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class FENTest {

    @Test
    fun `test parsing piece positions - case 01`() {
        val pieceDisposition = FenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            .getFenInfo().piecesDisposition
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_ROOK, Position.A8), pieceDisposition[0])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KNIGHT, Position.B8), pieceDisposition[1])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_BISHOP, Position.C8), pieceDisposition[2])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_QUEEN, Position.D8), pieceDisposition[3])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KING, Position.E8), pieceDisposition[4])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_BISHOP, Position.F8), pieceDisposition[5])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KNIGHT, Position.G8), pieceDisposition[6])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_ROOK, Position.H8), pieceDisposition[7])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.A7), pieceDisposition[8])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.B7), pieceDisposition[9])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.C7), pieceDisposition[10])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.D7), pieceDisposition[11])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.E7), pieceDisposition[12])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.F7), pieceDisposition[13])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.G7), pieceDisposition[14])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.H7), pieceDisposition[15])

        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.A2), pieceDisposition[16])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.B2), pieceDisposition[17])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.C2), pieceDisposition[18])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.D2), pieceDisposition[19])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.E2), pieceDisposition[20])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.F2), pieceDisposition[21])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.G2), pieceDisposition[22])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.H2), pieceDisposition[23])

        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_ROOK, Position.A1), pieceDisposition[24])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KNIGHT, Position.B1), pieceDisposition[25])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_BISHOP, Position.C1), pieceDisposition[26])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_QUEEN, Position.D1), pieceDisposition[27])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KING, Position.E1), pieceDisposition[28])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_BISHOP, Position.F1), pieceDisposition[29])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KNIGHT, Position.G1), pieceDisposition[30])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_ROOK, Position.H1), pieceDisposition[31])
    }

    @Test
    fun `test parsing piece positions - case 02`() {
        val pieceDisposition = FenString("r4rk1/1bq1bppp/p2p1n2/n1N1p1B1/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16")
            .getFenInfo().piecesDisposition
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_ROOK, Position.A8), pieceDisposition[0])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_ROOK, Position.F8), pieceDisposition[1])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KING, Position.G8), pieceDisposition[2])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_BISHOP, Position.B7), pieceDisposition[3])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_QUEEN, Position.C7), pieceDisposition[4])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_BISHOP, Position.E7), pieceDisposition[5])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.F7), pieceDisposition[6])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.G7), pieceDisposition[7])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.H7), pieceDisposition[8])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.A6), pieceDisposition[9])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.D6), pieceDisposition[10])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KNIGHT, Position.F6), pieceDisposition[11])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_KNIGHT, Position.A5), pieceDisposition[12])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KNIGHT, Position.C5), pieceDisposition[13])
        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.E5), pieceDisposition[14])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_BISHOP, Position.G5), pieceDisposition[15])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.C4), pieceDisposition[16])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.D4), pieceDisposition[17])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.E4), pieceDisposition[18])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.H4), pieceDisposition[19])

        Assertions.assertEquals(LocalizedPiece(Piece.BLACK_PAWN, Position.B3), pieceDisposition[20])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.C3), pieceDisposition[21])

        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.A2), pieceDisposition[22])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.B2), pieceDisposition[23])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_BISHOP, Position.C2), pieceDisposition[24])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.F2), pieceDisposition[25])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_PAWN, Position.G2), pieceDisposition[26])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KNIGHT, Position.H2), pieceDisposition[27])

        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_ROOK, Position.B1), pieceDisposition[28])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_QUEEN, Position.D1), pieceDisposition[29])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_ROOK, Position.F1), pieceDisposition[30])
        Assertions.assertEquals(LocalizedPiece(Piece.WHITE_KING, Position.G1), pieceDisposition[31])
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "r4rk1/1bq1bppp//p2p1n2/n1N1p1B1/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "r4rk1/1bq1bppp1/p2p1n2/n1N1p1B1/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "r4rk1/1bq1bp0pp/p2p1n2/n1N1p1B1/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "r4rk1/1bq1bppp/p2p1n2/n1N1p1B1B/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "r4rk1/1bq1bppp/p2p1n2/n1N1p1B1/8pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "r4rk1/1bq1bppp/p2p1n2/n1N1p1B1/2pPP9P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16",
            "rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1"
        ]
    )
    fun `should thrown FenException when piece disposition is invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "2kr2nr/pbp1qpp1/1pn5/2bpp2p/2B1P3/P1NPBN2/1PPQ1PPP/R4RK1 w - - 2 10, WHITE",
        "2kr3r/pbp2pp1/1p5n/2qPN2p/2Bn4/P1NP4/1PPQ1PPP/R4RK1 w - - 1 13, WHITE",
        "r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3, BLACK",
        "2kr2nr/pbp2pp1/1p6/2qPN2p/2Bn4/P1NP4/1PPQ1PPP/R4RK1 b - - 0 12, BLACK"
    )
    fun `test parsing side to move part`(fen: String, expectedSideToMove: Color) {
        Assertions.assertEquals(expectedSideToMove, FenString(fen).getFenInfo().sideToMove)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2kr2nr/pbp1qpp1/1pn5/2bpp2p/2B1P3/P1NPBN2/1PPQ1PPP/R4RK1",
            "2kr3r/pbp2pp1/1p5n/2qPN2p/2Bn4/P1NP4/1PPQ1PPP/R4RK1 x - - 1 13, WHITE",
            "r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R - KQkq - 3 3, BLACK"
        ]
    )
    fun `should throw FenException if side to move is invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3, A1, H1, A8, H8",
        "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK1R1 b Qkq - 5 4, null, H1, H1, H8",
        "r1bqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1P3N2/P1PP1PPP/RNBQK1R1 w Qq - 1 6, null, H8, null, H8",
        "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/R1BQK1R1 w Q - 3 7, null, H8, null, null",
        "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/1RBQK1R1 b - - 4 7, null, null, null, null",
        "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/1RBQK1R1 b, null, null, null, null",
        "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/1RBQK1R1 b -, null, null, null, null"
    )
    fun `test parsing castling flags`(
        fen: String,
        @ConvertWith(PositionConverter::class) expectedWhiteKing: Position?,
        @ConvertWith(PositionConverter::class) expectedWhiteQueen: Position?,
        @ConvertWith(PositionConverter::class) expectedBlackKing: Position?,
        @ConvertWith(PositionConverter::class) expectedBlackQueen: Position?
    ) {
        FenString(fen).getFenInfo().castlingFlags.apply {
            Assertions.assertEquals(expectedWhiteKing, expectedWhiteKing)
            Assertions.assertEquals(expectedWhiteQueen, expectedWhiteQueen)
            Assertions.assertEquals(expectedBlackKing, expectedBlackKing)
            Assertions.assertEquals(expectedBlackQueen, expectedBlackQueen)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "qnbbnrkr/pppppppp/8/8/8/8/PPPPPPPP/QNBBNRKR w KQkq - 0 1, QNBBNRKR, F1, H1, F1, H1",
        "qqqqkqqq/pppppppp/8/8/8/8/PPPPPPPP/QQQQKQQQ w - - 0 1, QQQQKQQQ, null, null, null, null",
        "nnrqbkrb/pppppppp/8/8/8/8/PPPPPPPP/NNRQBKRB w KQkq - 0 1, NNRQBKRB, C1, G1, C8, G8",
        "nbnrbkrq/pppppppp/8/8/8/8/PPPPPPPP/NBNRBKRQ w KQq - 0 1, NBNRBKRQ, D1, G1, null, G8"
    )
    fun `test parsing castling flags for non standard initial positions`(
        fen: String,
        piecesSetup: String,
        @ConvertWith(PositionConverter::class) expectedWhiteKing: Position?,
        @ConvertWith(PositionConverter::class) expectedWhiteQueen: Position?,
        @ConvertWith(PositionConverter::class) expectedBlackKing: Position?,
        @ConvertWith(PositionConverter::class) expectedBlackQueen: Position?
    ) {
        FenString(fen, ChessVarian.CHESS960).getFenInfo().castlingFlags.apply {
            Assertions.assertEquals(expectedWhiteKing, expectedWhiteKing)
            Assertions.assertEquals(expectedWhiteQueen, expectedWhiteQueen)
            Assertions.assertEquals(expectedBlackKing, expectedBlackKing)
            Assertions.assertEquals(expectedBlackQueen, expectedBlackQueen)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkqq - 2 3",
            "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK1R1 b Qk_q - 5 4",
            "r1bqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1P3N2/P1PP1PPP/RNBQK1R1 w Qxq - 1 6",
            "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/R1BQK1R1 w QA - 3 7",
            "1rbqk1r1/ppppnppp/2n5/2b1p3/2B1P3/1PN2N2/P1PP1PPP/1RBQK1R1 b s"
        ]
    )
    fun `should throw FenException if castling flags are invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "qqrqkqbq/pppppppp/8/8/8/8/PPPPPPPP/QQRQKQBQ w KxQkq - 0 1, QQRQKQBQ",
        "qqrqkqqq/pppppppp/8/8/8/8/PPPPPPPP/QQRQKQQQ w - K 0 1, QQRQKQQQ",
        "nnrqbkbb/pppppppp/8/8/8/8/PPPPPPPP/NNRQBKBB w KQkq - 0 1, NNRQBKBB",
        "nrnrbkrq/pppppppp/8/8/8/8/PPPPPPPP/NRNRBKRQ w KQq - 0 1, NRNRBKRQ"
    )
    fun `test thrown FenException if castlinf flags are invalid for a non standard pieces setup`(
        fen: String,
        piecesSetup: String
    ) {
        assertThrows<FenException> {
            FenString(fen, ChessVarian.CHESS960).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "rnbqkbnr/1pp1pppp/p7/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3, D6",
        "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq c3 0 4, C3",
        "rnbqkbnr/1pp1pp1p/p5p1/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR w KQkq - 0 5, null",
        "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq c3, C3",
        "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq, null",
        "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq -, null"
    )
    fun `test parsing en passant target square`(
        fen: String,
        @ConvertWith(PositionConverter::class) epTarget: Position?
    ) {
        Assertions.assertEquals(epTarget, FenString(fen).getFenInfo().epTarget)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "rnbqkbnr/1pp1pppp/p7/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq D6 0 3",
            "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq _ 0 4",
            "rnbqkbnr/1pp1pp1p/p5p1/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR w KQkq x 0 5",
            "rnbqkbnr/1pp1pppp/p7/4P3/2Pp1P2/8/PP1P2PP/RNBQKBNR b KQkq 3c"
        ]
    )
    fun `should throw FenException when en passant target is invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4, 0",
        "r1bqkbnr/pp3ppp/8/2pPp3/2Bn1B2/3P1N2/PPP2PPP/RN1Q1RK1 b kq - 2 7, 2",
        "r1bqk1nr/pp2bppp/8/2pPp3/2Bn1B2/3P1N2/PPPQ1PPP/RN3RK1 b kq - 4 8, 4",
        "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 9 9, 9",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - 97 58, 97",
        "r2qk1nr/pp2bppp/8/2pPpb2/2B2B2/3P1P2/PPPQ1P1P/RN3RK1 b kq - 100 59, 100",
        "r2qk1nr/pp2bppp/8/2pPpb2/2B2B2/3P1P2/PPPQ1P1P/RN3RK1 b kq - 190 59, 190",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - - 58, 0",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - -, 0",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq -, 0",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w -, 0",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w, 0"
    )
    fun `test parsing half move clock`(fen: String, expectedHalfMoveClock: Int) {
        Assertions.assertEquals(expectedHalfMoveClock, FenString(fen).getFenInfo().halfMoveClock)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - * 4, 0",
            "r1bqkbnr/pp3ppp/8/2pPp3/2Bn1B2/3P1N2/PPP2PPP/RN1Q1RK1 b kq - -1 7, 2",
            "r1bqk1nr/pp2bppp/8/2pPp3/2Bn1B2/3P1N2/PPPQ1PPP/RN3RK1 b kq - -10 8, 4",
            "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - x 9, 9",
            "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0x2 9, 9"
        ]
    )
    fun `should throw FenException when half move clock is invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4, 4",
        "r1bqkbnr/pp3ppp/8/2pPp3/2Bn1B2/3P1N2/PPP2PPP/RN1Q1RK1 b kq - 2 7, 7",
        "r1bqk1nr/pp2bppp/8/2pPp3/2Bn1B2/3P1N2/PPPQ1PPP/RN3RK1 b kq - 4 8, 8",
        "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 9 9, 9",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - 97 58, 58",
        "r2qk1nr/pp2bppp/8/2pPpb2/2B2B2/3P1P2/PPPQ1P1P/RN3RK1 b kq - 100 59, 59",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - - 123, 123",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - -, 1",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq -, 1",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w -, 1",
        "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w, 1"
    )
    fun `test parsing full move counter`(fen: String, expectedFullMoveCounter: Int) {
        Assertions.assertEquals(expectedFullMoveCounter, FenString(fen).getFenInfo().fullMoveCounter)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 *",
            "r1bqkbnr/pp3ppp/8/2pPp3/2Bn1B2/3P1N2/PPP2PPP/RN1Q1RK1 b kq - 2 -7",
            "r1bqk1nr/pp2bppp/8/2pPp3/2Bn1B2/3P1N2/PPPQ1PPP/RN3RK1 b kq - 4 8x",
            "r1bqkbnr/pp2pppp/2np4/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 9 _9",
            "r2qk1nr/pp2bppp/8/2pPp3/2B2Bb1/3P1P2/PPPQ1P1P/RN3RK1 w kq - 97 -58",
            "r2qk1nr/pp2bppp/8/2pPpb2/2B2B2/3P1P2/PPPQ1P1P/RN3RK1 b kq - 100 0"
        ]
    )
    fun `should throw FenException when full move clock is invalid`(fen: String) {
        assertThrows<FenException> {
            FenString(fen).getFenInfo()
        }
    }
}

class PositionConverter : ArgumentConverter {
    override fun convert(source: Any?, context: ParameterContext?) =
        if (source != "null") Position.valueOf(source as String) else null
}

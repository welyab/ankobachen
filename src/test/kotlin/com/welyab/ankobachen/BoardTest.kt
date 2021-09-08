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
import com.welyab.ankobachen.Square.A1
import com.welyab.ankobachen.Square.B1
import com.welyab.ankobachen.Square.B2
import com.welyab.ankobachen.Square.B7
import com.welyab.ankobachen.Square.C1
import com.welyab.ankobachen.Square.D1
import com.welyab.ankobachen.Square.D8
import com.welyab.ankobachen.Square.E1
import com.welyab.ankobachen.Square.E8
import com.welyab.ankobachen.Square.F8
import com.welyab.ankobachen.Square.G8
import com.welyab.ankobachen.Square.H8
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class BoardTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "r1b1kb1r/pppp1ppp/5q2/4n3/3KP3/2N3PN/PPP4P/R1BQ1B1R b kq - 0 12",
            "r3k2r/ppp2Npp/1b5n/4p2b/2B1P2q/BQP2P2/P5PP/RN5K w kq - 0 1",
            "r1b3kr/ppp1Bp1p/1b6/n2P4/2p3q1/2Q2N2/P4PPP/RN2R1K1 w - - 0 1",
            "r2n1rk1/1ppb2pp/1p1p4/3Ppq1n/2B3P1/2P4P/PP1N1P1K/R2Q1RN1 b - - 0 1",
            "3q1r1k/2p4p/1p1pBrp1/p2Pp3/2PnP3/5PP1/PP1Q2K1/5R1R w - - 0 1",
            "6k1/ppp2ppp/8/2n2K1P/2P2P1P/2Bpr3/PP4r1/4RR2 b - - 0 1",
            "rn3rk1/p5pp/2p5/3Ppb2/2q5/1Q6/PPPB2PP/R3K1NR b - - 0 1",
            "N1bk4/pp1p1Qpp/8/2b5/3n3q/8/PPP2RPP/RNB1rBK1 b - - 0 1",
            "8/2p3N1/6p1/5PB1/pp2Rn2/7k/P1p2K1P/3r4 w - - 0 1",
            "r1b1k1nr/p2p1ppp/n2B4/1p1NPN1P/6P1/3P1Q2/P1P1K3/q5b1 w - - 0 1",
            "1q2r3/k4p2/prQ2b1p/R7/1PP1B1p1/6P1/P5K1/8 w - - 0 1",
            "r1bqr1k1/ppp2pp1/3p4/4n1NQ/2B1PN2/8/P4PPP/b4RK1 w - - 0 1",
            "3r4/pp5Q/B7/k7/3q4/2b5/P4PPP/1R4K1 w - - 0 1",
            "rnbk1b1r/ppqpnQ1p/4p1p1/2p1N1B1/4N3/8/PPP2PPP/R3KB1R w - - 0 1",
            "3rnr1k/p1q1b1pB/1pb1p2p/2p1P3/2P2N2/PP4P1/1BQ4P/4RRK1 w - - 0 1",
            "8/Qp4pk/2p3b1/5p1p/3B3P/1P4P1/P1P1rnBK/3r4 b - - 0 1",
            "k7/1p1rr1pp/pR1p1p2/Q1pq4/P7/8/2P3PP/1R4K1 w - - 0 1",
            "3r1rk1/p1p4p/8/1PP1p1bq/2P5/3N1Pp1/PB2Q3/1R3RK1 b - - 0 1",
            "Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 0 1",
            "2rrk3/QR3pp1/2n1b2p/1BB1q3/3P4/8/P4PPP/6K1 w - - 0 1",
            "7k/pbp3bp/3p4/1p5q/3n2p1/5rB1/PP1NrN1P/1Q1BRRK1 b - - 0 1",
            "3r4/pR2N3/2pkb3/5p2/8/2B5/qP3PPP/4R1K1 w - - 0 1",
            "5qrk/p3b1rp/4P2Q/5P2/1pp5/5PR1/P6P/B6K w - - 0 1",
            "r1nk3r/2b2ppp/p3bq2/3pN3/Q2P4/B1NB4/P4PPP/4R1K1 w - - 0 1",
            "r1n5/pp2q1kp/2ppr1p1/4p1Q1/8/2N4R/PPP3PP/5RK1 w - - 0 1"
        ]
    )
    fun `getFen should return same FEN used to create board if no move was made`(
        fen: String
    ) {
        assertEquals(fen, Board(fen).getFen())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4, A4, E2, C6, G2"
        ]
    )
    fun `isEmpty should return true when square is empty and false when it not`(
        fen: String,
        firstEmpty: Square,
        secondEmpty: Square,
        firstOccupied: Square,
        secondOccupied: Square
    ) {
        Board(fen).run {
            assertTrue(isEmpty(firstEmpty))
            assertTrue(isEmpty(secondEmpty))
            assertFalse(isEmpty(firstOccupied))
            assertFalse(isEmpty(secondOccupied))
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4, A4, E2, C6, G2"
        ]
    )
    fun `isNotEmpty should return true when square is not empty and false when it is empty`(
        fen: String,
        firstEmpty: Square,
        secondEmpty: Square,
        firstOccupied: Square,
        secondOccupied: Square
    ) {
        Board(fen).run {
            assertFalse(isNotEmpty(firstEmpty))
            assertFalse(isNotEmpty(secondEmpty))
            assertTrue(isNotEmpty(firstOccupied))
            assertTrue(isNotEmpty(secondOccupied))
        }
    }

    @Test
    fun `assert toBitboardString is working`() {
        assertEquals(
            """
                1 1 1 1 1 1 1 1
                1 1 0 0 0 1 1 1
                0 0 1 0 0 0 0 0
                0 0 0 1 1 0 0 0
                0 0 0 0 1 0 0 0
                0 0 0 0 0 1 1 0
                1 1 1 1 0 1 1 1
                1 1 1 1 1 0 0 1

            """.trimIndent().normalizeLineBreaks(),
            Board("rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4")
                .toBitboardString().normalizeLineBreaks()
        )
    }

    @Test
    fun `assert toString is working`() {
        assertEquals(
            """
                ┌───┬───┬───┬───┬───┬───┬───┬───┐
                │ r │ n │ b │ q │ k │ b │ n │ r │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ p │ p │   │   │   │ p │ p │ p │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │ p │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │ p │ p │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │ P │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │ N │ P │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ P │ P │ P │ P │   │ P │ B │ P │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ R │ N │ B │ Q │ K │   │   │ R │
                └───┴───┴───┴───┴───┴───┴───┴───┘

            """.trimIndent().normalizeLineBreaks(),
            Board("rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4")
                .toString().normalizeLineBreaks()
        )
    }

    @Test
    fun `assert toString is working with FEN information`() {
        assertEquals(
            """
                FEN: rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4
                ┌───┬───┬───┬───┬───┬───┬───┬───┐
                │ r │ n │ b │ q │ k │ b │ n │ r │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ p │ p │   │   │   │ p │ p │ p │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │ p │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │ p │ p │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │ P │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │ N │ P │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ P │ P │ P │ P │   │ P │ B │ P │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ R │ N │ B │ Q │ K │   │   │ R │
                └───┴───┴───┴───┴───┴───┴───┴───┘

            """.trimIndent().normalizeLineBreaks(),
            Board("rnbqkbnr/pp3ppp/2p5/3pp3/4P3/5NP1/PPPP1PBP/RNBQK2R b KQkq - 1 4")
                .toString(true).normalizeLineBreaks()
        )
    }

    @Test
    fun `Board(false) should create an empty board - no pieces placed`() {
        assertEquals(
            """
                ┌───┬───┬───┬───┬───┬───┬───┬───┐
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                └───┴───┴───┴───┴───┴───┴───┴───┘

            """.trimIndent().normalizeLineBreaks(),
            Board(false).toString().normalizeLineBreaks()
        )
    }

    @Test
    fun `Board(true) should create a board with pieces in the initial position`() {
        assertEquals(
            """
                ┌───┬───┬───┬───┬───┬───┬───┬───┐
                │ r │ n │ b │ q │ k │ b │ n │ r │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ p │ p │ p │ p │ p │ p │ p │ p │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │   │   │   │   │   │   │   │   │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ P │ P │ P │ P │ P │ P │ P │ P │
                ├───┼───┼───┼───┼───┼───┼───┼───┤
                │ R │ N │ B │ Q │ K │ B │ N │ R │
                └───┴───┴───┴───┴───┴───┴───┴───┘

            """.trimIndent().normalizeLineBreaks(),
            Board(true).toString().normalizeLineBreaks()
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "r1b2r1k/4qp1p/p1Nppb1Q/4nP2/1p2P3/2N5/PPP4P/2KR1BR1 b - - 5 18, WHITE_KNIGHT, C6, BLACK_BISHOP, F6, WHITE_PAWN, A2"
        ]
    )
    fun `getPiece should return the right piece placed in specific square`(
        fen: String,
        firstPiece: Piece, firstSquare: Square,
        secondPiece: Piece, secondSquare: Square,
        thirdPiece: Piece, thirdSquare: Square
    ) {
        Board(fen).run {
            assertEquals(firstPiece, getPiece(firstSquare))
            assertEquals(secondPiece, getPiece(secondSquare))
            assertEquals(thirdPiece, getPiece(thirdSquare))
        }
    }

    @Test
    fun `getPiece should return all pieces types in the right location`() {
        Board().run {
            assertEquals(WHITE_ROOK, getPiece(A1))
            assertEquals(WHITE_KNIGHT, getPiece(B1))
            assertEquals(WHITE_BISHOP, getPiece(C1))
            assertEquals(WHITE_QUEEN, getPiece(D1))
            assertEquals(WHITE_KING, getPiece(E1))
            assertEquals(WHITE_PAWN, getPiece(B2))

            assertEquals(BLACK_ROOK, getPiece(H8))
            assertEquals(BLACK_KNIGHT, getPiece(G8))
            assertEquals(BLACK_BISHOP, getPiece(F8))
            assertEquals(BLACK_KING, getPiece(E8))
            assertEquals(BLACK_QUEEN, getPiece(D8))
            assertEquals(BLACK_PAWN, getPiece(B7))
        }
    }
}

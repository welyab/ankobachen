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
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

class BoardTest {

    @ParameterizedTest
    @CsvSource(
        "1q1q4/b1n5/n3n3/2p4q/5n2/7q/n1k1n3/1q1qb3 w - - 0 1, C4, BLACK, false",
        "8/8/8/8/8/2K5/8/8 w - - 0 1, C4, WHITE, true",
        "8/8/8/8/8/2K5/8/8 w - - 0 1, C5, WHITE, false",
        "8/8/8/8/8/5Q2/8/8 w - - 0 1, C6, WHITE, true",
        "8/8/8/8/8/5Q2/8/8 w - - 0 1, D6, WHITE, false",
        "8/8/8/8/8/8/8/4R3 w - - 0 1, E4, WHITE, true",
        "8/8/8/8/8/8/8/4R3 w - - 0 1, F4, WHITE, false",
        "8/8/8/8/8/8/1b6/8 w - - 0 1, F6, BLACK, true",
        "8/8/8/8/8/8/1b6/8 w - - 0 1, G6, BLACK, false",
        "8/8/8/2n5/8/8/8/8 w - - 0 1, D7, BLACK, true",
        "8/8/8/2n5/8/8/8/8 w - - 0 1, E5, BLACK, false",
        "8/8/8/4p3/8/8/8/8 w - - 0 1, D4, BLACK, true",
        "8/8/8/4p3/8/8/8/8 w - - 0 1, E4, BLACK, false",
        "8/8/8/4P3/8/8/8/8 w - - 0 1, D6, WHITE, true",
        "8/8/8/4P3/8/8/8/8 w - - 0 1, E6, WHITE, false"

    )
    fun `isAttacked should return true if a position is attacked by some piece and false if not`(
        fen: String,
        attackedPosition: Position,
        attackerColor: Color,
        expectedResult: Boolean
    ) {
        Assertions.assertEquals(
            expectedResult,
            Board(fen).isAttacked(attackedPosition, attackerColor)
        )
    }

    @ParameterizedTest
    @MethodSource("source - getAttackers should return the list of pieces that are attacking a specific position")
    fun `getAttackers should return the list of pieces that are attacking a specific position`(
        fen: String,
        attackedPosition: Position,
        attackerColor: Color,
        expectedAttackers: List<LocalizedPiece>
    ) {
        val attackers = Board(fen).getAttackers(attackedPosition, attackerColor)
        val filteredAttackers = expectedAttackers - attackers
        if (filteredAttackers.isEmpty()) return
        fail(
            "Expecting the positions in $attackedPosition to be attacked by $expectedAttackers, " +
                    "but positions in $filteredAttackers was missing"
        )
    }

    @ParameterizedTest
    @CsvSource(
        "8/8/1B6/2n5/6rR/8/1P6/Q7 w - - 0 1, D4, WHITE",
        "8/8/rP6/8/2B3R1/1q5b/q3N3/4q3 w - - 0 1, E6, BLACK"
    )
    fun `getAttackers should not return a piece if it is covered by another piece`(
        fen: String,
        attackedPosition: Position,
        attackerColor: Color
    ) {
        Assertions.assertTrue(Board(fen).getAttackers(attackedPosition, attackerColor).isEmpty())
    }

    @Test
    fun `test toString - case 01`() {
        Assertions.assertEquals(
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
            
            """.trimIndent().normalizedNewLines(),
            Board().toString().normalizedNewLines()
        )
    }

    @Test
    fun `test toString - case 02`() {
        Assertions.assertEquals(
            """
            ┌───┬───┬───┬───┬───┬───┬───┬───┐
            │ r │   │   │   │   │ r │ k │   │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │   │ b │ q │   │ b │ p │ p │ p │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │ p │   │   │ p │   │ n │   │   │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │ n │   │ N │   │ p │   │ B │   │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │   │   │ p │ P │ P │   │   │ P │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │   │ p │ P │   │   │   │   │   │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │ P │ P │ B │   │   │ P │ P │ N │
            ├───┼───┼───┼───┼───┼───┼───┼───┤
            │   │ R │   │ Q │   │ R │ K │   │
            └───┴───┴───┴───┴───┴───┴───┴───┘
            
            """.trimIndent().normalizedNewLines(),
            Board("r4rk1/1bq1bppp/p2p1n2/n1N1p1B1/2pPP2P/1pP5/PPB2PPN/1R1Q1RK1 b - - 1 16")
                .toString().normalizedNewLines()
        )
    }

    companion object {
        @JvmStatic
        fun `source - getAttackers should return the list of pieces that are attacking a specific position`() = listOf(
            Arguments.of(
                "8/2k5/8/8/8/8/8/8 w - - 0 1",
                Position.D7,
                Color.BLACK,
                listOf(
                    LocalizedPiece(Piece.BLACK_KING, Position.C7)
                )
            ),
            Arguments.of(
                "1Q6/6Q1/8/8/4Q3/8/8/8 w - - 0 1",
                Position.B7,
                Color.WHITE,
                listOf(
                    LocalizedPiece(Piece.WHITE_QUEEN, Position.E4),
                    LocalizedPiece(Piece.WHITE_QUEEN, Position.B8),
                    LocalizedPiece(Piece.WHITE_QUEEN, Position.G7)
                )
            ),
            Arguments.of(
                "8/2r4r/8/8/8/8/8/3r4 w - - 0 1",
                Position.D7,
                Color.BLACK,
                listOf(
                    LocalizedPiece(Piece.BLACK_ROOK, Position.C7),
                    LocalizedPiece(Piece.BLACK_ROOK, Position.H7),
                    LocalizedPiece(Piece.BLACK_ROOK, Position.D1)
                )
            ),
            Arguments.of(
                "7B/8/8/8/8/8/1B6/4B3 w - - 0 1",
                Position.C3,
                Color.WHITE,
                listOf(
                    LocalizedPiece(Piece.WHITE_BISHOP, Position.B2),
                    LocalizedPiece(Piece.WHITE_BISHOP, Position.H8),
                    LocalizedPiece(Piece.WHITE_BISHOP, Position.E1)
                )
            ),
            Arguments.of(
                "8/8/8/4n3/5n2/8/8/4n3 w - - 0 1",
                Position.D3,
                Color.BLACK,
                listOf(
                    LocalizedPiece(Piece.BLACK_KNIGHT, Position.E5),
                    LocalizedPiece(Piece.BLACK_KNIGHT, Position.F4),
                    LocalizedPiece(Piece.BLACK_KNIGHT, Position.E1)
                )
            ),
            Arguments.of(
                "8/8/8/8/8/8/3P1P2/8 w - - 0 1",
                Position.E3,
                Color.WHITE,
                listOf(
                    LocalizedPiece(Piece.WHITE_PAWN, Position.D2),
                    LocalizedPiece(Piece.WHITE_PAWN, Position.F2)
                )
            ),
            Arguments.of(
                "6Q1/8/2K5/7R/2P5/4N3/8/7B w - - 0 1",
                Position.D5,
                Color.WHITE,
                listOf(
                    LocalizedPiece(Piece.WHITE_KING, Position.C6),
                    LocalizedPiece(Piece.WHITE_QUEEN, Position.G8),
                    LocalizedPiece(Piece.WHITE_ROOK, Position.H5),
                    LocalizedPiece(Piece.WHITE_BISHOP, Position.H1),
                    LocalizedPiece(Piece.WHITE_KNIGHT, Position.E3),
                    LocalizedPiece(Piece.WHITE_PAWN, Position.C4)
                )
            )
        )

        data class GetAttackersTestCase(
            val fen: String
        )
    }
}

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
@file:Suppress("SpellCheckingInspection")

package com.welyab.ankobachen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

@ExperimentalStdlibApi
class BoardTest {

    @Test
    fun `test perft depth 5 fen rnbqkbnr|pppppppp|8|8|8|8|PPPPPPPP|RNBQKBNR w KQkq - 0 1`() {
        val expectedResult = """
            ┌───────┬─────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │   NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │      20 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │     400 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │    8902 │       34 │           0 │         0 │          0 │     12 │           0 │       0 │          0 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │  197281 │     1576 │           0 │         0 │          0 │    469 │           0 │       0 │          8 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     5 │ 4865609 │    82719 │         258 │         0 │          0 │  27351 │           6 │       0 │        347 │          0 │
            └───────┴─────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
        """.trimIndent()
        val calculator = PerftCalculator(
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            5
        )
        val result = calculator.getPerftResult().toString()
        assertEquals(expectedResult, result)
    }

    @Test
    fun `test perft depth 4 fen r3k2r|p1ppqpb1|bn2pnp1|3PN3|1p2P3|2N2Q1p|PPPBBPPP|R3K2R w KQkq -`() {
        val expectedResult = """
            ┌───────┬─────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │   NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │      48 │        8 │           0 │         2 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    2039 │      351 │           1 │        91 │          0 │      3 │           0 │       0 │          0 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │   97862 │    17102 │          45 │      3162 │          0 │    993 │           0 │       0 │          1 │          0 │
            ├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │ 4085603 │   757163 │        1929 │    128013 │      15172 │  25523 │          42 │       6 │         43 │          0 │
            └───────┴─────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
        """.trimIndent()
        val calculator = PerftCalculator(
            "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -",
            4
        )
        val result = calculator.getPerftResult().toString()
        assertEquals(expectedResult, result)
    }

    @Test
    fun `test perft depth 6 fen 8|2p5|3p4|KP5r|1R3p1k|8|4P1P1|8 w - -`() {
        val expectedResult = """
            ┌───────┬──────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │    NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │       14 │        1 │           0 │         0 │          0 │      2 │           0 │       0 │          0 │          0 │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │      191 │       14 │           0 │         0 │          0 │     10 │           0 │       0 │          0 │          0 │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │     2812 │      209 │           2 │         0 │          0 │    267 │           3 │       0 │          0 │          0 │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │    43238 │     3348 │         123 │         0 │          0 │   1680 │         106 │       0 │         17 │          0 │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     5 │   674624 │    52051 │        1165 │         0 │          0 │  52950 │        1292 │       3 │          0 │          0 │
            ├───────┼──────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     6 │ 11030083 │   940350 │       33325 │         0 │       7552 │ 452473 │       26067 │       0 │       2733 │          0 │
            └───────┴──────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
        """.trimIndent()
        val calculator = PerftCalculator(
            "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",
            6
        )
        val result = calculator.getPerftResult().toString()
        assertEquals(expectedResult, result)
    }

    @Test
    fun `test perft depth 4 fen r2q1rk1|pP1p2pp|Q4n2|bbp1p3|Np6|1B3NBn|pPPP1PPP|R3K2R b KQ - 0 1`() {
        val expectedResult = """
            ┌───────┬────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │  NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │      6 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    264 │       87 │           0 │         6 │         48 │     10 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │   9467 │     1021 │           4 │         0 │        120 │     38 │           2 │       0 │         22 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │ 422333 │   131393 │           0 │      7795 │      60032 │  15492 │          19 │       0 │          5 │          0 │
            └───────┴────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
        """.trimIndent()
        val calculator = PerftCalculator(
            "r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1",
            4
        )
        val result = calculator.getPerftResult().toString()
        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @CsvFileSource(
        resources = ["/chess960/perft-results.csv"],
        delimiter = '\t'
    )
    fun `test Chess 960 positions`(
        fen: String,
        totalNodesdepth1: Long,
        totalNodesDepth2: Long,
        totalNodesDepth3: Long,
        totalNodesDepth4: Long,
        totalNodesDepth5: Long,
        totalNodesDepth6: Long
    ) {
        val perftResult = PerftCalculator(fen, depth = 4).getPerftResult()
        assertEquals(totalNodesdepth1, perftResult.getPerftValue(1, PerftValue.NODES))
        assertEquals(totalNodesDepth2, perftResult.getPerftValue(2, PerftValue.NODES))
        assertEquals(totalNodesDepth3, perftResult.getPerftValue(3, PerftValue.NODES))
        assertEquals(totalNodesDepth4, perftResult.getPerftValue(4, PerftValue.NODES))
    }
}

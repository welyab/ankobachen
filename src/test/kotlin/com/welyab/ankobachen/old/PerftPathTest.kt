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

class PerftCalculatorTest {

    @Test
    fun `test rnbqkbnr-pppppppp-8-8-8-8-PPPPPPPP-RNBQKBNR w KQkq - 0 1 perft until depth 4`() {
        Assertions.assertEquals(
            """
            ┌───────┬────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │  NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │     20 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    400 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │   8902 │       34 │           0 │         0 │          0 │     12 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │ 197281 │     1576 │           0 │         0 │          0 │    469 │           0 │       0 │          8 │          0 │
            └───────┴────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
            """.trimIndent(),
            PerftCalculator("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 4).getPerftResult().toString()
        )
    }

    @Test
    fun `test r3k2r-p1ppqpb1-bn2pnp1-3PN3-1p2P3-2N2Q1p-PPPBBPPP-R3K2R w KQkq - perft until depth 3`() {
        Assertions.assertEquals(
            """
            ┌───────┬───────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │ NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │    48 │        8 │           0 │         2 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │  2039 │      351 │           1 │        91 │          0 │      3 │           0 │       0 │          0 │          0 │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │ 97862 │    17102 │          45 │      3162 │          0 │    993 │           0 │       0 │          1 │          0 │
            └───────┴───────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
            """.trimIndent(),
            PerftCalculator("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 3).getPerftResult().toString()
        )
    }

    @Test
    fun `test 8-2p5-3p4-KP5r-1R3p1k-8-4P1P1-8 w - perft until depth 5`() {
        Assertions.assertEquals(
            """
            ┌───────┬────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │  NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │     14 │        1 │           0 │         0 │          0 │      2 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    191 │       14 │           0 │         0 │          0 │     10 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │   2812 │      209 │           2 │         0 │          0 │    267 │           3 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │  43238 │     3348 │         123 │         0 │          0 │   1680 │         106 │       0 │         17 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     5 │ 674624 │    52051 │        1165 │         0 │          0 │  52950 │        1292 │       3 │          0 │          0 │
            └───────┴────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
            """.trimIndent(),
            PerftCalculator("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", 5).getPerftResult().toString()
        )
    }

    @Test
    fun `test r3k2r-Pppp1ppp-1b3nbN-nP6-BBP1P3-q4N2-Pp1P2PP-R2Q1RK1 w kq - 0 1 perft until depth 5`() {
        Assertions.assertEquals(
            """
            ┌───────┬────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │  NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │     14 │        1 │           0 │         0 │          0 │      2 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    191 │       14 │           0 │         0 │          0 │     10 │           0 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │   2812 │      209 │           2 │         0 │          0 │    267 │           3 │       0 │          0 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │  43238 │     3348 │         123 │         0 │          0 │   1680 │         106 │       0 │         17 │          0 │
            ├───────┼────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     5 │ 674624 │    52051 │        1165 │         0 │          0 │  52950 │        1292 │       3 │          0 │          0 │
            └───────┴────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
            """.trimIndent(),
            PerftCalculator("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", 5).getPerftResult().toString()
        )
    }

    @Test
    fun `test r3k2r-Pppp1ppp-1b3nbN-nP6-BBP1P3-q4N2-Pp1P2PP-R2Q1RK1 w kq - 0 1 perft until depth 4`() {
        Assertions.assertEquals(
            """
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
            
            """.trimIndent(),
            PerftCalculator("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 4).getPerftResult().toString()
        )
    }

    @Test
    fun `test k7-8-8-8-8-8-8-1QQ4K w - - 0 1 perft until depth 4`() {
        Assertions.assertEquals(
            """
            ┌───────┬───────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
            │ DEPTH │ NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     1 │    36 │        0 │           0 │         0 │          0 │      8 │           0 │       0 │          1 │          5 │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     2 │    41 │        2 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     3 │  1749 │        0 │           0 │         0 │          0 │    491 │           0 │       0 │         37 │         47 │
            ├───────┼───────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
            │     4 │  4370 │      111 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
            └───────┴───────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
            
            """.trimIndent(),
            PerftCalculator("k7/8/8/8/8/8/8/1QQ4K w - - 0 1", 4).getPerftResult().toString()
        )
    }
}

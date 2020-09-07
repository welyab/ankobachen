package com.welyab.ankobachen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

@ExperimentalStdlibApi
class FENTest {

    @ParameterizedTest
    @CsvFileSource(resources = ["/chess960/fen.csv"])
    fun `test fen parsing and generation`(fen: String) {
        val board1 = Board(fen)
        val board2 = Board(board1.getFen())
        assertEquals(board1, board2)
    }
}

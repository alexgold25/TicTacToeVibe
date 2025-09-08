package com.alexgold25.tictactoevibe

import com.alexgold25.tictactoevibe.ui.bestMove
import org.junit.Assert.assertEquals
import org.junit.Test

class AiEngineTest {
    @Test
    fun aiBlocksImmediateThreat() {
        val board = listOf<String?>(
            "X", "X", null,
            null, "O", null,
            null, null, null
        )
        assertEquals(2, bestMove(board))
    }

    @Test
    fun aiWinsWhenPossible() {
        val board = listOf<String?>(
            "X", null, null,
            "O", "O", null,
            "X", "X", null
        )
        assertEquals(5, bestMove(board))
    }
}


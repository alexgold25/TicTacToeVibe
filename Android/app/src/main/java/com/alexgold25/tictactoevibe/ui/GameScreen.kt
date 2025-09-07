@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.alexgold25.tictactoevibe.ui

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.input.pointer.PointerEventType
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.input.pointer.awaitPointerEventScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.LocalIndication
import androidx.compose.animation.core.animateFloatAsState



enum class WinType { ROW, COLUMN, DIAGONAL_MAIN, DIAGONAL_ANTI }
data class WinInfo(val type: WinType, val index: Int = 0)
@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    // Плоское поле 3x3: индексы 0..8
    var board by remember { mutableStateOf(List(9) { null as String? }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winInfo by remember { mutableStateOf<WinInfo?>(null) }
    var isHumanVsAi by remember { mutableStateOf(true) }

    fun reset() {
        board = List(9) { null }
        currentPlayer = "X"
        winInfo = null
    }

    fun isBoardFull() = board.all { it != null }

    // Определяем победителя/линию на плоском поле
    fun checkWinnerFlat(cells: List<String?>): WinInfo? {
        // строки
        for (r in 0..2) {
            val a = r * 3
            if (cells[a] != null && cells[a] == cells[a + 1] && cells[a] == cells[a + 2]) {
                return WinInfo(WinType.ROW, r)
            }
        }
        // столбцы
        for (c in 0..2) {
            if (cells[c] != null && cells[c] == cells[c + 3] && cells[c] == cells[c + 6]) {
                return WinInfo(WinType.COLUMN, c)
            }
        }
        // диагонали
        if (cells[0] != null && cells[0] == cells[4] && cells[0] == cells[8]) {
            return WinInfo(WinType.DIAGONAL_MAIN)
        }
        if (cells[2] != null && cells[2] == cells[4] && cells[2] == cells[6]) {
            return WinInfo(WinType.DIAGONAL_ANTI)
        }
        return null
    }

    fun winnerSymbol(cells: List<String?>): String? {
        val info = checkWinnerFlat(cells) ?: return null
        return when (info.type) {
            WinType.ROW -> cells[info.index * 3]
            WinType.COLUMN -> cells[info.index]
            WinType.DIAGONAL_MAIN -> cells[0]
            WinType.DIAGONAL_ANTI -> cells[2]
        }
    }

    fun minimax(cells: MutableList<String?>, maximizing: Boolean): Int {
        val win = winnerSymbol(cells)
        if (win == "O") return 1
        if (win == "X") return -1
        if (cells.all { it != null }) return 0

        return if (maximizing) {
            var best = Int.MIN_VALUE
            for (i in cells.indices) {
                if (cells[i] == null) {
                    cells[i] = "O"
                    val score = minimax(cells, false)
                    cells[i] = null
                    if (score > best) best = score
                }
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (i in cells.indices) {
                if (cells[i] == null) {
                    cells[i] = "X"
                    val score = minimax(cells, true)
                    cells[i] = null
                    if (score < best) best = score
                }
            }
            best
        }
    }

    fun bestMove(cells: List<String?>): Int? {
        var bestScore = Int.MIN_VALUE
        var move: Int? = null
        for (i in cells.indices) {
            if (cells[i] == null) {
                val copy = cells.toMutableList()
                copy[i] = "O"
                val score = minimax(copy, false)
                if (score > bestScore) {
                    bestScore = score
                    move = i
                }
            }
        }
        return move
    }

    val statusText by remember(board, winInfo, currentPlayer) {
        derivedStateOf {
            when {
                winInfo != null -> {
                    val winner = when (winInfo!!.type) {
                        WinType.ROW -> board[winInfo!!.index * 3]
                        WinType.COLUMN -> board[winInfo!!.index]
                        WinType.DIAGONAL_MAIN -> board[0]
                        WinType.DIAGONAL_ANTI -> board[2]
                    } ?: ""
                    "$winner wins"
                }
                isBoardFull() -> "Draw"
                else -> "Turn: $currentPlayer"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Верхняя панель: статус + новая игра
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = statusText, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.weight(1f))
            Button(onClick = { reset() }) { Text("New Game") }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = !isHumanVsAi,
                onClick = { isHumanVsAi = false; reset() },
                label = { Text("2 Players") }
            )
            FilterChip(
                selected = isHumanVsAi,
                onClick = { isHumanVsAi = true; reset() },
                label = { Text("Vs Computer") }
            )
        }

        // Игровое поле (квадрат)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val boardSize = minOf(maxWidth, maxHeight)
            val gap = 4.dp

            Box(modifier = Modifier.size(boardSize)) {
                Column(Modifier.fillMaxSize()) {
                    for (r in 0..2) {
                        Row(Modifier.weight(1f)) {
                            for (c in 0..2) {
                                val idx = r * 3 + c
                                val cell = board[idx]

                                // Подсветка нажатия через InteractionSource
                                val interaction = remember { MutableInteractionSource() }
                                val pressed by interaction.collectIsPressedAsState()

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(gap)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (pressed)
                                                MaterialTheme.colorScheme.surfaceVariant
                                            else
                                                MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable(
                                            interactionSource = interaction,
                                            indication = LocalIndication.current,
                                            enabled = (cell == null && winInfo == null)
                                        ) {
                                            if (board[idx] == null && winInfo == null) {
                                                board = board.toMutableList().also { it[idx] = currentPlayer }
                                                winInfo = checkWinnerFlat(board)
                                                if (winInfo == null && !isBoardFull()) {
                                                    if (isHumanVsAi) {
                                                        val aiIdx = bestMove(board)
                                                        if (aiIdx != null) {
                                                            board = board.toMutableList().also { it[aiIdx] = "O" }
                                                            winInfo = checkWinnerFlat(board)
                                                        }
                                                        if (winInfo == null && !isBoardFull()) {
                                                            currentPlayer = "X"
                                                        }
                                                    } else {
                                                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cell ?: "",
                                        fontSize = 32.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Линия победы с неоновым свечением
                winInfo?.let { info ->
                    val winLineColor = MaterialTheme.colorScheme.primary.toArgb()
                    val progress by animateFloatAsState(targetValue = 1f, label = "win-line")

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val side = size.minDimension
                        val cellSide = side / 3f
                        val stroke = side / 15f

                        val glowPaint = Paint().apply {
                            color = winLineColor
                            isAntiAlias = true
                            style = Paint.Style.STROKE
                            strokeWidth = stroke * 1.5f
                            maskFilter = BlurMaskFilter(stroke * 2f, BlurMaskFilter.Blur.NORMAL)
                        }

                        val linePaint = Paint().apply {
                            color = winLineColor
                            isAntiAlias = true
                            style = Paint.Style.STROKE
                            strokeWidth = stroke / 2f
                        }

                        val (start, end) = when (info.type) {
                            WinType.ROW -> {
                                val y = cellSide * info.index + cellSide / 2f
                                Offset(0f, y) to Offset(side, y)
                            }
                            WinType.COLUMN -> {
                                val x = cellSide * info.index + cellSide / 2f
                                Offset(x, 0f) to Offset(x, side)
                            }
                            WinType.DIAGONAL_MAIN -> {
                                Offset(0f, 0f) to Offset(side, side)
                            }
                            WinType.DIAGONAL_ANTI -> {
                                Offset(0f, side) to Offset(side, 0f)
                            }
                        }

                        val animatedEnd = Offset(
                            x = start.x + (end.x - start.x) * progress,
                            y = start.y + (end.y - start.y) * progress
                        )

                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawLine(start.x, start.y, animatedEnd.x, animatedEnd.y, glowPaint)
                            canvas.nativeCanvas.drawLine(start.x, start.y, animatedEnd.x, animatedEnd.y, linePaint)
                        }
                    }
                }
            }
        }
    }
}

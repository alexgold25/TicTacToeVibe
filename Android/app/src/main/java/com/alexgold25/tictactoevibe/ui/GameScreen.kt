@file:OptIn(ExperimentalComposeUiApi::class)

package com.alexgold25.tictactoevibe.ui

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
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
            androidx.compose.material3.Button(onClick = { reset() }) {
                Text("New Game")
            }
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
                                        .border(1.dp, MaterialTheme.colorScheme.primary)
                                        .background(
                                            if (pressed)
                                                MaterialTheme.colorScheme.surfaceVariant
                                            else
                                                MaterialTheme.colorScheme.surface
                                        )
                                        .clickable(
                                            interactionSource = interaction,
                                            indication = androidx.compose.foundation.LocalIndication.current,
                                            enabled = (cell == null && winInfo == null)
                                        ) {
                                            if (board[idx] == null && winInfo == null) {
                                                // применяем ход
                                                board = board.toMutableList().also { it[idx] = currentPlayer }
                                                // проверяем победу
                                                winInfo = checkWinnerFlat(board)
                                                // если игра не окончена — меняем игрока
                                                if (winInfo == null && !isBoardFull()) {
                                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
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

                // Линия победы (с мягким блюром и небольшой анимацией)
                winInfo?.let { info ->
                    val winLineColor = MaterialTheme.colorScheme.primary.toArgb()
                    val progress by animateFloatAsState(targetValue = 1f, label = "win-line")

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val side = size.minDimension
                        val cellSide = side / 3f
                        val stroke = side / 15f

                        val paint = Paint().apply {
                            color = winLineColor
                            isAntiAlias = true
                            style = Paint.Style.STROKE
                            strokeWidth = stroke
                            maskFilter = BlurMaskFilter(stroke, BlurMaskFilter.Blur.NORMAL)
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
                            canvas.nativeCanvas.drawLine(start.x, start.y, animatedEnd.x, animatedEnd.y, paint)
                        }
                    }
                }
            }
        }
    }
}


private fun checkWinner(board: List<List<String?>>): WinInfo? {
    for (i in 0..2) {
        if (board[i][0] != null && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
            return WinInfo(WinType.ROW, i)
        }
        if (board[0][i] != null && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
            return WinInfo(WinType.COLUMN, i)
        }
    }
    if (board[0][0] != null && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
        return WinInfo(WinType.DIAGONAL_MAIN)
    }
    if (board[0][2] != null && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
        return WinInfo(WinType.DIAGONAL_ANTI)
    }
    return null
}

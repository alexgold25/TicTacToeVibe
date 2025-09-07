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
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class WinType { ROW, COLUMN, DIAGONAL_MAIN, DIAGONAL_ANTI }
data class WinInfo(val type: WinType, val index: Int = 0)

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { null as String? } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winInfo by remember { mutableStateOf<WinInfo?>(null) }
    var hovered by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = minOf(maxWidth, maxHeight)
        Box(modifier = Modifier.size(boardSize)) {
            Column(Modifier.fillMaxSize()) {
                for (r in 0..2) {
                    Row(Modifier.weight(1f)) {
                        for (c in 0..2) {
                            val cell = board[r][c]
                            val isHovered = hovered == (r to c)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.primary)
                                    .background(
                                        if (isHovered) MaterialTheme.colorScheme.surfaceVariant
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .onPointerEvent(PointerEventType.Enter) {
                                        hovered = r to c
                                    }
                                    .onPointerEvent(PointerEventType.Exit) {
                                        hovered = null
                                    }
                                    .clickable(enabled = cell == null && winInfo == null) {
                                        board = board.mapIndexed { row, cols ->
                                            cols.mapIndexed { col, value ->
                                                if (row == r && col == c) currentPlayer else value
                                            }.toMutableList()
                                        }
                                        winInfo = checkWinner(board)
                                        if (winInfo == null) {
                                            currentPlayer = if (currentPlayer == "X") "O" else "X"
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

            winInfo?.let { info ->
                val winLineColor = MaterialTheme.colorScheme.primary.toArgb()
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = size.minDimension / 15f
                    val paint = Paint().apply {
                        color = winLineColor
                        isAntiAlias = true
                        style = Paint.Style.STROKE
                        strokeWidth = stroke
                        maskFilter = BlurMaskFilter(stroke, BlurMaskFilter.Blur.NORMAL)
                    }
                    val cell = size.minDimension / 3f
                    val (start, end) = when (info.type) {
                        WinType.ROW -> {
                            val y = cell * info.index + cell / 2f
                            Offset(0f, y) to Offset(size.minDimension, y)
                        }
                        WinType.COLUMN -> {
                            val x = cell * info.index + cell / 2f
                            Offset(x, 0f) to Offset(x, size.minDimension)
                        }
                        WinType.DIAGONAL_MAIN -> {
                            Offset(0f, 0f) to Offset(size.minDimension, size.minDimension)
                        }
                        WinType.DIAGONAL_ANTI -> {
                            Offset(0f, size.minDimension) to Offset(size.minDimension, 0f)
                        }
                    }
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawLine(start.x, start.y, end.x, end.y, paint)
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

package com.alexgold25.tictactoevibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alexgold25.tictactoevibe.ui.GameScreen
import com.alexgold25.tictactoevibe.ui.theme.TicTacToeVibeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeVibeTheme {
                GameScreen()
            }
        }
    }
}

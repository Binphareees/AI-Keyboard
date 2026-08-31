package com.aistudio.aikeyboard.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aistudio.aikeyboard.AIKeyboardApp
import com.aistudio.aikeyboard.ui.theme.AIKeyboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AIKeyboardApp

        setContent {
            AIKeyboardTheme(darkTheme = true) {
                MainScreen(app = app)
            }
        }
    }
}

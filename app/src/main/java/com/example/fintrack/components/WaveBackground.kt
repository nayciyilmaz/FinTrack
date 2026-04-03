package com.example.fintrack.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val wave1 = Path().apply {
            moveTo(0f, h * 0.80f)
            cubicTo(w * 0.25f, h * 0.70f, w * 0.5f, h * 0.90f, w * 0.75f, h * 0.77f)
            cubicTo(w * 0.88f, h * 0.70f, w * 0.92f, h * 0.75f, w, h * 0.73f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        val wave2 = Path().apply {
            moveTo(0f, h * 0.85f)
            cubicTo(w * 0.25f, h * 0.76f, w * 0.5f, h * 0.93f, w * 0.75f, h * 0.82f)
            cubicTo(w * 0.88f, h * 0.76f, w * 0.92f, h * 0.80f, w, h * 0.79f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        val wave3 = Path().apply {
            moveTo(0f, h * 0.90f)
            cubicTo(w * 0.3f, h * 0.83f, w * 0.6f, h * 0.96f, w * 0.8f, h * 0.87f)
            cubicTo(w * 0.9f, h * 0.83f, w * 0.95f, h * 0.86f, w, h * 0.85f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(wave1, color = Color(0xFFFF9E40).copy(alpha = 0.3f))
        drawPath(wave2, color = Color(0xFFFF9E40).copy(alpha = 0.5f))
        drawPath(wave3, color = Color(0xFFFF9E40).copy(alpha = 0.7f))
    }
}
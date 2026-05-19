package com.aceshot.musicplayer.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    onSeek: ((Float) -> Unit)? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val amplitude by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.3f,
        animationSpec = tween(500),
        label = "amplitude"
    )

    val seekModifier = if (onSeek != null) {
        modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onSeek((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    onSeek((change.position.x / size.width).coerceIn(0f, 1f))
                }
            }
    } else {
        modifier
    }

    Canvas(
        modifier = seekModifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val progressX = width * progress
        val step = 2f
        val frequency = 4f * Math.PI.toFloat()
        val waveAmplitude = height * 0.25f * amplitude
        val currentPhase = if (isPlaying) phase else 0f

        // Build one single continuous waveform path
        val wavePath = Path()
        wavePath.moveTo(0f, centerY)
        var x = 0f
        while (x <= width) {
            val normalizedX = x / width
            val wave = sin(normalizedX * frequency + currentPhase) * waveAmplitude
            wavePath.lineTo(x, centerY + wave)
            x += step
        }

        val strokeWidth = 3.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Draw inactive portion (full track, behind)
        clipRect(left = progressX, right = width) {
            drawPath(path = wavePath, color = trackColor, style = stroke)
        }

        // Draw active portion (played, clipped to progress)
        if (progressX > 0f) {
            clipRect(right = progressX) {
                drawPath(path = wavePath, color = primaryColor, style = stroke)
            }
        }

        // Thumb dot at current progress position
        val thumbWave = sin(progress * frequency + currentPhase) * waveAmplitude
        drawCircle(
            color = primaryColor,
            radius = 5.dp.toPx(),
            center = Offset(progressX, centerY + thumbWave)
        )
    }
}

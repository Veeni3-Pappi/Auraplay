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
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 2f * Math.PI.toFloat() else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val amplitude by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.2f,
        animationSpec = tween(400),
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
        val step = 3f
        val frequency = 6f * Math.PI.toFloat()

        // Background waveform (inactive part after progress)
        val bgPath = Path()
        bgPath.moveTo(progressX, centerY)
        var x = progressX
        while (x <= width) {
            val normalizedX = x / width
            val waveAmp = height * 0.3f * amplitude * 0.4f
            val wave = sin(normalizedX * frequency + phase * 0.3f) * waveAmp
            bgPath.lineTo(x, centerY + wave)
            x += step
        }
        drawPath(
            path = bgPath,
            color = surfaceVariant,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Active waveform (from 0 to progress)
        if (progressX > 0f) {
            val activePath = Path()
            activePath.moveTo(0f, centerY)
            x = 0f
            while (x <= progressX) {
                val normalizedX = x / width
                val waveAmp = height * 0.3f * amplitude
                val wave = sin(normalizedX * frequency + phase) * waveAmp
                activePath.lineTo(x, centerY + wave)
                x += step
            }
            drawPath(
                path = activePath,
                color = primaryColor,
                style = Stroke(width = 3.5f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Thumb dot on the waveform at current position
        val thumbWave = sin((progress) * frequency + phase) * (height * 0.3f * amplitude)
        drawCircle(
            color = primaryColor,
            radius = 6.dp.toPx(),
            center = Offset(progressX, centerY + thumbWave)
        )
    }
}

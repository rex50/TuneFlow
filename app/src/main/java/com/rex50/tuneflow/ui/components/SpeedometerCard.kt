package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.model.VolumeSettings

@Composable
fun SpeedometerCard(
    volumeSettings: VolumeSettings,
    serviceState: ServiceState
) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.speedometer_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            val minAcc = volumeSettings.minAcceleration
            val maxAcc = volumeSettings.maxAcceleration
            val normalized by remember {
                derivedStateOf {
                    ((serviceState.acceleration - minAcc) / (maxAcc - minAcc)).coerceIn(0f, 1f)
                }
            }
            Gauge(
                fraction = normalized,
                size = 200.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Acceleration: %.2f m/s²".format(serviceState.acceleration))
            Text("Volume: ${serviceState.volume}")
        }
    }
}

@Composable
private fun Gauge(
    fraction: Float,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val foregroundColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.then(Modifier.size(size))) {
        val strokeWidth = size.toPx() * 0.08f
        val radius = size.toPx() / 2 - strokeWidth
        val startAngle = 180f
        val sweepAngle = 180f

        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = foregroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle * fraction,
            useCenter = false,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

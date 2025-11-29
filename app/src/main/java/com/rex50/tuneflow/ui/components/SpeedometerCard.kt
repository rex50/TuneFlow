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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.SpeedUnit
import kotlin.math.min

/**
 * Displays a speedometer-style card showing current speed.
 *
 * The card includes:
 * - A semi-circular gauge showing speed relative to max speed
 * - Current speed value in selected unit (km/h, mph, or m/s)
 *
 * @param currentSpeed Current speed in meters per second
 * @param maxSpeed Maximum speed threshold in meters per second
 * @param speedUnit Unit to display speed in (KMH, MPH, or MPS)
 */
@Composable
fun SpeedometerCard(
    currentSpeed: Float,
    maxSpeed: Float,
    speedUnit: SpeedUnit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
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

            val normalized = (currentSpeed / maxSpeed).coerceIn(0f, 1f)
            Gauge(
                fraction = normalized,
                size = 200.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Display speed in selected unit
            val displayValue = speedUnit.convertFromMps(currentSpeed)
            Text(
                text = "%.1f %s".format(displayValue, speedUnit.getLabel()),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Draws a semi-circular gauge (speedometer-style) to visualize a fractional value.
 *
 * The gauge uses a half-height canvas (width:height = 2:1) to efficiently render
 * only the bottom semicircle needed for the speedometer visualization.
 *
 * Drawing approach:
 * 1. Canvas is sized as width=size, height=size/2
 * 2. Arc bounding box is a full circle positioned so only the bottom half is visible
 * 3. Arc starts at 180° (left) and sweeps 180° clockwise to 360° (right)
 * 4. The fraction parameter controls how much of the arc is filled with foreground color
 *
 * @param fraction Value between 0.0 and 1.0 representing the filled portion of the gauge
 * @param size Width of the gauge (height will be size/2)
 * @param modifier Optional modifier for the gauge
 */
@Composable
private fun Gauge(
    fraction: Float,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val foregroundColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.then(Modifier.size(width = size, height = size / 2))) {
        // Get actual pixel dimensions from the Canvas draw scope
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height

        // Calculate stroke width as 8% of canvas width for proportional scaling
        val strokeWidth = canvasWidth * 0.08f

        // Arc diameter must fit within the half-height canvas
        // Since we're drawing a semicircle, diameter = 2 * canvasHeight
        // Subtract strokeWidth to prevent clipping at edges
        val arcDiameter = (min(canvasWidth, canvasHeight * 2f) - strokeWidth).coerceAtLeast(0f)

        // Position the circular arc's bounding box:
        // - Horizontally centered in the canvas
        // - Vertically positioned so the top of the circle is at strokeWidth/2 from top
        // This ensures the bottom semicircle (180° to 360°) fits perfectly in the canvas
        val arcTopLeft = Offset(
            (canvasWidth - arcDiameter) / 2f,  // Center horizontally
            strokeWidth / 2f                    // Offset from top to prevent stroke clipping
        )
        val arcSize = Size(arcDiameter, arcDiameter)

        // Arc angles in Compose Canvas coordinate system:
        // - 0° = right (3 o'clock)
        // - 90° = bottom (6 o'clock)
        // - 180° = left (9 o'clock)
        // - 270° = top (12 o'clock)
        // We start at 180° (left) and sweep 180° clockwise to draw the bottom semicircle
        val startAngle = 180f
        val sweepAngle = 180f

        // Draw background arc (full semicircle)
        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,  // Don't draw lines to center (creates open arc, not pie slice)
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Draw foreground arc (progress indicator)
        // Multiply sweep by fraction to show progress from 0% to 100%
        drawArc(
            color = foregroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle * fraction,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

/**
 * Preview for SpeedometerCard composable.
 */
@Preview(showBackground = true)
@Composable
fun SpeedometerCardPreview() {
    SpeedometerCard(
        currentSpeed = 5f,
        maxSpeed = 10f,
        speedUnit = SpeedUnit.KILOMETERS_PER_HOUR
    )
}

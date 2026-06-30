package com.example.learningprojects.ui.theme.commonusablecomponent

import android.graphics.PathMeasure
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.ui.theme.enumclass.GraphType
import com.example.learningprojects.ui.theme.homescreen.AnalyticsPoint
import kotlin.Int
import kotlin.math.roundToInt

@Composable
fun CommonText(
    text: String,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    color: Color = Color(0xFFFFFFFF),
    modifier: Modifier,
    isSingleLine : Boolean = false
) {

    Text(
        text = text,
        maxLines = if (isSingleLine) 1 else Int.MAX_VALUE,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            color = color,
        ),
        modifier = modifier,
    )
}


@Composable
fun ReusableAnalyticsGraph(
    points: List<AnalyticsPoint>,
    graphType: GraphType,
    progress: Float,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    // Setup visual themes based on configuration
    val primaryColor = when (graphType) {
        GraphType.BATTERY -> Color(0xFF00BFA5) // Teal/Green
        GraphType.RAM -> Color(0xFF6200EE)     // Deep Purple
        GraphType.NETWORK -> Color(0xFF29B6F6) // Blue
    }
    val secondaryColor = Color(0xFFFF9800) // Orange (for upload line if network)

    // Track user clicks to calculate interaction popup overlay data
    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {

        // Dynamic Header Indicator Box showing selection results
        if (selectedIndex != null && selectedIndex!! < points.size) {
            val pt = points[selectedIndex!!]
            val detailText = when (graphType) {
                GraphType.BATTERY -> "${pt.time} -> Battery: ${pt.primaryValue.toInt()}%"
                GraphType.RAM -> "${pt.time} -> RAM Usage: ${pt.primaryValue.toInt()}%"
                GraphType.NETWORK -> "${pt.time} -> DL: ${pt.primaryValue.toInt()} Mbps | UL: ${pt.secondaryValue?.toInt()} Mbps"
            }
            Text(
                text = detailText,
                fontSize = 14.sp,
                color = primaryColor,
                modifier = Modifier.padding(start = 45.dp, bottom = 8.dp)
            )
        } else {
            Text(
                text = "Tap graph to inspect values",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 45.dp, bottom = 8.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Y-Axis Display
            Column(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("100", "75", "50", "25", "0").forEach {
                    Text(text = it, fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Graph Canvas Surface
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp)
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            // Map horizontal touch coordinate back to nearest data point array index
                            val sliceWidth = size.width / (points.size - 1)
                            val rawIndex = (offset.x / sliceWidth).roundToInt()
                            selectedIndex = rawIndex.coerceIn(0, points.size - 1)
                        }
                    }
            ) {
                val graphWidth = size.width
                val graphHeight = size.height

                // Helper to plot continuous Bezier curves
                fun getCurvePath(extractSecondary: Boolean): Path {
                    val path = Path()
                    points.forEachIndexed { idx, pt ->
                        val value =
                            if (extractSecondary) (pt.secondaryValue ?: 0f) else pt.primaryValue
                        val x = (idx.toFloat() / (points.size - 1)) * graphWidth
                        val normalizedY = value / 100f
                        val y = graphHeight - (normalizedY * graphHeight)

                        if (idx == 0) {
                            path.moveTo(x, y)
                        } else {
                            val prevValue = if (extractSecondary) (points[idx - 1].secondaryValue
                                ?: 0f) else points[idx - 1].primaryValue
                            val prevX = ((idx - 1).toFloat() / (points.size - 1)) * graphWidth
                            val prevY = graphHeight - ((prevValue / 100f) * graphHeight)
                            val ctrlX = prevX + (x - prevX) / 2f
                            path.cubicTo(ctrlX, prevY, ctrlX, y, x, y)
                        }
                    }
                    return path
                }

                // --- LINE 1 PROCESSING (Primary) ---
                val primaryLinePath = Path()
                val primaryMeasure =
                    android.graphics.PathMeasure(getCurvePath(false).asAndroidPath(), false)
                primaryMeasure.getSegment(
                    0f,
                    primaryMeasure.length * progress,
                    primaryLinePath.asAndroidPath(),
                    true
                )

                val posOut = FloatArray(2)
                primaryMeasure.getPosTan(primaryMeasure.length * progress, posOut, null)
                val currentX = posOut[0]

                // Construct closed layout under curve 1 for gradient masking
                val primaryFillPath = Path().apply {
                    addPath(primaryLinePath)
                    lineTo(currentX, graphHeight)
                    lineTo(0f, graphHeight)
                    close()
                }

                drawPath(
                    path = primaryFillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
                drawPath(
                    path = primaryLinePath,
                    color = primaryColor,
                    style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // --- LINE 2 PROCESSING (Secondary for Uploads if Network Mode) ---
                if (graphType == GraphType.NETWORK) {
                    val secondaryLinePath = Path()
                    val secondaryMeasure = PathMeasure(getCurvePath(true).asAndroidPath(), false)
                    secondaryMeasure.getSegment(
                        0f,
                        secondaryMeasure.length * progress,
                        secondaryLinePath.asAndroidPath(),
                        true
                    )

                    drawPath(
                        path = secondaryLinePath,
                        color = secondaryColor,
                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // --- INTERACTION INDICATOR PIN ---
                // Draws a vertical reference indicator line through the selected touch point
                selectedIndex?.let { index ->
                    if (index < points.size) {
                        val selectedX = (index.toFloat() / (points.size - 1)) * graphWidth

                        // Draw vertical helper line
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.4f),
                            start = Offset(selectedX, 0f),
                            end = Offset(selectedX, graphHeight),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Highlight Node Point Dot
                        val selY = graphHeight - ((points[index].primaryValue / 100f) * graphHeight)
                        drawCircle(
                            color = primaryColor,
                            radius = 6.dp.toPx(),
                            center = Offset(selectedX, selY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(selectedX, selY)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // X-Axis Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach {
                Text(text = it.time, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
@Composable
fun BlinkingDot(
    color: Color = Color.Green,
    size: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(size)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

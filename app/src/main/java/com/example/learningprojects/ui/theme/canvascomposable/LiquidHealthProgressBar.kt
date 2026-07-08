package com.example.learningprojects.ui.theme.canvascomposable

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.lightGrayHome
import com.example.learningprojects.ui.theme.progressCircularDark
import com.example.learningprojects.ui.theme.unFilteredColor
import kotlin.math.sin


@Composable
fun LiquidHealthProgressBar(
    healthPercentage: Float, // Value between 0f and 100f
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    liquidColor: Color = Color(0xFF2ECC71), // Vibrant health green
    emptyColor: Color = Color(0xFFBDC3C7).copy(alpha = 0.4f) // Gray with opacity
) {
    // Animate the health percentage for a smooth entrance/transition
    val animatedPercentage by animateFloatAsState(
        targetValue = healthPercentage.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "HealthProgress"
    )

    // Infinite transition to drive the liquid wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveOffset"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = size.toPx()
            val height = size.toPx()
            val radius = width / 2f

            // 1. Create a circular path to clip everything inside a circle

            val circlePath = Path().apply {
                addOval(
                    Rect(
                        center = Offset(radius, radius),
                        radius = radius
                    )
                )
            }

            // Clip the drawing scope to our circle so waves don't leak out
            clipPath(circlePath) {

                // 2. Draw the background/rest of the bar (Gray Opacity)
                drawRect(
                    color = emptyColor,
                    size = Size(width, height)
                )

                // 3. Calculate liquid level height (0% = bottom/height, 100% = top/0)
                val factor = animatedPercentage / 100f
                val liquidY = height * (1f - factor)

                // 4. Build the animated wave path
                val wavePath = Path().apply {
                    val waveAmplitude = 15f // Height of the wave crest
                    val waveFrequency = 0.05f // How bunched up the waves are

                    moveTo(0f, height) // Start at bottom left

                    // Trace the wave across the X axis
                    for (x in 0..width.toInt()) {
                        // Sine wave math: y = amplitude * sin(frequency * x + offset) + baseline_y
                        val y = waveAmplitude * sin(waveFrequency * x + waveOffset) + liquidY
                        lineTo(x.toFloat(), y)
                    }

                    lineTo(width, height) // Down to bottom right
                    close() // Close path back to bottom left
                }

                // 5. Draw the animated liquid
                drawPath(
                    path = wavePath,
                    color = liquidColor
                )
            }
        }

        // 6. Center Text Overlay showing the percentage
        Text(
            text = "${healthPercentage.toInt()}%",
            color = if (healthPercentage > 40f) Color.White else Color.DarkGray,
            fontSize = (size.value * 0.18f).sp, // Scaled dynamically with size
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun CircularHealthMeter(
    healthValue: Int = 82, // Standard value from image
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    startAnimation: Boolean,
    strokeWidth: Dp = 15.dp,
    filledColor: Color = MaterialTheme.colorScheme.secondary, // Vibrant Green
    unfilledColor: Color = unFilteredColor, // Dark gray, partially transparent
    backgroundColor: Color = Color(0xFF0A1931), // Deep Navy Blue
    textColorMain: Color = MaterialTheme.colorScheme.secondary, // Same vibrant Green
    textColorSub: Color = lightGrayHome // Light Gray
) {
    val animatedProgress by animateFloatAsState(
        targetValue =
            if (startAnimation)
                healthValue / 100f
            else
                0f,
        animationSpec = tween(1500,
            easing = LinearOutSlowInEasing),
        label = ""
    )

//    LaunchedEffect(Unit) {
//        startAnimation = true
//    }
//    val animatedProgress by animateFloatAsState(
//        targetValue = if (startAnimation) healthValue / 100f else 0f,
//        animationSpec = tween(
//            durationMillis = 1500,
//            easing = LinearOutSlowInEasing
//        ),
//        label = ""
//    )
    val animatedValue by animateIntAsState(
        targetValue = if (startAnimation) healthValue else 0,
        animationSpec = tween(
            durationMillis = 1500,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
//        val sweepAngle = (healthValue / 100f) * 360f
        val sweepAngle = animatedProgress * 360f

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val innerSize = size.toPx() - strokeWidth.toPx()
            val radius = innerSize / 2f
            val centerOffset = Offset(size.toPx() / 2f, size.toPx() / 2f)

            // Draw the unfilled background circle
            drawCircle(
                color = unfilledColor,
                radius = radius,
                center = centerOffset,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Draw the filled progress arc
            drawArc(
                color = filledColor,
                startAngle = -90f, // Start from the top
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                size = Size(innerSize, innerSize),
                topLeft = Offset(strokeWidth.toPx() / 2f, strokeWidth.toPx() / 2f)
            )
        }

        // Text elements in the center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = animatedValue.toString(),
                style = TextStyle(
                    fontSize = 40.sp,
                    fontFamily = FontFamily(Font(R.font.google_sans_bold)),
                    fontWeight = FontWeight.Bold,
                    color = textColorMain,
                    lineHeight = 40.sp
                ),

                )
            Text(
                text = "HEALTH",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.semi_bold)),
                    fontWeight = FontWeight.W600,
                    color = textColorSub,
                    lineHeight = 13.sp,
                    letterSpacing = 0.4.sp
                ),
                modifier = modifier.padding(top = 0.dp)
            )
        }
    }
}
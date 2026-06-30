package com.example.learningprojects.ui.theme.canvascomposable

import android.content.Context
import android.graphics.BlurMaskFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.ui.theme.homescreen.GraphPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CanvasActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sensorManager =
                getSystemService(Context.SENSOR_SERVICE) as SensorManager

            val sensors =
                sensorManager.getSensorList(
                    Sensor.TYPE_ALL
                )

            sensors.forEach {
                Log.d(
                    "Sensor",
                    "${it.name} - ${it.type}"
                )
            }

//            CanvasLearning()
//            SpeedometerDemo()
//            HealthProgressDemo()
            HealthMeterPreview()

        }
    }
}


@Composable
fun BatteryGraphCanvas(
    points: List<GraphPoint>,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (points.isEmpty()) return@Canvas

        val graphWidth = size.width
        val graphHeight = size.height

        // 1. Generate the base full path using your data bounds
        val completeLinePath = createGraphPath(points, graphWidth, graphHeight)

        // 2. Use PathMeasure to extract the animated segment (Left -> Right drawing effect)
        val animatedLinePath = Path()
        val pathMeasure = android.graphics.PathMeasure(completeLinePath.asAndroidPath(), false)
        val length = pathMeasure.length

        pathMeasure.getSegment(0f, length * progress, animatedLinePath.asAndroidPath(), true)

        // Get the final drawn point coordinate to anchor our fill path correctly
        val pos = FloatArray(2)
        pathMeasure.getPosTan(length * progress, pos, null)
        val currentX = pos[0]

        // 3. Construct the closing fill path bound to the animated timeline
        val fillPath = Path().apply {
            addPath(animatedLinePath)
            lineTo(currentX, graphHeight) // Drop down straight from where the line currently ends
            lineTo(0f, graphHeight)       // Back to the origin corner
            close()
        }

        // 4. Draw the fill (Primary Color fade)
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4CAF50).copy(alpha = 0.40f),
                    Color(0xFF4CAF50).copy(alpha = 0.00f)
                )
            )
        )

        // 5. Draw the primary line stroke
        drawPath(
            path = animatedLinePath,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun BatteryGraph(
    points: List<GraphPoint>,
    progress: Float,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 0.dp)
    ) {
        if (points.isEmpty()) return@Canvas
        val graphWidth = size.width
        val graphHeight = size.height
        print("$graphHeight")
        Log.d("height", "BatteryGraph: $graphHeight")
        Log.d("height", "BatteryGraph: $graphWidth")


        val linePath = createGraphPath(
            points = points,
            graphWidth = graphWidth,
            graphHeight = graphHeight,
//            progress = progress
        )

        val fillPath = createFillPath(
            linePath,
            graphWidth,
            graphHeight
        )

        val maxVoltage = 10.2f
        val minVoltage = 0.0f

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Green.copy(alpha = 0.35f),
                    Color.Green.copy(alpha = 0.05f)
                )
            )
        )
        drawPath(
            path = linePath,
            color = Color(0xFF4CAF50),
            style = Stroke(
                width = 6f
            )
        )
    }
}


@Composable
private fun graphAnimationProgress(): Float {
    return animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "graph_animation"
    ).value
}

private fun createGraphPath(
    points: List<GraphPoint>,
    graphWidth: Float,
    graphHeight: Float
): Path {
    val maxVoltage = 100f // Explicitly locked to match your 100% maximum bounds
    val minVoltage = 0f

    val path = Path()

    points.forEachIndexed { index, point ->
        val x = (index.toFloat() / (points.size - 1)) * graphWidth
        val normalized = (point.voltage - minVoltage) / (maxVoltage - minVoltage)
        val y = graphHeight - (normalized * graphHeight)

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            val prevX = ((index - 1).toFloat() / (points.size - 1)) * graphWidth
            val prevPoint = points[index - 1]
            val prevNormalized = (prevPoint.voltage - minVoltage) / (maxVoltage - minVoltage)
            val prevY = graphHeight - (prevNormalized * graphHeight)

            // Clean cubic/quadratic curve transition mapping
            val controlX1 = prevX + (x - prevX) / 2f
            path.cubicTo(
                controlX1, prevY,
                controlX1, y,
                x, y
            )
        }
    }
    return path
}

private fun createGraphPath1(
    points: List<GraphPoint>,
    graphWidth: Float,
    graphHeight: Float,
    progress: Float
): Path {

    val maxValue = points.maxOf { it.voltage }
    val minValue = points.minOf { it.voltage }

    val path = Path()

    points.forEachIndexed { index, point ->

        val x =
            (index.toFloat() / (points.size - 1)) * graphWidth

        val normalized =
            (point.voltage - minValue) /
                    (maxValue - minValue)

        val finalY =
            graphHeight - (normalized * graphHeight)
//
//        // animate from bottom to actual value
//        val y =
//            graphHeight -
//                    ((graphHeight - finalY) * progress)
        val waveOffset =
            sin((progress * 8f + index) * Math.PI).toFloat() * 20f

        val y =
            finalY + waveOffset * (1f - progress)

        if (index == 0) {
            path.moveTo(x, y)
        } else {

            val prevPoint = points[index - 1]

            val prevNormalized =
                (prevPoint.voltage - minValue) /
                        (maxValue - minValue)

            val prevFinalY =
                graphHeight - (prevNormalized * graphHeight)

            val prevY =
                graphHeight -
                        ((graphHeight - prevFinalY) * progress)

            val prevX =
                ((index - 1).toFloat() / (points.size - 1)) * graphWidth

            val midX = (prevX + x) / 2f

            path.quadraticTo(
                prevX,
                prevY,
                midX,
                (prevY + y) / 2
            )
        }
    }

    return path
}

private fun createFillPath(
    linePath: Path,
    graphWidth: Float,
    graphHeight: Float
): Path {

    return Path().apply {

        addPath(linePath)

        lineTo(graphWidth, graphHeight)

        lineTo(0f, graphHeight)

        close()
    }
}

@Composable
fun BatteryYAxis() {

    Column(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(
            "100%",
            "80%",
            "60%",
            "40%",
            "20%",
            "0%"
        ).forEach {

            Text(
                text = it,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BatteryXAxis(
    points: List<GraphPoint>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        points.forEach {

            Text(
                text = it.time,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BatteryChartScreen(
    points: List<GraphPoint>,
    progress: Float
) {

    Column(modifier = Modifier.padding(vertical = 10.dp))
    {
        Row {
            BatteryYAxis()
            BatteryGraphCanvas(
                points = points,
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 10.dp)
            )
        }
        BatteryXAxis(points) // Retain your original XAxis component here
    }

//    Column(
//        modifier = Modifier
//            .padding(vertical = 10.dp)
//    ) {
//
//        Row {
//
//            BatteryYAxis()
//
//            BatteryGraph(
//                points = points,
//                progress = progress,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(bottom = 10.dp)
//            )
//        }
//
//        BatteryXAxis(points)
//    }
}

@Composable
fun GradientCircularProgress(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier
        .background(Color.Cyan)
) {
    Canvas(
        modifier = modifier.size(220.dp)
    ) {

        val strokeWidth = 28f
        val sweepAngle = progress * 360f

        // Unfilled Track
        drawArc(
            color = Color.Gray,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // Gradient Progress
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF00FF88),
                    Color(0xFF00E676),
                    Color(0xFF00C853)
                )
            ),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CanvasLearning() {
    val context = LocalContext.current

    val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display?.refreshRate ?: 0f
    } else {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
            .refreshRate
    }
    Log.d("Display", "Refresh Rate = $refreshRate Hz")


    LaunchedEffect(Unit) {

        val connectivityManager =
            context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        val wifiInfo =
            capabilities?.transportInfo as? WifiInfo

        Log.d("Wifi", "SSID = ${wifiInfo?.ssid}")
        Log.d("Wifi", "Frequency = ${wifiInfo?.frequency}")
        Log.d("Wifi", "RSSI = ${wifiInfo?.rssi}")
        Log.d("Wifi", "RX = ${wifiInfo?.rxLinkSpeedMbps}")
        Log.d("Wifi", "TX = ${wifiInfo?.txLinkSpeedMbps}")
        Log.d("Wifi", "Link = ${wifiInfo?.linkSpeed}")
    }


    val model = Build.MODEL
    val manufacturer = Build.MANUFACTURER
    val deviceName = "$manufacturer $model"


//    GradientCircularProgress(
//        progress = 0.72f
//    )

//    BatteryChartScreen(
//        points = listOf(
//            GraphPoint("10:00", 100f),
//            GraphPoint("11:00", 80f),
//            GraphPoint("12:00", 60f),
//            GraphPoint("13:00", 90f),
//            GraphPoint("14:00", 30f)
//        )
//    )
//    BatteryGraph(
//        points = listOf(
//            GraphPoint("10:00", 10.2f),
//            GraphPoint("11:00", 8.1f),
//            GraphPoint("12:00", 3.9f),
//            GraphPoint("13:00", 9.8f),
//            GraphPoint("14:00", 1.6f)
//        )
//    )
}


@Composable
fun SpeedometerGauge(
    speed: Float,
    modifier: Modifier = Modifier,
    maxSpeed: Float = 200f
) {

    val animatedSpeed by animateFloatAsState(
        targetValue = speed.coerceIn(0f, maxSpeed),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    val currentColor by animateColorAsState(
        targetValue = when {
            animatedSpeed < 50f -> Color(0xFF00C853)
            animatedSpeed < 100f -> Color(0xFFFFD600)
            animatedSpeed < 150f -> Color(0xFFFF9100)
            else -> Color(0xFFFF3D00)
        },
        label = ""
    )


    val greenGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00E676),
            Color(0xFF69F0AE)
        )
    )

    val yellowGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD600),
            Color(0xFFFFFF8D)
        )
    )

    val orangeGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF9100),
            Color(0xFFFFB74D)
        )
    )

    val redGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF1744),
            Color(0xFFFF8A80)
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val strokeWidth = 36f

            val startAngle = 135f
            val totalSweep = 270f

            val segmentSweep = totalSweep / 4f

            val radius = size.minDimension / 2.4f

            // Progress calculation

            val greenSweep =
                (animatedSpeed.coerceIn(0f, 50f) / 50f) * segmentSweep

            val yellowSweep =
                ((animatedSpeed - 50f)
                    .coerceIn(0f, 50f) / 50f) * segmentSweep

            val orangeSweep =
                ((animatedSpeed - 100f)
                    .coerceIn(0f, 50f) / 50f) * segmentSweep

            val redSweep =
                ((animatedSpeed - 150f)
                    .coerceIn(0f, 50f) / 50f) * segmentSweep

            // Background Arc

            drawArc(
                color = Color(0xFFFFFF),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                style = Stroke(
                    width = 24f,
                    cap = StrokeCap.Round
                )
            )
            // Green

            if (greenSweep > 0f) {
                drawArc(
                    brush = greenGradient,
                    startAngle = startAngle,
                    sweepAngle = greenSweep,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Yellow

            if (yellowSweep > 0f) {
                drawArc(
                    brush = yellowGradient,
                    startAngle = startAngle + segmentSweep,
                    sweepAngle = yellowSweep,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth
                    )
                )
            }

            // Orange

            if (orangeSweep > 0f) {
                drawArc(
                    brush = orangeGradient,
                    startAngle = startAngle + segmentSweep * 2,
                    sweepAngle = orangeSweep,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth
                    )
                )
            }

            // Red

            if (redSweep > 0f) {
                drawArc(
                    brush = redGradient,
                    startAngle = startAngle + segmentSweep * 3,
                    sweepAngle = redSweep,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Tick Marks

            repeat(41) { i ->

                val angle =
                    startAngle + (totalSweep / 40f) * i

                val angleRad =
                    Math.toRadians(angle.toDouble())

                val outerRadius = radius + 18f

                val innerRadius =
                    if (i % 5 == 0)
                        radius - 12f
                    else
                        radius - 2f

                val startX =
                    center.x +
                            cos(angleRad).toFloat() * outerRadius

                val startY =
                    center.y +
                            sin(angleRad).toFloat() * outerRadius

                val endX =
                    center.x +
                            cos(angleRad).toFloat() * innerRadius

                val endY =
                    center.y +
                            sin(angleRad).toFloat() * innerRadius

                drawLine(
                    color = Color.White.copy(
                        alpha = if (i % 5 == 0) 0.8f else 0.35f
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth =
                        if (i % 5 == 0) 5f else 2f
                )
            }

            // Needle

            val progress =
                animatedSpeed / maxSpeed

            val needleAngle =
                startAngle + totalSweep * progress

            val needleRad =
                Math.toRadians(
                    needleAngle.toDouble()
                )

            val needleLength =
                radius - 40f

            val needleX =
                center.x +
                        cos(needleRad).toFloat() * needleLength

            val needleY =
                center.y +
                        sin(needleRad).toFloat() * needleLength

            drawLine(
                color = currentColor,
                start = center,
                end = Offset(
                    needleX,
                    needleY
                ),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )

            // Center Pin

            drawCircle(
                color = Color.White,
                radius = 20f,
                center = center
            )

            drawCircle(
                color = currentColor,
                radius = 12f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = animatedSpeed.toInt().toString(),
                color = currentColor,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Mbps",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun InternetSpeedometerScreen2() {
    var targetSpeed by remember { mutableStateOf(0f) }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Smooth animation with slightly longer duration for dramatic motion blur/shadow feel
    val animatedSpeed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = tween(durationMillis = 1500),
        label = "SpeedAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1626)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .background(Color(0xFF182239), shape = RoundedCornerShape(20.dp))
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Premium Speed Test",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 36f
                        val radius = size.minDimension / 2 - strokeWidth - 20f
                        val center = Offset(size.width / 2, size.height / 2 + 30f)
                        val arcSize = Size(radius * 2, radius * 2)
                        val topLeftOffset = Offset(center.x - radius, center.y - radius)

                        // 1. STATIC BACKGROUND TRACK (Grey Underlay)
                        drawArc(
                            color = Color(0xFF1F2A45),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize,
                            topLeft = topLeftOffset
                        )

                        // --- 3-LAYER CATEGORY PROGRESS ARC CALCULATIONS ---
                        val maxSpeed = 100f
                        val totalSweep = (animatedSpeed / maxSpeed) * 180f

                        // Layer 1: Average/Low (0 to 30 Mbps) -> Maps to 0° to 54° of sweep
                        val lowMaxSweep = (30f / maxSpeed) * 180f // 54 degrees
                        val lowSweep = if (totalSweep > lowMaxSweep) lowMaxSweep else totalSweep
                        if (lowSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF416C),
                                        Color(0xFFFF4B2B)
                                    )
                                ),
                                startAngle = 180f,
                                sweepAngle = lowSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= lowMaxSweep) StrokeCap.Round else StrokeCap.Butt
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 2: Good/Average (30 to 70 Mbps) -> Maps to next 72° of sweep
                        val midMaxSweep = (40f / maxSpeed) * 180f // 72 degrees
                        val midSweep = if (totalSweep > lowMaxSweep) {
                            if (totalSweep - lowMaxSweep > midMaxSweep) midMaxSweep else totalSweep - lowMaxSweep
                        } else 0f
                        if (midSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00F2FE),
                                        Color(0xFF4FACFE)
                                    )
                                ),
                                startAngle = 180f + lowMaxSweep,
                                sweepAngle = midSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= lowMaxSweep + midMaxSweep) StrokeCap.Round else StrokeCap.Butt
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 3: Super High / Excellent (70 to 100 Mbps) -> Maps to remaining 54°
                        val highSweep = if (totalSweep > (lowMaxSweep + midMaxSweep)) {
                            totalSweep - (lowMaxSweep + midMaxSweep)
                        } else 0f
                        if (highSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FF87),
                                        Color(0xFF60EFA0)
                                    )
                                ),
                                startAngle = 180f + lowMaxSweep + midMaxSweep,
                                sweepAngle = highSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }


                        // 2. DYNAMIC 3-COLOR MOTION SHADOW (NEEDLE TRAIL)
                        // Yeh needle ke peeche ek translucent sweeping gradient shadow banata hai
                        if (totalSweep > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    0.0f to Color(0xFFFF416C).copy(alpha = 0.4f), // Low zone shadow
                                    0.3f to Color(0xFF00F2FE).copy(alpha = 0.3f), // Mid zone shadow
                                    0.7f to Color(0xFF00FF87).copy(alpha = 0.2f), // High zone shadow
                                    1.0f to Color.Transparent,
                                    center = center
                                ),
                                startAngle = 180f,
                                sweepAngle = totalSweep,
                                useCenter = true, // Solid wedge shape shadow ke liye true kiya hai
                                size = Size((radius - 10f) * 2, (radius - 10f) * 2),
                                topLeft = Offset(
                                    center.x - (radius - 10f),
                                    center.y - (radius - 10f)
                                ),
                                alpha = 0.4f // Transparency handle karne ke liye
                            )
                        }


                        // 3. REALISTIC GRADIENT NEEDLE
                        val currentAngleDegree = 180f + totalSweep
                        val angleRadians = Math.toRadians(currentAngleDegree.toDouble())

                        val needleLength = radius - 15f
                        val needleBaseWidth = 12f

                        val tipX = center.x + needleLength * cos(angleRadians).toFloat()
                        val tipY = center.y + needleLength * sin(angleRadians).toFloat()

                        val baseAngleLeft = angleRadians - Math.toRadians(90.0)
                        val baseAngleRight = angleRadians + Math.toRadians(90.0)

                        val baseLeftX = center.x + needleBaseWidth * cos(baseAngleLeft).toFloat()
                        val baseLeftY = center.y + needleBaseWidth * sin(baseAngleLeft).toFloat()

                        val baseRightX = center.x + needleBaseWidth * cos(baseAngleRight).toFloat()
                        val baseRightY = center.y + needleBaseWidth * sin(baseAngleRight).toFloat()

                        val needlePath = Path().apply {
                            moveTo(baseLeftX, baseLeftY)
                            lineTo(tipX, tipY)
                            lineTo(baseRightX, baseRightY)
                            close()
                        }

                        // Needle core gradient based on its current position
                        val needleGradient = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFF00F2FE)),
                            start = center,
                            end = Offset(tipX, tipY)
                        )

                        drawPath(path = needlePath, brush = needleGradient)

                        // 4. METALLIC CENTER CAP
                        drawCircle(color = Color(0xFF0E1626), radius = 26f, center = center)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00F2FE), Color(0xFF1F2A45))
                            ),
                            radius = 18f,
                            center = center
                        )
                    }

                    // 5. SPEED DISPLAY TEXT
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 85.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", animatedSpeed),
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Dynamic text showing Category
                        val categoryText = when {
                            animatedSpeed < 30f -> "AVERAGE"
                            animatedSpeed < 70f -> "GOOD"
                            else -> "HIGH SPEED"
                        }
                        val categoryColor = when {
                            animatedSpeed < 30f -> Color(0xFFFA6C6C)
                            animatedSpeed < 70f -> Color(0xFF8AF0FF)
                            else -> Color(0xFF60EFA0)
                        }

                        Text(
                            text = categoryText,
                            color = categoryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isTesting = true
                            targetSpeed = 0f
                            delay(400)
                            // Randomly testing up to 100 Mbps
                            targetSpeed = Random.nextFloat() * 95f + 5f
                            delay(2200)
                            isTesting = false
                        }
                    },
                    enabled = !isTesting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FACFE),
                        disabledContainerColor = Color(0xFF444444)
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = if (isTesting) "Testing..." else "Start Test",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InternetSpeedometerScreen() {
    var targetSpeed by remember { mutableStateOf(0f) }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Smooth Premium Spring Animation
    val animatedSpeed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PremiumSpeedAnimation"
    )

    // Spacing aur layout ko clean karne ke liye '150' ki jagah '200' apply kiya hai
    val scaleLabels = listOf(0, 50, 100, 200, 400, 600, 800, 1000)

    // Highly balanced non-linear angular mapping logic for perfect dial layout
    fun getSweepAngleForSpeed(speed: Float): Float {
        return when {
            speed <= 0f -> 0f
            speed <= 50f -> (speed / 50f) * 30f              // 0 to 50 -> 30°
            speed <= 100f -> 30f + ((speed - 50f) / 50f) * 30f // 50 to 100 -> 30° (Total 60°)
            speed <= 200f -> 60f + ((speed - 100f) / 100f) * 30f // 100 to 200 -> 30° (Total 90° - Exact Top Center!)
            speed <= 400f -> 90f + ((speed - 200f) / 200f) * 25f // 200 to 400 -> 25° (Total 115°)
            speed <= 600f -> 115f + ((speed - 400f) / 200f) * 25f // 400 to 600 -> 25° (Total 140°)
            speed <= 800f -> 140f + ((speed - 600f) / 200f) * 20f // 600 to 800 -> 20° (Total 160°)
            speed <= 1000f -> 160f + ((speed - 800f) / 200f) * 20f // 800 to 1000 -> 20° (Total 180°)
            else -> 180f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .background(Color(0xFF121B2D), shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Premium Speed Test",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Box(
                    modifier = Modifier.size(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 32f
                        val radius = size.minDimension / 2 - strokeWidth - 40f
                        val center = Offset(size.width / 2, size.height / 2 + 30f)
                        val arcSize = Size(radius * 2, radius * 2)
                        val topLeftOffset = Offset(center.x - radius, center.y - radius)

                        // 1. BACKGROUND TRACK
                        drawArc(
                            color = Color(0xFF1A2436),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize,
                            topLeft = topLeftOffset
                        )

                        // --- 3-ZONE PREMIUM SEGMENTED CHROMATIC PROGRESS ---
                        val totalSweep = getSweepAngleForSpeed(animatedSpeed)

                        // Layer 1: Average Zone (0 to 100 Mbps) -> First 60 Degrees
                        val lowMaxSweep = getSweepAngleForSpeed(100f)
                        val lowSweep = if (totalSweep > lowMaxSweep) lowMaxSweep else totalSweep
                        if (lowSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF416C),
                                        Color(0xFFFF4B2B)
                                    )
                                ),
                                startAngle = 180f,
                                sweepAngle = lowSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= lowMaxSweep) StrokeCap.Round else StrokeCap.Round
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 2: Good/Fast Zone (100 to 600 Mbps) -> Maps up to 140 Degrees
                        val midMaxSweep = getSweepAngleForSpeed(600f) - lowMaxSweep
                        val midSweep = if (totalSweep > lowMaxSweep) {
                            if (totalSweep - lowMaxSweep > midMaxSweep) midMaxSweep else totalSweep - lowMaxSweep
                        } else 0f
                        if (midSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00F2FE),
                                        Color(0xFF4FACFE)
                                    )
                                ),
                                startAngle = 180f + lowMaxSweep,
                                sweepAngle = midSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= (lowMaxSweep + midMaxSweep)) StrokeCap.Round else StrokeCap.Butt
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 3: Hyper Speed Zone (600 to 1000 Mbps) -> Remaining 40 Degrees
                        val highSweep = if (totalSweep > (lowMaxSweep + midMaxSweep)) {
                            totalSweep - (lowMaxSweep + midMaxSweep)
                        } else 0f
                        if (highSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FF87),
                                        Color(0xFF60EFA0)
                                    )
                                ),
                                startAngle = 180f + lowMaxSweep + midMaxSweep,
                                sweepAngle = highSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // 2. AMBIENT MOTION BACKLIGHT GLOW (Sweeping tail)
                        if (totalSweep > 0f) {
                            drawArc(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00F2FE).copy(alpha = 0.15f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = radius + 0f
                                ),
                                startAngle = 178f,
                                sweepAngle = totalSweep,
                                useCenter = true,
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // 3. PERFECTLY SYMMETRIC NUMBER LABELS
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint().apply {
                                color = android.graphics.Color.parseColor("#8E9FBB")
                                textSize = 30f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                typeface = android.graphics.Typeface.create(
                                    android.graphics.Typeface.DEFAULT,
                                    android.graphics.Typeface.BOLD
                                )
                            }

                            scaleLabels.forEach { label ->
                                val sweepAngle = getSweepAngleForSpeed(label.toFloat())
                                val angleDeg = 180f + sweepAngle
                                val rad = Math.toRadians(angleDeg.toDouble())

                                // Dynamic Padding: Dial angles ke hisab se spacing auto-adjust hogi
                                val extraPadding = when {
                                    sweepAngle < 45f -> 46f     // Left side (0, 50) ko thoda dur dakelega
                                    sweepAngle in 45f..135f -> 38f // Top-center elements (100, 200, 400, 600)
                                    else -> 50f                // Right side (800, 1000)
                                }
                                val textDistance = radius + extraPadding

                                val labelX = center.x + textDistance * cos(rad).toFloat()

                                // Baseline Corrective Offset: Trigonometric alignment variables for seamless circle typography
                                val verticalCorrection = when {
                                    sweepAngle < 30f -> 6f      // Perfect vertical leveling for left edge
                                    sweepAngle > 150f -> 6f     // Perfect vertical leveling for right edge
                                    sweepAngle in 75f..105f -> 14f // Precise alignment for Top-Center peak
                                    else -> 10f
                                }
                                val labelY =
                                    center.y + textDistance * sin(rad).toFloat() + verticalCorrection

                                canvas.nativeCanvas.drawText(
                                    label.toString(),
                                    labelX,
                                    labelY,
                                    paint
                                )
                            }
                        }

                        // 4. REAL GRADIENT CHROMATIC NEEDLE (Dual-Tone Left/Right Shift)
                        val currentAngleDegree = 180f + totalSweep
                        val angleRadians = Math.toRadians(currentAngleDegree.toDouble())

                        val needleLength = radius - 6f
                        val needleBaseWidth = 9f

                        val tipX = center.x + needleLength * cos(angleRadians).toFloat()
                        val tipY = center.y + needleLength * sin(angleRadians).toFloat()

                        val leftBaseAngle = angleRadians - Math.toRadians(90.0)
                        val rightBaseAngle = angleRadians + Math.toRadians(90.0)

                        val leftBaseX = center.x + needleBaseWidth * cos(leftBaseAngle).toFloat()
                        val leftBaseY = center.y + needleBaseWidth * sin(leftBaseAngle).toFloat()

                        val rightBaseX = center.x + needleBaseWidth * cos(rightBaseAngle).toFloat()
                        val rightBaseY = center.y + needleBaseWidth * sin(rightBaseAngle).toFloat()

                        // Left Needle Side: Neon Cyan to Hot Pink Transition Glow
                        val leftPath = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(leftBaseX, leftBaseY)
                            lineTo(tipX, tipY)
                            close()
                        }
                        val leftGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF00F2FE), Color(0xFFFF1744)),
                            start = center,
                            end = Offset(tipX, tipY)
                        )
                        drawPath(path = leftPath, brush = leftGradient)

                        // Right Needle Side: Deep Blue to Solid Crimson Metallic Shadow Shift
                        val rightPath = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(rightBaseX, rightBaseY)
                            lineTo(tipX, tipY)
                            close()
                        }
                        val rightGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF0072FF), Color(0xFFB71C1C)),
                            start = center,
                            end = Offset(tipX, tipY)
                        )
                        drawPath(path = rightPath, brush = rightGradient)

                        // 5. CENTER BEVEL CHROME CAP
                        drawCircle(color = Color(0xFF0A0F1D), radius = 24f, center = center)
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3A4D6B),
                                    Color(0xFF111A2E)
                                )
                            ),
                            radius = 18f,
                            center = center
                        )
                        drawCircle(
                            color = Color(0xFF00F2FE),
                            radius = 5f,
                            center = center
                        ) // Center Electric Core
                    }

                    // 6. CENTRAL TEXT READOUT
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 90.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", animatedSpeed),
                            color = Color.White,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black
                        )

                        val (categoryText, categoryColor) = when {
                            animatedSpeed < 100f -> "STABLE" to Color(0xFFFF5252)
                            animatedSpeed < 600f -> "TURBO FAST" to Color(0xFF00F2FE)
                            else -> "HYPER DRIVE" to Color(0xFF00FF87)
                        }

                        Text(
                            text = categoryText,
                            color = categoryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isTesting = true
                            targetSpeed = 0f
                            delay(300)
                            // Up to 1000 Mbps random test simulation
                            targetSpeed = Random.nextFloat() * 970f + 30f
                            delay(2600)
                            isTesting = false
                        }
                    },
                    enabled = !isTesting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FACFE),
                        disabledContainerColor = Color(0xFF252A34)
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = if (isTesting) "TUNING..." else "TEST SPEED",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InternetSpeedometerScreen1() {
    var targetSpeed by remember { mutableStateOf(0f) }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // PREMIUM SPRING ANIMATION: Real physics click aur bounce effect ke liye
    val animatedSpeed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Sui thoda bounce karegi real gauge ki tarah
            stiffness = Spring.StiffnessLow
        ),
        label = "RealisticSpeedAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D)), // Ultra Dark Premium Background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFF121B2D), shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Hyper-Real Speedometer",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 32f
                        val radius = size.minDimension / 2 - strokeWidth - 25f
                        val center = Offset(size.width / 2, size.height / 2 + 40f)
                        val arcSize = Size(radius * 2, radius * 2)
                        val topLeftOffset = Offset(center.x - radius, center.y - radius)

                        // 1. BACKGROUND TRACK (Dark Metallic Grooves)
                        drawArc(
                            color = Color(0xFF1A2436),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize,
                            topLeft = topLeftOffset
                        )

                        // --- 3-LAYER CATEGORY PROGRESS ARC ---
                        val maxSpeed = 100f
                        val totalSweep = (animatedSpeed / maxSpeed) * 180f

                        val lowMaxSweep = (30f / maxSpeed) * 180f  // 54°
                        val midMaxSweep = (40f / maxSpeed) * 180f  // 72°

                        // Layer 1: Low/Average (Red/Orange)
                        val lowSweep = if (totalSweep > lowMaxSweep) lowMaxSweep else totalSweep
                        if (lowSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFE53935),
                                        Color(0xFFFF7300)
                                    )
                                ),
                                startAngle = 180f,
                                sweepAngle = lowSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= lowMaxSweep) StrokeCap.Round else StrokeCap.Butt
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 2: Good (Neon Cyan/Blue)
                        val midSweep = if (totalSweep > lowMaxSweep) {
                            if (totalSweep - lowMaxSweep > midMaxSweep) midMaxSweep else totalSweep - lowMaxSweep
                        } else 0f
                        if (midSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(colors = listOf(0xFF00F2FE.let {
                                    Color(
                                        it
                                    )
                                }, Color(0xFF0072FF))),
                                startAngle = 180f + lowMaxSweep,
                                sweepAngle = midSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = if (totalSweep <= lowMaxSweep + midMaxSweep) StrokeCap.Round else StrokeCap.Butt
                                ),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // Layer 3: Ultra High (Electric Emerald Green)
                        val highSweep =
                            if (totalSweep > (lowMaxSweep + midMaxSweep)) totalSweep - (lowMaxSweep + midMaxSweep) else 0f
                        if (highSweep > 0f) {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FF87),
                                        Color(0xFF00E676)
                                    )
                                ),
                                startAngle = 180f + lowMaxSweep + midMaxSweep,
                                sweepAngle = highSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                size = arcSize,
                                topLeft = topLeftOffset
                            )
                        }

                        // 2. BACKLIGHT GLOW (Visual Internal LED Reflection)
                        if (totalSweep > 0f) {
                            drawArc(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00F2FE).copy(alpha = 0.15f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = radius + 40f
                                ),
                                startAngle = 180f,
                                sweepAngle = totalSweep,
                                useCenter = true,
                                size = Size(radius * 2, radius * 2),
                                topLeft = topLeftOffset
                            )
                        }

                        // 3. 3D DUAL-TONE REALISTIC NEEDLE (LEFT & RIGHT SPLIT)
                        val currentAngleDegree = 180f + totalSweep
                        val angleRadians = Math.toRadians(currentAngleDegree.toDouble())

                        val needleLength = radius - 8f
                        val needleBaseWidth = 10f // Total base width 20f (10f left, 10f right)

                        // Needle Points
                        val tipX = center.x + needleLength * cos(angleRadians).toFloat()
                        val tipY = center.y + needleLength * sin(angleRadians).toFloat()

                        val leftBaseAngle = angleRadians - Math.toRadians(90.0)
                        val rightBaseAngle = angleRadians + Math.toRadians(90.0)

                        val leftBaseX = center.x + needleBaseWidth * cos(leftBaseAngle).toFloat()
                        val leftBaseY = center.y + needleBaseWidth * sin(leftBaseAngle).toFloat()

                        val rightBaseX = center.x + needleBaseWidth * cos(rightBaseAngle).toFloat()
                        val rightBaseY = center.y + needleBaseWidth * sin(rightBaseAngle).toFloat()

                        // --- LEFT SIDE PATH (Highlighted Surface) ---
                        val leftNeedlePath = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(leftBaseX, leftBaseY)
                            lineTo(tipX, tipY)
                            close()
                        }
                        // Left side gets a bright neon orange/red gradient
                        val leftGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF5252), Color(0xFFFF1744)),
                            start = center, end = Offset(tipX, tipY)
                        )
                        drawPath(path = leftNeedlePath, brush = leftGradient)

                        // --- RIGHT SIDE PATH (Shadowed/Dark Surface) ---
                        val rightNeedlePath = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(rightBaseX, rightBaseY)
                            lineTo(tipX, tipY)
                            close()
                        }
                        // Right side gets a darker crimson red gradient to create a 3D bevel edge
                        val rightGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFD50000), Color(0xFFB71C1C)),
                            start = center, end = Offset(tipX, tipY)
                        )
                        drawPath(path = rightNeedlePath, brush = rightGradient)


                        // 4. METALLIC NEEDLE CAP WITH BEVEL
                        drawCircle(
                            color = Color(0xFF060A13),
                            radius = 28f,
                            center = center
                        ) // Shadow ring
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF3A4D6B), Color(0xFF111A2E))
                            ),
                            radius = 20f,
                            center = center
                        ) // Chrome Bevel Cap
                        drawCircle(
                            color = Color(0xFFFF1744),
                            radius = 6f,
                            center = center
                        ) // Center Red Dot Pin
                    }

                    // 5. DIGITIAL SPEED READOUT
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 95.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", animatedSpeed),
                            color = Color.White,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black
                        )

                        val (categoryText, categoryColor) = when {
                            animatedSpeed < 30f -> "POOR CONNECTION" to Color(0xFFFF5252)
                            animatedSpeed < 70f -> "STABLE NET" to Color(0xFF00F2FE)
                            else -> "LIGHTNING FAST" to Color(0xFF00FF87)
                        }

                        Text(
                            text = categoryText,
                            color = categoryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isTesting = true
                            targetSpeed = 0f
                            delay(300)
                            targetSpeed = Random.nextFloat() * 92f + 8f
                            delay(2500)
                            isTesting = false
                        }
                    },
                    enabled = !isTesting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F75FE),
                        disabledContainerColor = Color(0xFF252A34)
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = if (isTesting) "ANALYZING..." else "TEST SPEED",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}


@Composable
fun IOSDynamicSemiCircleProgressBar(
    targetProgress: Float, // Value between 0.0f and 1.0f
    modifier: Modifier = Modifier.size(140.dp)
) {
    // Perfect Bottom Half Semi-Circle ke liye configurations
    val totalSweepAngle = 270f
    val startAngle =
        180f // Left side (9 o'clock) se shuru hokar bottom se hote hue right side jayega

    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = targetProgress) {
        animationTriggered = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) targetProgress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "ProgressAnimation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.width / 2) - 30f

            val startWidthPx = 2.dp.toPx()
            val endWidthPx = 15.dp.toPx()

            // Pure curve ko smoothly evaluate karne ke liye total steps
            val steps = totalSweepAngle.toInt()
            val filledSteps = (totalSweepAngle * animatedProgress).toInt()

            // Native Paint for Transparent Grey Blur Shadow
            val shadowPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.FILL
                color = Color(0xFFEEEDED).copy(alpha = 0.5f).toArgb() // Translucent Grey
                maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL) // Soft Edge Shadow
            }

            // Native Paint for Solid Active Color (Red)
            val redPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.FILL
                color = Color(0xFFFF4545).toArgb() // Premium iOS Red
            }

            // Pure Semi-circle path par loop chalayenge (0 se 180 degree tak)
            for (i in 0..steps) {
                val percentageOfArc = i.toFloat() / steps
                val currentAngleDegree = startAngle + (totalSweepAngle * percentageOfArc)
                val currentAngleRad = Math.toRadians(currentAngleDegree.toDouble())

                // Coordinates calculate karein
                val x = center.x + radius * cos(currentAngleRad).toFloat()
                val y = center.y + radius * sin(currentAngleRad).toFloat()

                // Dynamic width calculation (2dp se lekar 15dp tak linearly badhegi)
                val currentStrokeWidth =
                    startWidthPx + (endWidthPx - startWidthPx) * percentageOfArc
                val currentRadius = currentStrokeWidth / 2

                if (i <= filledSteps) {
                    // 1. FILLED ACTIVE RED COLOR
                    // Yeh progress ke hisab se badhega aur iski width bhi matching badhegi
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawCircle(x, y, currentRadius, redPaint)
                    }
                } else {
                    // 2. UNFILLED TRANSPARENT GREY SHADOW
                    // Bacha hua hissa shadow banega aur iski width bhi usi scaling size (moti) ko follow karegi
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawCircle(x, y, currentRadius, shadowPaint)
                    }
                }
            }
        }

        // Center Percentage Text
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun IOSDynamicWidthProgressBar(
    targetProgress: Float, // Value between 0.0f and 1.0f (e.g., 0.25f for 25%)
    modifier: Modifier = Modifier.size(140.dp)
) {
    val totalSweepAngle = 270f
    val startAngle = 135f

    // Animation state trigger karne ke liye
    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = targetProgress) {
        animationTriggered = true
    }

    // Smooth progress animation (0% se target tak)
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) targetProgress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "ProgressAnimation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            // Margins taaki corners cut na ho
            val radius = (size.width / 2) - 30f

            // ---------------------------------------------------------------
            // 1. UNFILLED BACKGROUND TRACK WITH BLUR SHADOW
            // ---------------------------------------------------------------
            drawIntoCanvas { canvas ->
                val shadowPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 32f // Mota blurred background track
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    // Transparent grey jaisa effect dene ke liye color code
                    color = Color(0x1F000000).toArgb()

                    // Blur effect apply kiya jisse glow/shadow look aaye
                    maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
                }

                canvas.nativeCanvas.drawArc(
                    center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius,
                    startAngle, totalSweepAngle, false, shadowPaint
                )
            }

            // Halka sa solid base grey track depth ke liye
            drawArc(
                color = Color(0x0D000000),
                startAngle = startAngle,
                sweepAngle = totalSweepAngle,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )

            // ---------------------------------------------------------------
            // 2. ACTIVE PROGRESS WITH INCREASING WIDTH (From 2dp to 15dp)
            // ---------------------------------------------------------------
            val currentSweep = totalSweepAngle * animatedProgress

            if (currentSweep > 0f) {
                val startWidthPx = 2.dp.toPx()
                val endWidthPx = 15.dp.toPx()

                // Pure arc ko chote-chote degree pieces mein tod kar draw karenge
                // taaki stroke width smoothly transition kare
                val steps = currentSweep.toInt().coerceAtLeast(1)

                for (i in 0..steps) {
                    val percentageOfArc = i.toFloat() / steps
                    val currentAngleDegree = startAngle + (currentSweep * percentageOfArc)
                    val currentAngleRad = Math.toRadians(currentAngleDegree.toDouble())

                    // Points calculate kar rahe hain circumference par
                    val x = center.x + radius * cos(currentAngleRad).toFloat()
                    val y = center.y + radius * sin(currentAngleRad).toFloat()

                    // Width linear tarike se badhegi: 2dp se start hokar 15dp tak
                    val currentStrokeWidth =
                        startWidthPx + (endWidthPx - startWidthPx) * percentageOfArc

                    // Chote circles ko line se jodte hue smooth tapering width draw hogi
                    drawCircle(
                        color = Color(0xFFFF4545), // iOS standard soft red
                        radius = currentStrokeWidth / 2,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // ---------------------------------------------------------------
        // 3. CENTER TEXT (PERCENTAGE)
        // ---------------------------------------------------------------
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun composePreview() {
    CanvasLearning()
}


@Composable
fun SpeedometerDemo() {

    var speed by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(Unit) {

        speed = 0f

        repeat(100) {
            speed += Random.nextFloat() * 2f
            delay(50)
        }

        repeat(60) {
            speed += Random.nextFloat()
            delay(100)
        }

        repeat(40) {
            speed += Random.nextFloat() * 0.3f
            delay(150)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        SpeedometerGauge(
//            speed = speed,
//            modifier = Modifier.size(340.dp)
//        )

//        InternetSpeedometerScreen()
//        IOSDynamicWidthProgressBar(
//            targetProgress = 0.75f, // Yeh automatic 0% se 25% animate hoga aur width badhayega
//            modifier = Modifier.size(150.dp)
//        )

        IOSDynamicSemiCircleProgressBar(
            targetProgress = 0.75f
        )


    }
}
//@Preview(showBackground = true)
//@Composable
//fun HealthProgressDemo() {
//    var health by remember { mutableStateOf(75f) }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        LiquidHealthProgressBar(healthPercentage = health, size = 220.dp)
//
//        Spacer(modifier = Modifier.height(40.dp))
//
//        Slider(
//            value = health,
//            onValueChange = { health = it },
//            valueRange = 0f..100f,
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun HealthMeterPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1931)) // Match background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularHealthMeter()
        Spacer(modifier = Modifier.height(24.dp))
        // Demonstrating a dynamic value
        CircularHealthMeter(healthValue = 45, size = 180.dp, strokeWidth = 16.dp)
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212
)
@Composable
fun SpeedometerPreview() {

//    var speed by remember {
//        mutableFloatStateOf(20f)
//    }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            speed = Random.nextInt(0, 200).toFloat()
//            delay(2000)
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        SpeedometerGauge(
//            speed = speed
//        )
//    }

    InternetSpeedometerScreen()
}
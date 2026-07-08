package com.example.learningprojects.ui.theme.homescreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.AccentGreen
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.lightGrayHome

@Composable
fun DeviceStatusLayout(
    item: DeviceStatusDummyModel,
    startAnimation: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue =
            if (startAnimation)
                item.usagePercentage / 100f
            else
                0f,
        animationSpec = tween(
            durationMillis = 1200
        ),
        label = ""
    )
    val colors = getStatusColors(item.name)
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(shape = CircleShape, color = item.color.copy(alpha = 0.21f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = "battery",
                    colorFilter = ColorFilter.tint(item.color),
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            if (!item.batteryTime.isEmpty()) {
                CommonText(
                    text = item.batteryTime,
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        if (!item.batteryPercentage.isEmpty()) {
            Spacer(Modifier.height(10.dp))
            CommonText(
                text = item.batteryPercentage,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.wrapContentSize(),
            )

        }
        if (!item.name.isEmpty()) {
            Spacer(Modifier.height(5.dp))
            CommonText(
                text = item.name,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = lightGrayHome,
                modifier = Modifier.wrapContentSize(),
                isSingleLine = true
            )
        }

        if (animatedProgress != 0f) {
            Spacer(Modifier.height(15.dp))
            CustomLinearProgressBar(
                progress = animatedProgress,
                progressColor = colors.darkColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
            CommonText(
                text = item.speed ?: "",
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.wrapContentSize(),
                isSingleLine = true
            )
        }

        if (!item.temp.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            CommonText(
                text = item.temp,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 0.dp)
                    .wrapContentSize(),
                isSingleLine = true
            )
        }
    }
}

@Composable
fun CustomLinearProgressBar(
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(
                    progressColor
                )
        )
    }
}

data class StatusColors(
    val lightColor: Color,
    val darkColor: Color
)

fun getStatusColors(name: String): StatusColors {
    return when (name) {
        "Battery" -> StatusColors(
            lightColor = Color(0xFFE8F5E9), // Light Green
            darkColor = Color(0xFF2E7D32)   // Dark Green
        )

        "RAM Usage" -> StatusColors(
            lightColor = Color(0xFFE3F2FD), // Light Blue
            darkColor = Color(0xFF1565C0)   // Blue
        )

        "Storage" -> StatusColors(
            lightColor = Color(0xFFE1F5FE), // Sky Blue
            darkColor = Color(0xFF0288D1)   // Dark Sky Blue
        )

        else -> StatusColors(
            lightColor = Color(0xFFF3F4F6),
            darkColor = Color.Gray
        )
    }
}


@Preview
@Composable
fun previewDeviceLayout() {
//    DeviceStatusLayout()
}

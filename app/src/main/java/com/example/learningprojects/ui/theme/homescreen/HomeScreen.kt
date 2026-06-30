package com.example.learningprojects.ui.theme.homescreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.canvascomposable.CircularHealthMeter
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.commonusablecomponent.ReusableAnalyticsGraph
import com.example.learningprojects.ui.theme.enumclass.GraphType
import com.example.learningprojects.ui.theme.homescreen.viewmodel.HomeViewModel
import com.example.learningprojects.utils.DeviceHealthManager.format1Digit
import com.example.learningprojects.utils.DeviceHealthManager.getBatteryRemainingTime
import com.example.learningprojects.utils.DeviceHealthManager.getPlayStoreApps

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.health.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadHealth(context)
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentGraphType = remember(selectedTab) {
        when (selectedTab) {
            0 -> GraphType.BATTERY
            1 -> GraphType.RAM
            else -> GraphType.NETWORK
        }
    }

    val graphProgressAnimatable = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(selectedTab) {
        graphProgressAnimatable.snapTo(0f) // Bina kisi delay ke animation ko 0 par jhatke se reset karein
        graphProgressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            )
        )
    }
    val graphProgress = graphProgressAnimatable.value

    val graphDataPoints = remember(currentGraphType) {
        when (currentGraphType) {
            GraphType.BATTERY -> listOf(
                AnalyticsPoint("6a", 90f), AnalyticsPoint("8a", 85f),
                AnalyticsPoint("10a", 70f), AnalyticsPoint("12p", 58f),
                AnalyticsPoint("2p", 74f), // Matches requirement
                AnalyticsPoint("4p", 60f), AnalyticsPoint("6p", 50f),
                AnalyticsPoint("Now", 45f)
            )
            GraphType.RAM -> listOf(
                AnalyticsPoint("6a", 30f), AnalyticsPoint("8a", 35f),
                AnalyticsPoint("10a", 55f), AnalyticsPoint("12p", 60f),
                AnalyticsPoint("2p", 45f), // Matches requirement
                AnalyticsPoint("4p", 65f), AnalyticsPoint("6p", 55f),
                AnalyticsPoint("Now", 50f)
            )
            GraphType.NETWORK -> listOf(
                // primaryValue = Download (34f), secondaryValue = Upload (33f)
                AnalyticsPoint("6a", 20f, 15f), AnalyticsPoint("8a", 45f, 35f),
                AnalyticsPoint("10a", 70f, 60f), AnalyticsPoint("12p", 85f, 75f),
                AnalyticsPoint("2p", 34f, 33f), // Matches requirement
                AnalyticsPoint("4p", 60f, 50f), AnalyticsPoint("6p", 80f, 70f),
                AnalyticsPoint("Now", 55f, 45f)
            )
        }
    }

    val list = listOf<DeviceStatusDummyModel>(
        DeviceStatusDummyModel(
            batteryTime = getBatteryRemainingTime(state?.battery?:0),
            batteryPercentage = "${state?.battery}%",
            usagePercentage = state?.battery?.toFloat()?:0f,
            image = R.drawable.outline_battery_android_0_24,
            temp = "${state?.batteryTemp}°C",
            name = "Battery"
        ),
        DeviceStatusDummyModel(
            batteryTime = "${state?.totalRamGb?.format1Digit()}GB",
            batteryPercentage = "${state?.ramUsage}%",
            usagePercentage = state?.ramUsage?.toFloat()?:0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "${state?.usedRamGb?.format1Digit()}GB Used- ${state?.availableRamGb?.format1Digit()}GB Free",
            name = "RAM Usage"
        ),
        DeviceStatusDummyModel(
            batteryTime = "${state?.totalStorageGb?.format1Digit()}GB",
            batteryPercentage = "${state?.storageUsage}%",
            usagePercentage = state?.storageUsage?.toFloat()?:0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "${state?.usedStorageGB?.format1Digit()}GB Used - ${state?.availableStorageGb?.format1Digit()}GB Free",
            name = "Storage"
        ),
        DeviceStatusDummyModel(
            batteryTime = "4h 22m",
            batteryPercentage = "45%",
            image = R.drawable.outline_android_wifi_4_bar_question_24,
            temp = "40C",
            name = "Wifi"
        ),
    )


    val deviceFeatureList = listOf<DeviceFeactures>(
        DeviceFeactures(
            batteryImage = R.drawable.icons8_flash_50,
            title = "FlashLight"
        ),
        DeviceFeactures(
            batteryImage = R.drawable.outline_battery_android_0_24,
            title = "Batt.Saver"
        ),
        DeviceFeactures(
            batteryImage = R.drawable.icons8_voice_64,
            title = "Volume"
        ),
        DeviceFeactures(
            batteryImage = R.drawable.outline_light_mode_24,
            title = "Brightness"
        ),
        DeviceFeactures(
            batteryImage = R.drawable.icons8_info_32,
            title = "Device Info"
        ),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommonText(
                        text = "GoodEvening",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(resId = R.font.regular)),
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.wrapContentSize(),
                        color = Color(0xFF637380)
                    )
                    CommonText(
                        text = "Pankaj kumar",
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                        fontWeight = FontWeight.W800,
                        modifier = Modifier.wrapContentSize(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF183669),
                                Color(0xFF0F375B)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 25.dp)

            )
            {
                CircularHealthMeter(healthValue = state?.overallHealth?:54, size = 140.dp, strokeWidth = 14.dp)
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                ) {

                    CommonText(
                        text = "Excellent",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                        fontWeight = FontWeight.W800,
                        modifier = Modifier.wrapContentSize(),
                        color = MaterialTheme.colorScheme.background
                    )
                    Spacer(Modifier.height(4.dp))
                    CommonText(
                        text = "Device Running Smoothly",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.wrapContentSize(),
                        color = Color(0xFF6F7F9D)
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {

                            CommonText(
                                text = "Battery".uppercase(),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.wrapContentSize(),
                                color = Color(0xFF6F7F9D)
                            )
                            Spacer(Modifier.height(4.dp))

                            CommonText(
                                text = "${state?.battery}%",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(resId = R.font.regular)),
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.wrapContentSize(),
                                color = Color(0xFFFFFFFF)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {

                            CommonText(
                                text = "CPU TEMP".uppercase(),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.wrapContentSize(),
                                color = Color(0xFF6F7F9D)
                            )
                            Spacer(Modifier.height(4.dp))
                            CommonText(
                                text = "${state?.batteryTemp}°C",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(resId = R.font.regular)),
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.wrapContentSize(),
                            )
                        }
                    }

                    Spacer(
                        Modifier
                            .height(15.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {

                            CommonText(
                                text = "ram".uppercase(),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.wrapContentSize(),
                                color = Color(0xFF6F7F9D)
                            )
                            Spacer(Modifier.height(5.dp))

                            CommonText(
                                text = "${state?.availableRam}%",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(resId = R.font.regular)),
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.wrapContentSize(),
                            )
                        }

                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {

                            CommonText(
                                text = "storage".uppercase(),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.wrapContentSize(),
                                color = Color(0xFF6F7F9D)
                            )
                            Spacer(Modifier.height(5.dp))

                            CommonText(
                                text = "${state?.availableStorage}%",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(resId = R.font.regular)),
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.wrapContentSize(),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(15.dp))
        }
        items(list.chunked(2)) { rowItems ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {

                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        DeviceStatusLayout(item)
                    }
                }

                // Agar last row me sirf 1 item ho
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(15.dp))
        }
        item {
            Column(
                modifier = Modifier
                    .wrapContentSize()
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
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mobile_2_24dp_01147b___fill0_wght400_grad0_opsz24),
                        contentDescription = "null",
                        Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    CommonText(
                        text = "Device Temperature",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.wrapContentSize(),
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(
                                shape = RoundedCornerShape(5.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            )
                            .padding(5.dp)
                    ) {
                        CommonText(
                            text = "Normal",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.wrapContentSize(),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        CommonText(
                            text = "56",
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                            fontWeight = FontWeight.W800,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.wrapContentSize(),
                        )
                        Spacer(Modifier.height(10.dp))
                        CommonText(
                            text = "CPU",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(resId = R.font.regular)),
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.wrapContentSize(),
                        )
                    }

                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        CommonText(
                            text = "56",
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                            fontWeight = FontWeight.W800,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.wrapContentSize(),
                        )
                        Spacer(Modifier.height(10.dp))
                        CommonText(
                            text = "GPU",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(resId = R.font.regular)),
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.wrapContentSize(),
                        )
                    }

                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        CommonText(
                            text = "56",
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                            fontWeight = FontWeight.W800,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.wrapContentSize(),
                        )
                        Spacer(Modifier.height(10.dp))
                        CommonText(
                            text = "Battery",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(resId = R.font.regular)),
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.wrapContentSize(),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                items(deviceFeatureList) { item ->
                    HorizontalLayout(item)
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .wrapContentSize()
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
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommonText(
                        text = "Analytics",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                        fontWeight = FontWeight.W800,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.wrapContentSize(),
                    )
                    TrendingTabLayout(
                        selectedTab = selectedTab,
                        onTabSelected = {
                            selectedTab = it
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                ReusableAnalyticsGraph(
                    points = graphDataPoints,
                    graphType = currentGraphType,
                    progress = graphProgress
                )
            }
            Spacer(Modifier.height(15.dp))
        }

    }
}

@Composable
fun TrendingTabLayout(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Battery", "Ram", "Network")
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 20.dp)
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
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(14)
            )
            .padding(0.dp)
    ) {

        tabs.forEachIndexed { index, title ->

            val isSelected = selectedTab == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    bottomEnd = 14.dp
                                )
                            )
                        } else {
                            Modifier
                        }
                    )
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    )
                    .clickable {
                        onTabSelected(index)
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {

                CommonText(
                    text = title,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontWeight = FontWeight.W400,
                    color = if (isSelected)
                        Color.White
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.wrapContentSize(),
                )
            }
        }
    }
}

@Preview
@Composable
fun homeScreenPreview() {
//    HomeScreen()
}
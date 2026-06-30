package com.example.learningprojects.ui.theme.devicescreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.learningprojects.ui.theme.AccentGreen
import com.example.learningprojects.ui.theme.LightGrayText
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.topProgressLight
import com.example.learningprojects.ui.theme.topProgressLight2
import com.example.learningprojects.utils.Utils
import com.example.learningprojects.utils.Utils.formatSecurityPatch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceScreen(
    viewModel: DeviceViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadDeviceInfo(context)
    }

    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val model = deviceInfo.find { it.modelName == "Model" }?.modelValue ?: ""
    val androidVersion = deviceInfo.find { it.modelName == "Android Version" }?.modelValue ?: ""
    val apiLevel = deviceInfo.find { it.modelName == "API Level" }?.modelValue ?: ""
    val deviceName = deviceInfo.find { it.modelName == "Device" }?.modelValue ?: ""
    val securityPatch = deviceInfo.find {
        it.modelName == "Security Patch"
    }?.modelValue.orEmpty()

    val actualDate = if (securityPatch.isNotBlank()) {
        Utils.formatSecurityPatch(securityPatch)
    } else {
        ""
    }
    Log.d("listDebug", "DeviceScreen: ${deviceInfo}")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CommonText(
                    text = "Device Info",
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                    fontWeight = FontWeight.W800,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.wrapContentSize(),
                )

                Spacer(Modifier.height(10.dp))
                CommonText(
                    text = "${model} Android ${androidVersion}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.wrapContentSize(),
                    isSingleLine = true
                )
                Spacer(Modifier.height(15.dp))

            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        shape = RoundedCornerShape(20.dp),
                        spotColor = LightGrayText,
                        elevation = 4.dp,
                    )
                    .border(
                        width = 0.dp,
                        color = LightGrayText,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            listOf<Color>(
                                topProgressLight,
                                topProgressLight2
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.mobile_2_24dp_01147b___fill0_wght400_grad0_opsz24),
                    contentDescription = "null",
                    colorFilter = ColorFilter.tint(AccentGreen),
                    modifier = Modifier.size(50.dp)
                )
                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    CommonText(
                        text = "$model",
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                        fontWeight = FontWeight.W800,
                        modifier = Modifier.wrapContentSize(),
                        color = MaterialTheme.colorScheme.background
                    )
                    CommonText(
                        text = "Android ${androidVersion} ${deviceName}",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(resId = R.font.regular)),
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.wrapContentSize(),
                        color = LightGrayText
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vital_signs_24dp_01147b___fill0_wght400_grad0_opsz24),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AccentGreen),
                            modifier = Modifier.size(12.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        CommonText(
                            text = "Security Patch:${actualDate}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(resId = R.font.regular)),
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.wrapContentSize(),
                            color = AccentGreen
                        )
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        LightGrayText,
                        RoundedCornerShape(15.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .padding()
            ) {
                Column {
                    deviceInfo.forEachIndexed { index, item ->
                        DeviceItemLayout(
                            item = item,
                            showDivider = index != deviceInfo.lastIndex
                        )
                    }
                }
            }

            Spacer(Modifier.height(15.dp))
        }

    }
}

@Preview
@Composable
fun DeviceInfoPreview() {
//    DeviceScreen()
}
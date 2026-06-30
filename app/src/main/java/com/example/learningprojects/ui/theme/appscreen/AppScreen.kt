package com.example.learningprojects.ui.theme.appscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.homescreen.DeviceStatusDummyModel
import com.example.learningprojects.ui.theme.homescreen.DeviceStatusLayout
import com.example.learningprojects.ui.theme.homescreen.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.appsInfo.collectAsState()
    val installedApp by viewModel.playStoreAppsCount.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
//        viewModel.loadApps(context)
//        viewModel.loadAppss(context)
    }


    val list = listOf<DeviceStatusDummyModel>(
        DeviceStatusDummyModel(
            batteryTime = "",
            batteryPercentage = "${state?.totalApps}",
            usagePercentage = 0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "${installedApp} User. ${state?.systemApps} System",
            name = "Installed App"
        ),
        DeviceStatusDummyModel(
            batteryTime = "",
            batteryPercentage = "${state?.totalApps}",
            usagePercentage = 0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "${state?.userApps} User. ${state?.systemApps} System",
            name = "Installed App"
        ),
        DeviceStatusDummyModel(
            batteryTime = "",
            batteryPercentage = "${state?.totalApps}",
            usagePercentage = 0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "Installed App",
            name = ""
        ),
        DeviceStatusDummyModel(
            batteryTime = "",
            batteryPercentage = "${state?.totalApps}",
            usagePercentage = 0f,
            image = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24,
            temp = "Installed App",
            name = ""
        ),
    )
    val appTypeList = listOf<String>(
        "All Apps", "User Apps", "System Apps", "High Storage Apps"
    )
    var selectedType by remember {
        mutableStateOf(appTypeList.first())
    }
    val playStoreApps by viewModel.playStoreApps.collectAsState()
//    val playStoreApps = remember {
//        DeviceHealthManager.getPlayStoreAppInfo(context)
//    }

    val systemApps = state?.systemAppList.orEmpty()

    val appList = remember(selectedType, state) {
        when (selectedType) {

            "All Apps" ->
                (playStoreApps + systemApps)
                    .distinctBy { it.packageName }
                    .sortedBy { it.appName }

            "User Apps" ->
                playStoreApps

            "System Apps" ->
                systemApps

            "High Storage Apps" ->
                emptyList() // baad me implement kar lena

            else ->
                emptyList()
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {
        item {
            CommonText(
                text = "AppManager",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.wrapContentSize(),
            )

            Spacer(Modifier.height(10.dp))
            CommonText(
                text = "Monitor Storage,Memory Consumption & battery impact",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.wrapContentSize(),
                isSingleLine = true
            )
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
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(appTypeList) { name ->
                    AppTypeLayout(
                        name,
                        isSelected = selectedType == name,
                        onClick = {
                            selectedType = name
                        }
                    )
                }
            }
        }

        items(appList) { it ->
            AppShowLayout(it.appName)
        }
    }
}
package com.example.learningprojects.ui.theme.homescreen

import androidx.compose.ui.graphics.Color

data class DeviceStatusDummyModel(
    val batteryTime: String,
    val batteryPercentage : String,
    val usagePercentage : Float = 0f,
    val image: Int,
    val temp : String,
    val name : String,
    val color : Color,
    val speed : String? = null
)

data class DeviceFeactures(
    val batteryImage : Int,
    val title : String,
    val color : Color
)

data class GraphPoint(
    val time: String,
    val voltage: Float
)

data class AnalyticsPoint(
    val time: String,
    val primaryValue: Float,     // Used for Battery %, Ram %, or Network Download
    val secondaryValue: Float? = null // Used optionally for Network Upload
)

data class DeviceHealth(
    val battery: Int,
    val ramUsage: Int,
    val storageUsage: Int,
    val batteryTemp: Float,
    val overallHealth: Int,
    val availableStorage: Int,
    val availableRam: Int,

    val totalRamGb: Float,
    val availableRamGb: Float,

    val totalStorageGb: Float,
    val availableStorageGb: Float,
    val usedRamGb: Float,
    val usedStorageGB: Float,
)

data class InstalledAppsInfo(
    val totalApps: Int,
    val userApps: Int,
    val systemApps: Int,
)
data class AppsResult(
    val totalApps: Int,
    val userApps: Int,
    val systemApps: Int,
    val userAppList: List<AppInfo>,
    val systemAppList: List<AppInfo>
)

data class AppInfo(
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean
)
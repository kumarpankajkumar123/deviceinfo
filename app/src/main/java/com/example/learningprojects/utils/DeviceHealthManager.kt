package com.example.learningprojects.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.learningprojects.ui.theme.devicescreen.DeviceInfoModel
import com.example.learningprojects.ui.theme.homescreen.AppInfo
import com.example.learningprojects.ui.theme.homescreen.AppsResult
import com.example.learningprojects.ui.theme.homescreen.InstalledAppsInfo

object DeviceHealthManager {

    fun getBatteryPercentage(context: Context): Int {
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        return batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
    }

    fun getRamUsage(context: Context): ActivityManager.MemoryInfo {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
        Log.d("Debugging", "RAM total: ${bytesToGB(memoryInfo.totalMem)} GB")
        Log.d("Debugging", "RAM available: ${bytesToGB(memoryInfo.availMem)} GB")
        Log.d("Debugging", "RAM used: ${bytesToGB(usedMemory)} GB")

        return memoryInfo
    }

    fun getStorageUsage(): StatFs {
        val statFs = StatFs(Environment.getDataDirectory().path)

        val total = statFs.totalBytes
        val available = statFs.availableBytes
        val used = total - available
        Log.d("Debugging", "Storage: ${bytesToGB(statFs.totalBytes)} GB")
        Log.d("Debugging", "Storage: ${bytesToGB(statFs.availableBytes)} GB")
        Log.d("Debugging", "Storage: ${bytesToGB(used)} GB")


        return statFs
    }

    fun getBatteryTemperature(context: Context): Float {

        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val temp =
            intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0

        return temp / 10f
    }

    fun bytesToGB(bytes: Long): Float {
        return bytes / (1024f * 1024f * 1024f)
    }

    fun Float.format1Digit(): String {
        return String.format("%.1f", this)
    }

    fun getBatteryRemainingTime(
        batteryPercent: Int
    ): String {

        val averageHoursAt100 = 8f

        val remainingHoursDecimal =
            (batteryPercent / 100f) * averageHoursAt100

        val hours = remainingHoursDecimal.toInt()

        val minutes =
            ((remainingHoursDecimal - hours) * 60).toInt()

        return "${hours}h ${minutes}m"
    }

    fun getInstalledAppsInfo(context: Context): InstalledAppsInfo {
        val packageManager = context.packageManager

        // 1. Set up an intent to target all standard launcher apps
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // 2. Query activities using the MATCH_ALL flag to bypass hidden restrictions
        val resolvedInfos =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    mainIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
            }

        // Remove duplicates (some apps register multiple main activities)
        val distinctApps = resolvedInfos.distinctBy { it.activityInfo.packageName }

        var userApps = 0
        var systemApps = 0

        for (resolveInfo in distinctApps) {
            val appInfo = resolveInfo.activityInfo.applicationInfo

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            if (isSystem && !isUpdatedSystem) {
                systemApps++
            } else {
                // This captures all 3rd party apps downloaded from Google Play Console/Store
                // as well as bloatware updated by the user
                userApps++
            }
        }

        return InstalledAppsInfo(
            totalApps = distinctApps.size,
            userApps = userApps,
            systemApps = systemApps
        )
    }

    fun getAppsInfo1(context: Context): InstalledAppsInfo {

        val pm = context.packageManager

        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        var userApps = 0
        var systemApps = 0

        packages.forEach { app ->

            if (shouldIgnorePackage(app.packageName))
                return@forEach

            val hasLauncher =
                pm.getLaunchIntentForPackage(app.packageName) != null

            val isSystem =
                (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val isUpdatedSystem =
                (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            if (!hasLauncher) {
                return@forEach
            }

            if (!isSystem || isUpdatedSystem) {
                userApps++
            } else {
                systemApps++
            }
        }

        return InstalledAppsInfo(
            totalApps = userApps + systemApps,
            userApps = userApps,
            systemApps = systemApps
        )
    }

    private fun shouldIgnorePackage(packageName: String): Boolean {

        val ignorePrefixes = listOf(
            "com.android.cts",
            "com.android.test",
            "com.android.overlay",
            "com.google.android.overlay",
            "com.mediatek",
            "com.android.virtualmachine",
            "com.android.internal",
            "com.android.theme",
            "com.google.mainline",
            "com.google.android.module",
            "com.google.android.ext",
            "com.google.android.cellbroadcast",
            "com.android.wifi.resources",
            "com.android.uwb.resources",
            "com.android.networkstack",
            "com.google.android.networkstack",
            "android.autoinstalls"
        )
        return ignorePrefixes.any {
            packageName.startsWith(it)
        }
    }


    fun getApps(context: Context): AppsResult {

        val pm = context.packageManager

        val installedApps = pm.getInstalledApplications(
            PackageManager.GET_META_DATA
        )

        val userApps = mutableListOf<AppInfo>()
        val systemApps = mutableListOf<AppInfo>()

        installedApps.forEach { app ->

            val appInfo = AppInfo(
                appName = pm.getApplicationLabel(app).toString(),
                packageName = app.packageName,
                isSystemApp = isSystemApp(app)
            )

            if (appInfo.isSystemApp) {
                systemApps.add(appInfo)
            } else {
                userApps.add(appInfo)
            }
        }

        return AppsResult(
            totalApps = installedApps.size,
            userApps = userApps.size,
            systemApps = systemApps.size,
            userAppList = userApps.sortedBy { it.appName },
            systemAppList = systemApps.sortedBy { it.appName }
        )
    }
    private fun isSystemApp(app: ApplicationInfo): Boolean {

        val isSystem =
            (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        val isUpdatedSystem =
            (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

        // Updated system apps are treated as user apps
        return isSystem && !isUpdatedSystem
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun getPlayStoreInstalledAppsCount(context: Context): Int {

        val pm = context.packageManager

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .count { app ->

                try {
                    val installSource = pm.getInstallSourceInfo(app.packageName)

                    installSource.installingPackageName == "com.android.vending"

                } catch (e: Exception) {
                    false
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getPlayStoreApps(context: Context): List<ApplicationInfo> {

        val pm = context.packageManager

        return pm.getInstalledApplications(0).filter { app ->

            try {

                val installSource =
                    pm.getInstallSourceInfo(app.packageName)

                val isPlayStore =
                    installSource.installingPackageName == "com.android.vending"

                val hasLauncher =
                    pm.getLaunchIntentForPackage(app.packageName) != null

                isPlayStore && hasLauncher

            } catch (e: Exception) {
                false
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    fun getPlayStoreInstalledApps(context: Context): List<String> {

        val pm = context.packageManager

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { app ->

                try {
                    val installSource = pm.getInstallSourceInfo(app.packageName)

                    if (installSource.installingPackageName == "com.android.vending") {
                        pm.getApplicationLabel(app).toString()
                    } else {
                        null
                    }

                } catch (e: Exception) {
                    null
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getPlayStoreAppInfo(context: Context): List<AppInfo> {

        val pm = context.packageManager

        return getPlayStoreApps(context).map {

            AppInfo(
                appName = pm.getApplicationLabel(it).toString(),
                packageName = it.packageName,
                isSystemApp = false
            )
        }
    }
    fun getResolution(context: Context): String {
        val metrics = context.resources.displayMetrics
        return "${metrics.widthPixels} x ${metrics.heightPixels}"
    }
    fun getBattery(context: Context): Int {
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        return batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
    }
    fun getStorage(): Pair<String, String> {
        val stat = StatFs(Environment.getDataDirectory().path)

        val total = stat.totalBytes / (1024.0 * 1024 * 1024)
        val free = stat.availableBytes / (1024.0 * 1024 * 1024)

        return Pair(
            String.format("%.2f GB", total),
            String.format("%.2f GB", free)
        )
    }
    fun getTotalRam(context: Context): String {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalGB = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
        return String.format("%.2f GB", totalGB)
    }
    fun getRefreshRate(context: Context): String {

        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            context.display?.refreshRate
        } else {
            // Android 10 and below
            @Suppress("DEPRECATION")
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            @Suppress("DEPRECATION")
            wm.defaultDisplay.refreshRate
        }

        return refreshRate?.let {
            if (it % 1 == 0f) {
                "${it.toInt()} Hz"
            } else {
                String.format("%.1f Hz", it)
            }
        } ?: "Unknown"
    }

    fun getCompleteDeviceInfo(context: Context): List<DeviceInfoModel> {

        val storage = getStorage()

        return listOf(
            DeviceInfoModel("Manufacturer", Build.MANUFACTURER),
            DeviceInfoModel("Brand", Build.BRAND),
            DeviceInfoModel("Model", Build.MODEL),
            DeviceInfoModel("Device", Build.DEVICE),
            DeviceInfoModel("Product", Build.PRODUCT),
            DeviceInfoModel("Build Number", Build.DISPLAY),
            DeviceInfoModel("Board", Build.BOARD),
            DeviceInfoModel("Hardware", Build.HARDWARE),
            DeviceInfoModel("Android Version", Build.VERSION.RELEASE),
            DeviceInfoModel("API Level", Build.VERSION.SDK_INT.toString()),
            DeviceInfoModel("Security Patch", Build.VERSION.SECURITY_PATCH),
            DeviceInfoModel("CPU", Build.SUPPORTED_ABIS.joinToString()),
            DeviceInfoModel("Resolution", getResolution(context)),
            DeviceInfoModel("Refresh Rate", getRefreshRate(context)),
            DeviceInfoModel("Total RAM", getTotalRam(context)),
            DeviceInfoModel("Storage", storage.first),
            DeviceInfoModel("Free Storage", storage.second),
            DeviceInfoModel("Battery", "${getBattery(context)}%"),
            DeviceInfoModel("Kernel", System.getProperty("os.version") ?: "Unknown")
        )
    }

    fun getWifiName(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val info = wifiManager.connectionInfo

        return info.ssid?.replace("\"", "")
    }
    fun getWifiBand(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val freq = wifiManager.connectionInfo.frequency
        val rssi = wifiManager.connectionInfo.rssi
        return when {
            freq in 2400..2500 -> "2.4 GHz"
            freq in 4900..5900 -> "5 GHz"
            freq in 5925..7125 -> "6 GHz"
            else -> "Unknown"
        }
    }

}

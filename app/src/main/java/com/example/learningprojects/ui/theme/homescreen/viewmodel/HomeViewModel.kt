package com.example.learningprojects.ui.theme.homescreen.viewmodel

import android.content.Context
import android.os.Build
import android.os.StatFs
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.learningprojects.ui.theme.homescreen.AppInfo
import com.example.learningprojects.ui.theme.homescreen.AppsResult
import com.example.learningprojects.ui.theme.homescreen.DeviceHealth
import com.example.learningprojects.utils.DeviceHealthManager
import com.example.learningprojects.utils.DeviceHealthManager.bytesToGB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _health =
        MutableStateFlow<DeviceHealth?>(null)

    val health = _health.asStateFlow()

    fun loadHealth(context: Context) {

        val battery =
            DeviceHealthManager.getBatteryPercentage(context)

        val ram = DeviceHealthManager.getRamUsage(context)
        val usedMemory = ram.totalMem - ram.availMem
        val usage = ((usedMemory.toDouble() / ram.totalMem) * 100).toInt()

        val storage = DeviceHealthManager.getStorageUsage()
        val total = storage.totalBytes
        val used = total - storage.availableBytes
        val usageStorage = ((used.toDouble() / total) * 100).toInt()

        val temp =
            DeviceHealthManager.getBatteryTemperature(context)

        val ramPercent =
            ((ram.availMem.toDouble() / ram.totalMem) * 100).toInt()

        val storagePercent =
            ((storage.availableBytes.toDouble() / storage.totalBytes) * 100).toInt()

        val cpuScore = calculateTemperatureScore(temp)

        val overallHealth = (battery + ramPercent + storagePercent + cpuScore) / 4

        _health.value = DeviceHealth(
            battery = battery,
            ramUsage = usage,
            storageUsage = usageStorage,
            batteryTemp = temp,
            overallHealth = overallHealth,
            availableRam = ramPercent,
            availableStorage = storagePercent,

            totalRamGb = bytesToGB(ram.totalMem),
            availableRamGb = bytesToGB(ram.availMem),
            usedRamGb = bytesToGB(usedMemory),

            // Storage
            totalStorageGb = bytesToGB(storage.totalBytes),
            availableStorageGb = bytesToGB(storage.availableBytes),
            usedStorageGB = bytesToGB(used),

            )
    }

    private val _storageInfo =
        MutableStateFlow<StatFs?>(null)

    val storageInfo = _storageInfo.asStateFlow()

    fun getStorageInfo(context: Context){
        val storage = DeviceHealthManager.getStorageUsage()

    }

    private val _appsInfo =
        MutableStateFlow<AppsResult?>(null)

    val appsInfo = _appsInfo.asStateFlow()

    fun loadInstalledApps(context: Context) {
        _appsInfo.value =
            DeviceHealthManager.getApps(context)
    }

    private val _playStoreAppsCount = MutableStateFlow(0)
    val playStoreAppsCount = _playStoreAppsCount.asStateFlow()

    private val _playStoreApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val playStoreApps = _playStoreApps.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.R)
    fun loadAppss(context: Context) {
        _playStoreApps.value = DeviceHealthManager.getPlayStoreAppInfo(context)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun loadApps(context: Context) {
        _playStoreAppsCount.value =
            DeviceHealthManager.getPlayStoreInstalledAppsCount(context)
    }

    private fun calculateTemperatureScore(temp: Float): Int {
        return when {
            temp <= 35 -> 100
            temp <= 40 -> 90
            temp <= 45 -> 75
            temp <= 50 -> 60
            temp <= 55 -> 40
            else -> 20
        }
    }
}
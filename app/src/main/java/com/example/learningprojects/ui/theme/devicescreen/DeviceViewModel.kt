package com.example.learningprojects.ui.theme.devicescreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.learningprojects.utils.DeviceHealthManager.getCompleteDeviceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeviceViewModel : ViewModel() {

    private val _deviceInfo = MutableStateFlow<List<DeviceInfoModel>>(emptyList())
    val deviceInfo: StateFlow<List<DeviceInfoModel>> = _deviceInfo

    fun loadDeviceInfo(context: Context) {
        _deviceInfo.value = getCompleteDeviceInfo(context)
    }

}
package com.example.learningprojects.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat

object WifiInfoHelper {

    @SuppressLint("MissingPermission")
    private fun getWifiInfo(context: Context): WifiInfo? {

        val cm = context.getSystemService(ConnectivityManager::class.java)

        val network = cm.activeNetwork ?: return null
        val capabilities = cm.getNetworkCapabilities(network) ?: return null

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            return null

        return capabilities.transportInfo as? WifiInfo
    }

    @SuppressLint("MissingPermission")
    fun getWifiName(context: Context): String {

        val cm = context.getSystemService(ConnectivityManager::class.java)

        val network = cm.activeNetwork ?: return "No Network"
        val capabilities = cm.getNetworkCapabilities(network) ?: return "No Network"

        // Wi-Fi Connected
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

            val wifiInfo = capabilities.transportInfo as? WifiInfo

            val ssid = wifiInfo?.ssid?.removeSurrounding("\"")

            Log.d("Wifi", "SSID = $ssid")
            Log.d("Wifi", "BSSID = ${wifiInfo?.bssid}")

            if (!ssid.isNullOrBlank() && ssid != "<unknown ssid>") {
                return ssid
            }

            return "Wi-Fi"
        }

        // Mobile Data Connected
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

            val telephony =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val carrier = telephony.simOperatorName

            if (carrier.isNotBlank()) {
                return carrier
            }

            return "Mobile Data"
        }

        return "No Network"
    }

    fun getWifiBand(context: Context): String {

        val frequency = getWifiInfo(context)?.frequency ?: return "Unknown"

        return when (frequency) {
            in 2400..2500 -> "2.4 GHz"
            in 4900..5900 -> "5 GHz"
            in 5925..7125 -> "6 GHz"
            else -> "Unknown"
        }
    }

    fun getFrequency(context: Context): Int {
        return getWifiInfo(context)?.frequency ?: 0
    }

    fun getRssi(context: Context): Int {
        return getWifiInfo(context)?.rssi ?: 0
    }

    fun getLinkSpeed(context: Context): Int {
        return getWifiInfo(context)?.linkSpeed ?: 0
    }

    fun getTxSpeed(context: Context): Int {
        return getWifiInfo(context)?.txLinkSpeedMbps ?: 0
    }

    fun getRxSpeed(context: Context): Int {
        return getWifiInfo(context)?.rxLinkSpeedMbps ?: 0
    }

    fun getWifiStandard(context: Context): String {

        val wifiInfo = getWifiInfo(context) ?: return "Unknown"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (wifiInfo.wifiStandard) {
                ScanResult.WIFI_STANDARD_LEGACY -> "Legacy"
                ScanResult.WIFI_STANDARD_11N -> "WiFi 4"
                ScanResult.WIFI_STANDARD_11AC -> "WiFi 5"
                ScanResult.WIFI_STANDARD_11AX -> "WiFi 6"
                ScanResult.WIFI_STANDARD_11BE -> "WiFi 7"
                else -> "Unknown"
            }
        } else {
            "Unknown"
        }
    }

    @SuppressLint("MissingPermission")
    fun registerWifiCallback(
        context: Context,
        onWifiChanged: (WifiInfo?) -> Unit
    ): ConnectivityManager.NetworkCallback {

        val cm = context.getSystemService(ConnectivityManager::class.java)

        val callback = object : ConnectivityManager.NetworkCallback(
            ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO
        ) {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
                onWifiChanged(wifiInfo)
            }
        }

        cm.registerDefaultNetworkCallback(callback)

        return callback
    }

    fun hasWifiPermission(context: Context): Boolean {

        val location = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val nearby = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return location && nearby
    }
}
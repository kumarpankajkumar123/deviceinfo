package com.example.learningprojects.ui.theme.homescreen

data class WifiUiState(
    val wifiName: String = "",
    val band: String = "",
    val frequency: Int = 0,
    val rssi: Int = 0,
    val linkSpeed: Int = 0,
    val txSpeed: Int = 0,
    val rxSpeed: Int = 0,
    val standard: String = ""
)
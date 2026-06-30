package com.example.learningprojects.ui.theme.navigation

import com.example.learningprojects.R


sealed class Screen(val route: String, val title: String, val iconRes: Int) {
    object Home : Screen("home", "Home", iconRes = R.drawable.home_24dp_01147b___fill0_wght400_grad0_opsz24)
    object App : Screen("app", "App", R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24)
    object Sensors : Screen("sensors", "Sensors", R.drawable.vital_signs_24dp_01147b___fill0_wght400_grad0_opsz24)
    object Device : Screen("device", "Device", R.drawable.mobile_2_24dp_01147b___fill0_wght400_grad0_opsz24)
}
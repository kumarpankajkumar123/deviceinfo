package com.example.learningprojects.ui.theme.navigation.navitiongraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.learningprojects.ui.theme.appscreen.AppScreen
import com.example.learningprojects.ui.theme.devicescreen.DeviceScreen
import com.example.learningprojects.ui.theme.homescreen.HomeScreen
import com.example.learningprojects.ui.theme.navigation.Screen
import com.example.learningprojects.ui.theme.sensorscreen.SensorsScreens

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.App.route) { AppScreen() }
        composable(Screen.Sensors.route) { SensorsScreens() }
        composable(Screen.Device.route) { DeviceScreen() }
    }
}
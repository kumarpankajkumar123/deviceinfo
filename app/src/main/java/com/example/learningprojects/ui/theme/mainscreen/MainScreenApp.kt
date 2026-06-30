package com.example.learningprojects.ui.theme.mainscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.DarkGrayText
import com.example.learningprojects.ui.theme.LightGrayText
import com.example.learningprojects.ui.theme.navigation.Screen
import com.example.learningprojects.ui.theme.navigation.navitiongraph.NavigationGraph

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenApp(isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.App, Screen.Sensors, Screen.Device)

    val selectedColor = MaterialTheme.colorScheme.primary // AccentGreen (0xFF4CAF50)
    val unselectedColor = if (isDarkMode) LightGrayText else DarkGrayText
//    val scrollBehavior1 = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollBehavior1 = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
//        topBar =
//            {
//                TopAppBar(
//                    modifier = Modifier
//                        .padding(top = 10.dp, end = 16.dp),
//                    title = {},
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.background,
//                        scrolledContainerColor = MaterialTheme.colorScheme.background
//                    ),
//                    actions = {
//                        Box(
//                            Modifier
//                                .size(40.dp)
//                                .background(
//                                    MaterialTheme.colorScheme.surfaceVariant,
//                                    CircleShape
//                                ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Image(
//                                painter = if (isDarkMode) painterResource(id =  R.drawable.outline_dark_mode_24)else painterResource(id = R.drawable.outline_light_mode_24),
//                                contentDescription = null,
//                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
//                                modifier = Modifier
//                                    .size(24.dp)
//                                    .clickable { onThemeToggle() }
//                            )
//                        }
//                    },
//                    scrollBehavior = scrollBehavior1
//                )
//            },
        bottomBar = {
            // CUSTOM BOTTOM NAVIGATION START
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface // Light/Dark theme par auto color switch hoga
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        // Smooth color change animation
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) selectedColor else unselectedColor,
                            label = "color"
                        )

                        // COLUMN LAYOUT FOR EACH TAB ITEM
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // Ripple effect hatane ke liye
                            ) {
                                if (!isSelected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }) {
                            // 1. ICON
                            Image(
                                painter = painterResource(screen.iconRes),
                                contentDescription = screen.title,
                                colorFilter = ColorFilter.tint(contentColor),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // 2. SMALL TITLE TEXT
                            Text(
                                text = screen.title,
                                color = contentColor,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // 3. SMALL SELECTION DOT (Sirf tab select hone par hi visible hoga)
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        color = if (isSelected) selectedColor else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
            // CUSTOM BOTTOM NAVIGATION END
        }) { innerPadding ->
        // Navigation Graph jo content ko padding ke andar perfectly set rakhega
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavigationGraph(
                navController = navController,
            )


            FloatingActionButton(
                onClick = { onThemeToggle() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 16.dp)
                    .size(40.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.background,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    painter = painterResource(
                        if (isDarkMode) R.drawable.outline_dark_mode_24
                        else R.drawable.outline_light_mode_24
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun PreviewMainScreen() {
    MainScreenApp(false) {

    }
}
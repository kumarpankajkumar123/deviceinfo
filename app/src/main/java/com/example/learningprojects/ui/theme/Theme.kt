package com.example.learningprojects.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = progressCircularDark,
    background = PureBlack,
    surface = BottomNavigationDark,      // #0D1527 -> Deep Midnight Blue
    onBackground = whiteColor,
    onSurface = darkContainerColor,
    tertiaryContainer = progressCircularDark,
    secondary = progressCircularDark,
    onSecondary = filterSelectedColorDark


)

private val LightColorScheme = lightColorScheme(
    primary = progressCircularLight,
    background = PureWhite,
    surface = BottomNavigationLight,
    onBackground = blackColor,
    onSurface = lightContainerColor,
    tertiaryContainer = bannerColor2,
    secondary = progressCircularLight,
    onSecondary = filterSelectedColorLight
)

@Composable
fun LearningProjectsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme1 = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
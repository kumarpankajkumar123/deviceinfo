package com.example.learningprojects.ui.theme.appscreen

data class AppUsageItem(
    val title: String,
    val value: String,
    val progress: Float,      // 0f to 100f
    val color: androidx.compose.ui.graphics.Color
)


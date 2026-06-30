package com.example.learningprojects.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Utils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatSecurityPatch(date: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

        val localDate = LocalDate.parse(date, inputFormatter)
        return localDate.format(outputFormatter)
    }
}
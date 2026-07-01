package com.example.learningprojects.ui.theme.appscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.lightGrayHome

@Composable
fun AppTypeLayout(
    name: String, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = Color.LightGray.copy(alpha = 0.5f),
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.onSecondary
                else MaterialTheme.colorScheme.background
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center) {
        CommonText(
            text = name,
            fontFamily = if (isSelected) FontFamily(Font(R.font.google_sans_bold)) else FontFamily(
                Font(R.font.regular)
            ),
            fontSize = 15.sp,
            fontWeight = FontWeight.W400,
            color = if (isSelected) MaterialTheme.colorScheme.background
            else lightGrayHome,
            modifier = Modifier.wrapContentSize()
        )
    }
}
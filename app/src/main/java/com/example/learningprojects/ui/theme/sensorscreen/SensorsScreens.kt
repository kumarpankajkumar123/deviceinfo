package com.example.learningprojects.ui.theme.sensorscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.AccentGreen
import com.example.learningprojects.ui.theme.LightGrayText
import com.example.learningprojects.ui.theme.commonusablecomponent.BlinkingDot
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SensorsScreens() {

    val list = listOf<SensorItemModel>(
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
        SensorItemModel(
            name = "Accelerometer",
            Quantity = "9.81 m/s²",
            status = "X: 0.12 Y: −0.08 Z: 9.81",
            image = R.drawable.icons8_voice_64
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CommonText(
                text = "Sensors Center",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.wrapContentSize(),
            )

            Spacer(Modifier.height(10.dp))
            CommonText(
                text = "8 sensors active All nominal",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.wrapContentSize(),
                isSingleLine = true
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = LightGrayText,
                    shape = RoundedCornerShape(15.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlinkingDot(
                    color = AccentGreen
                )
                CommonText(
                    text = "All sensors online",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.wrapContentSize(),
                    isSingleLine = true
                )
            }
            CommonText(
                text = "just update now",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.wrapContentSize(),
                isSingleLine = true
            )

        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { item ->
                SensorsItemLayout(item)
            }
        }
    }

}

@Preview
@Composable
fun PreviewSensorsScreen() {

}
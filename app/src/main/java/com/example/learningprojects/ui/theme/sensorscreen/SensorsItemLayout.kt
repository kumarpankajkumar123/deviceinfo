package com.example.learningprojects.ui.theme.sensorscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.AccentGreen
import com.example.learningprojects.ui.theme.DarkGrayText
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText

@Composable
fun SensorsItemLayout(
    item: SensorItemModel
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        _root_ide_package_.com.example.learningprojects.ui.theme.LightGrayText.copy(
                            alpha = 0.15f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icons8_info_32),
                    contentDescription = "battery",
                    colorFilter = ColorFilter.tint(DarkGrayText),
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(AccentGreen, CircleShape)
            )
//            CommonText(
//                text = item.batteryTime,
//                fontFamily = FontFamily(Font(resId = R.font.regular)),
//                fontSize = 13.sp,
//                fontWeight = FontWeight.W400,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.wrapContentSize()
//            )
        }

        Spacer(Modifier.height(10.dp))
        CommonText(
            text = item.Quantity,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.wrapContentSize(),
        )
        Spacer(Modifier.height(5.dp))

        CommonText(
            text = item.name,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(resId = R.font.regular)),
            fontWeight = FontWeight.W400,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.wrapContentSize(),
        )
        Spacer(Modifier.height(15.dp))
        CommonText(
            text = item.status,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(resId = R.font.regular)),
            fontWeight = FontWeight.W400,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.wrapContentSize(),
        )

    }
}
package com.example.learningprojects.ui.theme.devicescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.LightGrayText
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText

@Composable
fun DeviceItemLayout(
    item: DeviceInfoModel,
    showDivider: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CommonText(
                text = item.modelName,
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(resId = R.font.regular)),
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 16.dp),
            )
            Spacer(Modifier.width(10.dp))
            CommonText(
                text = item.modelValue,
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 16.dp),
                isSingleLine = true
            )
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = LightGrayText
            )
        }
    }

}
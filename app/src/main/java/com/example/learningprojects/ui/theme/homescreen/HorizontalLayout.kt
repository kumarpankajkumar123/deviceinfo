package com.example.learningprojects.ui.theme.homescreen

import android.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText

@Composable
fun HorizontalLayout(item: DeviceFeactures) {

    Column(
        modifier = Modifier
            .width(95.dp)
            .height(120.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(vertical = 20.dp, horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(item.color.copy(alpha = 0.15f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.batteryImage),
                contentDescription = "null",
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(item.color),
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        CommonText(
            text = item.title,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(resId = R.font.regular)),
            fontWeight = FontWeight.W400,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.wrapContentSize(),
            isSingleLine = true
        )

    }

}


@Preview
@Composable
fun HorizontalLayoutPreview() {
//    HorizontalLayout()
}
package com.example.learningprojects.ui.theme.appscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText

@Composable
fun AppShowLayout(name : String){

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                .fillMaxWidth()
        ) {

        }


        CommonText(
            text = name,
            fontSize = 13.sp,
            fontFamily = FontFamily(Font(resId = R.font.regular)),
            fontWeight = FontWeight.W400,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.wrapContentSize(),
        )
    }

}
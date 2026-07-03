package com.example.learningprojects.ui.theme.appscreen

import android.widget.Space
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningprojects.R
import com.example.learningprojects.ui.theme.AccentGreen
import com.example.learningprojects.ui.theme.PureWhite
import com.example.learningprojects.ui.theme.commonusablecomponent.CommonText
import com.example.learningprojects.ui.theme.homescreen.CustomLinearProgressBar
import com.example.learningprojects.ui.theme.lightGrayHome

@Composable
fun AppShowLayout(name: String) {


    val rowList = listOf(
        AppUsageItem(
            title = "Storage",
            value = "5.2 GB",
            progress = 80f,
            color = Color(0xFF4FC3F7)
        ),
        AppUsageItem(
            title = "RAM",
            value = "390 MB",
            progress = 55f,
            color = Color(0xFF9C6BFF)
        ),
        AppUsageItem(
            title = "Battery",
            value = "12%",
            progress = 12f,
            color = Color(0xFFFF7043)
        )
    )

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
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
            .background(MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            )
            {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            shape = RoundedCornerShape(10.dp),
                            color = PureWhite
                        )
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.database_24dp_01147b___fill0_wght400_grad0_opsz24),
                        contentDescription = "null",
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    CommonText(
                        text = name,
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(resId = R.font.google_sans_bold)),
                        fontWeight = FontWeight.W800,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.wrapContentSize(),
                    )
                    CommonText(
                        text = name,
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(resId = R.font.regular17pt)),
                        fontWeight = FontWeight.W400,
                        color = lightGrayHome,
                        modifier = Modifier.wrapContentSize(),
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sharp_access_time_24),
                            contentDescription = "null",
                            colorFilter = ColorFilter.tint(color = lightGrayHome),
                            modifier = Modifier.size(
                                15.dp
                            )
                        )
                        CommonText(
                            text = name,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(resId = R.font.regular)),
                            fontWeight = FontWeight.W400,
                            color = lightGrayHome,
                            modifier = Modifier.wrapContentSize(),
                        )
                    }

                }
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.background
                    )
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            ) {
                CommonText(
                    text = "Battery",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.wrapContentSize(),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowList.forEach { item ->
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    AppProgressShow(item)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .border(
                        width = 0.5.dp,
                        color = lightGrayHome,
                        shape = CircleShape
                    )
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                CommonText(
                    text = "Cache Memory",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                    fontWeight = FontWeight.W600,
                    color = lightGrayHome,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .border(
                        width = 0.5.dp,
                        color = lightGrayHome,
                        shape = CircleShape
                    )
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                CommonText(
                    text = "View Details",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                    fontWeight = FontWeight.W600,
                    color = lightGrayHome,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }

        }
    }
}


@Composable
fun AppProgressShow(item: AppUsageItem) {
    val animatedProgress by animateFloatAsState(
        targetValue = 80f,
        animationSpec = tween(1000),
        label = ""
    )

    Column(
        modifier = Modifier
            .width(110.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CommonText(
                text = item.title,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                fontWeight = FontWeight.W600,
                color = lightGrayHome,
                modifier = Modifier.wrapContentSize(),
            )
            CommonText(
                text = item.value,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(resId = R.font.semi_bold)),
                fontWeight = FontWeight.W600,
                color = item.color,
                modifier = Modifier.wrapContentSize(),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CustomLinearProgressBar(
            progress = animatedProgress,
            progressColor = item.color,
            modifier = Modifier
                .wrapContentSize()
                .height(4.dp)
                .padding(0.dp)
        )

    }
}

@Preview
@Composable
fun PreviewDemo() {
    AppShowLayout("pankaj")
}
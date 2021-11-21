package com.agelousis.payments.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.agelousis.payments.R

private val fonts = FontFamily(
    Font(R.font.ubuntu_regular),
    Font(R.font.ubuntu_bold, weight = FontWeight.Bold),
    Font(R.font.ubuntu_light, weight = FontWeight.Light),
    Font(R.font.ubuntu_light, weight = FontWeight.Thin),
    Font(R.font.ubuntu, weight = FontWeight.Normal, style = FontStyle.Italic)
)

val textViewTitleFont = TextStyle(
    fontFamily = FontFamily(Font(R.font.ubuntu_regular)),
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)

val textViewTitleLabelFont = TextStyle(
    fontFamily = FontFamily(Font(R.font.ubuntu_regular)),
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp
)

val textViewLabel = TextStyle(
    fontFamily = FontFamily(Font(R.font.ubuntu_light)),
    fontWeight = FontWeight.Light,
    fontSize = 12.sp
)

val textViewValueLabel = TextStyle(
    fontFamily = FontFamily(Font(R.font.ubuntu_light)),
    fontWeight = FontWeight.Light,
    fontSize = 14.sp
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    body2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    h2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp
    ),
    h3 = TextStyle(
        fontFamily = fonts,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp
    )
)
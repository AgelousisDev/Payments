package com.agelousis.payments.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

val ColorAccent = Color(0xFF1E88E5)

val DarkColorPalette = darkColors(
    primary = ColorAccent,
    primaryVariant = ColorAccent,
    secondary = ColorAccent,
)

val LightColorPalette = lightColors(
    primary = ColorAccent,
    primaryVariant = ColorAccent,
    secondary = ColorAccent,
)

@Composable
fun appColors() =
    when {
        // Material You colors for Android 12+
        Build.VERSION.SDK_INT >= 31 -> {
            val mainDark700 = colorResource(android.R.color.system_accent1_700)
            val secondary200 = colorResource(android.R.color.system_accent2_200)
            when {
                isSystemInDarkTheme() -> darkColors(
                    primary = colorResource(android.R.color.system_accent1_200),
                    primaryVariant = mainDark700,
                    secondary = secondary200,
                )
                else -> lightColors(
                    primary = colorResource(android.R.color.system_accent1_500),
                    primaryVariant = mainDark700,
                    secondary = secondary200,
                )
            }
        }
        isSystemInDarkTheme() -> DarkColorPalette
        else -> LightColorPalette
    }

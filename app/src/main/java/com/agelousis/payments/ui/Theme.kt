package com.agelousis.payments.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.agelousis.payments.R

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
fun systemAccentColor() =
    when {
        // Material You colors for Android 12+
        Build.VERSION.SDK_INT >= 31 -> {
            when {
                isSystemInDarkTheme() ->
                    colorResource(android.R.color.system_accent1_700)
                else ->
                    colorResource(android.R.color.system_accent2_200)
            }
        }
        else ->
            colorResource(
                id = R.color.dayNightTextOnBackground
            )
    }

@Composable
fun appColorScheme() =
    when {
        Build.VERSION.SDK_INT >= 31 ->
            when {
                isSystemInDarkTheme() -> dynamicDarkColorScheme(LocalContext.current)
                else -> dynamicLightColorScheme(LocalContext.current)
            }
        else ->
            when {
                isSystemInDarkTheme() ->
                    darkColorScheme()
                else ->
                    lightColorScheme()
            }
    }


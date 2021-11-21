package com.agelousis.payments.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R

@Composable
fun BottomSheetNavigationLine() =
    Divider(
        thickness = 2.5.dp,
        modifier = Modifier.padding(
            top = 12.dp,
            bottom = 8.dp
        ).width(
            width = 20.dp
        ).background(
            color = colorResource(id = R.color.grey),
            shape = RoundedCornerShape(50)
        )
    )
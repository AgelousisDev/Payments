package com.agelousis.payments.ui.rows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.ui.textViewLabelFont

@Composable
fun HeaderRowLayout(
    headerModel: HeaderModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                height = 20.dp
            )
            .background(
                color = headerModel.headerBackgroundColor?.let { Color(it) } ?: colorResource(
                    id = R.color.whiteTwo
                )
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = headerModel.header,
            style = textViewLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp
                )
        )
    }
}
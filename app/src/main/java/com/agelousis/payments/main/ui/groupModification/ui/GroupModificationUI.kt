package com.agelousis.payments.main.ui.groupModification.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R

@Composable
fun GroupModificationLayout() {
    Column {
        Image(
            painter = painterResource(
                R.drawable.ic_group
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = 150.dp
                )
        )
    }
}
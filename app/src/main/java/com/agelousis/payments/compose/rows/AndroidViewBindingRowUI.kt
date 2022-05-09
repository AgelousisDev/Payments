package com.agelousis.payments.compose.rows

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.EmptyModel

@Composable
fun EmptyRowLayout(
    emptyModel: EmptyModel,
    modifier: Modifier
) {
    AndroidViewBinding(
        factory = EmptyRowLayoutBinding::inflate,
        modifier = modifier
    ) {
        this.emptyModel = emptyModel
    }
}
package com.agelousis.payments.ui.composableViews

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont
import com.agelousis.payments.ui.textViewValueLabelFont
import com.agelousis.payments.utils.models.SimpleDialogDataModel

@Composable
fun BocSimpleDialog(
    show: Boolean,
    simpleDialogDataModel: SimpleDialogDataModel
) {
    if (show)
        AlertDialog(
            onDismissRequest = simpleDialogDataModel.dismissBlock ?: {},
            title = {
                Text(
                    text = simpleDialogDataModel.title,
                    style = textViewTitleLabelFont
                )
            },
            text = {
                Text(
                    text = simpleDialogDataModel.message,
                    style = textViewLabelFont
                )
            },
            confirmButton = {
                TextButton(
                    onClick = simpleDialogDataModel.positiveButtonBlock ?: {}
                ) {
                    Text(
                        text = simpleDialogDataModel.positiveButtonText ?: stringResource(id = R.string.key_ok_label),
                        style = textViewValueLabelFont
                    )
                }
            },
            dismissButton = {
                if (simpleDialogDataModel.negativeButtonText != null
                    && simpleDialogDataModel.negativeButtonBlock != null
                )
                    TextButton(
                        onClick = simpleDialogDataModel.negativeButtonBlock
                    ) {
                        Text(
                            text = simpleDialogDataModel.negativeButtonText
                        )
                    }
            },
            shape = RoundedCornerShape(
                size = 8.dp
            )
        )
}
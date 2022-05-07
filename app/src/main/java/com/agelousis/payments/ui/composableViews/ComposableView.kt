package com.agelousis.payments.ui.composableViews

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.ui.*
import com.agelousis.payments.utils.models.SimpleDialogDataModel

@Composable
fun SimpleDialog(
    show: Boolean,
    simpleDialogDataModel: SimpleDialogDataModel
) {
    if (show)
        AlertDialog(
            onDismissRequest = simpleDialogDataModel.dismissBlock ?: {},
            title = {
                Text(
                    text = simpleDialogDataModel.title,
                    style = textViewAlertTitleFont
                )
            },
            text = {
                Text(
                    text = simpleDialogDataModel.message,
                    style = textViewAlertLabelFont
                )
            },
            confirmButton = {
                TextButton(
                    onClick = simpleDialogDataModel.positiveButtonBlock ?: {}
                ) {
                    Text(
                        text = simpleDialogDataModel.positiveButtonText ?: stringResource(id = R.string.key_ok_label),
                        style = textViewAlertLabelFont
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
                            text = simpleDialogDataModel.negativeButtonText,
                            style = textViewAlertLabelFont
                        )
                    }
            },
            shape = RoundedCornerShape(
                size = 16.dp
            )
        )
}
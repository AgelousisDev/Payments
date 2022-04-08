package com.agelousis.payments.utils.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.agelousis.payments.R
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

typealias PositiveButtonBlock = () -> Unit
typealias ItemPositionDialogBlock = (Int) -> Unit

fun Context.showTwoButtonsDialog(simpleDialogDataModel: SimpleDialogDataModel) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(simpleDialogDataModel.title)
        .setMessage(simpleDialogDataModel.message)
        .setIcon(simpleDialogDataModel.icon ?: 0)
        .setCancelable(simpleDialogDataModel.isCancellable ?: true)
        .setNegativeButton(simpleDialogDataModel.negativeButtonText ?: resources.getString(R.string.key_cancel_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            simpleDialogDataModel.negativeButtonBlock?.invoke()
        }
        .setPositiveButton(simpleDialogDataModel.positiveButtonText ?: resources.getString(R.string.key_ok_label)) { _, _ ->
            simpleDialogDataModel.positiveButtonBlock?.invoke()
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    simpleDialogDataModel.positiveButtonBackgroundColor?.let { positiveButtonBackgroundColor ->
        materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(positiveButtonBackgroundColor)
    }
}

fun Context.showSimpleDialog(simpleDialogDataModel: SimpleDialogDataModel) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(simpleDialogDataModel.title)
        .setCancelable(simpleDialogDataModel.isCancellable ?: true)
        .setMessage(simpleDialogDataModel.message)
        .setIcon(simpleDialogDataModel.icon ?: 0)
        .setPositiveButton(simpleDialogDataModel.positiveButtonText ?: resources.getString(R.string.key_ok_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            simpleDialogDataModel.positiveButtonBlock?.invoke()
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    simpleDialogDataModel.positiveButtonBackgroundColor?.let { positiveButtonBackgroundColor ->
        materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(positiveButtonBackgroundColor)
    }
}

fun Context.showListDialog(
    title: String,
    items: List<String>,
    isCancellable: Boolean = true,
    itemPositionDialogBlock: ItemPositionDialogBlock
) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setCancelable(isCancellable)
        .setItems(items.toTypedArray()) { p0, p1 ->
            p0.dismiss()
            itemPositionDialogBlock(p1)
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
}
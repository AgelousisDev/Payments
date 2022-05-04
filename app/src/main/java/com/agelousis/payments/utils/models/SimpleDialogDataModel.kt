package com.agelousis.payments.utils.models

import com.agelousis.payments.utils.extensions.PositiveButtonBlock

data class SimpleDialogDataModel(
    val title: String,
    val message: String,
    val icon: Int? = null,
    val isCancellable: Boolean? = null,
    val positiveButtonText: String? = null,
    val positiveButtonBackgroundColor: Int? = null,
    val positiveButtonBlock: PositiveButtonBlock? = null,
    val negativeButtonText: String? = null,
    val negativeButtonBlock: PositiveButtonBlock? = null,
    val dismissBlock: PositiveButtonBlock? = null,
)
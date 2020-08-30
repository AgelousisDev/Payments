package com.agelousis.monthlyfees.main.ui.payments.models

import androidx.annotation.DrawableRes

data class EmptyModel(
    val title: String? = null,
    val message: String? = null,
    @DrawableRes var imageIconResource: Int? = null
)
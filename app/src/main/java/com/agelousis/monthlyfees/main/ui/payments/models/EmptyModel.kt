package com.agelousis.monthlyfees.main.ui.payments.models

import androidx.annotation.DrawableRes

data class EmptyModel(val text: String, val emptyStateImageIsVisible: Boolean = true, @DrawableRes var imageIconResource: Int? = null)
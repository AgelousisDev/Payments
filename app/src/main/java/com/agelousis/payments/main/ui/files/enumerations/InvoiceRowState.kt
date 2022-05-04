package com.agelousis.payments.main.ui.files.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

enum class InvoiceRowState {
    NORMAL, SELECTED;

    val backgroundColor
        get() = when(this) {
            NORMAL -> R.color.whiteTwo
            SELECTED -> R.color.lightRed
        }

    fun getBackgroundColor(context: Context) =
        when(this) {
            NORMAL -> ContextCompat.getColor(context, R.color.whiteTwo)
            SELECTED -> ContextCompat.getColor(context, R.color.lightRed)
        }

    val other
        get() = when(this) {
            NORMAL -> SELECTED
            SELECTED -> NORMAL
        }

}
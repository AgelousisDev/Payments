package com.agelousis.payments.main.ui.files.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

enum class FileRowState {
    NORMAL, SELECTED;

    fun getBackgroundColor(context: Context) =
        when(this) {
            NORMAL -> ContextCompat.getColor(context, R.color.white)
            SELECTED -> ContextCompat.getColor(context, R.color.lightRed)
        }

    val other
        get() = when(this) {
            NORMAL -> SELECTED
            SELECTED -> NORMAL
        }

}
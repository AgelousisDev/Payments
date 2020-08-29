package com.agelousis.monthlyfees.main.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.utils.extensions.fromVector

enum class SwipeItemType {
    PERSON_ITEM,
    PDF_ITEM;

    fun getColors(context: Context) =
        intArrayOf(
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.colorAccent)
        )

    fun getIcons(context: Context) =
        when(this) {
            PERSON_ITEM ->
                arrayOf(
                    ContextCompat.getDrawable(context, R.drawable.ic_delete)?.fromVector(padding = 0),
                    ContextCompat.getDrawable(context, R.drawable.ic_pdf)?.fromVector(padding = 0)
                )
            PDF_ITEM ->
                arrayOf(
                    ContextCompat.getDrawable(context, R.drawable.ic_delete)?.fromVector(padding = 0),
                    ContextCompat.getDrawable(context, R.drawable.ic_share)?.fromVector(padding = 0)
                )
        }

}
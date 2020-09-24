package com.agelousis.payments.main.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.utils.extensions.fromVector

enum class SwipeItemType {
    PERSON_ITEM,
    PDF_ITEM;

    fun getIcons(context: Context) =
        arrayOf(
            ContextCompat.getDrawable(context, R.drawable.ic_delete)?.also {
                it.setTint(ContextCompat.getColor(context, R.color.red))
            }?.fromVector(padding = 0),
            ContextCompat.getDrawable(context, R.drawable.ic_pdf)?.also {
                it.setTint(ContextCompat.getColor(context, R.color.colorAccent))
            }?.fromVector(padding = 0)
        )

}
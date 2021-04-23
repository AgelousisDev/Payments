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
            ContextCompat.getDrawable(context, R.drawable.ic_delete)?.fromVector(padding = 0),
            ContextCompat.getDrawable(
                context,
                when(this) {
                    PERSON_ITEM -> R.drawable.ic_invoice
                    PDF_ITEM -> R.drawable.ic_share
                }
            )?.fromVector(padding = 0)
        )

}
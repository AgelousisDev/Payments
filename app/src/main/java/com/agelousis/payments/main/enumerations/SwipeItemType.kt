package com.agelousis.payments.main.enumerations

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.utils.extensions.fromVector

enum class SwipeItemType {
    BALANCE_OVERVIEW_ITEM,
    CLIENT_ITEM,
    PDF_ITEM;

    fun getIcons(context: Context): Array<Bitmap?> {

        return arrayOf(
            ContextCompat.getDrawable(context, R.drawable.ic_delete)?.fromVector(padding = 0),
            ContextCompat.getDrawable(
                context,
                when (this) {
                    CLIENT_ITEM -> R.drawable.ic_invoice
                    PDF_ITEM -> R.drawable.ic_share
                    else -> return arrayOf()
                }
            )?.fromVector(padding = 0)
        )
    }

}
package com.agelousis.payments.main.enumerations

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.agelousis.payments.R
import com.agelousis.payments.utils.extensions.fromVector

enum class SwipeItemType {
    PERSON_ITEM,
    PDF_ITEM;

    fun getColors(context: Context) =
        when(this) {
            PERSON_ITEM -> null
            PDF_ITEM ->
                intArrayOf(
                    ContextCompat.getColor(context, R.color.red),
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
        }

    fun getIcons(context: Context) =
        when(this) {
            PERSON_ITEM ->
                arrayOf(
                    ContextCompat.getDrawable(context, R.drawable.ic_delete)?.also {
                        it.setTint(ContextCompat.getColor(context, R.color.red))
                    }?.fromVector(padding = 0),
                    ContextCompat.getDrawable(context, R.drawable.ic_pdf)?.also {
                        it.setTint(ContextCompat.getColor(context, R.color.colorAccent))
                    }?.fromVector(padding = 0)
                )
            PDF_ITEM ->
                arrayOf(
                    ContextCompat.getDrawable(context, R.drawable.ic_delete)?.fromVector(padding = 0),
                    ContextCompat.getDrawable(context, R.drawable.ic_share)?.fromVector(padding = 0)
                )
        }

}
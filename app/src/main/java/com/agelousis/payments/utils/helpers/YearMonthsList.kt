package com.agelousis.payments.utils.helpers

import android.content.Context
import com.agelousis.payments.R
import java.util.*
import kotlin.collections.ArrayList

class YearMonthsList(private val context: Context) {

    val formattedMonths by lazy {
        val months = arrayListOf<String>()
        for (month in context.resources.getStringArray(R.array.key_months_array)) {
            months.add(
                String.format(
                    "%s %d",
                    month,
                    Calendar.getInstance().get(Calendar.YEAR)
                )
            )
        }
        months
    }

}
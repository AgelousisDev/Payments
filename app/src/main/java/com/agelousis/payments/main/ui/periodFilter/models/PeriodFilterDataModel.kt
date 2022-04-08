package com.agelousis.payments.main.ui.periodFilter.models

import android.os.Parcelable
import com.agelousis.payments.utils.constants.Constants
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class PeriodFilterDataModel(val minimumMonthDate: Date?,
                                 val maximumMonthDate: Date?
): Parcelable {

    val minimumMonthDateValue
        get() = minimumMonthDate?.let { SimpleDateFormat(Constants.MONTH_DATE_FORMAT, Locale.US).format(it) }

    val maximumMonthDateValue
        get() = maximumMonthDate?.let { SimpleDateFormat(Constants.MONTH_DATE_FORMAT, Locale.US).format(it) }

}
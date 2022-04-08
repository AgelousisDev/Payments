package com.agelousis.payments.views.dateLayout.models

import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerListener
import java.util.*

data class YearMonthPickerDataModel(
    val calendar: Calendar,
    var yearMonthPickerListener: YearMonthPickerListener
)
package com.agelousis.payments.views.dateLayout.extensions

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.formattedDateWith
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("yearMonthFormattedDate")
fun setYearMonthFormattedDate(materialTextView: MaterialTextView, yearMonthPickerDataModel: YearMonthPickerDataModel?) {
    materialTextView.text = yearMonthPickerDataModel?.calendar?.time?.formattedDateWith(
        pattern = Constants.YEAR_MONTH_DATE_FORMAT
    ) ?: return
}
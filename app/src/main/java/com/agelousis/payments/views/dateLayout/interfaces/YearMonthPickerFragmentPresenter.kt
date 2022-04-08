package com.agelousis.payments.views.dateLayout.interfaces

interface YearMonthPickerFragmentPresenter {
    fun onApply()
    fun onYearSet(year: Int)
    fun onMonthSet(month: Int, adapterPosition: Int)
}
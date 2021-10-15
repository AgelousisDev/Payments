package com.agelousis.payments.views.extensions

import androidx.databinding.BindingAdapter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.formattedDateWith
import com.agelousis.payments.views.currencyEditText.CurrencyFieldLayout
import com.agelousis.payments.views.dateLayout.DateFieldLayout
import com.agelousis.payments.views.dateLayout.YearMonthPickerFieldLayout
import com.agelousis.payments.views.detailsSwitch.DetailsAppSwitch
import com.agelousis.payments.views.personDetailsLayout.PersonDetailsLayout
import com.agelousis.payments.views.personDetailsLayout.PersonDetailsPickerLayout
import com.agelousis.payments.views.searchLayout.MaterialSearchView
import java.util.*

@BindingAdapter("personDetails")
fun setPersonDetails(personDetailsLayout: PersonDetailsLayout, details: String?) {
    details?.let {
        personDetailsLayout.value = it
    }
}

@BindingAdapter("personDetailsPicker")
fun setPersonPickerDetails(personDetailsPickerLayout: PersonDetailsPickerLayout, details: String?) {
    details?.let {
        personDetailsPickerLayout.value = it
    }
}

@BindingAdapter("personDetailsClickListener")
fun setPersonDetailsClickListener(personDetailsPickerLayout: PersonDetailsPickerLayout, block: () -> Unit) {
    personDetailsPickerLayout.setOnDetailsPressed(
        listener = {
            block()
        }
    )
}

@BindingAdapter("currencyAmount")
fun setCurrencyAmount(currencyFieldLayout: CurrencyFieldLayout, amount: Double?) {
    amount?.let {
        currencyFieldLayout.doubleValue = it
    }
}

val showingCurrentDate
    get() = Date() formattedDateWith Constants.GENERAL_DATE_FORMAT

@BindingAdapter("dateValue")
fun setDateValue(dateFieldLayout: DateFieldLayout, date: String?) {
    date?.let {
        dateFieldLayout.dateValue = it
    }
}

@BindingAdapter("monthYearValue")
fun setMonthYearValue(monthYearMonthPickerFieldLayout: YearMonthPickerFieldLayout, date: String?) {
    date?.let {
        monthYearMonthPickerFieldLayout.dateValue = it
    }
}

@BindingAdapter("materialSearchProfile")
fun setMaterialSearchProfile(materialSearchView: MaterialSearchView, profileImagePath: String?) {
    profileImagePath?.let {
        materialSearchView.binding.profileImagePath = it
    }
}

@BindingAdapter("appSwitchIsChecked")
fun setAppSwitchChecked(detailsAppSwitch: DetailsAppSwitch, isChecked: Boolean?) {
    isChecked?.let {
        detailsAppSwitch.isChecked = it
    }
}

@BindingAdapter("appSwitchIsEnabled")
fun setAppSwitchEnabled(detailsAppSwitch: DetailsAppSwitch, isEnabled: Boolean) {
    detailsAppSwitch.appSwitchIsEnabled = isEnabled
}
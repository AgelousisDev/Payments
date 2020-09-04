package com.agelousis.payments.views.extensions

import androidx.databinding.BindingAdapter
import com.agelousis.payments.views.currencyEditText.CurrencyFieldLayout
import com.agelousis.payments.views.dateLayout.DateFieldLayout
import com.agelousis.payments.views.detailsSwitch.DetailsAppSwitch
import com.agelousis.payments.views.personDetailsLayout.PersonDetailsLayout
import com.agelousis.payments.views.personDetailsLayout.PersonDetailsPickerLayout
import com.agelousis.payments.views.searchLayout.MaterialSearchView

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

@BindingAdapter("currencyAmount")
fun setCurrencyAmount(currencyFieldLayout: CurrencyFieldLayout, amount: Double?) {
    amount?.let {
        currencyFieldLayout.doubleValue = it
    }
}

@BindingAdapter("dateValue")
fun setDateValue(dateFieldLayout: DateFieldLayout, date: String?) {
    date?.let {
        dateFieldLayout.dateValue = it
    }
}

@BindingAdapter("materialSearchProfile")
fun setMaterialSearchProfile(materialSearchView: MaterialSearchView, profileImagePath: String?) {
    profileImagePath?.let {
        materialSearchView.binding?.profileImagePath = it
    }
}

@BindingAdapter("appSwitchIsChecked")
fun setAppSwitchChecked(detailsAppSwitch: DetailsAppSwitch, isChecked: Boolean?) {
    isChecked?.let {
        detailsAppSwitch.isChecked = it
    }
}
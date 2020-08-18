package com.agelousis.monthlyfees.views.extensions

import androidx.databinding.BindingAdapter
import com.agelousis.monthlyfees.views.currencyEditText.CurrencyFieldLayout
import com.agelousis.monthlyfees.views.dateLayout.DateFieldLayout
import com.agelousis.monthlyfees.views.detailsSwitch.DetailsAppSwitch
import com.agelousis.monthlyfees.views.personDetailsLayout.PersonDetailsLayout
import com.google.android.material.switchmaterial.SwitchMaterial

@BindingAdapter("personDetails")
fun setPersonDetails(personDetailsLayout: PersonDetailsLayout, details: String?) {
    details?.let {
        personDetailsLayout.value = it
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

@BindingAdapter("appSwitchIsChecked")
fun setAppSwitchChecked(detailsAppSwitch: DetailsAppSwitch, isChecked: Boolean?) {
    isChecked?.let {
        detailsAppSwitch.isChecked = it
    }
}
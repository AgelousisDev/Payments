package com.agelousis.monthlyfees.views.personDetailsLayout.extensions

import androidx.databinding.BindingAdapter
import com.agelousis.monthlyfees.views.personDetailsLayout.PersonDetailsLayout

@BindingAdapter("personDetails")
fun setPersonDetails(personDetailsLayout: PersonDetailsLayout, details: String?) {
    details?.let {
        personDetailsLayout.value = it
    }
}
package com.agelousis.monthlyfees.views.searchLayout.enumerations

import com.agelousis.monthlyfees.R

enum class MaterialSearchViewIconState {
    SEARCH, CLOSE;

    val icon
        get() = when(this) {
            SEARCH -> R.drawable.ic_search
            CLOSE -> R.drawable.ic_close
        }

}
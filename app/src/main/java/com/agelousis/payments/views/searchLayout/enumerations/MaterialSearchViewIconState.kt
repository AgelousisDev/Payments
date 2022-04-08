package com.agelousis.payments.views.searchLayout.enumerations

import com.agelousis.payments.R

enum class MaterialSearchViewIconState {
    SEARCH, CLOSE;

    val icon
        get() = when(this) {
            SEARCH -> R.drawable.ic_search
            CLOSE -> R.drawable.ic_close
        }

}
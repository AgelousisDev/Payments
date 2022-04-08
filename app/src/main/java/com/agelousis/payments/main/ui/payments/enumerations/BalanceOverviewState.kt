package com.agelousis.payments.main.ui.payments.enumerations

import com.agelousis.payments.R

enum class BalanceOverviewState {
    NORMAL,
    EXPANDED;

    val icon
        get() = when(this) {
            NORMAL -> R.drawable.ic_baseline_keyboard_arrow_down_24
            EXPANDED -> R.drawable.ic_baseline_keyboard_arrow_up_24
        }

    val other
        get() = when(this) {
            NORMAL -> EXPANDED
            EXPANDED -> NORMAL
        }
}
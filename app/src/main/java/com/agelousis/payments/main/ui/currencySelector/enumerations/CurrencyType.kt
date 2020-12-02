package com.agelousis.payments.main.ui.currencySelector.enumerations

import com.agelousis.payments.R

enum class CurrencyType {
    USD,
    EUR,
    JPY,
    GBP,
    CHF,
    CNY,
    SEK;

    val icon
        get() = when(this) {
            USD -> R.drawable.ic_united_states_of_america
            EUR -> R.drawable.ic_european_union
            JPY -> R.drawable.ic_flag_of_japan
            GBP -> R.drawable.ic_united_kingdom
            CHF -> R.drawable.ic_switzerland
            CNY -> R.drawable.ic_china
            SEK -> R.drawable.ic_sweden
        }

    val symbol
        get() = when(this) {
            USD -> "$"
            EUR -> "€"
            JPY -> "¥"
            GBP -> "£"
            CHF -> "Fr "
            CNY -> "¥"
            SEK -> "kr "
        }

}
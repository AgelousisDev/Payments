package com.agelousis.payments.login.enumerations

import com.agelousis.payments.R

enum class UIMode {
    LIGHT_MODE, DARK_MODE;

    val icon
        get() = when(this) {
            LIGHT_MODE -> R.drawable.ic_moon
            DARK_MODE -> R.drawable.ic_sun
        }

    val theOther
        get() = when(this) {
            LIGHT_MODE -> DARK_MODE
            DARK_MODE -> LIGHT_MODE
        }

}
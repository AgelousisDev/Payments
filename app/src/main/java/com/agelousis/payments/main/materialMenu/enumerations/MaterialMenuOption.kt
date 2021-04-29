package com.agelousis.payments.main.materialMenu.enumerations

import com.agelousis.payments.R

enum class MaterialMenuOption {
    HOME,
    PROFILE,
    INVOICES,
    HISTORY,
    GUIDE;

    val menuIcon
        get() = when(this) {
            HOME -> R.drawable.ic_home
            PROFILE -> R.drawable.ic_person
            INVOICES -> R.drawable.ic_invoice
            HISTORY -> R.drawable.ic_diagram
            GUIDE -> R.drawable.ic_guide
        }

    val label
        get() = when(this) {
            HOME -> R.string.key_home_label
            PROFILE -> R.string.key_profile_label
            INVOICES -> R.string.key_invoices_label
            HISTORY -> R.string.key_history_label
            GUIDE -> R.string.key_guide_label
        }

    var isEnabled = true

}
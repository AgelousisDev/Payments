package com.agelousis.payments.main.ui.dashboard.enumerations

import com.agelousis.payments.R

enum class DashboardStatisticsType {
    GROUPS,
    CLIENTS,
    PAYMENTS,
    INVOICES;

    val bottomNavigationMenuItemId
        get() = when(this) {
            GROUPS,
            CLIENTS,
            PAYMENTS -> R.id.paymentsFragment
            INVOICES -> R.id.filesFragment
        }

}
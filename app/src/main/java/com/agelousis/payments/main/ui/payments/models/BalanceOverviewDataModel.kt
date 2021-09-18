package com.agelousis.payments.main.ui.payments.models

import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.enumerations.BalanceOverviewState

data class BalanceOverviewDataModel(
    val icon: Int,
    val title: Int,
    val subtitle: Int,
    var currentBalanceOverviewState: BalanceOverviewState?,
    val currentBalance: Double?
) {

    companion object {

        infix fun getBalanceOverviewDataModelWith(
            currentBalance: Double?
        ) =
            BalanceOverviewDataModel(
                icon = R.drawable.ic_monthly_salary,
                title = R.string.key_balance_overview_label,
                subtitle = R.string.key_set_your_monthly_balance_label,
                currentBalanceOverviewState = if (currentBalance != null) BalanceOverviewState.EXPANDED else BalanceOverviewState.NORMAL,
                currentBalance = currentBalance
            )

    }

}
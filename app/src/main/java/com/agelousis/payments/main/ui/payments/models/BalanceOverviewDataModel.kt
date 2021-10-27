package com.agelousis.payments.main.ui.payments.models

import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.enumerations.BalanceOverviewState

data class BalanceOverviewDataModel(
    val icon: Int,
    val title: Int,
    val subtitle: Int,
    var currentBalanceOverviewState: BalanceOverviewState?,
    var currentBalance: Double?,
    val lastPaymentMonthList: List<LastPaymentMonthDataModel>
) {

    companion object {

        fun getBalanceOverviewDataModelWith(
            currentBalance: Double?,
            lastPaymentMonthList: List<LastPaymentMonthDataModel>
        ) =
            BalanceOverviewDataModel(
                icon = R.drawable.ic_monthly_salary,
                title = R.string.key_balance_overview_label,
                subtitle = R.string.key_set_your_monthly_balance_label,
                currentBalanceOverviewState = BalanceOverviewState.NORMAL,
                currentBalance = currentBalance,
                lastPaymentMonthList = lastPaymentMonthList
            )

    }

}
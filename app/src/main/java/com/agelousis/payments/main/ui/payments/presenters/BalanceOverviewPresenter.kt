package com.agelousis.payments.main.ui.payments.presenters

interface BalanceOverviewPresenter {
    fun onBalanceOverviewState() {}
    fun onSaveBalance(balance: Double) {}
}
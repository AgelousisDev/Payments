package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.BalanceOverviewRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.BalanceOverviewDataModel
import com.agelousis.payments.main.ui.payments.presenters.BalanceOverviewPresenter
import com.agelousis.payments.utils.helpers.CurrencyHelper
import com.agelousis.payments.views.currencyEditText.interfaces.AmountListener

class BalanceOverviewViewHolder(
    private val binding: BalanceOverviewRowLayoutBinding,
    private val balanceOverviewPresenter: BalanceOverviewPresenter
): RecyclerView.ViewHolder(binding.root), BalanceOverviewPresenter, AmountListener {

    override fun onAmountChanged(amount: Double?) {
        onSaveBalance(
            balance = amount ?: return
        )
    }

    override fun onBalanceOverviewState() {
        balanceOverviewPresenter.onBalanceOverviewState()
    }

    override fun onSaveBalance(balance: Double) {
        balanceOverviewPresenter.onSaveBalance(
            balance = balance
        )
    }

    fun bind(balanceOverviewDataModel: BalanceOverviewDataModel) {
        binding.balanceOverviewDataModel = balanceOverviewDataModel
        binding.presenter = this
        binding.currencyFieldLayout.binding.increaseAmountLayout.isGone = true
        binding.currencyFieldLayout.currency = CurrencyHelper getCurrencyFromCurrencySymbol MainApplication.currencySymbol
        configureBalanceListener()
        binding.executePendingBindings()
    }

    private fun configureBalanceListener() {
        binding.currencyFieldLayout.amountListener = this
    }

}
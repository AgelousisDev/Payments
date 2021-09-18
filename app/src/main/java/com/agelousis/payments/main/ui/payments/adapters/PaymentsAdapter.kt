package com.agelousis.payments.main.ui.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.*
import com.agelousis.payments.main.ui.payments.enumerations.PaymentsAdapterViewType
import com.agelousis.payments.main.ui.payments.models.*
import com.agelousis.payments.main.ui.payments.presenters.BalanceOverviewPresenter
import com.agelousis.payments.main.ui.payments.presenters.GroupPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentAmountSumPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.payments.main.ui.payments.viewHolders.*

class PaymentsAdapter(
    private val list: ArrayList<Any>,
    private val groupPresenter: GroupPresenter,
    private val paymentPresenter: PaymentPresenter,
    private val paymentAmountSumPresenter: PaymentAmountSumPresenter,
    private val balanceOverviewPresenter: BalanceOverviewPresenter
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            PaymentsAdapterViewType.EMPTY_VIEW.type ->
                EmptyViewHolder(
                    binding = EmptyRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            PaymentsAdapterViewType.BALANCE_VIEW.type ->
                BalanceOverviewViewHolder(
                    binding = BalanceOverviewRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    balanceOverviewPresenter = balanceOverviewPresenter
                )
            PaymentsAdapterViewType.GROUP_VIEW.type ->
                GroupViewHolder(
                    binding = GroupRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            PaymentsAdapterViewType.PAYMENT_VIEW.type ->
                PaymentViewHolder(
                    binding = PaymentRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            PaymentsAdapterViewType.PAYMENT_AMOUNT_SUM_VIEW.type ->
                PaymentAmountSumViewHolder(
                    binding = PaymentAmountSumLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            else ->
                EmptyViewHolder(
                    binding = EmptyRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? EmptyViewHolder)?.bind(
            emptyModel = list.getOrNull(
                index = position
            ) as? EmptyModel ?: return
        )
        (holder as? BalanceOverviewViewHolder)?.bind(
            balanceOverviewDataModel = list.getOrNull(
                index = position
            ) as? BalanceOverviewDataModel ?: return
        )
        (holder as? GroupViewHolder)?.bind(
            groupModel = list.getOrNull(
                index = position
            ) as? GroupModel ?: return,
            presenter = groupPresenter
        )
        (holder as? PaymentViewHolder)?.bind(
            clientModel = list.getOrNull(
                index = position
            ) as? ClientModel ?: return,
            presenter = paymentPresenter
        )
        (holder as? PaymentAmountSumViewHolder)?.bind(
            paymentAmountSumModel = list.getOrNull(
                index = position
            ) as? PaymentAmountSumModel ?: return,
            presenter = paymentAmountSumPresenter
        )
    }

    override fun getItemViewType(position: Int): Int {
        (list.getOrNull(index = position) as? EmptyModel)?.let { return PaymentsAdapterViewType.EMPTY_VIEW.type }
        (list.getOrNull(index = position) as? BalanceOverviewDataModel)?.let { return PaymentsAdapterViewType.BALANCE_VIEW.type }
        (list.getOrNull(index = position) as? GroupModel)?.let { return PaymentsAdapterViewType.GROUP_VIEW.type }
        (list.getOrNull(index = position) as? ClientModel)?.let { return PaymentsAdapterViewType.PAYMENT_VIEW.type }
        (list.getOrNull(index = position) as? PaymentAmountSumModel)?.let { return PaymentsAdapterViewType.PAYMENT_AMOUNT_SUM_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

    fun restoreItem(position: Int) = notifyItemChanged(position)

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? PaymentViewHolder)?.clearAnimation()
    }

}
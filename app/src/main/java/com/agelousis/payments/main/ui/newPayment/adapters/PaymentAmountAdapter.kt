package com.agelousis.payments.main.ui.newPayment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.newPayment.viewHolders.PaymentAmountViewHolder
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.isSizeOne

class PaymentAmountAdapter(private val paymentModelList: ArrayList<PaymentAmountModel>, private val vat: Int?, private val presenter: NewPaymentPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PaymentAmountViewHolder(
            binding = PaymentAmountRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PaymentAmountViewHolder)?.bind(
            paymentAmountModel = paymentModelList.getOrNull(
                index = position
            ) ?: return,
            vat = vat,
            title = if (paymentModelList.isSizeOne) holder.itemView.context.resources.getString(R.string.key_single_payment_label) else null,
            presenter = presenter
        )
    }

    override fun getItemCount() = paymentModelList.size

    fun reloadData() {
        sortPayments()
        notifyItemRangeChanged(
            0,
            paymentModelList.size
        )
    }

    private fun sortPayments() {
        val tempList = arrayListOf<PaymentAmountModel>()
        tempList.addAll(
            paymentModelList
        )
        paymentModelList.clear()
        paymentModelList.addAll(
            tempList.sortedByDescending { it.paymentMonthDate }
        )
    }

    fun removeItem(position: Int) {
        paymentModelList.removeAt(position)
        notifyItemRemoved(position)
    }

}
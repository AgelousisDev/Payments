package com.agelousis.payments.main.ui.newPayment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.newPayment.viewHolders.PaymentAmountViewHolder
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel

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
            presenter = presenter
        )
    }

    override fun getItemCount() = paymentModelList.size

    fun reloadData() = notifyDataSetChanged()

    fun removeItem(position: Int) {
        paymentModelList.removeAt(position)
        notifyItemRemoved(position)
    }

}
package com.agelousis.monthlyfees.main.ui.newPayment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.newPayment.viewHolders.PaymentAmountViewHolder
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel

class PaymentAmountAdapter(private val paymentModelList: List<PaymentAmountModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            ) ?: return
        )
    }

    override fun getItemCount() = paymentModelList.size

    fun reloadData() = notifyDataSetChanged()

}
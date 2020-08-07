package com.agelousis.monthlyfees.main.ui.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.EmptyRowLayoutBinding
import com.agelousis.monthlyfees.databinding.GroupRowLayoutBinding
import com.agelousis.monthlyfees.databinding.PaymentRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.enumerations.PaymentsAdapterViewType
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentModel
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.EmptyViewHolder
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.PaymentViewHolder

class PaymentsAdapter(private val list: List<Any>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        (holder as? GroupViewHolder)?.bind(
            groupModel = list.getOrNull(
                index = position
            ) as? GroupModel ?: return
        )
        (holder as? PaymentViewHolder)?.bind(
            paymentModel = list.getOrNull(
                index = position
            ) as? PaymentModel ?: return
        )
    }

    override fun getItemViewType(position: Int): Int {
        (list.getOrNull(index = position) as? EmptyModel)?.let { return PaymentsAdapterViewType.EMPTY_VIEW.type }
        (list.getOrNull(index = position) as? GroupModel)?.let { return PaymentsAdapterViewType.GROUP_VIEW.type }
        (list.getOrNull(index = position) as? PaymentModel)?.let { return PaymentsAdapterViewType.PAYMENT_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

}
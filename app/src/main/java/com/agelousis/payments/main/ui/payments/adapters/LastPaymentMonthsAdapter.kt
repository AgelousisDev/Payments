package com.agelousis.payments.main.ui.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.LastPaymentMonthRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.LastPaymentMonthDataModel
import com.agelousis.payments.main.ui.payments.viewHolders.LastPaymentMonthViewHolder

class LastPaymentMonthsAdapter(private val lastPaymentMonthList: List<LastPaymentMonthDataModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LastPaymentMonthViewHolder(
            binding = LastPaymentMonthRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? LastPaymentMonthViewHolder)?.bind(
            lastPaymentMonthDataModel = lastPaymentMonthList.getOrNull(
                index = position
            ) ?: return
        )
    }

    override fun getItemCount() = lastPaymentMonthList.size

}
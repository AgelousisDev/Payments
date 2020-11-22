package com.agelousis.payments.main.menuOptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.HeaderRowLayoutBinding
import com.agelousis.payments.databinding.PaymentsMenuOptionRowLayoutBinding
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuAdapterViewType
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.menuOptions.viewHolders.PaymentsMenuOptionViewHolder
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.files.viewHolders.HeaderViewHolder

class PaymentsMenuOptionAdapter(private val list: List<Any>, private val paymentsMenuOptionPresenter: PaymentsMenuOptionPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            PaymentsMenuAdapterViewType.HEADER_VIEW.type ->
                HeaderViewHolder(
                    binding = HeaderRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            PaymentsMenuAdapterViewType.PAYMENT_MENU_OPTION_VIEW.type ->
                PaymentsMenuOptionViewHolder(
                    binding = PaymentsMenuOptionRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            else ->
                HeaderViewHolder(
                    binding = HeaderRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? HeaderViewHolder)?.bind(
            headerModel = list.getOrNull(index = position) as? HeaderModel ?: return
        )
        (holder as? PaymentsMenuOptionViewHolder)?.bind(
            paymentsMenuOptionType = list.getOrNull(index = position) as? PaymentsMenuOptionType ?: return,
            presenter = paymentsMenuOptionPresenter
        )
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        (list.getOrNull(index = position) as? HeaderModel)?.let {
            return PaymentsMenuAdapterViewType.HEADER_VIEW.type
        }
        (list.getOrNull(index = position) as? PaymentsMenuOptionType)?.let {
            return PaymentsMenuAdapterViewType.PAYMENT_MENU_OPTION_VIEW.type
        }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

}
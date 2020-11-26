package com.agelousis.payments.main.ui.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.databinding.GroupRowLayoutBinding
import com.agelousis.payments.databinding.PaymentAmountSumLayoutBinding
import com.agelousis.payments.databinding.PaymentRowLayoutBinding
import com.agelousis.payments.main.ui.payments.enumerations.PaymentsAdapterViewType
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.presenters.GroupPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentAmountSumPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.payments.main.ui.payments.viewHolders.EmptyViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentAmountSumViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentViewHolder

class PaymentsAdapter(private val list: ArrayList<Any>, private val groupPresenter: GroupPresenter, private val paymentPresenter: PaymentPresenter, private val paymentAmountSumPresenter: PaymentAmountSumPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        (holder as? GroupViewHolder)?.bind(
            groupModel = list.getOrNull(
                index = position
            ) as? GroupModel ?: return,
            presenter = groupPresenter
        )
        (holder as? PaymentViewHolder)?.bind(
            personModel = list.getOrNull(
                index = position
            ) as? PersonModel ?: return,
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
        (list.getOrNull(index = position) as? GroupModel)?.let { return PaymentsAdapterViewType.GROUP_VIEW.type }
        (list.getOrNull(index = position) as? PersonModel)?.let { return PaymentsAdapterViewType.PAYMENT_VIEW.type }
        (list.getOrNull(index = position) as? PaymentAmountSumModel)?.let { return PaymentsAdapterViewType.PAYMENT_AMOUNT_SUM_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

    fun restoreItem(position: Int) = notifyItemChanged(position)

    /*fun removeItemAndUpdate(context: Context, position: Int): Boolean {
        list.removeAt(position)
        notifyItemRemoved(position)
        //notifyItemRangeChanged(position, list.size)

        val uselessHeaderRow = list.filterIsInstance<GroupModel>().firstOrNull { headerModel ->
            list.filterIsInstance<PersonModel>().all { headerModel.groupId != it.groupId }
        }
        uselessHeaderRow?.let {
            val headerPosition = list.indexOf(it)
            list.removeAt(headerPosition)
            notifyItemRemoved(headerPosition)
            //notifyItemRangeChanged(headerPosition, list.size)
        }
        addEmptyViewIf(emptyRow = EmptyModel(
            title = context.resources.getString(R.string.key_no_persons_title_message),
            message = context.resources.getString(R.string.key_no_persons_message),
            imageIconResource = R.drawable.ic_invoice
        )) {
            list.isEmpty()
        }
        return list.any { it is EmptyModel }
    }

    private fun addEmptyViewIf(emptyRow: EmptyModel, predicate: () -> Boolean) {
        if (predicate()) {
            list.add(emptyRow)
            notifyItemInserted(0)
            notifyItemRangeChanged(0, list.size)
        }
    }*/

}
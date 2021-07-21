package com.agelousis.payments.views.dateLayout.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.MonthPickerRowLayoutBinding
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerFragmentPresenter
import com.agelousis.payments.views.dateLayout.models.MonthDataModel

class MonthPickerViewHolder(private val binding: MonthPickerRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(monthDataModel: MonthDataModel, yearMonthPickerFragmentPresenter: YearMonthPickerFragmentPresenter) {
        binding.monthDataModel = monthDataModel
        binding.presenter = yearMonthPickerFragmentPresenter
        binding.executePendingBindings()
    }

}
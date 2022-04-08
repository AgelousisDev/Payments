package com.agelousis.payments.views.dateLayout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.MonthPickerRowLayoutBinding
import com.agelousis.payments.databinding.YearPickerRowLayoutBinding
import com.agelousis.payments.views.dateLayout.enumerations.YearMonthPickerAdapterViewType
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerFragmentPresenter
import com.agelousis.payments.views.dateLayout.models.MonthDataModel
import com.agelousis.payments.views.dateLayout.models.YearDataModel
import com.agelousis.payments.views.dateLayout.viewHolders.MonthPickerViewHolder
import com.agelousis.payments.views.dateLayout.viewHolders.YearPickerViewHolder

class YearMonthPickerAdapter(private val yearMonthItemList: List<Any>, private val yearMonthPickerFragmentPresenter: YearMonthPickerFragmentPresenter):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(
            parent.context
        )
        return when(viewType) {
            YearMonthPickerAdapterViewType.YEARS_VIEW.type ->
                YearPickerViewHolder(
                    binding = YearPickerRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            else ->
                MonthPickerViewHolder(
                    binding = MonthPickerRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? YearPickerViewHolder)?.bind(
            yearDataModel = yearMonthItemList.getOrNull(
                index = position
            ) as? YearDataModel ?: return,
            yearMonthPickerFragmentPresenter = yearMonthPickerFragmentPresenter
        )
        (holder as? MonthPickerViewHolder)?.bind(
            monthDataModel = yearMonthItemList.getOrNull(
                index = position
            ) as? MonthDataModel ?: return,
            yearMonthPickerFragmentPresenter = yearMonthPickerFragmentPresenter
        )
    }

    override fun getItemCount() = yearMonthItemList.size

    override fun getItemViewType(position: Int): Int {
        (yearMonthItemList.getOrNull(index = position) as? YearDataModel)?.let { return YearMonthPickerAdapterViewType.YEARS_VIEW.type }
        (yearMonthItemList.getOrNull(index = position) as? MonthDataModel)?.let { return YearMonthPickerAdapterViewType.MONTHS_VIEW.type }
        return super.getItemViewType(position)
    }

}
package com.agelousis.payments.views.dateLayout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ViewPagerYearRowLayoutBinding
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerFragmentPresenter
import com.agelousis.payments.views.dateLayout.viewHolders.ViewPagerYearViewHolder

class ViewPagerYearAdapter(private val years: List<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewPagerYearViewHolder(
            binding = ViewPagerYearRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ViewPagerYearViewHolder)?.bind(
            year = years.getOrNull(
                index = position
            ) ?: return
        )
    }

    override fun getItemCount() = years.size

}
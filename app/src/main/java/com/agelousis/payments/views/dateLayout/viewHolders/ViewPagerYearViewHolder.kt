package com.agelousis.payments.views.dateLayout.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ViewPagerYearRowLayoutBinding

class ViewPagerYearViewHolder(private val binding: ViewPagerYearRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(year: String) {
        binding.year = year
        binding.executePendingBindings()
    }

}
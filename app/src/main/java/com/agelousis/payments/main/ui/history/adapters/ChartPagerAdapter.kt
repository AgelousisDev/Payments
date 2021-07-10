package com.agelousis.payments.main.ui.history.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.history.ChartFragment
import com.agelousis.payments.main.ui.history.enumerations.ChartType

class ChartPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int) =
        ChartFragment instanceWith when(position) {
            0 -> ChartType.PIE_CHART
            1 -> ChartType.LINE_CHART
            else -> ChartType.PIE_CHART
        }

    fun getPageIcon(position: Int) =
        when(position) {
            0 -> R.drawable.ic_baseline_pie_chart_24
            else -> R.drawable.ic_baseline_show_chart_24
        }

}
package com.agelousis.payments.main.ui.dashboard.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.dashboard.ChartFragment
import com.agelousis.payments.main.ui.dashboard.DashboardFragment
import com.agelousis.payments.main.ui.dashboard.enumerations.ChartType

class ChartPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int) =
        when(position) {
            0 ->
                DashboardFragment.shared
            else ->
                ChartFragment instanceWith when(position) {
                    1 -> ChartType.PIE_CHART
                    2 -> ChartType.LINE_CHART
                    else -> ChartType.PIE_CHART
                }
        }


    fun getPageIcon(position: Int) =
        when(position) {
            0 -> R.drawable.ic_payment
            1 -> R.drawable.ic_baseline_pie_chart_24
            else -> R.drawable.ic_baseline_show_chart_24
        }

}
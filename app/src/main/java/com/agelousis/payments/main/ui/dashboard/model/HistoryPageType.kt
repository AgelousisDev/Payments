package com.agelousis.payments.main.ui.dashboard.model

import androidx.compose.runtime.Composable
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.dashboard.enumerations.ChartType
import com.agelousis.payments.main.ui.dashboard.ui.ChartLayout
import com.agelousis.payments.main.ui.dashboard.ui.DashboardLayout
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel

typealias HistoryPageTypeComposableLayout = @Composable (viewModel: DashboardViewModel) -> Unit

sealed class HistoryPageType(
    val icon: Int,
    val historyPageTypeComposableLayout: HistoryPageTypeComposableLayout
) {
    object Dashboard: HistoryPageType(
        icon = R.drawable.ic_payment,
        historyPageTypeComposableLayout = { dashboardViewModel ->
            DashboardLayout(
                viewModel = dashboardViewModel
            )
        }
    )
    object PieChart: HistoryPageType(
        icon = R.drawable.ic_baseline_pie_chart_24,
        historyPageTypeComposableLayout = { dashboardViewModel ->
            ChartLayout(
                chartType = ChartType.PIE_CHART,
                viewModel = dashboardViewModel
            )
        }
    )
    object LineChart: HistoryPageType(
        icon = R.drawable.ic_baseline_show_chart_24,
        historyPageTypeComposableLayout = { dashboardViewModel ->
            ChartLayout(
                chartType = ChartType.LINE_CHART,
                viewModel = dashboardViewModel
            )
        }
    )
}
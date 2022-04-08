package com.agelousis.payments.main.ui.dashboard.model

import com.agelousis.payments.main.ui.dashboard.enumerations.DashboardStatisticsType

data class DashboardStatisticsDataModel(
    val dashboardStatisticsType: DashboardStatisticsType,
    val size: Int,
    val labelResource: Int,
    val backgroundColor: Int,
    val icon: Int
)
package com.agelousis.payments.main.ui.dashboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.ui.horizontalMargin
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun DashboardLayout(
    viewModel: DashboardViewModel
) {
    Column {
        Text(
            text = stringResource(
                id = R.string.key_dashboard_label
            ),
            style = textViewTitleFont,
            modifier = horizontalMargin
        )
        LazyVerticalGrid(
            cells = GridCells.Fixed(
                count = 2
            ),
            contentPadding = PaddingValues(
                all = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = viewModel.dashboardStatisticsDataMutableState
            ) { dashboardStatisticsDataModel ->
                StatisticCardLayout(
                    dashboardStatisticsDataModel = dashboardStatisticsDataModel
                )
            }
        }
    }
}

@Composable
fun StatisticCardLayout(
    dashboardStatisticsDataModel: DashboardStatisticsDataModel
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        shape = RoundedCornerShape(12.dp),
        elevation = 10.dp,
        onClick = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .height(
                height = 65.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(
                        size = 60.dp
                    )
                    .padding(
                        all = 8.dp
                    )
                    .background(
                        color = colorResource(
                            id = dashboardStatisticsDataModel.backgroundColor
                        ),
                        shape = RoundedCornerShape(
                            size = 30.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = dashboardStatisticsDataModel.icon
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        colorResource(
                            id = R.color.white
                        )
                    ),
                    modifier = Modifier.size(
                        size = 25.dp
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(
                        all = 8.dp
                    )
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dashboardStatisticsDataModel.size.toString(),
                    style = textViewTitleLabelFont
                )
                Text(
                    text = stringResource(
                        id = dashboardStatisticsDataModel.labelResource
                    ),
                    style = textViewLabelFont,
                    color = colorResource(
                        id = R.color.grey
                    )
                )
            }
        }
    }
}
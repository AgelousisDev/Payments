package com.agelousis.payments.main.ui.dashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.ui.*
import com.agelousis.payments.utils.extensions.euroFormattedString

@Composable
fun DashboardLayout(
    viewModel: DashboardViewModel
) {
    val animationState = remember {
        MutableTransitionState(
            initialState = false
        ).apply {
            // Start the animation immediately.
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = animationState,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(
                        id = R.string.key_dashboard_label
                    ),
                    style = textViewTitleFont,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    ),
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        )
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(
                        all = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = viewModel.dashboardStatisticsDataMutableState
                    ) { dashboardStatisticsDataModel ->
                        StatisticCardLayout(
                            viewModel = viewModel,
                            dashboardStatisticsDataModel = dashboardStatisticsDataModel
                        )
                    }
                }
            }
            item {
                DashboardInsightLayout(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun StatisticCardLayout(
    viewModel: DashboardViewModel,
    dashboardStatisticsDataModel: DashboardStatisticsDataModel
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        shape = RoundedCornerShape(12.dp),
        elevation = 10.dp,
        onClick = {
            viewModel.onDashboardPage(
                bottomNavigationMenuItemId = dashboardStatisticsDataModel.dashboardStatisticsType.bottomNavigationMenuItemId
            )
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
                    modifier = Modifier
                        .size(
                            size = 25.dp
                        )
                )
            }
            Column(
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 16.dp
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

@Composable
fun DashboardInsightLayout(
    viewModel: DashboardViewModel
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        shape = RoundedCornerShape(12.dp),
        elevation = 10.dp,
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Column {
            Text(
                text = stringResource(
                    id = R.string.key_recent_insights_label
                ),
                style = textViewTitleLabelFont,
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 16.dp
                    )
            )
            SeparatorGreyLine()
            // Total Incoming Payments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(
                        id = R.string.key_total_incoming_payments_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                )
                Text(
                    text = viewModel.paymentAmountModelList?.mapNotNull { it.paymentAmount }?.sum()?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.grey
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                )
            }
            // Maximum incoming group\'s payment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(
                        id = R.string.key_maximum_incoming_group_payment_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1.5f
                        )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupAmount true)?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.grey
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 0.5f
                        )
                )
            }
            // Minimum incoming group\'s payment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(
                        id = R.string.key_minimum_incoming_group_payment_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1.5f
                        )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupAmount false)?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.grey
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 0.5f
                        )
                )
            }
            // Today payments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(
                        id = R.string.key_today_payments_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                )
                Text(
                    text = viewModel.todayPaymentClientName ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.grey
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                )
            }
        }
    }
}
package com.agelousis.payments.main.ui.dashboard.ui

import android.content.res.Configuration
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.compose.*
import com.agelousis.payments.utils.extensions.euroFormattedString

@Composable
fun DashboardLayout(
    viewModel: DashboardViewModel
) {
    val context = LocalContext.current
    val animationState = remember {
        MutableTransitionState(
            initialState = false
        ).apply {
            // Start the animation immediately.
            targetState = true
        }
    }
    val dashboardLazyColumnState = rememberLazyListState()
    (context as? MainActivity)?.bottomAppBarState = dashboardLazyColumnState.firstVisibleItemIndex == 0
    AnimatedVisibility(
        visibleState = animationState,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        LazyColumn(
            state = dashboardLazyColumnState,
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
                StatisticsCardUI(
                    viewModel = viewModel
                )
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
fun StatisticsCardUI(
    viewModel: DashboardViewModel
) {
    when(LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE ->
            LazyRow(
                contentPadding = PaddingValues(
                    all = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = viewModel.dashboardStatisticsDataMutableState
                ) { dashboardStatisticsDataModel ->
                    StatisticsCardLayout(
                        viewModel = viewModel,
                        dashboardStatisticsDataModel = dashboardStatisticsDataModel
                    )
                }
            }
        else ->
            GridLazyColumnRow(
                items = viewModel.dashboardStatisticsDataMutableState,
                columnCount = 2,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp
                    )
            ) { dashboardStatisticsDataModel ->
                StatisticsCardLayout(
                    viewModel = viewModel,
                    dashboardStatisticsDataModel = dashboardStatisticsDataModel
                )
            }
    }
}

@Composable
fun StatisticsCardLayout(
    viewModel: DashboardViewModel,
    dashboardStatisticsDataModel: DashboardStatisticsDataModel
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
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
                    style = textViewTitleLabelFont,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = stringResource(
                        id = dashboardStatisticsDataModel.labelResource
                    ),
                    style = textViewLabelFont,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
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
    val orientation = LocalConfiguration.current.orientation
    Card(
        interactionSource = remember { MutableInteractionSource() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> 16.dp
                    else -> 110.dp
                }
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
                    ),
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
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
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = viewModel.paymentAmountModelListMutableState?.mapNotNull { it.paymentAmount }?.sum()?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
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
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupAmount true)?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 0.5f
                        )
                )
            }
            // Maximum incoming group\'s name
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
                        id = R.string.key_maximum_incoming_group_name_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1.5f
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupName true) ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
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
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupAmount false)?.euroFormattedString ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    ),
                    modifier = Modifier
                        .weight(
                            weight = 0.5f
                        )
                )
            }
            // Minimum incoming group\'s name
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
                        id = R.string.key_minimum_incoming_group_name_label
                    ),
                    style = textViewLabelFont,
                    modifier = Modifier
                        .weight(
                            weight = 1.5f
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = (viewModel getMaxIncomingGroupName  false) ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
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
                        ),
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
                Text(
                    text = viewModel.todayPaymentClientName ?: stringResource(
                        id = R.string.key_empty_field_label
                    ),
                    style = textViewValueLabelFont,
                    textAlign = TextAlign.End,
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
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
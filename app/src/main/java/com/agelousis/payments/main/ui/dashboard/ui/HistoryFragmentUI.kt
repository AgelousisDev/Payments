package com.agelousis.payments.main.ui.dashboard.ui

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.agelousis.payments.R
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.main.ui.dashboard.model.HistoryPageType
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.ui.appColorScheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun HistoryLayout(
    viewModel: DashboardViewModel
) {
    val historyPageTypes = listOf(
        HistoryPageType.Dashboard,
        HistoryPageType.PieChart,
        HistoryPageType.LineChart
    )
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (tabRowConstrainedReference, pagerConstrainedReference, emptyViewConstrainedReference) = createRefs()
        if (!viewModel.clientModelListMutableState.isNullOrEmpty())
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        height = 1.dp,
                        modifier = Modifier
                            .tabIndicatorOffset(
                                currentTabPosition = tabPositions[pagerState.currentPage]
                            )
                    )
                },
                //backgroundColor = systemAccentColor(),
                contentColor = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(tabRowConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            ) {
                historyPageTypes.forEachIndexed { index, historyPageType ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                         },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = historyPageType.icon
                                ),
                                contentDescription = null
                            )
                        },
                        selectedContentColor = appColorScheme().primary,
                        unselectedContentColor = colorResource(
                            id = R.color.grey
                        )
                    )
                }
            }
        if (!viewModel.clientModelListMutableState.isNullOrEmpty())
            HorizontalPager(
                count = historyPageTypes.size,
                modifier = Modifier
                    .constrainAs(pagerConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(tabRowConstrainedReference.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                state = pagerState
            ) { page ->
                HistoryPageLayout(
                    viewModel = viewModel,
                    historyPageType = historyPageTypes[page]
                )
            }
        if (viewModel.clientModelListMutableState.isNullOrEmpty())
            AndroidView(
                factory = { context ->
                    EmptyRowLayoutBinding.inflate(
                        LayoutInflater.from(
                            context
                        ),
                        null,
                        false
                    ).also { emptyRowLayoutBinding ->
                        emptyRowLayoutBinding.emptyModel = EmptyModel(
                            title = context.resources.getString(R.string.key_no_clients_title_message),
                            message = context.resources.getString(R.string.key_add_clients_from_home_message),
                            animationJsonIcon = "empty_animation.json"
                        )
                    }.root
                },
                modifier = Modifier
                    .constrainAs(emptyViewConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
            )
    }
}

@Composable
fun HistoryPageLayout(
    viewModel: DashboardViewModel,
    historyPageType: HistoryPageType
) {
    historyPageType.historyPageTypeComposableLayout(viewModel)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryLayoutPreview() {
    HistoryLayout(
        viewModel = DashboardViewModel()
    )
}
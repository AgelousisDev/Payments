package com.agelousis.payments.main.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.agelousis.payments.main.ui.dashboard.enumerations.ChartType
import com.agelousis.payments.main.ui.dashboard.ui.ChartLayout
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors

class ChartFragment: Fragment() {

    companion object {

        private const val CHART_TYPE_EXTRA = "ChartFragment=chartTypeExtra"

        infix fun instanceWith(chartType: ChartType) =
            ChartFragment().also { chartFragment ->
                chartFragment.arguments = Bundle().also {
                    it.putSerializable(
                        CHART_TYPE_EXTRA,
                        chartType
                    )
                }
            }

    }

    private val chartType by lazy {
        arguments?.getSerializable(
            CHART_TYPE_EXTRA
        ) as? ChartType
    }
    private val clientModelList by lazy {
        (parentFragment as? HistoryFragment)?.clientModelList
    }
    private val paymentAmountModelList by lazy {
        (parentFragment as? HistoryFragment)?.paymentAmountModelList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    ChartLayout(
                        chartType = chartType ?: return@MaterialTheme,
                        clientModelList = clientModelList ?: return@MaterialTheme,
                        paymentAmountModelList = paymentAmountModelList ?: return@MaterialTheme
                    )
                }
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ChartFragmentPreview() {
        ChartLayout(
            chartType = ChartType.PIE_CHART,
            clientModelList = clientModelList ?: return,
            paymentAmountModelList = paymentAmountModelList ?: return
        )
    }

}
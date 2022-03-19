package com.agelousis.payments.main.ui.dashboard.ui

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.dashboard.enumerations.ChartType
import com.agelousis.payments.main.ui.dashboard.listeners.PaymentLineChartGestureListener
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.agelousis.payments.utils.extensions.toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChartLayout(
    chartType: ChartType,
    viewModel: DashboardViewModel
) {
    val orientation = LocalConfiguration.current.orientation
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (pieChartConstrainedReference, lineChartConstrainedReference) = createRefs()
        when(chartType) {
            ChartType.PIE_CHART ->
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            configurePieChart(
                                colors = (viewModel.clientModelListMutableState ?: return@apply).sortedBy {
                                    it.groupName
                                }.groupBy {
                                    it.groupName
                                }.map {
                                    it.value.firstOrNull()?.groupColor ?: 0
                                },
                                entries = (viewModel.clientModelListMutableState ?: return@apply).sortedBy {
                                    it.groupName
                                }.groupBy {
                                    it.groupName
                                }.map { map ->
                                    val groupPaymentAmountPercentage = (viewModel.clientModelListMutableState ?: return@apply).asSequence().filter {
                                        it.groupName == map.key
                                    }.mapNotNull { clientModel ->
                                        clientModel.payments
                                    }.flatten().mapNotNull { paymentAmountModel ->
                                        paymentAmountModel.paymentAmount
                                    }.sum().toFloat() / (viewModel.clientModelListMutableState ?: return@apply).mapNotNull { paymentAmountModel ->
                                        paymentAmountModel.payments
                                    }.flatten().mapNotNull { paymentAmountModel ->
                                        paymentAmountModel.paymentAmount
                                    }.sum().toFloat() * 100
                                    val groupName = when {
                                        groupPaymentAmountPercentage <= 4f ->
                                            null
                                        else ->
                                            if ((map.value.firstOrNull()?.groupName?.length ?: 0) > 12)
                                                "${map.value.firstOrNull()?.groupName?.take(n = 9)}..."
                                            else
                                                map.value.firstOrNull()?.groupName
                                    }
                                    PieEntry(
                                        groupPaymentAmountPercentage,
                                        groupName
                                    )
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .constrainAs(pieChartConstrainedReference) {
                            start.linkTo(parent.start, if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 32.dp)
                            top.linkTo(parent.top, if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 32.dp)
                            end.linkTo(parent.end, if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 32.dp)
                            bottom.linkTo(parent.bottom, 90.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )
            ChartType.LINE_CHART ->
                AndroidView(
                    factory = { context ->
                        LineChart(context).apply {
                            val payments = arrayListOf(
                                *(viewModel.clientModelListMutableState ?: return@apply).asSequence().mapNotNull {
                                    it.payments
                                }.flatten().toList().toTypedArray(),
                                *(viewModel.paymentAmountModelListMutableState ?: return@apply).toTypedArray()
                            )
                            this configureLineChart payments.sortedBy {
                                    it.paymentMonthDate
                                }.groupBy {
                                    it.paymentMonthDate
                                }.map { map ->
                                    Entry(
                                        map.key?.time?.toFloat() ?: 0f,
                                        map.value.mapNotNull { it.paymentAmount }.sum().toFloat()
                                    )
                                }.toList()
                        }
                    },
                    modifier = Modifier
                        .constrainAs(lineChartConstrainedReference) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, 90.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )
        }
    }
}

private fun PieChart.configurePieChart(colors: List<Int>, entries: List<PieEntry>) {
    isDrawHoleEnabled = true
    setUsePercentValues(true)
    setEntryLabelTextSize(12f)
    setEntryLabelColor(ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground))
    centerText = resources.getString(R.string.key_incoming_payments_label)
    setCenterTextSize(24f)
    description.isEnabled = false
    setBackgroundColor(Color.TRANSPARENT)
    setHoleColor(Color.TRANSPARENT)
    setCenterTextColor(ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground))
    isRotationEnabled = false
    holeRadius = 55f
    renderer.paintRender.setShadowLayer(
        4f,
        0f,
        0f,
        ContextCompat.getColor(
            context ?: return,
            R.color.dayNightTextOnBackground
        )
    )

    /*val legend = binding.pieChart.legend
    legend.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    legend.orientation = Legend.LegendOrientation.HORIZONTAL
    legend.setDrawInside(false)
    legend.form = Legend.LegendForm.CIRCLE
    legend.isEnabled = true
    legend.isWordWrapEnabled = true*/
    legend.isEnabled = false
    loadPieChartData(
        colors = colors,
        entries = entries
    )
}

private fun PieChart.loadPieChartData(colors: List<Int>, entries: List<PieEntry>) {
    val dataSet = PieDataSet(entries, null)
    dataSet.colors = colors

    val data = PieData(dataSet)
    data.setValueFormatter(
        object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value > 4f) String.format("%.1f%%", value) else ""
            }
        }
    )
    data.setDrawValues(true)
    data.setValueTextSize(12f)
    data.setValueTextColor(ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground))

    this.data = data
    invalidate()

    animateY(1400, Easing.EaseOutCirc)
}

private infix fun LineChart.configureLineChart(entries: List<Entry>) {
    val desc = Description()
    desc.text = ""
    description = desc
    legend?.isEnabled = false
    xAxis?.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
    xAxis?.position = XAxis.XAxisPosition.BOTTOM
    xAxis?.valueFormatter = object: ValueFormatter() {
        override fun getFormattedValue(value: Float) =
            SimpleDateFormat(Constants.GRAPH_DATE_FORMAT, Locale.US).format(value.toLong())
    }
    //lineChart.xAxis.setAvoidFirstLastClipping(true)
    xAxis?.isGranularityEnabled = true
    xAxis?.setDrawLimitLinesBehindData(true)
    axisLeft?.valueFormatter = object: ValueFormatter() {
        override fun getFormattedValue(value: Float) =
            if (value == 0f)
                "0"
            else
                value.toDouble().euroFormattedString
    }
    axisLeft?.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
    axisRight?.isEnabled = false
    onChartGestureListener =
        object: PaymentLineChartGestureListener(
            chart = this
        ) {
            override fun onAmountSelected(amount: String) {
                context?.toast(
                    message = amount
                )
            }
        }
    //setLabelCount(entries.size, true)
    xAxis?.granularity = 1f
    //setMaxVisibleValueCount(4)
    this setLineChartData entries
}

private infix fun LineChart.setLineChartData(entries: List<Entry>) {
    val dataSets = arrayListOf<ILineDataSet>()
    dataSets.add(
        LineDataSet(
            entries,
            resources.getString(R.string.key_amount_label)
        ).also {
            it.setDrawFilled(true)
            it.fillDrawable = ContextCompat.getDrawable(context ?: return, R.drawable.graph_draw_gradient_background)
            it.setDrawCircles(true)
            it.circleRadius = 4.0f
            it.setDrawValues(false)
            it.lineWidth = 3.0f
            it.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            it.color = ContextCompat.getColor(context ?: return, R.color.colorAccent)
            it.setCircleColor(ContextCompat.getColor(context ?: return, R.color.colorAccent))
            it.highLightColor = ContextCompat.getColor(context ?: return, R.color.green)
        }
    )
    data = LineData(dataSets)
    invalidate()
}
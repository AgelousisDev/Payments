package com.agelousis.payments.main.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agelousis.payments.R
import com.agelousis.payments.databinding.ChartFragmentLayoutBinding
import com.agelousis.payments.main.ui.history.adapters.GroupDataAdapter
import com.agelousis.payments.main.ui.history.enumerations.ChartType
import com.agelousis.payments.main.ui.history.listeners.PaymentLineChartGestureListener
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.agelousis.payments.utils.extensions.toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*

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

    private lateinit var binding: ChartFragmentLayoutBinding
    private val chartType by lazy {
        arguments?.getSerializable(
            CHART_TYPE_EXTRA
        ) as? ChartType
    }
    private val clientModelList by lazy {
        (parentFragment as? HistoryFragment)?.clientModelList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ChartFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.chartType = chartType
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        when(chartType) {
            ChartType.LINE_CHART ->
                configureLineChart(
                    entries = clientModelList?.asSequence()?.mapNotNull {
                        it.payments
                    }?.flatten()?.sortedBy {
                        it.paymentMonthDate
                    }?.groupBy {
                        it.paymentMonthDate
                    }?.map { map ->
                        Entry(
                            map.key?.time?.toFloat() ?: 0f,
                            map.value.mapNotNull { it.paymentAmount }.sum().toFloat()
                        )
                    }?.toList() ?: return
                )
            ChartType.PIE_CHART -> {
                configureGroupDataRecyclerView()
                configurePieChart(
                    colors = clientModelList?.sortedBy {
                        it.groupName
                    }?.groupBy {
                        it.groupName
                    }?.map {
                        it.value.firstOrNull()?.groupColor ?: 0
                    } ?: return,
                    entries = clientModelList?.sortedBy {
                        it.groupName
                    }?.groupBy {
                        it.groupName
                    }?.map { map ->
                        val groupPaymentAmountPercentage = (clientModelList?.asSequence()?.filter {
                            it.groupName == map.key
                        }?.mapNotNull { clientModel ->
                            clientModel.payments
                        }?.flatten()?.mapNotNull { paymentAmountModel ->
                            paymentAmountModel.paymentAmount
                        }?.sum()?.toFloat() ?: 0f) / (clientModelList?.mapNotNull { paymentAmountModel ->
                            paymentAmountModel.payments
                        }?.flatten()?.mapNotNull { paymentAmountModel ->
                            paymentAmountModel.paymentAmount
                        }?.sum()?.toFloat() ?: 0f) * 100
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
                    } ?: return
                )
            }
            else -> {}
        }
    }

    private fun configureLineChart(entries: List<Entry>) {
        val desc = Description()
        desc.text = ""
        binding.lineChart.description = desc
        binding.lineChart.legend?.isEnabled = false
        binding.lineChart.xAxis?.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
        binding.lineChart.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        binding.lineChart.xAxis?.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float) =
                SimpleDateFormat(Constants.GRAPH_DATE_FORMAT, Locale.US).format(value.toLong())
        }
        //lineChart.xAxis.setAvoidFirstLastClipping(true)
        binding.lineChart.xAxis?.isGranularityEnabled = true
        binding.lineChart.xAxis?.setDrawLimitLinesBehindData(true)
        binding.lineChart.axisLeft?.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float) =
                if (value == 0f)
                    "0"
                else
                    value.toDouble().euroFormattedString
        }
        binding.lineChart.axisLeft?.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
        binding.lineChart.axisRight?.isEnabled = false
        binding.lineChart.onChartGestureListener =
            object: PaymentLineChartGestureListener(chart = binding.lineChart) {
                override fun onAmountSelected(amount: String) {
                    context?.toast(
                        message = amount
                    )
                }
            }
        //lineChart.xAxis.setLabelCount(entries.size, true)
        binding.lineChart.xAxis?.granularity = 1f
        //lineChart.setMaxVisibleValueCount(4)
        setLineChartData(
            entries = entries
        )
    }

    private fun setLineChartData(entries: List<Entry>) {
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
        binding.lineChart.data = LineData(dataSets)
        binding.lineChart.invalidate()
    }

    private fun configureGroupDataRecyclerView() {
        binding.groupDataRecyclerView.adapter = GroupDataAdapter(
            groupModelList = clientModelList?.groupBy { it.groupName }?.map { map ->
                GroupModel(
                    color = map.value.first().groupColor,
                    groupName = map.key
                )
            } ?: listOf()
        )
    }

    private fun configurePieChart(colors: List<Int>, entries: List<PieEntry>) {
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setEntryLabelTextSize(12f)
        binding.pieChart.setEntryLabelColor(ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground))
        binding.pieChart.centerText = resources.getString(R.string.key_clients_by_group_label)
        binding.pieChart.setCenterTextSize(24f)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setBackgroundColor(Color.TRANSPARENT)
        binding.pieChart.setHoleColor(Color.TRANSPARENT)
        binding.pieChart.setCenterTextColor(ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground))
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.holeRadius = 55f
        binding.pieChart.renderer.paintRender.setShadowLayer(
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
        binding.pieChart.legend.isWordWrapEnabled = true*/
        binding.pieChart.legend.isEnabled = false
        loadPieChartData(
            colors = colors,
            entries = entries
        )
    }

    private fun loadPieChartData(colors: List<Int>, entries: List<PieEntry>) {
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

        binding.pieChart.data = data
        binding.pieChart.invalidate()

        binding.pieChart.animateY(1400, Easing.EaseOutCirc)
    }

}
package com.agelousis.payments.main.ui.history

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.history.listeners.PaymentLineChartGestureListener
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.agelousis.payments.utils.extensions.toast
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.history_fragment_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment: Fragment(R.layout.history_fragment_layout) {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePayments()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { payments ->
            configureLineChart(
                payments = payments.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten().sortedBy { it.paymentMonthDate }
            )
        }
    }

    private fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
        }
    }

    private fun configureLineChart(payments: List<PaymentAmountModel>) {
        val desc = Description()
        desc.text = ""
        lineChart.description = desc
        lineChart.legend.isEnabled = false
        lineChart.xAxis.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float) =
                SimpleDateFormat(Constants.GRAPH_DATE_FORMAT, Locale.US).format(value.toLong())
        }
        lineChart.xAxis.setAvoidFirstLastClipping(true)
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.setDrawLimitLinesBehindData(true)
        lineChart.axisLeft.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float) =
                value.toDouble().euroFormattedString
        }
        lineChart.axisLeft.textColor = ContextCompat.getColor(context ?: return, R.color.dayNightTextOnBackground)
        lineChart.axisRight.isEnabled = false
        lineChart.onChartGestureListener = object: PaymentLineChartGestureListener(chart = lineChart) {
            override fun onAmountSelected(amount: String) {
                context?.toast(
                    message = amount
                )
            }
        }
        val entries = arrayListOf<Entry>()
        payments.forEach {
            entries.add(
                Entry(
                    it.paymentMonthDate?.time?.toFloat() ?: 0.0f,
                    it.paymentAmount?.toFloat() ?: 0.0f
                )
            )
        }
        lineChart.xAxis.setLabelCount(entries.size, true)
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
        lineChart.data = LineData(dataSets)
        lineChart.invalidate()
    }

}
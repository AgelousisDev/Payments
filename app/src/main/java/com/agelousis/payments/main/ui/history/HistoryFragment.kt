package com.agelousis.payments.main.ui.history

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.calendar
import com.agelousis.payments.utils.extensions.toDateWith
import com.github.mikephil.charting.components.Description
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
                payments = payments.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten().sortedByDescending { it.paymentMonthDate }
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
        desc.text = resources.getString(R.string.key_payments_label)
        desc.textSize = 20.0f
        lineChart.description = desc

        lineChart.xAxis.valueFormatter = object: ValueFormatter() {
            val simpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

            override fun getFormattedValue(value: Float) =
                simpleDateFormat.format(Date(value.toLong() * 1000L))
        }

        val entries = arrayListOf<Entry>()
        payments.forEach {
            entries.add(
                Entry(
                    it.paymentMonthDate?.calendar?.timeInMillis?.toFloat() ?: 0.0f,
                    it.paymentAmount?.toFloat() ?: 0.0f
                )
            )
        }
        setLineChartData(
            entries = entries
        )
    }

    private fun setLineChartData(entries: List<Entry>) {
       val dataSets = arrayListOf<ILineDataSet>()

        val highLineDataSet = LineDataSet(entries, resources.getString(R.string.key_amount_label))
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4.0f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3.0f
        highLineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        highLineDataSet.color = ContextCompat.getColor(context ?: return, R.color.colorAccent)
        highLineDataSet.setCircleColor(ContextCompat.getColor(context ?: return, R.color.colorAccent))
        highLineDataSet.highLightColor = ContextCompat.getColor(context ?: return, R.color.green)
        dataSets.add(highLineDataSet)

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }

}
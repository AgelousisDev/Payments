package com.agelousis.payments.main.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.databinding.HistoryFragmentLayoutBinding
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment: Fragment() {

    private lateinit var binding: HistoryFragmentLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HistoryFragmentLayoutBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePayments()
        addObservers()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { payments ->
            configureEntries(
                payments = payments.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten()
            )
        }
    }

    private fun configureEntries(payments: List<PaymentAmountModel>) {
        val entries = arrayListOf<Entry>()

        payments.sortedBy { it.paymentMonthDate }.groupBy { it.paymentMonthDate }.forEach { (monthDate, list) ->
            entries.add(
                Entry(
                    monthDate?.time?.toFloat() ?: 0f,
                    list.mapNotNull { it.paymentAmount }.sum().toFloat()
                )
            )
        }

        configureLineChart(
            entries = entries
        )
    }

    private fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
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

}
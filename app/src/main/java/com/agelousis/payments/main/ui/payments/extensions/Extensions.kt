package com.agelousis.payments.main.ui.payments.extensions

import android.content.Context
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.adapters.LastPaymentMonthsAdapter
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.LastPaymentMonthDataModel
import com.agelousis.payments.utils.extensions.toCalendar
import java.time.LocalDate
import java.util.*

fun List<ClientModel>.getThreeLastPaymentMonths(context: Context): List<LastPaymentMonthDataModel> {
    val lastPaymentMonthList = arrayListOf<LastPaymentMonthDataModel>()
    val calendar = Date().toCalendar()
    val currentMonth = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
    val lastMonth = currentMonth.minusMonths(1)
    val twoMonthsAgo = currentMonth.minusMonths(2)
    val paymentAmountModelList = mapNotNull { clientModel ->
        clientModel.payments
    }.flatten()

    /** Current Month **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == currentMonth
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = currentMonth.monthValue) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = currentMonth.monthValue) ?: ""
        )
    )

    /** Last Month **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == lastMonth
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = lastMonth.monthValue) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = lastMonth.monthValue) ?: ""
        )
    )

    /** Two Months Ago **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == twoMonthsAgo
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = twoMonthsAgo.monthValue) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = twoMonthsAgo.monthValue) ?: ""
        )
    )

    return lastPaymentMonthList
}

@BindingAdapter("lastPaymentMonthsAdapter")
fun lastPaymentMonthsAdapterWith(recyclerView: RecyclerView, lastPaymentMonthList: List<LastPaymentMonthDataModel>) {
    recyclerView.adapter = LastPaymentMonthsAdapter(
        lastPaymentMonthList = lastPaymentMonthList
    )
}
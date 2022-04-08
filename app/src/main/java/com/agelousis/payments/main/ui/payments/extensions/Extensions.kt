package com.agelousis.payments.main.ui.payments.extensions

import android.content.Context
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.adapters.LastPaymentMonthsAdapter
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.LastPaymentMonthDataModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.isLandscape
import com.agelousis.payments.utils.extensions.isSizeOne
import com.agelousis.payments.utils.extensions.toCalendar
import java.time.LocalDate
import java.util.*

infix fun List<ClientModel>.applyPaymentRowBackground(
    context: Context
) {
    when (context.isLandscape) {
        true ->
            forEach { clientModel ->
                clientModel.backgroundDrawable =
                    if (clientModel.hasPaymentToday)
                        R.drawable.payment_row_marked_radius_background
                    else
                        R.drawable.payment_row_radius_background
            }
        else ->
            if (isSizeOne)
                firstOrNull()?.backgroundDrawable =
                    if (firstOrNull()?.hasPaymentToday == true)
                        R.drawable.payment_row_marked_radius_background
                    else
                        R.drawable.payment_row_radius_background
            else {
                firstOrNull()?.backgroundDrawable =
                    if (firstOrNull()?.hasPaymentToday == true)
                        R.drawable.payment_row_marked_header_background
                    else
                        R.drawable.payment_row_header_background
                lastOrNull()?.backgroundDrawable =
                    if (lastOrNull()?.hasPaymentToday == true)
                        R.drawable.payment_row_marked_footer_background
                    else
                        R.drawable.payment_row_footer_background
            }
    }
}

fun List<ClientModel>.getSixLastPaymentMonths(
    context: Context,
    independentPaymentAmountModelList: List<PaymentAmountModel>? = null
): List<LastPaymentMonthDataModel> {
    val lastPaymentMonthList = arrayListOf<LastPaymentMonthDataModel>()
    val calendar = Date().toCalendar()
    val currentMonth = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
    val lastMonth = currentMonth.minusMonths(1)
    val twoMonthsAgo = currentMonth.minusMonths(2)
    val threeMonthsAgo = currentMonth.minusMonths(3)
    val fourMonthsAgo = currentMonth.minusMonths(4)
    val fiveMonthsAgo = currentMonth.minusMonths(5)
    val paymentAmountModelList = listOf(
        *mapNotNull { clientModel ->
            clientModel.payments
        }.flatten().toTypedArray(),
        *independentPaymentAmountModelList?.toTypedArray() ?: arrayOf()
    )

    /** Current Month **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == currentMonth
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = currentMonth.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = currentMonth.monthValue - 1) ?: ""
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
                    .getOrNull(index = lastMonth.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = lastMonth.monthValue - 1) ?: ""
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
                    .getOrNull(index = twoMonthsAgo.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = twoMonthsAgo.monthValue - 1) ?: ""
        )
    )

    /** Three Months Ago **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == threeMonthsAgo
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = threeMonthsAgo.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = threeMonthsAgo.monthValue - 1) ?: ""
        )
    )

    /** Four Months Ago **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == fourMonthsAgo
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = fourMonthsAgo.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = fourMonthsAgo.monthValue - 1) ?: ""
        )
    )

    /** Five Months Ago **/
    paymentAmountModelList.filter { paymentAmountModel ->
        paymentAmountModel.paymentMonthLocalDate == fiveMonthsAgo
    }.takeIf { payments ->
        payments.isNotEmpty()
    }?.let { payments ->
        lastPaymentMonthList.add(
            LastPaymentMonthDataModel(
                monthLabel = context.resources.getStringArray(R.array.key_months_array)
                    .getOrNull(index = fiveMonthsAgo.monthValue - 1) ?: "",
                amount = payments.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }.sum()
            )
        )
    } ?: lastPaymentMonthList.add(
        LastPaymentMonthDataModel(
            monthLabel = context.resources.getStringArray(R.array.key_months_array)
                .getOrNull(index = fiveMonthsAgo.monthValue - 1) ?: ""
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
package com.agelousis.payments.main.ui.payments.extensions

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.enumerations.PaymentType

typealias PaymentTypeClosure = (paymentType: PaymentType) -> Unit

fun Context.showPaymentsTypeMenu(anchor: View, paymentTypeClosure: PaymentTypeClosure) {
    PopupMenu(this, anchor, Gravity.END).also {
        it.menuInflater.inflate(R.menu.payment_type_menu, it.menu)
        it.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.cashPaymentItem ->
                    paymentTypeClosure(PaymentType.CASH_PAYMENT)
                R.id.onlinePaymentItem ->
                    paymentTypeClosure(PaymentType.ONLINE_PAYMENT)
                R.id.checkPaymentItem ->
                    paymentTypeClosure(PaymentType.CHECK_PAYMENT)
            }
            true
        }
    }.show()

}
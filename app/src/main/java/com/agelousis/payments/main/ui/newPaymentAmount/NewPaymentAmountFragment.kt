package com.agelousis.payments.main.ui.newPaymentAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.agelousis.payments.R
import com.agelousis.payments.databinding.FragmentNewPaymentAmountLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.views.currencyEditText.interfaces.AmountListener
import kotlinx.android.synthetic.main.fragment_new_payment_amount_layout.*
import java.util.*

class NewPaymentAmountFragment: Fragment(), AmountListener {

    companion object {
        const val PAYMENT_AMOUNT_DATA_EXTRA = "NewPaymentAmountFragment=paymentAmountDataExtra"
    }

    override fun onAmountChanged(amount: Double?) {
        amountLayout.infoLabel =
            if (amount != null && !amount.toInt().isZero)
                String.format(
                    resources.getString(R.string.key_vat_value_count_message),
                    (activity as? MainActivity)?.userModel?.vat?.percentageEnclosed ?: ""
                )
            else null
    }

    private val args: NewPaymentAmountFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context ?: return).inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentNewPaymentAmountLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.paymentAmountModel = args.paymentAmountDataModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        amountLayout.amountListener = this
        paymentMonthDetailsLayout.setOnDetailsPressed {
            context?.showListDialog(
                title = resources.getString(R.string.key_select_option_label),
                items = resources.getStringArray(R.array.key_months_array).toList()
            ) { position ->
                paymentMonthDetailsLayout.value = resources.getStringArray(R.array.key_months_array).getOrNull(index = position)
            }
        }
        if (args.lastPaymentMonthIndex > -1)
            paymentMonthDetailsLayout.value = resources.getStringArray(R.array.key_months_array).getOrNull(index = args.lastPaymentMonthIndex + 1)
        else
            dateDetailsLayout.dateSelectionClosure = { dateString ->
                (dateString toDateWith Constants.GENERAL_DATE_FORMAT)?.toCalendar?.let { calendar ->
                    if (paymentMonthDetailsLayout.value.isNullOrEmpty() || paymentMonthDetailsLayout.value == resources.getString(R.string.key_empty_field_label))
                        paymentMonthDetailsLayout.value = resources.getStringArray(R.array.key_months_array).getOrNull(index = calendar.get(Calendar.MONTH) + 1)
                }
            }
        if (dateDetailsLayout.dateValue.isNullOrEmpty() && args.paymentAmountDataModel?.paymentDate.isNullOrEmpty())
            after(
                millis = 600
            ) {
                dateDetailsLayout.dateValue = Date() formattedDateWith Constants.GENERAL_DATE_FORMAT
            }
        skipPaymentAppSwitchLayout.setOnClickListener {
            skipPaymentAppSwitchLayout.isChecked = !skipPaymentAppSwitchLayout.isChecked
        }
    }

    fun checkInputFields() {
        ifLet(
            amountLayout.doubleValue,
            dateDetailsLayout.dateValue,
        ) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                PAYMENT_AMOUNT_DATA_EXTRA,
                PaymentAmountModel(
                    paymentAmount = it.first().toString().toDouble(),
                    paymentMonth = paymentMonthDetailsLayout.value,
                    paymentDate = it.second().toString(),
                    skipPayment = skipPaymentAppSwitchLayout.isChecked,
                    paymentNote = notesField.text?.toString()
                )
            )
            findNavController().popBackStack()
        } ?: run {
            amountLayout.errorState = amountLayout.doubleValue == null
            dateDetailsLayout.errorState = dateDetailsLayout.dateValue == null
        }
    }

}
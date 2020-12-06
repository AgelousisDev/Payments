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
import com.agelousis.payments.views.detailsSwitch.interfaces.AppSwitchListener
import java.util.*

class NewPaymentAmountFragment: Fragment(), AmountListener {

    companion object {
        const val PAYMENT_AMOUNT_DATA_EXTRA = "NewPaymentAmountFragment=paymentAmountDataExtra"
    }

    override fun onAmountChanged(amount: Double?) {
        binding?.amountLayout?.infoLabel =
            if (amount != null && !amount.toInt().isZero)
                String.format(
                    resources.getString(R.string.key_vat_value_count_message),
                    (activity as? MainActivity)?.userModel?.vat?.percentageEnclosed ?: ""
                )
            else null
    }

    private var binding: FragmentNewPaymentAmountLayoutBinding? = null
    private val args: NewPaymentAmountFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context ?: return).inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPaymentAmountLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.paymentAmountModel = args.paymentAmountDataModel
            it.defaultPaymentAmount = (activity as? MainActivity)?.userModel?.defaultPaymentAmount
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding?.amountLayout?.amountListener = this
        binding?.dateDetailsLayout?.dateSelectionClosure = {
            if (it.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.isDatePassed == true)
            binding?.paymentDateNotificationSwitchLayout?.isChecked = false
        }
        binding?.paymentDateNotificationSwitchLayout?.appSwitchListener = object: AppSwitchListener {
            override fun onAppSwitchValueChanged(isChecked: Boolean) {
                if (isChecked)
                    if (binding?.dateDetailsLayout?.dateValue?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.isDatePassed == true) {
                        binding?.paymentDateNotificationSwitchLayout?.isChecked = false
                        context?.toast(
                            message = resources.getString(R.string.key_payment_date_notification_warning_message)
                        )
                    }
            }
        }
        args.lastPaymentMonthDate?.let { lastPaymentMonthDate ->
            val paymentMonthCalendar = lastPaymentMonthDate.toCalendar(plusMonths = 1)
            binding?.paymentMonthDetailsLayout?.dateValue = String.format(
                "%s %s",
                resources.getStringArray(R.array.key_months_array).getOrNull(index = paymentMonthCalendar.get(Calendar.MONTH)) ?: "",
                paymentMonthCalendar.get(Calendar.YEAR)
            )
        }
        if (binding?.paymentMonthDetailsLayout?.dateValue == null && args.paymentAmountDataModel?.paymentMonth == null) {
            val paymentMonthCalendar = Date().calendar
            binding?.paymentMonthDetailsLayout?.dateValue = String.format(
                "%s %s",
                resources.getStringArray(R.array.key_months_array).getOrNull(index = paymentMonthCalendar.get(Calendar.MONTH)) ?: "",
                paymentMonthCalendar.get(Calendar.YEAR)
            )
        }
        binding?.skipPaymentAppSwitchLayout?.setOnClickListener {
            binding?.skipPaymentAppSwitchLayout?.isChecked = binding?.skipPaymentAppSwitchLayout?.isChecked == false
        }
        binding?.singlePaymentAppSwitchLayout?.appSwitchListener = object: AppSwitchListener {
            override fun onAppSwitchValueChanged(isChecked: Boolean) {
                binding?.paymentMonthDetailsLayout?.visibility = if (isChecked) View.GONE else View.VISIBLE
            }
        }
    }

    fun checkInputFields() {
        ifLet(
            binding?.amountLayout?.doubleValue,
            binding?.dateDetailsLayout?.dateValue
        ) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                PAYMENT_AMOUNT_DATA_EXTRA,
                PaymentAmountModel(
                    paymentAmount = it.first().toString().toDouble(),
                    paymentMonth = binding?.paymentMonthDetailsLayout?.dateValue,
                    paymentDate = it.second().toString(),
                    skipPayment = binding?.skipPaymentAppSwitchLayout?.isChecked,
                    paymentNote = binding?.notesField?.text?.toString(),
                    paymentDateNotification = binding?.paymentDateNotificationSwitchLayout?.isChecked,
                    singlePayment = binding?.singlePaymentAppSwitchLayout?.isChecked
                )
            )
            findNavController().popBackStack()
        } ?: run {
            binding?.amountLayout?.errorState = binding?.amountLayout?.doubleValue == null
            binding?.dateDetailsLayout?.errorState = binding?.dateDetailsLayout?.dateValue == null
        }
    }

}
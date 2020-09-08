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
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.ifLet
import com.agelousis.payments.utils.extensions.second
import com.agelousis.payments.utils.extensions.third
import kotlinx.android.synthetic.main.fragment_new_payment_amount_layout.*

class NewPaymentAmountFragment: Fragment() {

    companion object {
        const val PAYMENT_AMOUNT_DATA_EXTRA = "NewPaymentAmountFragment=paymentAmountDataExtra"
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
                    startDate = it.second().toString(),
                    paymentDate = it.third().toString(),
                    endDate = it[3].toString(),
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
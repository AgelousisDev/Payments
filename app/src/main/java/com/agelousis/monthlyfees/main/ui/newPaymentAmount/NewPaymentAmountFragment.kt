package com.agelousis.monthlyfees.main.ui.newPaymentAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.monthlyfees.databinding.FragmentNewPaymentAmountLayoutBinding
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel
import com.agelousis.monthlyfees.utils.extensions.ifLet
import com.agelousis.monthlyfees.utils.extensions.second
import kotlinx.android.synthetic.main.fragment_new_payment_amount_layout.*

class NewPaymentAmountFragment: Fragment() {

    companion object {
        const val PAYMENT_AMOUNT_DATA_EXTRA = "NewPaymentAmountFragment=paymentAmountDataExtra"
    }

    private val args: NewPaymentAmountFragmentArgs by navArgs()

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
            dateDetailsLayout.dateValue
        ) {
            (activity as? MainActivity)?.floatingButtonState = true
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                PAYMENT_AMOUNT_DATA_EXTRA,
                PaymentAmountModel(
                    paymentAmount = it.first().toString().toDouble(),
                    paymentDate = it.second().toString(),
                    skipPayment = skipPaymentAppSwitchLayout.isChecked,
                    paymentNote = notesField.text?.toString()
                )
            )
            findNavController().popBackStack()
        }
    }

}
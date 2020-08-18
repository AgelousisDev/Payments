package com.agelousis.monthlyfees.main.ui.newPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.monthlyfees.databinding.FragmentNewPaymentLayoutBinding
import com.agelousis.monthlyfees.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.monthlyfees.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel
import kotlinx.android.synthetic.main.fragment_new_payment_layout.*

class NewPaymentFragment: Fragment(), NewPaymentPresenter {

    override fun onPaymentAmount(paymentAmountModel: PaymentAmountModel?) {
        findNavController().navigate(
            NewPaymentFragmentDirections.actionNewPaymentFragmentToNewPaymentAmountFragment(
                paymentAmountDataModel = paymentAmountModel
            )
        )
    }

    private val args: NewPaymentFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentNewPaymentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.groupModel = args.groupDataModel
            it.personModel = args.personDataModel
            it.presenter = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
    }

    private fun setupUI() {
        activeAppSwitchLayout.setOnClickListener {
            activeAppSwitchLayout.isChecked = !activeAppSwitchLayout.isChecked
        }
        freeAppSwitchLayout.setOnClickListener {
            freeAppSwitchLayout.isChecked = !freeAppSwitchLayout.isChecked
        }
    }

    private fun configureRecyclerView() {
        paymentAmountRecyclerView.adapter = PaymentAmountAdapter(
            paymentModelList = args.personDataModel?.payments ?: return
        )
    }

}
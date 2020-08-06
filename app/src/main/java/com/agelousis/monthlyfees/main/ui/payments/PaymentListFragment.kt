package com.agelousis.monthlyfees.main.ui.payments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.main.ui.payments.viewModels.PaymentListViewModel

class PaymentListFragment : Fragment(R.layout.fragment_payment_list_layout) {

    private val viewModel by lazy { ViewModelProvider(this).get(PaymentListViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureObservers()
    }
    
    private fun configureRecyclerView() {

    }

    private fun configureObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner, Observer {

        })
    }

}
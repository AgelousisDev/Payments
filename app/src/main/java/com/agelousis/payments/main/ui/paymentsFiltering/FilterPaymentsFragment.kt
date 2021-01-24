package com.agelousis.payments.main.ui.paymentsFiltering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.agelousis.payments.databinding.FilterPaymentsFragmentLayoutBinding

class FilterPaymentsFragment: Fragment() {

    private lateinit var binding: FilterPaymentsFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FilterPaymentsFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    fun checkFilterEntries() {

    }

}
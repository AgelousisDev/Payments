package com.agelousis.payments.main.ui.paymentsFiltering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.agelousis.payments.custom.itemTouchHelper.DraggableItemTouchHelper
import com.agelousis.payments.databinding.FilterPaymentsFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.paymentsFiltering.adapters.PaymentsFilteringAdapter
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType

class FilterPaymentsFragment: Fragment() {

    companion object {
        const val PAYMENTS_FILTERING_OPTION_DATA_EXTRA = "FilterPaymentsFragment=paymentsFilteringOptionDataExtra"
    }

    private lateinit var binding: FilterPaymentsFragmentLayoutBinding
    private val paymentsFilteringOptionTypeList
        get() = (activity as? MainActivity)?.paymentsFilteringOptionTypes ?: listOf(
            PaymentsFilteringOptionType.FREE,
            PaymentsFilteringOptionType.CHARGE,
            PaymentsFilteringOptionType.EXPIRED
        )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FilterPaymentsFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        binding.filterPaymentsRecyclerView.adapter = PaymentsFilteringAdapter(
            paymentsFilteringOptionTypeList = paymentsFilteringOptionTypeList
        )
        binding.filterPaymentsRecyclerView.setHasFixedSize(true)
        ItemTouchHelper(
            DraggableItemTouchHelper(
                list = paymentsFilteringOptionTypeList
            )
        ).attachToRecyclerView(binding.filterPaymentsRecyclerView)
    }

    fun saveFiltersAndDismiss() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PAYMENTS_FILTERING_OPTION_DATA_EXTRA,
            paymentsFilteringOptionTypeList
        )
        findNavController().popBackStack()
    }

}
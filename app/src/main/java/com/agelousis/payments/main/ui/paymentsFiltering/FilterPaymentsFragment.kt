package com.agelousis.payments.main.ui.paymentsFiltering

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.base.BaseViewBindingFragment
import com.agelousis.payments.custom.itemTouchHelper.DraggableItemTouchHelper
import com.agelousis.payments.databinding.FilterPaymentsFragmentLayoutBinding
import com.agelousis.payments.main.ui.paymentsFiltering.adapters.PaymentsFilteringAdapter
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.utils.extensions.applyFloatingButtonBottomMarginWith

class FilterPaymentsFragment: BaseViewBindingFragment<FilterPaymentsFragmentLayoutBinding>(
    inflate = FilterPaymentsFragmentLayoutBinding::inflate
) {

    companion object {
        const val PAYMENTS_FILTERING_OPTION_DATA_EXTRA = "FilterPaymentsFragment=paymentsFilteringOptionDataExtra"
    }

    private val paymentsFilteringOptionTypeList
        get() = MainApplication.paymentsFilteringOptionTypes ?: PaymentsFilteringOptionType.values().toList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        binding?.filterPaymentsRecyclerView?.applyFloatingButtonBottomMarginWith(
            items = paymentsFilteringOptionTypeList
        )
        binding?.filterPaymentsRecyclerView?.adapter = PaymentsFilteringAdapter(
            paymentsFilteringOptionTypeList = paymentsFilteringOptionTypeList
        )
        binding?.filterPaymentsRecyclerView?.setHasFixedSize(true)
        ItemTouchHelper(
            DraggableItemTouchHelper(
                list = paymentsFilteringOptionTypeList
            )
        ).attachToRecyclerView(binding?.filterPaymentsRecyclerView)
    }

    fun saveFiltersAndDismiss() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PAYMENTS_FILTERING_OPTION_DATA_EXTRA,
            paymentsFilteringOptionTypeList
        )
        findNavController().popBackStack()
    }

}
package com.agelousis.payments.views.dateLayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.databinding.YearMonthPickerFragmentLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel

class YearMonthPickerBottomSheetFragment: BasicBottomSheetDialogFragment() {

    companion object {

        fun show(
            supportFragmentManager: FragmentManager,
            yearMonthPickerDataModel: YearMonthPickerDataModel
        ) {
            YearMonthPickerBottomSheetFragment().also { fragment ->
                fragment.yearMonthPickerDataModel = yearMonthPickerDataModel
            }.show(
                supportFragmentManager,
                Constants.YEAR_MONTH_PICKER_FRAGMENT_TAG
            )
        }

    }

    private lateinit var binding: YearMonthPickerFragmentLayoutBinding
    private var yearMonthPickerDataModel: YearMonthPickerDataModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = YearMonthPickerFragmentLayoutBinding.inflate(
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

    }

}
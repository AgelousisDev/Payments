package com.agelousis.payments.views.dateLayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.agelousis.payments.R
import com.agelousis.payments.databinding.YearMonthPickerFragmentLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.calendar
import com.agelousis.payments.views.dateLayout.adapters.YearMonthPickerAdapter
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerFragmentPresenter
import com.agelousis.payments.views.dateLayout.models.MonthDataModel
import com.agelousis.payments.views.dateLayout.models.YearDataModel
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class YearMonthPickerBottomSheetFragment: BottomSheetDialogFragment(), YearMonthPickerFragmentPresenter {

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

    override fun onYearSet(year: Int) {
        yearMonthPickerDataModel?.calendar?.set(
            Calendar.YEAR,
            year
        )
        binding.yearMonthPickerDataModel = yearMonthPickerDataModel
    }

    override fun onMonthSet(month: Int, adapterPosition: Int) {
        yearMonthItemList.forEachIndexed { index, any ->
            (any as? MonthDataModel)?.isSelected = index == adapterPosition
        }
        binding.yearMonthPickerRecyclerView.adapter?.notifyItemRangeChanged(
            yearMonthItemList.indexOfFirst { it is MonthDataModel },
            yearMonthItemList.filterIsInstance<MonthDataModel>().size
        )
        yearMonthPickerDataModel?.calendar?.set(
            Calendar.MONTH,
            month
        )
        binding.yearMonthPickerDataModel = yearMonthPickerDataModel
    }

    override fun onApply() {
        dismiss()
        yearMonthPickerDataModel?.yearMonthPickerListener?.onYearMonthSet(
            year = yearMonthPickerDataModel?.calendar?.get(
                Calendar.YEAR
            ) ?: Date().calendar.get(
                Calendar.YEAR
            ),
            month = yearMonthPickerDataModel?.calendar?.get(
                Calendar.MONTH
            ) ?: Date().calendar.get(
                Calendar.MONTH
            )
        )
    }

    private lateinit var binding: YearMonthPickerFragmentLayoutBinding
    private var yearMonthPickerDataModel: YearMonthPickerDataModel? = null
    private val yearMonthItemList by lazy {
        val arrayList = arrayListOf<Any>()
        arrayList.add(
            YearDataModel(
                yearValue = yearMonthPickerDataModel?.calendar?.get(
                    Calendar.YEAR
                ) ?: Date().calendar.get(
                    Calendar.YEAR
                )
            )
        )
        arrayList.addAll(
            resources.getStringArray(R.array.key_months_cut_array).mapIndexed { index, _ ->
                MonthDataModel(
                    monthValue = index,
                    isSelected = index == yearMonthPickerDataModel?.calendar?.get(
                        Calendar.MONTH
                    ) ?: Date().calendar.get(
                        Calendar.MONTH
                    )
                )
            }
        )
        arrayList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = YearMonthPickerFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        binding.yearMonthPickerDataModel = yearMonthPickerDataModel
        binding.presenter = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        (binding.yearMonthPickerRecyclerView.layoutManager as? GridLayoutManager)?.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                when(yearMonthItemList.getOrNull(index = position)) {
                    is MonthDataModel -> 1
                    else -> 4
                }
        }
        binding.yearMonthPickerRecyclerView.adapter = YearMonthPickerAdapter(
            yearMonthItemList = yearMonthItemList,
            yearMonthPickerFragmentPresenter = this
        )
    }

}
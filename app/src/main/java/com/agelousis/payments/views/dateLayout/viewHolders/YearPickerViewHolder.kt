package com.agelousis.payments.views.dateLayout.viewHolders

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.agelousis.payments.databinding.YearPickerRowLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.custom.CarouselTransformer
import com.agelousis.payments.views.dateLayout.adapters.ViewPagerYearAdapter
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerFragmentPresenter
import com.agelousis.payments.views.dateLayout.models.YearDataModel

class YearPickerViewHolder(private val binding: YearPickerRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(yearDataModel: YearDataModel, yearMonthPickerFragmentPresenter: YearMonthPickerFragmentPresenter) {
        binding.yearPickerViewPager.apply {
            val availableYears = Constants.Years.availableYears.map {
                it.toString()
            }
            adapter = ViewPagerYearAdapter(
                years = availableYears
            )
            offscreenPageLimit = availableYears.size
            setPageTransformer(
                CarouselTransformer(
                    context = context,
                    nextItemWidth = resources.displayMetrics.widthPixels / 3f
                )
            )
            registerOnPageChangeCallback(
                object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        yearMonthPickerFragmentPresenter.onYearSet(
                            year = Constants.Years.availableYears.getOrNull(
                                index = position
                            ) ?: return
                        )
                    }
                }
            )
            currentItem = Constants.Years.availableYears.indexOf(yearDataModel.yearValue)
        }
    }

}
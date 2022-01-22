package com.agelousis.payments.views.dateLayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.databinding.DateFieldLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.utils.extensions.calendar
import com.agelousis.payments.views.dateLayout.interfaces.DatePickerPresenter
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerListener
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import java.util.*

class YearMonthPickerFieldLayout(context: Context, attributeSet: AttributeSet? = null): FrameLayout(context, attributeSet),
    DatePickerPresenter, YearMonthPickerListener {

    val binding = DateFieldLayoutBinding.inflate(LayoutInflater.from(context), this, false)
    var dateValue: String? = null
        set(value) {
            field = value
            value?.let {
                binding.dateView.text = it
                dateSelectionClosure?.invoke(it)
            }
        }
        get() = if (binding.dateView.text?.toString()?.isEmpty() == true) null else binding.dateView.text?.toString()

    private var errorState = false
        set(value) {
            field = value
            binding.lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }
    private var dateSelectionClosure: DateSelectionClosure? = null
    var selectedCalendar: Calendar? = null

    override fun onYearMonthSet(year: Int, month: Int) {
        errorState = false
        dateValue = String.format(
            "%s %s",
            context.resources.getStringArray(R.array.key_months_array).getOrNull(index = month) ?: "",
            year
        )
    }

    override fun onDatePickerShow() {
        YearMonthPickerBottomSheetFragment.show(
            supportFragmentManager = (context as? MainActivity)?.supportFragmentManager ?: return,
            yearMonthPickerDataModel = YearMonthPickerDataModel(
                calendar = selectedCalendar ?: Date().calendar,
                yearMonthPickerListener = this
            )
        )
    }

    init {
        initAttributesAndView(
            attributeSet = attributeSet
        )
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                value = attributes.getString(R.styleable.PersonDetailsLayout_value),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                endIcon = attributes.getResourceId(R.styleable.PersonDetailsLayout_endIconResource, 0)
            )
            binding.presenter = this
            attributes.recycle()
        }
        addView(binding.root)
    }

}
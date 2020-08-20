package com.agelousis.monthlyfees.views.dateLayout

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.DateFieldLayoutBinding
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.views.dateLayout.interfaces.DatePickerPresenter
import com.agelousis.monthlyfees.views.personDetailsLayout.models.PersonDetailsViewDataModel
import kotlinx.android.synthetic.main.date_field_layout.view.*
import java.util.*

class DateFieldLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet),
    DatePickerPresenter {

    var dateValue: String? = null
        set(value) {
            field = value
            value?.let { dateView.text = it }
        }
        get() = if (dateView.text?.toString()?.isEmpty() == true) null else dateView.text?.toString()

    var errorState = false
        set(value) {
            field = value
            lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    override fun onDatePickerShow() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, selectedYear, selectedMonthOfYear, selectedDayOfMonth ->
            errorState = false
            dateView.text = String.format(Constants.DATE_FORMAT_VALUE, selectedDayOfMonth, selectedMonthOfYear + 1, selectedYear)
        }, year, month, day).show()
    }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            val binding = DateFieldLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                value = attributes.getString(R.styleable.PersonDetailsLayout_value),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true)
            )
            binding.presenter = this
            attributes.recycle()
            addView(binding.root)
        }
    }

}
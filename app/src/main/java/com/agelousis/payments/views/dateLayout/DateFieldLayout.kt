package com.agelousis.payments.views.dateLayout

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.databinding.DateFieldLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.dateLayout.interfaces.DatePickerPresenter
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import java.util.*

typealias DateSelectionClosure = (String) -> Unit
class DateFieldLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet), DatePickerPresenter {

    private var binding: DateFieldLayoutBinding? = null
    var dateValue: String? = null
        set(value) {
            field = value
            value?.let {
                binding?.dateView?.text = it
                dateSelectionClosure?.invoke(it)
            }
        }
        get() = if (binding?.dateView?.text?.toString()?.isEmpty() == true) null else binding?.dateView?.text?.toString()

    var errorState = false
        set(value) {
            field = value
            binding?.lineSeparator?.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
            //dateIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, if (value) R.color.red else R.color.dayNightTextOnBackground))
        }
    var dateSelectionClosure: DateSelectionClosure? = null

    override fun onDatePickerShow() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, selectedYear, selectedMonthOfYear, selectedDayOfMonth ->
            errorState = false
            dateValue = String.format(Constants.DATE_FORMAT_VALUE, selectedDayOfMonth, selectedMonthOfYear + 1, selectedYear)
        }, year, month, day).show()
    }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding = DateFieldLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding?.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                value = attributes.getString(R.styleable.PersonDetailsLayout_value),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                icon = attributes.getResourceId(R.styleable.PersonDetailsLayout_iconResource, 0)
            )
            binding?.presenter = this
            attributes.recycle()
            addView(binding?.root)
        }
    }

}
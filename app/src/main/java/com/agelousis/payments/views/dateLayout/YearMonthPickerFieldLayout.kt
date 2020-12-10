package com.agelousis.payments.views.dateLayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.databinding.DateFieldLayoutBinding
import com.agelousis.payments.views.dateLayout.interfaces.DatePickerPresenter
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.util.*

class YearMonthPickerFieldLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet), DatePickerPresenter, MonthPickerDialog.OnDateSetListener {

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

    override fun onDateSet(selectedMonth: Int, selectedYear: Int) {
        errorState = false
        dateValue = String.format(
            "%s %s",
            context.resources.getStringArray(R.array.key_months_array).getOrNull(index = selectedMonth) ?: "",
            selectedYear
        )
    }

    override fun onDatePickerShow() {
        val builder = MonthPickerDialog.Builder(context, this , Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH))
        builder.setMinYear(1990)
            .setMaxYear(2030)
            .setTitle(context.resources.getString(R.string.key_payment_month_label))
            .build()
            .show()
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
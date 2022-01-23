package com.agelousis.payments.views.dateLayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.databinding.DateFieldLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.calendar
import com.agelousis.payments.views.dateLayout.interfaces.DatePickerPresenter
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

typealias DateSelectionClosure = (String) -> Unit
class DateFieldLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet), DatePickerPresenter {

    private lateinit var layoutBinding: DateFieldLayoutBinding
    var dateValue: String? = null
        set(value) {
            field = value
            value?.let {
                layoutBinding.dateView.text = it
                dateSelectionClosure?.invoke(it)
            }
        }
        get() = if (layoutBinding.dateView.text?.toString()?.isEmpty() == true) null else layoutBinding.dateView.text?.toString()
    private var selectedTimeInMillis = Calendar.getInstance().timeInMillis

    var errorState = false
        set(value) {
            field = value
            layoutBinding.lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
            //dateIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, if (value) R.color.red else R.color.dayNightTextOnBackground))
        }
    var dateSelectionClosure: DateSelectionClosure? = null

    override fun onDatePickerShow() {
        MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(layoutBinding.dataModel?.label)
            .setCalendarConstraints(CalendarConstraints.Builder().setStart(0).build())
            .setSelection(selectedTimeInMillis)
            .build().also { picker ->
                picker.addOnPositiveButtonClickListener { timeInMillis ->
                    selectedTimeInMillis = timeInMillis
                    errorState = false
                    val selectedCalendar = timeInMillis.calendar
                    dateValue = String.format(
                        Constants.DATE_FORMAT_VALUE,
                        selectedCalendar.get(Calendar.DAY_OF_MONTH),
                        selectedCalendar.get(Calendar.MONTH) + 1,
                        selectedCalendar.get(Calendar.YEAR)
                    )
                }
            }
            .show(
                (context as? AppCompatActivity)?.supportFragmentManager ?: return,
                null
            )
    }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            layoutBinding = DateFieldLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            layoutBinding.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                value = attributes.getString(R.styleable.PersonDetailsLayout_value),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                endIcon = attributes.getResourceId(R.styleable.PersonDetailsLayout_endIconResource, 0)
            )
            layoutBinding.presenter = this
            attributes.recycle()
            addView(layoutBinding.root)
        }
    }

}
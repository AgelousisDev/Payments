package com.agelousis.monthlyfees.views.personDetailsLayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.PersonDetailsPickerLayoutBinding
import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.monthlyfees.views.personDetailsLayout.models.PersonDetailsViewDataModel
import kotlinx.android.synthetic.main.person_details_picker_layout.view.*

class PersonDetailsPickerLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var value: String? = null
        set(value) {
            field = value
            value?.let { personDetailsPickerValueView.text = it }
        }
        get() = if (personDetailsPickerValueView.text?.toString()?.isEmpty() == true) null else personDetailsPickerValueView.text?.toString()

    var errorState = false
        set(value) {
            field = value
            lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            val binding = PersonDetailsPickerLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                imeOptionsType = ImeOptionsType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_imeOptionType, 0)],
                type = PersonDetailFieldType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_fieldType, 0)]
            )
            attributes.recycle()
            addView(binding.root)
        }
    }

    fun setOnDetailsPressed(listener: OnClickListener) =
        personDetailsPickerLayout.setOnClickListener(listener)

}
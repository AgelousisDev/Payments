package com.agelousis.monthlyfees.views.personDetailsLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.PersonDetailsFieldLayoutBinding
import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.monthlyfees.views.personDetailsLayout.models.PersonDetailsViewDataModel
import kotlinx.android.synthetic.main.person_details_field_layout.view.*

class PersonDetailsLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var value: String? = null
        set(value) {
            field = value
            value?.let { personDetailField.setText(it) }
        }
        get() = personDetailField.text?.toString()

    val amount: Double?
        get() = personDetailField.text?.toString()?.toDoubleOrNull()

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            val binding = PersonDetailsFieldLayoutBinding.inflate(LayoutInflater.from(context), null, false)
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

}
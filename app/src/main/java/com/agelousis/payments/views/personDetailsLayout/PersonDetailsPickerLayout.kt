package com.agelousis.payments.views.personDetailsLayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PersonDetailsPickerLayoutBinding
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel

class PersonDetailsPickerLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    private lateinit var binding: PersonDetailsPickerLayoutBinding
    var value: String? = null
        set(value) {
            field = value
            value?.let { binding.personDetailsPickerValueView.text = it }
        }
        get() = if (binding.personDetailsPickerValueView.text?.toString()?.isEmpty() == true) null else binding.personDetailsPickerValueView.text?.toString()

    var errorState = false
        set(value) {
            field = value
            binding.lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding = PersonDetailsPickerLayoutBinding.inflate(LayoutInflater.from(context), null, false)
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

    fun setOnDetailsPressed(listener: OnClickListener) {
        binding.personDetailsPickerLayout.setOnClickListener(listener)
    }

}
package com.agelousis.payments.views.personDetailsLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PersonDetailsFieldLayoutBinding
import com.agelousis.payments.utils.extensions.showKeyboard
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import kotlinx.android.synthetic.main.person_details_field_layout.view.*

class PersonDetailsLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var value: String? = null
        set(value) {
            field = value
            value?.let { personDetailField.setText(it) }
        }
        get() = if (personDetailField.text?.toString()?.isEmpty() == true) null else personDetailField.text?.toString()

    var errorState = false
        set(value) {
            field = value
            lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

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

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        personDetailsLayout.setOnClickListener {
            personDetailField.requestFocus()
            personDetailField.text?.toString()?.takeIf { it.isNotEmpty() }?.length?.let {
                personDetailField.setSelection(it)
            }
            context?.showKeyboard(
                view = personDetailField
            )
        }
        personDetailField.doOnTextChanged { _, _, _, _ ->
            errorState = false
        }
    }

}
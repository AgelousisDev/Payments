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

class PersonDetailsLayout(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    private var binding: PersonDetailsFieldLayoutBinding? = null
    var value: String? = null
        set(value) {
            field = value
            value?.let { binding?.personDetailField?.setText(it) }
        }
        get() = if (binding?.personDetailField?.text?.toString()?.isEmpty() == true) null else binding?.personDetailField?.text?.toString()

    var errorState = false
        set(value) {
            field = value
            binding?.lineSeparator?.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding = PersonDetailsFieldLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding?.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                imeOptionsType = ImeOptionsType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_imeOptionType, 0)],
                type = PersonDetailFieldType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_fieldType, 0)]
            )
            attributes.recycle()
            addView(binding?.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        binding?.personDetailsLayout?.setOnClickListener {
            binding?.personDetailField?.requestFocus()
            binding?.personDetailField?.text?.toString()?.takeIf { it.isNotEmpty() }?.length?.let {
                binding?.personDetailField?.setSelection(it)
            }
            context?.showKeyboard(
                view = binding?.personDetailField ?: return@setOnClickListener
            )
        }
        binding?.personDetailField?.doOnTextChanged { _, _, _, _ ->
            errorState = false
        }
    }

}
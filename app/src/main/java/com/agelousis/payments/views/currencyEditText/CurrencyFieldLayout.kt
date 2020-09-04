package com.agelousis.payments.views.currencyEditText

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.databinding.CurrencyEditTextLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import com.raccyprus.carservices.entries.interfaces.AmountListener
import kotlinx.android.synthetic.main.currency_edit_text_layout.view.*

class CurrencyFieldLayout(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs) {

    var amountListener: AmountListener? = null
    private var current = ""
    var errorState = false
        set(value) {
            field = value
            lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    var doubleValue: Double? = null
        set(value) {
            field = value
            value?.let {
                currencyField.setText(it.toString())
            }
        }
        get() {
            return currencyField.text?.toString()?.replace(Constants.EURO_VALUE, "")?.toDoubleOrNull()
        }

    init {
        initAttributesAndView(attributeSet = attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            val binding = CurrencyEditTextLayoutBinding.inflate(LayoutInflater.from(context), null, false)
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
        currencyField.doOnTextChanged { text, _, _, _ ->
            errorState = false
            if (text?.isEmpty() == true) {
                currencyField.text?.clear()
                return@doOnTextChanged
            }
            if (text?.toString() != current) {
                current = String.format("${Constants.EURO_VALUE}%s", text.toString().replace(Constants.EURO_VALUE, ""))
                currencyField.setText(current)
                currencyField.setSelection(current.length)
            }
        }
        currencyField.doAfterTextChanged {
            amountListener?.onAmountChanged(
                amount = doubleValue,
                id = tag as? Int
            )
        }
    }

}

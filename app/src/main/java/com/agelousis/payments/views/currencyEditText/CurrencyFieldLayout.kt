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
import com.agelousis.payments.utils.extensions.isZero
import com.agelousis.payments.utils.extensions.showKeyboard
import com.agelousis.payments.views.currencyEditText.interfaces.AmountListener
import com.agelousis.payments.views.currencyEditText.presenters.CurrencyLayoutPresenter
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel

class CurrencyFieldLayout(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs), CurrencyLayoutPresenter {

    override fun onIncrease() {
        doubleValue = (doubleValue ?: 0.0) + 10.0
    }

    override fun onDecrease() {
        if (!doubleValue.isZero)
            doubleValue?.takeIf { it - 10 >= 0 }?.let {
                doubleValue = it - 10
            }
    }

    private var binding: CurrencyEditTextLayoutBinding? = null
    var amountListener: AmountListener? = null
    private var current = ""
    var errorState = false
        set(value) {
            field = value
            binding?.lineSeparator?.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }

    var doubleValue: Double? = null
        set(value) {
            field = value
            value?.let {
                binding?.currencyField?.setText(it.toString())
            }
        }
        get() {
            return binding?.currencyField?.text?.toString()?.replace(Constants.EURO_VALUE, "")?.toDoubleOrNull()
        }
    var infoLabel: String? = null
        set(value) {
            field = value
            value?.let {
                binding?.infoLabel?.text = it
            }
            binding?.infoLabel?.visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

    init {
        initAttributesAndView(attributeSet = attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding = CurrencyEditTextLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding?.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                imeOptionsType = ImeOptionsType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_imeOptionType, 0)],
                type = PersonDetailFieldType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_fieldType, 0)]
            )
            binding?.presenter = this
            attributes.recycle()
            addView(binding?.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        binding?.currencyLayout?.setOnClickListener {
            binding?.currencyField?.requestFocus()
            binding?.currencyField?.text?.toString()?.takeIf { it.isNotEmpty() }?.length?.let {
                binding?.currencyField?.setSelection(it)
            }
            context?.showKeyboard(
                view = binding?.currencyField ?: return@setOnClickListener
            )
        }
        binding?.currencyField?.doOnTextChanged { text, _, _, _ ->
            errorState = false
            if (text?.isEmpty() == true) {
                binding?.currencyField?.text?.clear()
                return@doOnTextChanged
            }
            if (text?.toString() != current) {
                current = String.format("${Constants.EURO_VALUE}%s", text.toString().replace(Constants.EURO_VALUE, ""))
                binding?.currencyField?.setText(current)
                binding?.currencyField?.setSelection(current.length)
            }
        }
        binding?.currencyField?.doAfterTextChanged {
            amountListener?.onAmountChanged(
                amount = doubleValue
            )

        }
    }

}

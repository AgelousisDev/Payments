package com.agelousis.payments.views.currencyEditText

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.databinding.CurrencyEditTextLayoutBinding
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.hideKeyboard
import com.agelousis.payments.utils.extensions.isZero
import com.agelousis.payments.utils.extensions.showKeyboard
import com.agelousis.payments.utils.helpers.CurrencyHelper
import com.agelousis.payments.views.currencyEditText.interfaces.AmountListener
import com.agelousis.payments.views.currencyEditText.presenters.CurrencyLayoutPresenter
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import kotlin.properties.Delegates

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

    lateinit var binding: CurrencyEditTextLayoutBinding
    var amountListener: AmountListener? = null
    var errorState = false
        set(value) {
            field = value
            binding.lineSeparator.setBackgroundColor(ContextCompat.getColor(context, if (value) R.color.red else R.color.grey))
        }
    private var currencySymbol = Constants.EURO_SYMBOL
    var currency: String? by Delegates.observable("EUR") { _, _, newValue ->
        currencySymbol = CurrencyHelper getSymbolFromCurrencyCode newValue
        binding.currencyField.hint = String.format(
            resources.getString(R.string.key_amount_value_hint),
            currencySymbol
        )
    }
    private val decimalDelimiter = "."
    private var previousText = ""
    private val prefixText: String
        get() = currencySymbol
    private var textAmount: String = ""
        set(value) {
            field = value
            binding.currencyField.setText(value)
        }
        get() {
            return binding.currencyField.text.toString().replace(prefixText, "")
        }
    private var amountHasBeenSetManually = false
    var doubleValue: Double? = null
        set(value) {
            field = value
            value?.let {
                amountHasBeenSetManually = true
                binding.currencyField.setText(it.toString())
                formatEditTextAfterLoosingFocus()
                amountHasBeenSetManually = false
            }
        }
        get() =
            textAmount.let {
                var value = it
                if (value.isNotEmpty()) {
                    if (value.contains(",")) {
                        val array = value.split(",")
                        value = array[0].replace(".", "") + "." + array[1]
                    } else if (countUniqueCharacters(input = value) > 1)
                        value = value.replace(".", "")
                    value.toDoubleOrNull()
                }
                else
                    null
            }
    var infoLabel: String? = null
        set(value) {
            field = value
            value?.let {
                binding.infoLabel.text = it
            }
            binding.infoLabel.visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

    init {
        initAttributesAndView(attributeSet = attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.PersonDetailsLayout, 0, 0)
            binding = CurrencyEditTextLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            binding.dataModel = PersonDetailsViewDataModel(
                label = attributes.getString(R.styleable.PersonDetailsLayout_label),
                showLine = attributes.getBoolean(R.styleable.PersonDetailsLayout_showLine, true),
                imeOptionsType = ImeOptionsType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_imeOptionType, 0)],
                type = PersonDetailFieldType.values()[attributes.getInt(R.styleable.PersonDetailsLayout_fieldType, 0)]
            )
            binding.presenter = this
            attributes.recycle()
            addView(binding.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        binding.currencyLayout.setOnClickListener {
            binding.currencyField.requestFocus()
            binding.currencyField.text?.toString()?.takeIf { it.isNotEmpty() }?.length?.let {
                binding.currencyField.setSelection(it)
            }
            context?.showKeyboard(
                view = binding.currencyField
            )
        }
        binding.currencyField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //amountInputPresenter?.onActionDone()
                context?.hideKeyboard(
                    view = binding.currencyField
                )
                formatEditTextAfterLoosingFocus()
            }
            false
        }
        binding.currencyField.doOnTextChanged { charSequence, _, _, _ ->
            errorState = false
            if (amountHasBeenSetManually)
                return@doOnTextChanged
            charSequence?.toString()?.let { text ->
                if (previousText == text)
                    return@doOnTextChanged

                //In case the current and next character are zeros, set again zero.
                if (textAmount == "00") {
                    previousText = "$prefixText${0}"
                    binding.currencyField.setText(previousText)
                    binding.currencyField.setSelection(previousText.length)
                    return@doOnTextChanged
                }

                //Don't let the user to add zero in front of already existed number.
                if (textAmount.isNotEmpty() && textAmount.length > 1
                    && textAmount.first().toString() == "0"
                    && textAmount[1].toString() != decimalDelimiter) {
                    binding.currencyField.setText(previousText)
                    binding.currencyField.setSelection(previousText.length)
                    return@doOnTextChanged
                }

                if (text.isEmpty() || textAmount.isEmpty()) {
                    previousText = ""
                    binding.currencyField.setText("")
                } else if (text.length > 1 && !text.contains(prefixText)) {
                    binding.currencyField.setText(previousText)
                    binding.currencyField.setSelection(previousText.length)
                } else {
                    previousText = "$prefixText$textAmount"

                    //Setting the right selection.
                    val selection = binding.currencyField.selectionStart

                    binding.currencyField.setText(previousText)
                    if (textAmount.isNotEmpty())
                        if (textAmount.length == 1)
                            binding.currencyField.setSelection(previousText.length)
                        else
                            binding.currencyField.setSelection(selection)
                }
            }
        }
        binding.currencyField.doAfterTextChanged {
            amountListener?.onAmountChanged(
                amount = doubleValue
            )

        }
    }

    private fun formatEditTextAfterLoosingFocus() {
        try {
            val unwrappedAmount = doubleValue ?: return
            var formattedString = CurrencyHelper.formatAmount(
                value = unwrappedAmount,
                currencyCode = currency
            )
            //We are doing that because the already implemented formatAmount method
            //removes the space after the currency that this is the way we are
            //using in this subclass.
            formattedString = formattedString.replace(currencySymbol, currencySymbol)
            //setting text after format to EditText

            binding.currencyField.setText(formattedString)
            binding.currencyField.setSelection(binding.currencyField.text?.length ?: return)
            binding.currencyField.clearFocus()
        }
        catch (e: Exception) {
            // catch block
        }
    }

    private fun countUniqueCharacters(input: String): Int {
        var count = 0
        for (c in input) {
            if (c == '.')
                count++
        }
        return count
    }

}

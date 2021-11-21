package com.agelousis.payments.forgotPassword.extensions

import android.text.InputFilter
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.agelousis.payments.utils.extensions.hideKeyboard
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("textInputEditTextMaxLength")
fun setTextInputTextMaxLength(textInputEditText: TextInputEditText, maxLength: Int?) {
    maxLength?.let { textMaxLength ->
        textInputEditText.filters = arrayOf(
            InputFilter.LengthFilter(textMaxLength)
        )
        textInputEditText.doAfterTextChanged { editable ->
            if ((editable?.length ?: 0) == textMaxLength)
                textInputEditText.context.hideKeyboard(
                    view = textInputEditText
                )
        }
    }
}
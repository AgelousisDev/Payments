package com.agelousis.payments.forgotPassword.extensions

import android.text.InputFilter
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.agelousis.payments.utils.extensions.hideKeyboard
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("pinAfterTextChanged")
fun setPinAfterTextChanged(textInputEditText: TextInputEditText, forgotPasswordViewModel: ForgotPasswordViewModel) {
    textInputEditText.doAfterTextChanged {
        forgotPasswordViewModel.pinLiveData.value = it?.toString() ?: return@doAfterTextChanged
    }
}

@BindingAdapter("newPasswordAfterTextChanged")
fun setNewPasswordAfterTextChanged(textInputEditText: TextInputEditText, forgotPasswordViewModel: ForgotPasswordViewModel) {
    textInputEditText.doAfterTextChanged {
        forgotPasswordViewModel.newPasswordLiveData.value = it?.toString() ?: return@doAfterTextChanged
    }
}

@BindingAdapter("repeatNewPasswordAfterTextChanged")
fun setRepeatNewPasswordAfterTextChanged(textInputEditText: TextInputEditText, forgotPasswordViewModel: ForgotPasswordViewModel) {
    textInputEditText.doAfterTextChanged {
        forgotPasswordViewModel.repeatNewPasswordLiveData.value = it?.toString() ?: return@doAfterTextChanged
    }
}

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
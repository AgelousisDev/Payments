package com.agelousis.payments.forgotPassword.extensions

import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.google.android.material.textfield.TextInputEditText

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
package com.agelousis.monthlyfees.login

import android.content.Context
import com.agelousis.monthlyfees.R

enum class SignInState {
    LOGIN, SIGN_UP;

    fun getTitle(context: Context) =
        when(this) {
            SIGN_UP -> context.resources.getString(R.string.key_sign_up_label)
            LOGIN -> context.resources.getString(R.string.key_login_label)
        }

}
package com.agelousis.monthlyfees.main.ui.settings.models

import android.content.Context
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.login.models.UserModel

enum class OptionType {
    CHANGE_USERNAME,
    CHANGE_PASSWORD,
    CHANGE_PROFILE_IMAGE,
    CHANGE_BIOMETRICS_STATE;

    fun getLocalizedTitle(context: Context) =
        when(this) {
            CHANGE_USERNAME -> context.resources.getString(R.string.key_username_label)
            CHANGE_PASSWORD -> context.resources.getString(R.string.key_password_label)
            CHANGE_PROFILE_IMAGE -> context.resources.getString(R.string.key_change_profile_picture_label)
            CHANGE_BIOMETRICS_STATE -> context.resources.getString(R.string.key_biometrics_label)
        }

    var userModel: UserModel? = null

}
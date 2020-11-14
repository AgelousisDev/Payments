package com.agelousis.payments.main.ui.personalInformation.models

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.login.models.UserModel

enum class OptionType {
    CHANGE_FIRST_NAME,
    CHANGE_LAST_NAME,
    CHANGE_USERNAME,
    CHANGE_PASSWORD,
    CHANGE_PROFILE_IMAGE,
    CHANGE_BIOMETRICS_STATE,
    CHANGE_ADDRESS,
    CHANGE_ID_CARD_NUMBER,
    CHANGE_SOCIAL_INSURANCE_NUMBER,
    VAT,
    DEFAULT_PAYMENT_AMOUNT,
    DEFAULT_MESSAGE_TEMPLATE;


    fun getLocalizedTitle(context: Context) =
        when(this) {
            CHANGE_FIRST_NAME -> context.resources.getString(R.string.key_first_name_label)
            CHANGE_LAST_NAME -> context.resources.getString(R.string.key_surname_label)
            CHANGE_USERNAME -> context.resources.getString(R.string.key_username_label)
            CHANGE_PASSWORD -> context.resources.getString(R.string.key_password_label)
            CHANGE_PROFILE_IMAGE -> context.resources.getString(R.string.key_change_profile_picture_label)
            CHANGE_BIOMETRICS_STATE -> context.resources.getString(R.string.key_biometrics_label)
            CHANGE_ADDRESS -> context.resources.getString(R.string.key_address_label)
            CHANGE_ID_CARD_NUMBER -> context.resources.getString(R.string.key_id_card_number_label)
            CHANGE_SOCIAL_INSURANCE_NUMBER -> context.resources.getString(R.string.key_social_insurance_number_label)
            VAT -> context.resources.getString(R.string.key_vat_percent_label)
            DEFAULT_PAYMENT_AMOUNT -> context.resources.getString(R.string.key_default_payment_amount_label)
            DEFAULT_MESSAGE_TEMPLATE -> context.resources.getString(R.string.key_default_message_template_label)
        }

    var userModel: UserModel? = null
    var biometricAvailability = false

}
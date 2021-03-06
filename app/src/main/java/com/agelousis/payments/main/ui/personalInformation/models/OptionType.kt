package com.agelousis.payments.main.ui.personalInformation.models

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.countrySelector.enumerations.CountryDataModel
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType

enum class OptionType {
    CHANGE_FIRST_NAME,
    CHANGE_LAST_NAME,
    CHANGE_USERNAME,
    CHANGE_PASSWORD,
    CHANGE_PASSWORD_PIN,
    CHANGE_PROFILE_IMAGE,
    CHANGE_BIOMETRICS_STATE,
    CHANGE_ADDRESS,
    CHANGE_ID_CARD_NUMBER,
    CHANGE_SOCIAL_INSURANCE_NUMBER,
    CHANGE_CURRENCY,
    CHANGE_COUNTRY,
    VAT,
    DEFAULT_PAYMENT_AMOUNT,
    DEFAULT_MESSAGE_TEMPLATE,
    EXPORT_DATABASE,
    DELETE_USER;

    fun getLocalizedTitle(context: Context) =
        when(this) {
            CHANGE_FIRST_NAME -> context.resources.getString(R.string.key_first_name_label)
            CHANGE_LAST_NAME -> context.resources.getString(R.string.key_surname_label)
            CHANGE_USERNAME -> context.resources.getString(R.string.key_username_label)
            CHANGE_PASSWORD -> context.resources.getString(R.string.key_password_label)
            CHANGE_PASSWORD_PIN -> context.resources.getString(R.string.key_forgot_password_pin_hint)
            CHANGE_PROFILE_IMAGE -> context.resources.getString(R.string.key_change_profile_picture_label)
            CHANGE_BIOMETRICS_STATE -> context.resources.getString(R.string.key_biometrics_label)
            CHANGE_ADDRESS -> context.resources.getString(R.string.key_address_label)
            CHANGE_ID_CARD_NUMBER -> context.resources.getString(R.string.key_id_card_number_label)
            CHANGE_SOCIAL_INSURANCE_NUMBER -> context.resources.getString(R.string.key_social_insurance_number_label)
            CHANGE_CURRENCY -> context.resources.getString(R.string.key_select_currency_label)
            CHANGE_COUNTRY -> context.resources.getString(R.string.key_select_country_label)
            VAT -> context.resources.getString(R.string.key_vat_percent_label)
            DEFAULT_PAYMENT_AMOUNT -> context.resources.getString(R.string.key_default_payment_amount_label)
            DEFAULT_MESSAGE_TEMPLATE -> context.resources.getString(R.string.key_default_message_template_label)
            EXPORT_DATABASE -> context.resources.getString(R.string.key_export_database_label)
            DELETE_USER -> context.resources.getString(R.string.key_delete_user_label)
        }

    var userModel: UserModel? = null
    var biometricAvailability = false
    var currencyType: CurrencyType? = null
    var countryDataModel: CountryDataModel? = null

}
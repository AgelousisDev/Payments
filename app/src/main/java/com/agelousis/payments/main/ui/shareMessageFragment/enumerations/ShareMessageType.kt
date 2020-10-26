package com.agelousis.payments.main.ui.shareMessageFragment.enumerations

import com.agelousis.payments.R
import com.agelousis.payments.utils.constants.Constants

enum class ShareMessageType {
    CALL, SMS, WHATS_APP, VIBER, EMAIL;

    val icon
        get() = when(this) {
            CALL -> R.drawable.ic_telephone
            SMS -> R.drawable.ic_sms
            WHATS_APP -> R.drawable.ic_whatsapp
            VIBER -> R.drawable.ic_viber
            EMAIL -> R.drawable.ic_email
        }

    val packageName
        get() = when(this) {
            CALL, SMS -> null
            WHATS_APP -> Constants.WHATS_APP_PACKAGE_NAME
            VIBER -> Constants.VIBER_PACKAGE_NAME
            EMAIL -> null
        }

    val schemeUrl
        get() = when(this) {
            CALL, SMS -> null
            WHATS_APP -> Constants.WHATS_APP_SCHEME_URL
            VIBER -> Constants.VIBER_SCHEME_URL
            EMAIL -> null
        }

    var isEnabled = true

}
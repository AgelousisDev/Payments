package com.agelousis.payments.utils.constants

import android.graphics.Color

object Constants {
    const val IMAGE_MIME_TYPE = "image/*"
    const val PROFILE_IMAGE_NAME = "profile_image"
    const val EURO_SYMBOL = "€"
    const val DATE_FORMAT_VALUE = "%d/%d/%d"
    const val GENERAL_MIME_TYPE = "*/*"
    const val BIN_FILE_EXTENSION = "bin"
    const val DATE_FORMAT = "MMM dd, yyyy HH:mm:ss"
    const val VIEWING_DATE_FORMAT = "MMMM dd, yyyy"
    const val GENERAL_DATE_FORMAT = "dd/MM/yyyy"
    const val FILE_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"
    const val MONTH_DATE_FORMAT = "MMMM yyyy"
    const val GRAPH_DATE_FORMAT = "MMM yyyy"
    const val YEAR_MONTH_DATE_FORMAT = "MMM, yyyy"
    const val PDF_FILE_NAME_FORMAT_VALUE = "sample_%s.pdf"
    const val PDF_MIME_TYPE = "application/pdf"
    const val GOOGLE_DRIVE_URI = "content://com.google.android.apps.docs.storage"
    const val USER_SELECTION_FRAGMENT_TAG = "userSelectionFragmentTag"
    const val GROUP_IMAGE_NAME = "group_image"
    const val SHARE_MESSAGE_FRAGMENT_TAG = "shareMessageFragmentTag"
    const val WHATS_APP_PACKAGE_NAME = "com.whatsapp"
    const val WHATS_APP_SCHEME_URL = "https://wa.me/%s?text=%s"
    const val VIBER_PACKAGE_NAME = "com.viber.voip"
    const val VIBER_SCHEME_URL = "viber://add?number=%s"
    const val PAYMENTS_CSV_FILE = "payments.csv"
    const val CSV_MIME_TYPE = "text/csv"
    const val SHARED_PREFERENCES_NAME = "preferences"
    const val SHARED_PREFERENCES_NOTIFICATION_REQUEST_CODE_KEY = "notification_request_code"
    const val DARK_MODE_KEY = "dark_mode"
    const val FORGOT_PASSWORD_FRAGMENT_TAG = "forgotPasswordFragmentTag"
    const val PAYMENTS_MENU_OPTIONS_FRAGMENT_TAG = "paymentMenuOptionsFragmentTag"
    const val EXPORT_DATABASE_FILE_NAME = "PAYMENTS.payments"
    const val EXPORT_DATABASE_FILE_NAME_EXTENSION = ".payments"
    const val TOTAL_PAYMENTS_AMOUNT_FRAGMENT_TAG = "totalPaymentsFragmentTag"
    const val SHARED_PREFERENCES_CURRENCY_SYMBOL_KEY = "currency_symbol"
    const val CURRENCY_SELECTOR_FRAGMENT_TAG = "currencySelectorFragmentTag"
    const val SHARED_PREFERENCES_FIRST_TIME_KEY = "first_time"
    const val COUNTRY_SELECTOR_FRAGMENT_TAG = "countrySelectorFragmentTag"
    const val SHARED_PREFERENCES_COUNTRY_DATA_KEY = "country_data"
    const val SHARED_PREFERENCES_PAYMENTS_FILTERING_DATA_KEY = "payments_filtering_option_types"
    const val MATERIAL_MENU_FRAGMENT_TAG = "materialMenuFragmentTag"
    const val LOADER_DIALOG_TAG = "LoaderDialog"
    const val SHOW_NOTIFICATION_INTENT_ACTION = "SHOW_NOTIFICATION"
    const val CLIENTS_SELECTOR_FRAGMENT_TAG = "clientsSelectorFragmentTag"
    const val SHARED_PREFERENCES_CLIENT_MODEL_DATA = "client_model_data"
    const val GROUP_SELECTOR_FRAGMENT_TAG = "groupSelectorFragmentTag"
    const val YEAR_MONTH_PICKER_FRAGMENT_TAG = "yearMonthPickerFragmentTag"
    const val COLOR_SELECTOR_FRAGMENT_TAG = "colorSelectorFragmentTag"
    const val SHARED_PREFERENCES_BALANCE_OVERVIEW_DATA_KEY = "balanceOverview"

    object Colors {
        val colorPickerColors = intArrayOf(
            Color.parseColor("#FFE066"),
            Color.parseColor("#FFBA59"),
            Color.parseColor("#FF8C8C"),
            Color.parseColor("#FF99E5"),
            Color.parseColor("#C3A6FF"),
            Color.parseColor("#9FBCF5"),
            Color.parseColor("#8CE2FF"),
            Color.parseColor("#87F5B5"),
            Color.parseColor("#BCF593"),
            Color.parseColor("#E2F587"),
            Color.parseColor("#D9BCAD"),
            Color.parseColor("#46526D")
        )
    }

    object Years {
        val availableYears = (2000..2030).toList()
    }

}
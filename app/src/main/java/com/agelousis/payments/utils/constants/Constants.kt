package com.agelousis.payments.utils.constants

import android.graphics.Color

object Constants {
    const val IMAGE_MIME_TYPE = "image/*"
    const val PROFILE_IMAGE_NAME = "profile_image"
    const val EURO_VALUE = "€"
    const val DATE_FORMAT_VALUE = "%d/%d/%d"
    const val GENERAL_MIME_TYPE = "*/*"
    const val BIN_FILE_EXTENSION = "bin"
    const val DATE_FORMAT = "MMM dd, yyyy HH:mm:ss"
    const val GENERAL_DATE_FORMAT = "dd/MM/yyyy"
    const val FILE_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"
    const val MONTH_DATE_FORMAT = "MMMM yyyy"
    const val PDF_FILE_NAME_FORMAT_VALUE = "sample_%s.pdf"
    const val PDF_MIME_TYPE = "application/pdf"
    const val GOOGLE_DRIVE_URI = "content://com.google.android.apps.docs.storage"
    const val USER_SELECTION_FRAGMENT_TAG = "userSelectionFragmentTag"
    const val GROUP_IMAGE_NAME = "group_image"
    const val SHARE_MESSAGE_FRAGMENT_TAG = "shareMessageFragmentTag"
    const val WHATS_APP_PACKAGE_NAME = "com.whatsapp"
    const val WHATS_APP_SCHEME_URL = "https://wa.me/357%s?text=%s"
    const val VIBER_PACKAGE_NAME = "com.viber.voip"
    const val VIBER_SCHEME_URL = "viber://add?number=357%s"

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
            Color.parseColor("#D9BCAD")
        )
    }

}
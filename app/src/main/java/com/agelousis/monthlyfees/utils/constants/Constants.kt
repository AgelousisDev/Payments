package com.agelousis.monthlyfees.utils.constants

import android.graphics.Color

object Constants {
    const val SHARED_PREFERENCES_KEY = "sharedPreferences"
    const val IMAGE_MIME_TYPE = "image/*"
    const val PROFILE_IMAGE_NAME = "profile_image"
    const val EURO_VALUE = "€"
    const val DATE_FORMAT_VALUE = "%d/%d/%d"
    const val CSV_FILE = "CSV_FILE.csv"
    const val DATABASE_FILE_NAME = "MONTHLY_FEES.db"
    const val GENERAL_MIME_TYPE = "*/*"
    const val BIN_FILE_EXTENSION = "bin"
    const val OCTET_STREAM_MIME_TYPE = "application/octet-stream"

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
package com.agelousis.monthlyfees.main.ui.files.models

import com.agelousis.monthlyfees.utils.constants.Constants
import java.text.SimpleDateFormat
import java.util.*

data class FileDataModel(val fileId: Int? = null,
                         val description: String?,
                         val fileName: String?,
                         val dateTime: String?
) {

    val fileDate: Date
        get() = dateTime?.let { SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault()).parse(it) } ?: Date()

    val showingDate: String?
        get() = dateTime?.let {
                val fileDateFormat = SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault())
                val fileDate = fileDateFormat.parse(it)
                val showingDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
                showingDateFormat.format(fileDate ?: return@let null)
        }

}
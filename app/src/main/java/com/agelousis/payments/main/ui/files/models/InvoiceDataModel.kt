package com.agelousis.payments.main.ui.files.models

import android.os.Parcelable
import com.agelousis.payments.main.ui.files.enumerations.InvoiceRowState
import com.agelousis.payments.utils.constants.Constants
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class InvoiceDataModel(val fileId: Int? = null,
                            val description: String?,
                            val fileName: String?,
                            val dateTime: String?
): Parcelable {

    val fileDate: Date
        get() = dateTime?.let { SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault()).parse(it) } ?: Date()

    val showingDate: String?
        get() = dateTime?.let {
                val fileDateFormat = SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault())
                val fileDate = fileDateFormat.parse(it)
                val showingDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
                showingDateFormat.format(fileDate ?: return@let null)
        }

    @IgnoredOnParcel
    var fileData: ByteArray? = null

    @IgnoredOnParcel
    var invoiceRowState = InvoiceRowState.NORMAL

}
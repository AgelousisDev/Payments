package com.agelousis.payments.main.ui.periodFilter.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.utils.extensions.pdfFormattedCurrentDate
import java.io.File
import java.util.*

class PeriodFilterViewModel: ViewModel() {

    var periodFilterMinimumPaymentMonthDate by mutableStateOf<String?>(value = null)
    var periodFilterMaximumPaymentMonthDate by mutableStateOf<String?>(value = null)

    suspend fun insertFile(userModel: UserModel?, file: File, description: String) {
        DBManager.insertFile(
            userId = userModel?.id,
            invoiceDataModel = InvoiceDataModel(
                description = description,
                fileName = file.name,
                dateTime = Date().pdfFormattedCurrentDate
            ).also {
                it.fileData = file.readBytes()
            }
        )
    }

}
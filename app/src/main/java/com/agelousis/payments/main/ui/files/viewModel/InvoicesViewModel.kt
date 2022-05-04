package com.agelousis.payments.main.ui.files.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.utils.extensions.after
import java.io.File
import java.io.FileOutputStream

class InvoicesViewModel: ViewModel() {

    var itemsFilteredList by mutableStateOf(value = emptyList<Any>())
    val invoicesLiveData by lazy { MutableLiveData<List<InvoiceDataModel>>() }
    val fileDeletionLiveData by lazy { MutableLiveData<Boolean>() }
    val selectedInvoicesLiveData by lazy { MutableLiveData<List<InvoiceDataModel>>() }

    suspend fun initializeInvoices(userId: Int?) {
        DBManager.initializeFiles(
            userId = userId
        ) {
            invoicesLiveData.value = it
        }
    }

    suspend fun deleteInvoices(context: Context, invoiceDataModelList: List<InvoiceDataModel?>) {
        invoiceDataModelList.forEach { fileDataModel ->
            deleteActualInvoice(
                context = context,
                fileName = fileDataModel?.fileName
            )
        }
        DBManager.deleteFiles(
            fileIds = invoiceDataModelList.mapNotNull { it?.fileId }
        ) {
            fileDeletionLiveData.value = true
        }
    }

    private fun deleteActualInvoice(context: Context, fileName: String?) {
        File(context.filesDir, fileName ?: return).delete()
    }

    fun createFilesIfRequired(context: Context, invoices: List<InvoiceDataModel>) {
        invoices.forEach loop@ { fileDataModel ->
            val file = File(context.filesDir, fileDataModel.fileName ?: return@loop)
            if (!file.exists()) {
                file.createNewFile()
                FileOutputStream(file).use { fileOutputStream ->
                    fileOutputStream.write(fileDataModel.fileData ?: return@loop)
                }
            }
        }
    }

}
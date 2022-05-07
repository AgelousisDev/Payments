package com.agelousis.payments.main.ui.files.viewModel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.utils.extensions.after
import java.io.File
import java.io.FileOutputStream

typealias InvoiceDataModelBlock = (InvoiceDataModel) -> Unit

class InvoicesViewModel: ViewModel() {

    var itemsFilteredList = mutableStateListOf<Any>()
    var searchQuery by mutableStateOf<String?>(value = "")
    val invoicesLiveData by lazy { MutableLiveData<List<InvoiceDataModel>>() }
    val fileDeletionLiveData by lazy { MutableLiveData<Boolean>() }
    val selectedInvoiceModelList = mutableStateListOf<InvoiceDataModel>()
    var invoicesDeletionState by mutableStateOf(value = false)
    var updateInvoicesState by mutableStateOf(value = true)
    var invoiceDataModelBlock: InvoiceDataModelBlock? = null

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

    infix fun onInvoiceDataModel(
        invoiceDataModel: InvoiceDataModel
    ) {
        invoiceDataModelBlock?.invoke(invoiceDataModel)
    }

}
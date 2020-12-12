package com.agelousis.payments.main.ui.files.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.files.models.FileDataModel
import java.io.File
import java.io.FileOutputStream

class FilesViewModel: ViewModel() {

    val filesLiveData by lazy { MutableLiveData<List<FileDataModel>>() }
    val fileDeletionLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun initializeFiles(context: Context, userId: Int?) {
        val dbManager = DBManager(context = context)
        dbManager.initializeFiles(
            userId = userId
        ) {
            filesLiveData.value = it
        }
    }

    suspend fun deleteFiles(context: Context, fileDataModelList: List<FileDataModel?>) {
        fileDataModelList.forEach { fileDataModel ->
            deleteActualFile(
                context = context,
                fileName = fileDataModel?.fileName
            )
        }
        val dbManager = DBManager(context = context)
        dbManager.deleteFiles(
            fileIds = fileDataModelList.mapNotNull { it?.fileId }
        ) {
            fileDeletionLiveData.value = true
        }
    }

    private fun deleteActualFile(context: Context, fileName: String?) {
        File(context.filesDir, fileName ?: return).delete()
    }

    fun createFilesIfRequired(context: Context, files: List<FileDataModel>) {
        files.forEach loop@ { fileDataModel ->
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
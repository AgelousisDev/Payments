package com.agelousis.monthlyfees.main.ui.files.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel
import java.io.File

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

    suspend fun deleteFile(context: Context, fileDataModel: FileDataModel) {
        deleteActualFile(
            context = context,
            fileName = fileDataModel.fileName
        )
        val dbManager = DBManager(context = context)
        dbManager.deleteFile(
            fileId = fileDataModel.fileId
        ) {
            fileDeletionLiveData.value = true
        }
    }

    private fun deleteActualFile(context: Context, fileName: String?) {
        File(context.filesDir, fileName ?: return).delete()
    }

}
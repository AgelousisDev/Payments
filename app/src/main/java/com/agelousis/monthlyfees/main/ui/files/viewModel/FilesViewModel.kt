package com.agelousis.monthlyfees.main.ui.files.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel

class FilesViewModel: ViewModel() {

    val fileInsertionLiveData by lazy { MutableLiveData<Boolean>() }
    val filesLiveData by lazy { MutableLiveData<List<FileDataModel>>() }

    suspend fun insertFile(context: Context, userId: Int?, fileDataModel: FileDataModel) {
        val dbManager = DBManager(context = context)
        dbManager.insertFile(
            userId = userId,
            fileDataModel = fileDataModel
        ) {
            fileInsertionLiveData.value = true
        }
    }

    suspend fun initializeFiles(context: Context, userId: Int?) {
        val dbManager = DBManager(context = context)
        dbManager.initializeFiles(
            userId = userId
        ) {
            filesLiveData.value = it
        }
    }

}
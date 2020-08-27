package com.agelousis.monthlyfees.main.ui.files.presenter

import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel

interface FilePresenter {
    fun onFileSelected(fileDataModel: FileDataModel)
}
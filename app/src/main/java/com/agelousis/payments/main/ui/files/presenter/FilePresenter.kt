package com.agelousis.payments.main.ui.files.presenter

import com.agelousis.payments.main.ui.files.models.FileDataModel

interface FilePresenter {
    fun onFileSelected(fileDataModel: FileDataModel, adapterPosition: Int)
    fun onFileLongPressed(adapterPosition: Int)
}
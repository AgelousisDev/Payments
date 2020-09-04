package com.agelousis.payments.main.ui.files.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.FileRowLayoutBinding
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.files.presenter.FilePresenter

class FileViewHolder(private val binding: FileRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(fileDataModel: FileDataModel, presenter: FilePresenter) {
        binding.fileDataModel = fileDataModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}
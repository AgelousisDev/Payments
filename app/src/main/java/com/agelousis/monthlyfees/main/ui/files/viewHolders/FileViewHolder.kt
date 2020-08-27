package com.agelousis.monthlyfees.main.ui.files.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.FileRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel
import com.agelousis.monthlyfees.main.ui.files.presenter.FilePresenter

class FileViewHolder(private val binding: FileRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(fileDataModel: FileDataModel, presenter: FilePresenter) {
        binding.fileDataModel = fileDataModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}
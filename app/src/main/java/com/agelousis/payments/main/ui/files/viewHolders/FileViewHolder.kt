package com.agelousis.payments.main.ui.files.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.FileRowLayoutBinding
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.main.ui.files.presenter.InvoicePresenter

class FileViewHolder(private val binding: FileRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(invoiceDataModel: InvoiceDataModel, presenter: InvoicePresenter) {
        binding.fileDataModel = invoiceDataModel
        binding.adapterPosition = bindingAdapterPosition
        binding.presenter = presenter
        binding.fileRowDescription.isSelected = true
        binding.fileRowDate.isSelected = true
        binding.fileRowCardView.setOnLongClickListener {
            presenter.onInvoiceLongPressed(
                adapterPosition = bindingAdapterPosition
            )
            true
        }
        binding.executePendingBindings()
    }

}
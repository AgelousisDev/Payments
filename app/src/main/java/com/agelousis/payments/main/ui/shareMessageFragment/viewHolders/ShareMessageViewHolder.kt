package com.agelousis.payments.main.ui.shareMessageFragment.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ShareMessageRowLayoutBinding
import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType
import com.agelousis.payments.main.ui.shareMessageFragment.presenters.ShareMessagePresenter

class ShareMessageViewHolder(private val binding: ShareMessageRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(shareMessageType: ShareMessageType, presenter: ShareMessagePresenter) {
        binding.shareMessageType = shareMessageType
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}
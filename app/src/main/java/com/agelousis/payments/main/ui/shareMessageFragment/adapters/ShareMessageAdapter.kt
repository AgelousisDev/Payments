package com.agelousis.payments.main.ui.shareMessageFragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ShareMessageRowLayoutBinding
import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType
import com.agelousis.payments.main.ui.shareMessageFragment.presenters.ShareMessagePresenter
import com.agelousis.payments.main.ui.shareMessageFragment.viewHolders.ShareMessageViewHolder

class ShareMessageAdapter(private val shareMessageTypeList: List<ShareMessageType>, private val presenter: ShareMessagePresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShareMessageViewHolder(
            binding = ShareMessageRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ShareMessageViewHolder)?.bind(
            shareMessageType = shareMessageTypeList.getOrNull(
                index = position
            ) ?: return,
            presenter = presenter
        )
    }

    override fun getItemCount() = shareMessageTypeList.size

}
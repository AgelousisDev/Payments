package com.agelousis.payments.main.ui.clientsSelector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ClientSelectorRowLayoutBinding
import com.agelousis.payments.main.ui.clientsSelector.presenters.ClientSelectorPresenter
import com.agelousis.payments.main.ui.clientsSelector.viewHolders.ClientSelectorViewHolder
import com.agelousis.payments.main.ui.payments.models.ClientModel

class ClientsSelectorAdapter(private val clientModelList: List<ClientModel>, private val clientSelectorPresenter: ClientSelectorPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ClientSelectorViewHolder(
            binding = ClientSelectorRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ClientSelectorViewHolder)?.bind(
            clientModel = clientModelList.getOrNull(
                index = position
            ) ?: return,
            clientSelectorPresenter = clientSelectorPresenter
        )
    }

    override fun getItemCount() = clientModelList.size

    fun reloadData() = notifyDataSetChanged()

}
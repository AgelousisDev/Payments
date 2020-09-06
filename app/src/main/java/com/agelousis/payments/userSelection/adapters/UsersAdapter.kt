package com.agelousis.payments.userSelection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.UserRowLayoutBinding
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.userSelection.presenters.UserSelectionPresenter
import com.agelousis.payments.userSelection.viewHolders.UserViewHolder

class UsersAdapter(private val users: List<UserModel>, private val presenter: UserSelectionPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(
            binding = UserRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? UserViewHolder)?.bind(
            userModel = users.getOrNull(index = position) ?: return,
            presenter = presenter
        )
    }

    override fun getItemCount() = users.size

}
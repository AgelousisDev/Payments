package com.agelousis.payments.userSelection

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.userSelection.adapters.UsersAdapter
import com.agelousis.payments.userSelection.presenters.UserSelectionPresenter
import com.agelousis.payments.utils.constants.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.user_selection_fragment_layout.*

class UserSelectionFragment: BottomSheetDialogFragment(), UserSelectionPresenter {

    companion object {
        private const val USERS_EXTRA = "UserSelectionFragment=usersExtra"

        fun show(supportFragmentManager: FragmentManager, users: ArrayList<UserModel>) {
            UserSelectionFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelableArrayList(USERS_EXTRA, users)
                }
            }.show(
                supportFragmentManager, Constants.USER_SELECTION_FRAGMENT_TAG
            )
        }

    }

    override fun onUserSelected(userModel: UserModel) {
        (activity as? LoginActivity)?.onUserSelected(
            userModel = userModel,
        )
        dismiss()
    }

    private val users by lazy { arguments?.getParcelableArrayList<UserModel>(USERS_EXTRA) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.user_selection_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        usersRecyclerView.adapter = UsersAdapter(
            users = users ?: return,
            presenter = this
        )
    }

}
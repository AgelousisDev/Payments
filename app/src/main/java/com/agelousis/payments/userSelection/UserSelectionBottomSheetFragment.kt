package com.agelousis.payments.userSelection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import com.agelousis.payments.userSelection.ui.UserSelectionLayout
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.saveImage
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import java.io.File

class UserSelectionBottomSheetFragment: BasicBottomSheetDialogFragment() {

    companion object {
        private const val USERS_EXTRA = "UserSelectionFragment=usersExtra"

        fun show(supportFragmentManager: FragmentManager, users: ArrayList<UserModel>) {
            UserSelectionBottomSheetFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelableArrayList(USERS_EXTRA, users)
                }
            }.show(
                supportFragmentManager, Constants.USER_SELECTION_FRAGMENT_TAG
            )
        }

    }

    private val users by lazy {
        arguments?.getParcelableArrayList<UserModel>(USERS_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    UserSelectionLayout(
                        userModelList = users,
                        userSelectionBlock = this@UserSelectionBottomSheetFragment::onUserSelected
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveUserProfileImages()
    }

    private fun onUserSelected(userModel: UserModel) {
        (activity as? LoginActivity)?.onUserSelected(
            userModel = userModel,
        )
        dismiss()
    }

    private fun saveUserProfileImages() {
        users?.forEach { userModel ->
            if (!File(context?.filesDir, userModel.profileImage ?: return@forEach).exists())
                context?.saveImage(
                    fileName = userModel.profileImage,
                    byteArray = userModel.profileImageData
                )
        }
    }

    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        UserSelectionLayout(
            userModelList = users,
            userSelectionBlock = this@UserSelectionBottomSheetFragment::onUserSelected
        )
    }

}
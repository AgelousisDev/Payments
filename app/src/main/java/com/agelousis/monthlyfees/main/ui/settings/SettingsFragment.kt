package com.agelousis.monthlyfees.main.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.databinding.FragmentSettingsLayoutBinding
import com.agelousis.monthlyfees.login.LoginActivity
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.ui.settings.adapters.OptionTypesAdapter
import com.agelousis.monthlyfees.main.ui.settings.models.OptionType
import com.agelousis.monthlyfees.utils.extensions.loadImageUri
import com.agelousis.monthlyfees.utils.extensions.openGallery
import com.agelousis.monthlyfees.utils.extensions.saveProfileImage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_settings_layout.*

class SettingsFragment: Fragment(), OptionPresenter {

    override fun onChangeProfilePicture() {
        openGallery(
            requestCode = LoginActivity.PROFILE_SELECT_REQUEST_CODE
        )
    }

    override fun onUsernameChange(newUsername: String) {
        newUserModel?.username = newUsername
    }

    override fun onPasswordChange(newPassword: String) {
        newUserModel?.password = newPassword
    }

    override fun onBiometricsState(state: Boolean) {
        newUserModel?.biometrics = state
    }

    private val dbManager by lazy { context?.let { DBManager(context = it) } }
    private val newUserModel by lazy { (activity as? MainActivity)?.userModel }
    private val optionTypes by lazy {
        arrayListOf(
            OptionType.CHANGE_USERNAME.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_PASSWORD.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_PROFILE_IMAGE.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_BIOMETRICS_STATE.also {
                it.userModel = newUserModel
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSettingsLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        optionRecyclerView.adapter = OptionTypesAdapter(
            optionTypes = optionTypes,
            optionPresenter = this
        )
        optionRecyclerView.addItemDecoration(DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        ))
    }

    suspend fun updateUser(successBlock: () -> Unit) {
        dbManager?.updateUser(
            userModel = newUserModel ?: return,
            updateBlock = successBlock
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        when(requestCode) {
            LoginActivity.PROFILE_SELECT_REQUEST_CODE ->
                data?.data?.let { imageUri ->
                    newUserModel?.profileImage = context?.saveProfileImage(
                        byteArray = context?.contentResolver?.openInputStream(imageUri)?.readBytes()
                    )
                    profileImageView.setBackgroundResource(0)
                    profileImageView.loadImageUri(
                        imageUri = imageUri
                    )
                }
        }
    }

}
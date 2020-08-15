package com.agelousis.monthlyfees.main.ui.personalInformation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.databinding.FragmentPersonalInformationLayoutBinding
import com.agelousis.monthlyfees.login.LoginActivity
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.ui.personalInformation.adapters.OptionTypesAdapter
import com.agelousis.monthlyfees.main.ui.personalInformation.models.OptionType
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_personal_information_layout.*

class PersonalInformationFragment: Fragment(), OptionPresenter {

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

    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) }
    private val dbManager by lazy { context?.let { DBManager(context = it) } }
    private val newUserModel by lazy { (activity as? MainActivity)?.userModel?.copy() }
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
                it.biometricAvailability = context?.hasBiometrics == true && sharedPreferences?.userModel != null
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPersonalInformationLayoutBinding.inflate(
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
        optionRecyclerView.addItemDecoration(DividerItemRecyclerViewDecorator(
            context = context ?: return,
            margin = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        ))
    }

    suspend fun updateUser(successBlock: (UserModel?) -> Unit) {
        if ((activity as? MainActivity)?.userModel != newUserModel)
            dbManager?.updateUser(
                userModel = newUserModel ?: return,
                userBlock = successBlock
            )
        else {
            context?.toast(
                message = resources.getString(R.string.key_no_changes_message)
            )
            findNavController().popBackStack()
        }
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
                    optionTypes.firstOrNull { it == OptionType.CHANGE_PROFILE_IMAGE }?.userModel?.profileImage = newUserModel?.profileImage
                    optionRecyclerView.scheduleLayoutAnimation()
                    (optionRecyclerView.adapter as? OptionTypesAdapter)?.reloadData()
                }
        }
    }

}
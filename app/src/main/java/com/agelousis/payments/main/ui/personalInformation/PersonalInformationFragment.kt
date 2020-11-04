package com.agelousis.payments.main.ui.personalInformation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.databinding.FragmentPersonalInformationLayoutBinding
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.personalInformation.adapters.OptionTypesAdapter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_personal_information_layout.*

class PersonalInformationFragment: Fragment(), OptionPresenter {

    override fun onChangeProfilePicture() {
        openGallery(
            requestCode = LoginActivity.PROFILE_SELECT_REQUEST_CODE
        )
    }

    override fun onFirstNameChange(newFirstName: String) {
        if (newFirstName == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.firstName = newFirstName
    }

    override fun onLastNameChange(newLastName: String) {
        if (newLastName == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.lastName = newLastName
    }

    override fun onUsernameChange(newUsername: String) {
        if (newUsername == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.username = newUsername
    }

    override fun onPasswordChange(newPassword: String) {
        if (newPassword == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.password = newPassword
    }

    override fun onBiometricsState(state: Boolean) {
        newUserModel?.biometrics = state
    }

    override fun onAddressChange(newAddress: String) {
        if (newAddress == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.address = newAddress
    }

    override fun onIdCardNumberChange(newIdCardNumber: String) {
        if (newIdCardNumber == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.idCardNumber = newIdCardNumber
    }

    override fun onSocialInsuranceNumberChange(newSocialInsuranceNumber: String) {
        if (newSocialInsuranceNumber == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.socialInsuranceNumber = newSocialInsuranceNumber
    }

    override fun onVatChange(newVat: Int) {
        newUserModel?.vat = newVat
    }

    override fun onPaymentAmountChange(newPaymentAmount: Double) {
        newUserModel?.defaultPaymentAmount = newPaymentAmount
    }

    private val dbManager by lazy { context?.let { DBManager(context = it) } }
    private val newUserModel by lazy { (activity as? MainActivity)?.userModel?.copy() }
    private val optionList by lazy {
        arrayListOf(
            OptionType.CHANGE_FIRST_NAME.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_LAST_NAME.also {
                it.userModel = newUserModel
            },
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
                it.biometricAvailability = context?.hasBiometrics == true
            },
            OptionType.CHANGE_ADDRESS.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_ID_CARD_NUMBER.also {
                it.userModel = newUserModel
            },
            OptionType.CHANGE_SOCIAL_INSURANCE_NUMBER.also {
                it.userModel = newUserModel
            },
            HeaderModel(
                dateTime = null,
                header = resources.getString(R.string.key_payment_setting_label)
            ),
            OptionType.VAT.also {
                it.userModel = newUserModel
            },
            OptionType.DEFAULT_PAYMENT_AMOUNT.also {
                it.userModel = newUserModel
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
            list = optionList,
            optionPresenter = this
        )
        optionRecyclerView.addItemDecoration(DividerItemRecyclerViewDecorator(
            context = context ?: return,
            margin = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        ) {
            optionList.getOrNull(index = it) !is HeaderModel
        })
        optionRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            (activity as? MainActivity)?.floatingButtonState = optionRecyclerView.canScrollVertically(1)
        }
    }

    suspend fun updateUser(successBlock: (UserModel?) -> Unit) {
        if ((activity as? MainActivity)?.userModel != newUserModel) {
            dbManager?.updateUser(
                userModel = newUserModel ?: return,
                userBlock = successBlock
            )
        }
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
                    loadImageBitmap(
                        imageUri = imageUri
                    ) { bitmap ->
                        newUserModel?.profileImage?.let {
                            context?.deleteInternalFile(
                                fileName = it
                            )
                        }
                        newUserModel?.profileImage = context?.saveProfileImage(
                            bitmap = bitmap
                        )
                        newUserModel?.profileImageData = bitmap?.byteArray
                        optionList.firstOrNullWithType(
                            typeBlock = {
                                it as OptionType
                            }
                        ) {
                            it is OptionType
                        }?.userModel?.profileImage = newUserModel?.profileImage
                        optionRecyclerView.scheduleLayoutAnimation()
                        (optionRecyclerView.adapter as? OptionTypesAdapter)?.reloadData()
                    }
                }
        }
    }

}
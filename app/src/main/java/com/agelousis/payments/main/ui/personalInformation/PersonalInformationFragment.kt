package com.agelousis.payments.main.ui.personalInformation

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.base.BaseBindingFragment
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.databinding.FragmentPersonalInformationLayoutBinding
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.countrySelector.CountrySelectorDialogFragment
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.currencySelector.CurrencySelectorDialogFragment
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.personalInformation.extensions.configureRecyclerView
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.main.ui.personalInformation.presenter.PersonalInformationPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.CountryHelper
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalInformationFragment: BaseBindingFragment<FragmentPersonalInformationLayoutBinding>(
    inflate = FragmentPersonalInformationLayoutBinding::inflate
), OptionPresenter, Animator.AnimatorListener, PersonalInformationPresenter {

    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
        updateUser()
    }

    override fun onChangeProfilePicture() {
        (activity as? MainActivity)?.activityLauncher?.launch(
            input = galleryIntent
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK)
                return@launch
            result.data?.data?.let { imageUri ->
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
                            it as? OptionType
                        }
                    ) {
                        it is OptionType
                    }?.userModel?.profileImage = newUserModel?.profileImage
                    binding?.optionRecyclerView?.adapter?.notifyItemChanged(
                        optionList.indexOfWithType(
                            typeBlock = {
                                it as? OptionType
                            },
                            predicate = { optionType ->
                                optionType == OptionType.CHANGE_PROFILE_IMAGE
                            }
                        ).takeIf {
                            it > -1
                        } ?: return@loadImageBitmap
                    )
                }
            }
        }
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

    override fun onPasswordPinChange(newPasswordPin: String) {
        if (newPasswordPin == resources.getString(R.string.key_empty_field_label)) return
        newUserModel?.passwordPin = newPasswordPin
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

    override fun onMessageTemplateChange(newMessageTemplate: String) {
        newUserModel?.defaultMessageTemplate = newMessageTemplate
    }

    override fun onDeleteUser() {
        initializeUserDeletion()
    }

    override fun onExportDatabase() {
        (activity as? MainActivity)?.initializeDatabaseExport()
    }

    override fun onChangeCurrency() {
        CurrencySelectorDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            currencySelectorFragmentPresenter = object: CurrencySelectorFragmentPresenter {
                override fun onCurrencySelected(currencyType: CurrencyType) {
                    optionList.firstOrNullWithType(
                        typeBlock = {
                            it as? OptionType
                        },
                        predicate = {
                            it == OptionType.CHANGE_CURRENCY
                        }
                    )?.currencyType = currencyType
                    binding?.optionRecyclerView?.adapter?.notifyItemChanged(
                        optionList.indexOfWithType(
                            typeBlock = {
                                it as? OptionType
                            },
                            predicate = { optionType ->
                                optionType == OptionType.CHANGE_CURRENCY
                            }
                        ).takeIf {
                            it > -1
                        } ?: return
                    )
                }
            }
        )
    }

    override fun onChangeCountry() {
        CountrySelectorDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            countrySelectorFragmentPresenter = object: CountrySelectorFragmentPresenter {
                override fun onCountrySelected(countryDataModel: CountryDataModel) {
                    optionList.firstOrNullWithType(
                        typeBlock = {
                            it as? OptionType
                        },
                        predicate = {
                            it == OptionType.CHANGE_COUNTRY
                        }
                    )?.countryDataModel = countryDataModel
                    binding?.optionRecyclerView?.adapter?.notifyItemChanged(
                        optionList.indexOfWithType(
                            typeBlock = {
                                it as? OptionType
                            },
                            predicate = { optionType ->
                                optionType == OptionType.CHANGE_COUNTRY
                            }
                        ).takeIf {
                            it > -1
                        } ?: return
                    )
                }
            },
            selectedCountryDataModel = optionList.firstOrNullWithType(
                typeBlock = {
                    it as? OptionType
                },
                predicate = {
                    it == OptionType.CHANGE_COUNTRY
                }
            )?.countryDataModel,
            userModel = newUserModel
        )
    }

    override fun onBalanceOverviewStateChange(state: Boolean) {
        sharedPreferences?.balanceOverviewState = state
    }

    override fun onProfilePicturePressed() {
        redirectToProfilePictureFragment()
    }

    override fun onBindData(binding: FragmentPersonalInformationLayoutBinding?) {
        super.onBindData(binding)
        binding?.userModel = (activity as? MainActivity)?.userModel
        binding?.presenter = this
    }

    private val sharedPreferences by lazy {
        context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val dbManager by lazy { context?.let { DBManager(context = it) } }
    private val newUserModel by lazy {
        (activity as? MainActivity)?.userModel?.copy()
    }
    val optionList by lazy {
        arrayListOf(
            HeaderModel(
                dateTime = null,
                header = resources.getString(R.string.key_personal_information_label)
            ),
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
            OptionType.CHANGE_PASSWORD_PIN.also {
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
            OptionType.CHANGE_BALANCE_OVERVIEW_STATE.also {
                it.balanceOverviewAvailability = sharedPreferences?.balanceOverviewState == true
            },
            OptionType.EXPORT_DATABASE,
            OptionType.DELETE_USER,
            HeaderModel(
                dateTime = null,
                header = resources.getString(R.string.key_payment_setting_label)
            ),
            OptionType.CHANGE_CURRENCY.also {
                it.currencyType = CurrencyType.values().firstOrNull { currencyType -> currencyType.symbol == MainApplication.currencySymbol }
            },
            OptionType.CHANGE_COUNTRY.also {
                it.countryDataModel = CountryHelper.getCountryDataModelList(context = context ?: return@also).firstOrNull { countryDataModel -> countryDataModel == MainApplication.countryDataModel }
            },
            OptionType.VAT.also {
                it.userModel = newUserModel
            },
            OptionType.DEFAULT_PAYMENT_AMOUNT.also {
                it.userModel = newUserModel
            },
            OptionType.DEFAULT_MESSAGE_TEMPLATE.also {
                it.userModel = newUserModel
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
    }

    private fun setupUI() {
        binding?.materialTextViewTitle?.isSelected = true
    }

    fun playProfileSuccessAnimation() {
        binding?.profileAnimationView?.isVisible = true
        binding?.profileAnimationView?.playAnimation()
        binding?.profileAnimationView?.addAnimatorListener(this)
    }

    private fun updateUser() {
        if ((activity as? MainActivity)?.userModel != newUserModel)
            uiScope.launch {
                dbManager?.updateUser(
                    userModel = newUserModel ?: return@launch
                ) { newUserModel ->
                    this@PersonalInformationFragment restartMainActivityOrPopBackStack newUserModel
                }
            }
        else
            this restartMainActivityOrPopBackStack null
    }

    private infix fun restartMainActivityOrPopBackStack(userModel: UserModel?) {
        userModel?.let {
            startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                ).also {
                    it.putExtra(MainActivity.USER_MODEL_EXTRA, userModel)
                }
            )
            activity?.finish()
        } ?: findNavController().popBackStack()
    }

    private fun initializeUserDeletion() {
        context?.showTwoButtonsDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_delete_label),
                message = resources.getString(R.string.key_delete_user_message),
                positiveButtonText = resources.getString(R.string.key_delete_label),
                positiveButtonBackgroundColor = ContextCompat.getColor(context ?: return, R.color.red),
                positiveButtonBlock = this::deleteUser
            )
        )
    }

    private fun deleteUser() {
        uiScope.launch {
            dbManager?.deleteUser(
                userId = (activity as? MainActivity)?.userModel?.id ?: return@launch
            ) {
                startActivity(Intent(context, LoginActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun redirectToProfilePictureFragment() {
        findNavController().navigate(
            PersonalInformationFragmentDirections.actionPersonalInformationFragmentToProfilePictureFragment(),
        )
    }

}
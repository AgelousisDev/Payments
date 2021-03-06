package com.agelousis.payments.main.ui.personalInformation

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.payments.custom.itemDecoration.HeaderItemDecoration
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.databinding.FragmentPersonalInformationLayoutBinding
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.countrySelector.CountrySelectorDialogFragment
import com.agelousis.payments.main.ui.countrySelector.enumerations.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.currencySelector.CurrencySelectorDialogFragment
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.personalInformation.adapters.OptionTypesAdapter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.main.ui.personalInformation.presenter.PersonalInformationPresenter
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.CountryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalInformationFragment: Fragment(), OptionPresenter, Animator.AnimatorListener, PersonalInformationPresenter {

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
                    binding.optionRecyclerView.scheduleLayoutAnimation()
                    (binding.optionRecyclerView.adapter as? OptionTypesAdapter)?.reloadData()
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
                    binding.optionRecyclerView.scheduleLayoutAnimation()
                    (binding.optionRecyclerView.adapter as? OptionTypesAdapter)?.reloadData()
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
                    binding.optionRecyclerView.scheduleLayoutAnimation()
                    (binding.optionRecyclerView.adapter as? OptionTypesAdapter)?.reloadData()
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

    override fun onProfilePicturePressed() {
        (activity as? MainActivity)?.showProfilePicture()
    }

    private lateinit var binding: FragmentPersonalInformationLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val dbManager by lazy { context?.let { DBManager(context = it) } }
    private val newUserModel by lazy { (activity as? MainActivity)?.userModel?.copy() }
    private val optionList by lazy {
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
                it.countryDataModel = CountryHelper.countryDataModelList.firstOrNull { countryDataModel -> countryDataModel == MainApplication.countryDataModel }
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPersonalInformationLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
    }

    private fun setupUI() {
        binding.materialTextViewTitle.isSelected = true
    }

    private fun configureRecyclerView() {
        binding.optionRecyclerView.adapter = OptionTypesAdapter(
            list = optionList,
            optionPresenter = this
        )
        binding.optionRecyclerView.addItemDecoration(DividerItemRecyclerViewDecorator(
            context = context ?: return,
            margin = resources.getDimension(R.dimen.activity_general_horizontal_margin).toInt()
        ) {
            optionList.getOrNull(index = it) !is HeaderModel && optionList.getOrNull(index = it) != OptionType.DELETE_USER
        })
        binding.optionRecyclerView.addItemDecoration(
            HeaderItemDecoration(
                parent = binding.optionRecyclerView
            ) {
                optionList.getOrNull(index = it) is HeaderModel
            }
        )
        binding.optionRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            binding.headerConstraintLayout.elevation = if (binding.optionRecyclerView.canScrollVertically(-1)) 8.inPixel else 0.0f
        }
    }

    fun playProfileSuccessAnimation() {
        binding.profileAnimationView.visibility = View.VISIBLE
        binding.profileAnimationView.playAnimation()
        binding.profileAnimationView.addAnimatorListener(this)
    }

    private fun updateUser() {
        if ((activity as? MainActivity)?.userModel != newUserModel) {
            uiScope.launch {
                dbManager?.updateUser(
                    userModel = newUserModel ?: return@launch
                ) {
                    startActivity(Intent(context, LoginActivity::class.java))
                    activity?.finish()
                }
            }
        }
        else {
            context?.toast(
                message = resources.getString(R.string.key_no_changes_message)
            )
            findNavController().popBackStack()
        }
    }

    private fun initializeUserDeletion() {
        context?.showTwoButtonsDialog(
            title = resources.getString(R.string.key_delete_label),
            message = resources.getString(R.string.key_delete_user_message),
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = this::deleteUser
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

}
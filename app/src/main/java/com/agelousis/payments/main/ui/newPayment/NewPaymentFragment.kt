package com.agelousis.payments.main.ui.newPayment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.database.DatabaseTriggeringType
import com.agelousis.payments.databinding.FragmentNewPaymentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.payments.main.ui.newPayment.extensions.*
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.newPayment.viewModels.NewPaymentViewModel
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.enumerations.PaymentType
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.CountryHelper
import com.agelousis.payments.utils.models.ContactDataModel
import com.agelousis.payments.views.detailsSwitch.interfaces.AppSwitchListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewPaymentFragment: Fragment(), NewPaymentPresenter, GroupSelectorFragmentPresenter, CountrySelectorFragmentPresenter {

    override fun onCountrySelected(countryDataModel: CountryDataModel) {
        if (layoutBinding.phoneLayout.value?.contains(" ") == true)
            layoutBinding.phoneLayout.value = String.format(
                "%s %s",
                countryDataModel.countryZipCode ?: "",
                layoutBinding.phoneLayout.value?.split(
                    " "
                )?.second()
            )
        else
            layoutBinding.phoneLayout.value = String.format(
                "%s %s",
                countryDataModel.countryZipCode ?: "",
                layoutBinding.phoneLayout.value ?: ""
            )
        selectedCountryDataModel = countryDataModel
        layoutBinding.selectedCountryDataModel = countryDataModel
    }

    override fun onGroupSelected(groupModel: GroupModel) {
        layoutBinding.groupDetailsLayout.errorState = false
        layoutBinding.groupDetailsLayout.value = groupModel.groupName
    }

    override fun onPaymentAmount(paymentAmountModel: PaymentAmountModel?) {
        fillCurrentPersonModel()
        paymentAmountUpdateIndex = paymentAmountModel?.let { availablePayments.indexOf(it) }
        findNavController().navigate(
            NewPaymentFragmentDirections.actionNewPaymentFragmentToNewPaymentAmountFragment(
                paymentAmountDataModel = paymentAmountModel,
                lastPaymentMonthDate = availablePayments.firstOrNull()?.paymentMonthDate
            )
        )
    }

    override fun onCalendarEvent(paymentAmountModel: PaymentAmountModel?) {
        this createCalendarEventWith paymentAmountModel
    }

    override fun onContactSelection() {
        if (ActivityCompat.checkSelfPermission(
                context ?: return,
                android.Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED)
            (activity as? MainActivity)?.requestPermission(
                permission = android.Manifest.permission.READ_CONTACTS
            ) { isGranted ->
                if (isGranted)
                    onContactSelection()
            }
        else
            (activity as? MainActivity)?.selectContact { contactDataModel ->
                this.contactDataModel = contactDataModel
            }
    }

    override fun onClearPayments() {
        availablePayments.apply {
            (layoutBinding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.notifyItemRangeRemoved(
                0,
                size
            )
            clear()
        }
        layoutBinding.paymentsAreAvailable = false
        configureRecyclerViewMargins()
    }

    lateinit var layoutBinding: FragmentNewPaymentLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<NewPaymentViewModel>()
    val args: NewPaymentFragmentArgs by navArgs()
    private val databaseTriggeringType by lazy {
        args.groupDataModel?.let { DatabaseTriggeringType.INSERT } ?: DatabaseTriggeringType.UPDATE
    }
    val availableGroups by lazy { arrayListOf<GroupModel>() }
    val availablePayments by lazy { ArrayList(args.clientDataModel?.payments ?: listOf()) }
    var currentClientModel: ClientModel? = null
    private var paymentAmountUpdateIndex: Int? = null
    private var addPaymentButtonState = true
        set(value) {
            field = value
            layoutBinding.addPaymentButton.animateAlpha(toAlpha = if (value) 1.0f else 0.2f)
            layoutBinding.addPaymentButton.isEnabled = value
        }
    private var selectedPaymentType = PaymentType.CASH_PAYMENT
    val fieldsHaveChanged
        get() = args.clientDataModel?.let {
            fillCurrentPersonModel()
            currentClientModel != it
        } ?: true
    var selectedCountryDataModel: CountryDataModel? = null
    private var contactDataModel: ContactDataModel? = null
        set(value) {
            field = value
            layoutBinding.firstNameLayout.value = value?.firstName
            layoutBinding.surnameLayout.value = value?.lastName
            layoutBinding.emailLayout.value = value?.email
            layoutBinding.phoneLayout.value = value?.phoneNumber
        }

    override fun onResume() {
        super.onResume()
        currentClientModel?.let {
            layoutBinding.clientModel = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutBinding = FragmentNewPaymentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.groupModel = args.groupDataModel
            it.clientModel = args.clientDataModel
            it.userModel = (activity as? MainActivity)?.userModel
            it.paymentsAreAvailable = availablePayments.isNotEmpty()
            it.presenter = this
        }
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
        configureScrollView()
        initializeGroups()
        initializeNewPayments()
        configureObservers()
        initializeCountryDataModel()
        setupContactUI()
    }

    private fun setupUI() {
        layoutBinding.paymentTypeLayout.setOnDetailsPressed {
            context?.showListDialog(
                title = resources.getString(R.string.key_payment_type_label),
                items = resources.getStringArray(R.array.key_payment_type_array).toList()
            ) {
                val paymentType = PaymentType.values().getOrNull(index = it) ?: return@showListDialog
                selectedPaymentType = paymentType
                layoutBinding.paymentTypeLayout.value = paymentType.getLocalizedTitle(
                    resources = resources
                )
            }
        }
        layoutBinding.countryCodeLayout.setOnDetailsPressed {
            showCountryCodesSelector()
        }
        layoutBinding.groupDetailsLayout.setOnDetailsPressed {
            showGroupsSelectionFragment()
        }
        layoutBinding.activeAppSwitchLayout.setOnClickListener {
            layoutBinding.activeAppSwitchLayout.isChecked = layoutBinding.activeAppSwitchLayout.isChecked == false
        }
        layoutBinding.freeAppSwitchLayout.setOnClickListener {
            layoutBinding.freeAppSwitchLayout.isChecked = layoutBinding.freeAppSwitchLayout.isChecked == false
        }
        layoutBinding.freeAppSwitchLayout.appSwitchListener = object: AppSwitchListener {
            override fun onAppSwitchValueChanged(isChecked: Boolean) {
                addPaymentButtonState = !isChecked
            }
        }
    }

    private fun configureObservers() {
        viewModel.groupsLiveData.observe(viewLifecycleOwner) {
            availableGroups.clear()
            availableGroups.addAll(it)
        }
        viewModel.clientInsertionStateLiveData.observe(viewLifecycleOwner) { paymentInsertionState ->
            if (paymentInsertionState) {
                (activity as? MainActivity)?.floatingButtonState = false
                scheduleNotification()
                currentClientModel = null
                findNavController().popBackStack()
            }
        }
    }

    private fun initializeCountryDataModel() {
        selectedCountryDataModel = args.clientDataModel?.phone?.let { currentPhoneValue ->
            CountryHelper.getCountryDataFromZipCode(
                context = context ?: return,
                zipCode = currentPhoneValue.split(" ").firstOrNull()
            )
        } ?: MainApplication.countryDataModel
        layoutBinding.selectedCountryDataModel = selectedCountryDataModel
    }

    private fun initializeNewPayments() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<PaymentAmountModel>(
            NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA
        )?.observe(viewLifecycleOwner) { paymentAmountModel ->
            paymentAmountUpdateIndex?.let { index ->
                availablePayments.set(
                    index = index,
                    paymentAmountModel
                )
            } ?: run {
                availablePayments.add(paymentAmountModel)
                redirectToSMSAppIf(
                    payment = paymentAmountModel
                ) {
                    !currentClientModel?.phone.isNullOrEmpty()
                }
            }
            configureRecyclerViewMargins()
            (layoutBinding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
            layoutBinding.paymentsAreAvailable = availablePayments.isNotEmpty()
        }
    }

    private fun initializeGroups() {
        uiScope.launch {
            viewModel.initializeGroups(
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

    fun checkInputFields() {
        fillCurrentPersonModel()
        ifLet(
            currentClientModel?.groupName,
            currentClientModel?.firstName,
            currentClientModel?.surname,
        ) {
            checkDatabasePaymentAction()
        } ?: run {
            layoutBinding.nestedScrollView.post {
                layoutBinding.nestedScrollView.smoothScrollTo(0, 0)
            }
            layoutBinding.groupDetailsLayout.errorState = layoutBinding.groupDetailsLayout.value == null
            layoutBinding.firstNameLayout.errorState = layoutBinding.firstNameLayout.value == null
            layoutBinding.surnameLayout.errorState = layoutBinding.surnameLayout.value == null
        }
    }

    private fun checkDatabasePaymentAction() {
        uiScope.launch {
            when(databaseTriggeringType) {
                DatabaseTriggeringType.INSERT ->
                    viewModel.addClient(
                        userId = (activity as? MainActivity)?.userModel?.id,
                        clientModel = currentClientModel ?: return@launch
                    )
                DatabaseTriggeringType.UPDATE ->
                    viewModel.updateClient(
                        userId = (activity as? MainActivity)?.userModel?.id,
                        clientModel = currentClientModel ?: return@launch
                    )
            }
        }
    }

    private fun fillCurrentPersonModel() {
        var phone = layoutBinding.phoneLayout.value
        selectedCountryDataModel?.countryZipCode?.let { zipCode ->
            if (layoutBinding.phoneLayout.value?.startsWith(zipCode) == false)
                phone = String.format(
                    "%s%s",
                    "$zipCode ",
                    layoutBinding.phoneLayout.value
                )
        }
        currentClientModel = ClientModel(
            personId = args.clientDataModel?.personId,
            groupId = availableGroups.firstOrNull { it.groupName.equals(layoutBinding.groupDetailsLayout.value, ignoreCase = true) }?.groupId,
            groupName = layoutBinding.groupDetailsLayout.value,
            firstName = layoutBinding.firstNameLayout.value,
            surname = layoutBinding.surnameLayout.value,
            phone = phone,
            parentName = layoutBinding.parentNameLayout.value,
            parentPhone = layoutBinding.parentPhoneLayout.value,
            email = layoutBinding.emailLayout.value,
            active = layoutBinding.activeAppSwitchLayout.isChecked,
            free = layoutBinding.freeAppSwitchLayout.isChecked,
            messageTemplate = layoutBinding.messageTemplateField.text?.toString(),
            payments = availablePayments,
            paymentType = selectedPaymentType,
            groupColor = args.clientDataModel?.groupColor,
            groupImage = args.clientDataModel?.groupImage
        )
    }

}
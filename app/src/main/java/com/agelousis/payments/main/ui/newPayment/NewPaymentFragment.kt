package com.agelousis.payments.main.ui.newPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.database.DatabaseTriggeringType
import com.agelousis.payments.databinding.FragmentNewPaymentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.ui.countrySelector.CountrySelectorDialogFragment
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.groupSelector.GroupSelectorDialogFragment
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.payments.main.ui.newPayment.enumerations.PaymentAmountRowState
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.newPayment.viewModels.NewPaymentViewModel
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.enumerations.PaymentType
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.CountryHelper
import com.agelousis.payments.utils.models.CalendarDataModel
import com.agelousis.payments.utils.models.NotificationDataModel
import com.agelousis.payments.views.detailsSwitch.interfaces.AppSwitchListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewPaymentFragment: Fragment(), NewPaymentPresenter, GroupSelectorFragmentPresenter, CountrySelectorFragmentPresenter {

    override fun onCountrySelected(countryDataModel: CountryDataModel) {
        if (binding.phoneLayout.value?.contains(" ") == true)
            binding.phoneLayout.value = String.format(
                "%s %s",
                countryDataModel.countryZipCode ?: "",
                binding.phoneLayout.value?.split(
                    " "
                )?.second()
            )
        else
            binding.phoneLayout.value = String.format(
                "%s %s",
                countryDataModel.countryZipCode ?: "",
                binding.phoneLayout.value ?: ""
            )
        selectedCountryDataModel = countryDataModel
        binding.selectedCountryDataModel = countryDataModel
    }

    override fun onGroupSelected(groupModel: GroupModel) {
        binding.groupDetailsLayout.errorState = false
        binding.groupDetailsLayout.value = groupModel.groupName
    }

    override fun onPaymentAmount(paymentAmountModel: PaymentAmountModel?) {
        restorePaymentsToNormalState()
        fillCurrentPersonModel()
        paymentAmountUpdateIndex = paymentAmountModel?.let { availablePayments.indexOf(it) }
        findNavController().navigate(
            NewPaymentFragmentDirections.actionNewPaymentFragmentToNewPaymentAmountFragment(
                paymentAmountDataModel = paymentAmountModel,
                lastPaymentMonthDate = availablePayments.firstOrNull()?.paymentMonthDate
            )
        )
    }

    override fun onPaymentAmountLongPressed(adapterPosition: Int) {
        availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState = availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState?.otherState ?: PaymentAmountRowState.NORMAL
        (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
        when((activity as? MainActivity)?.floatingButtonType) {
            FloatingButtonType.NORMAL ->
                if (availablePayments.any { it.paymentAmountRowState == PaymentAmountRowState.CAN_BE_DISMISSED })
                    (activity as? MainActivity)?.setFloatingButtonAsPaymentRemovalButton()
            FloatingButtonType.NEGATIVE ->
                if (availablePayments.all { it.paymentAmountRowState == PaymentAmountRowState.NORMAL })
                    (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
        }
        if (availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState == PaymentAmountRowState.CAN_BE_DISMISSED)
            paymentReadyForDeletionIndexArray.add(adapterPosition)
        else
            paymentReadyForDeletionIndexArray.remove(adapterPosition)
    }

    override fun onCalendarEvent(paymentAmountModel: PaymentAmountModel?) {
        (context ?: return) createCalendarEventWith CalendarDataModel(
            calendar = paymentAmountModel?.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.calendar ?: return,
            title = String.format(
                "%s %s",
                binding.firstNameLayout.value ?: return,
                binding.surnameLayout.value ?: return
            ),
            description = String.format(
                resources.getString(R.string.key_calendar_event_amount_value),
                paymentAmountModel.getAmountWithoutVat(
                    context = context ?: return,
                    vat = (activity as? MainActivity)?.userModel?.vat ?: return
                )
            ),
            email = binding.emailLayout.value ?: return
        )
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(NewPaymentViewModel::class.java) }
    private val args: NewPaymentFragmentArgs by navArgs()
    private val databaseTriggeringType by lazy {
        args.groupDataModel?.let { DatabaseTriggeringType.INSERT } ?: DatabaseTriggeringType.UPDATE
    }
    private val availableGroups by lazy { arrayListOf<GroupModel>() }
    private val availablePayments by lazy { ArrayList(args.clientDataModel?.payments ?: listOf()) }
    private lateinit var binding: FragmentNewPaymentLayoutBinding
    private var currentClientModel: ClientModel? = null
    private var paymentReadyForDeletionIndexArray = arrayListOf<Int>()
    private var paymentAmountUpdateIndex: Int? = null
    private var addPaymentButtonState = true
        set(value) {
            field = value
            binding.addPaymentButton.animateAlpha(toAlpha = if (value) 1.0f else 0.2f)
            binding.addPaymentButton.isEnabled = value
        }
    private var selectedPaymentType = PaymentType.CASH_PAYMENT
    val fieldsHaveChanged
        get() = args.clientDataModel?.let {
            fillCurrentPersonModel()
            currentClientModel != it
        } ?: true
    private var selectedCountryDataModel: CountryDataModel? = null

    override fun onResume() {
        super.onResume()
        currentClientModel?.let {
            binding.personModel = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewPaymentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.groupModel = args.groupDataModel
            it.personModel = args.clientDataModel
            it.userModel = (activity as? MainActivity)?.userModel
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
        initializeGroups()
        initializeNewPayments()
        configureObservers()
        initializeCountryDataModel()
    }

    private fun setupUI() {
        binding.paymentTypeLayout.setOnDetailsPressed {
            context?.showListDialog(
                title = resources.getString(R.string.key_payment_type_label),
                items = resources.getStringArray(R.array.key_payment_type_array).toList()
            ) {
                val paymentType = PaymentType.values().getOrNull(index = it) ?: return@showListDialog
                selectedPaymentType = paymentType
                binding.paymentTypeLayout.value = paymentType.getLocalizedTitle(
                    resources = resources
                )
            }
        }
        binding.countryCodeLayout.setOnDetailsPressed {
            showCountryCodesSelector()
        }
        binding.groupDetailsLayout.setOnDetailsPressed {
            showGroupsSelectionFragment()
        }
        binding.activeAppSwitchLayout.setOnClickListener {
            binding.activeAppSwitchLayout.isChecked = binding.activeAppSwitchLayout.isChecked == false
        }
        binding.freeAppSwitchLayout.setOnClickListener {
            binding.freeAppSwitchLayout.isChecked = binding.freeAppSwitchLayout.isChecked == false
        }
        binding.freeAppSwitchLayout.appSwitchListener = object: AppSwitchListener {
            override fun onAppSwitchValueChanged(isChecked: Boolean) {
                addPaymentButtonState = !isChecked
            }
        }
    }

    private fun showGroupsSelectionFragment() {
        availableGroups.forEach { groupModel ->
            groupModel.isSelected = groupModel.groupName?.lowercase() == binding.groupDetailsLayout.value?.lowercase()
        }
        GroupSelectorDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            groupModelList = availableGroups,
            groupSelectorFragmentPresenter = this
        )
    }

    private fun showCountryCodesSelector() {
        CountrySelectorDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            countrySelectorFragmentPresenter = this,
            selectedCountryDataModel = selectedCountryDataModel,
            userModel = (activity as? MainActivity)?.userModel,
            updateGlobalCountryState = false
        )
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
        binding.selectedCountryDataModel = selectedCountryDataModel
    }

    private fun redirectToSMSAppIf(payment: PaymentAmountModel, predicate: () -> Boolean) {
        if (predicate())
            context?.showTwoButtonsDialog(
                title = resources.getString(R.string.key_sms_label),
                message = resources.getString(R.string.key_send_sms_message),
                icon = R.drawable.ic_sms,
                positiveButtonText = resources.getString(R.string.key_send_label),
                positiveButtonBlock = {
                    context?.sendSMSMessage(
                        mobileNumbers = listOf(
                            binding.phoneLayout.value ?: ""
                        ),
                        message = String.format(
                            "%s\n%s",
                            binding.messageTemplateField.text?.toString() ?: "",
                            payment.paymentMonth ?: payment.paymentDate ?: ""
                        )
                    )
                },
                isCancellable = false
            )
    }

    private fun scheduleNotification() {
        availablePayments.filter { it.paymentDateNotification == true }.forEachIndexed { index, paymentAmountModel ->
            (context ?: return@forEachIndexed) scheduleNotification NotificationDataModel(
                calendar = paymentAmountModel.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.defaultTimeCalendar ?: return@forEachIndexed,
                notificationId = index,
                title = currentClientModel?.fullName,
                body = String.format(
                    resources.getString(R.string.key_notification_amount_value),
                    paymentAmountModel.paymentAmount?.euroFormattedString ?: ""
                ),
                date = paymentAmountModel.paymentDate.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.formattedDateWith(pattern = Constants.VIEWING_DATE_FORMAT),
                groupName = currentClientModel?.groupName,
                groupImage = /*currentPersonModel?.personImage ?: */currentClientModel?.groupImage ?: args.groupDataModel?.groupImage,
                groupTint = currentClientModel?.groupColor
            )
        }
    }

    private fun initializeNewPayments() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<PaymentAmountModel>(NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA)
            ?.observe(viewLifecycleOwner) { paymentAmountModel ->
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
                (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
            }
    }

    private fun configureRecyclerView() {
        binding.paymentAmountRecyclerView.adapter = PaymentAmountAdapter(
            paymentModelList = availablePayments,
            vat = (activity as? MainActivity)?.userModel?.vat,
            presenter = this
        )
        binding.paymentAmountRecyclerView.scheduleLayoutAnimation()
        (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
    }

    private fun initializeGroups() {
        uiScope.launch {
            viewModel.initializeGroups(
                context = context ?: return@launch,
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

    fun checkInputFields() {
        fillCurrentPersonModel(
            saveState = true
        )
        ifLet(
            currentClientModel?.groupName,
            currentClientModel?.firstName,
            currentClientModel?.surname,
            currentClientModel?.phone,
        ) {
            checkDatabasePaymentAction()
        } ?: run {
            binding.nestedScrollView.post {
                binding.nestedScrollView.smoothScrollTo(0, 0)
            }
            binding.groupDetailsLayout.errorState = binding.groupDetailsLayout.value == null
            binding.firstNameLayout.errorState = binding.firstNameLayout.value == null
            binding.surnameLayout.errorState = binding.surnameLayout.value == null
            binding.phoneLayout.errorState = binding.phoneLayout.value == null
        }
    }

    private fun checkDatabasePaymentAction() {
        uiScope.launch {
            when(databaseTriggeringType) {
                DatabaseTriggeringType.INSERT ->
                    viewModel.addClient(
                        context = this@NewPaymentFragment.context ?: return@launch,
                        userId = (activity as? MainActivity)?.userModel?.id,
                        clientModel = currentClientModel ?: return@launch
                    )
                DatabaseTriggeringType.UPDATE ->
                    viewModel.updateClient(
                        context = this@NewPaymentFragment.context ?: return@launch,
                        userId = (activity as? MainActivity)?.userModel?.id,
                        clientModel = currentClientModel ?: return@launch
                    )
            }
        }
    }

    private fun restorePaymentsToNormalState() {
        if (availablePayments.any { it.paymentAmountRowState == PaymentAmountRowState.CAN_BE_DISMISSED }) {
            availablePayments.forEach {
                it.paymentAmountRowState = PaymentAmountRowState.NORMAL
            }
            (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
            paymentReadyForDeletionIndexArray.clear()
            (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
        }
    }

    private fun fillCurrentPersonModel(saveState: Boolean = false) {
        var phone = binding.phoneLayout.value
        selectedCountryDataModel?.countryZipCode?.let { zipCode ->
            if (binding.phoneLayout.value?.startsWith(zipCode) == false)
                phone = String.format(
                    "%s%s",
                    "$zipCode ",
                    binding.phoneLayout.value
                )
        }
        currentClientModel = ClientModel(
            personId = args.clientDataModel?.personId,
            groupId = availableGroups.firstOrNull { it.groupName.equals(binding.groupDetailsLayout.value, ignoreCase = true) }?.groupId,
            groupName = binding.groupDetailsLayout.value,
            firstName = binding.firstNameLayout.value,
            surname = binding.surnameLayout.value,
            phone = phone,
            parentName = binding.parentNameLayout.value,
            parentPhone = binding.parentPhoneLayout.value,
            email = binding.emailLayout.value,
            active = binding.activeAppSwitchLayout.isChecked,
            free = binding.freeAppSwitchLayout.isChecked,
            messageTemplate = binding.messageTemplateField.text?.toString(),
            payments = if (saveState && !binding.activeAppSwitchLayout.isChecked) listOf() else availablePayments,
            paymentType = selectedPaymentType,
            groupColor = args.clientDataModel?.groupColor,
            groupImage = args.clientDataModel?.groupImage
        )
    }

    fun dismissPayment() {
        paymentReadyForDeletionIndexArray.sortDescending()
        paymentReadyForDeletionIndexArray.forEach { paymentReadyForDeletionIndex ->
            (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.removeItem(
                position = paymentReadyForDeletionIndex
            )
            (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
        }
        paymentReadyForDeletionIndexArray.clear()
    }

}
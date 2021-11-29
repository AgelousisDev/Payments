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
import com.agelousis.payments.main.ui.shareMessageFragment.ShareMessageBottomSheetFragment
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.CountryHelper
import com.agelousis.payments.utils.models.CalendarDataModel
import com.agelousis.payments.utils.models.ContactDataModel
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
            (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.notifyItemRangeRemoved(
                0,
                size
            )
            clear()
        }
        binding.paymentsAreAvailable = false
        configureRecyclerViewMargins()
    }

    override fun onClientShareMessage() {
        ShareMessageBottomSheetFragment.show(
            supportFragmentManager = activity?.supportFragmentManager ?: return,
            clientModel = args.clientDataModel ?: return
        )
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<NewPaymentViewModel>()
    val args: NewPaymentFragmentArgs by navArgs()
    private val databaseTriggeringType by lazy {
        args.groupDataModel?.let { DatabaseTriggeringType.INSERT } ?: DatabaseTriggeringType.UPDATE
    }
    val availableGroups by lazy { arrayListOf<GroupModel>() }
    val availablePayments by lazy { ArrayList(args.clientDataModel?.payments ?: listOf()) }
    lateinit var binding: FragmentNewPaymentLayoutBinding
    var currentClientModel: ClientModel? = null
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
    var selectedCountryDataModel: CountryDataModel? = null
    private var contactDataModel: ContactDataModel? = null
        set(value) {
            field = value
            binding.firstNameLayout.value = value?.firstName
            binding.surnameLayout.value = value?.lastName
            binding.emailLayout.value = value?.email
            binding.phoneLayout.value = value?.phoneNumber
        }

    override fun onResume() {
        super.onResume()
        currentClientModel?.let {
            binding.clientModel = it
        }
        (activity as? MainActivity)?.shareMessageMenuItemIsVisible = !args.clientDataModel?.email.isNullOrEmpty()
                || !args.clientDataModel?.phone.isNullOrEmpty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewPaymentLayoutBinding.inflate(
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
        return binding.root
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
            (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
            binding.paymentsAreAvailable = availablePayments.isNotEmpty()
        }
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
        fillCurrentPersonModel()
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

    private fun fillCurrentPersonModel() {
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
            payments = availablePayments,
            paymentType = selectedPaymentType,
            groupColor = args.clientDataModel?.groupColor,
            groupImage = args.clientDataModel?.groupImage
        )
    }

}
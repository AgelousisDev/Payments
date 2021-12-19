package com.agelousis.payments.main.ui.payments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.FragmentPaymentsLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.menuOptions.PaymentsMenuOptionsBottomSheetFragment
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.payments.main.ui.payments.extensions.*
import com.agelousis.payments.main.ui.payments.models.*
import com.agelousis.payments.main.ui.payments.presenters.*
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.paymentsFiltering.FilterPaymentsFragment
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.periodFilter.models.PeriodFilterDataModel
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.main.ui.totalPaymentsAmount.TotalPaymentsAmountBottomSheetDialogFragment
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PaymentsFragment: Fragment(), GroupPresenter, ClientPresenter, PaymentPresenter, PaymentAmountSumPresenter,
    PaymentsFragmentPresenter, BalanceOverviewPresenter {

    override fun onDeletePayments() {
        this configureMultipleDeleteActionWith filteredList.filterIsInstance<ClientModel>().filter { it.isSelected }.mapNotNull { it.personId }
    }

    override fun onExportInvoice() {
        initializePDFCreation(
            clients = filteredList.filterIsInstance<ClientModel>().filter { it.isSelected },
            fromMultipleSelection = true
        )
    }

    override fun onPaymentsSendSms() {
        context?.sendSMSMessage(
            mobileNumbers = itemsList.filterIsInstance<ClientModel>().filter { it.isSelected }.mapNotNull { it.phone },
            message = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
        )
    }

    override fun onPaymentsSendEmail() {
        context?.textEmail(
            *itemsList.filterIsInstance<ClientModel>().filter { it.isSelected }.mapNotNull { it.email }.toTypedArray(),
            content = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
        )
    }

    override fun onGroupSelected(groupModel: GroupModel) {
        (activity as? MainActivity)?.startGroupActivity(
            groupModel = groupModel
        )
    }

    override fun onClientSelected(clientModel: ClientModel, adapterPosition: Int) {
        when {
            filteredList.filterIsInstance<ClientModel>().any { it.isSelected } ->
                onClientLongPressed(
                    paymentIndex = adapterPosition,
                    isSelected = !clientModel.isSelected
                )
            else -> {
                if (findNavController().currentDestination?.id != R.id.paymentsFragment)
                    return
                findNavController().navigate(
                    PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                        clientDataModel = clientModel
                    )
                )
            }
        }
    }

    override fun onClientLongPressed(paymentIndex: Int, isSelected: Boolean) {
        (filteredList.getOrNull(
            index = paymentIndex
        ) as? ClientModel)?.isSelected = isSelected
        appBarIsVisible = filteredList.filterIsInstance<ClientModel>().any {
            it.isSelected
        }
        selectedPayments = filteredList.filterIsInstance<ClientModel>().count {
            it.isSelected
        }
        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.updateIn(
            position = paymentIndex
        )
        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.reloadData()
    }

    override fun onPaymentSelected(paymentAmountModel: PaymentAmountModel, adapterPosition: Int) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToNewPaymentAmountFragment(
                paymentAmountDataModel =  paymentAmountModel
            )
        )
    }

    override fun onPaymentLongPressed(paymentIndex: Int, isSelected: Boolean) {

    }

    override fun onPersonAdd(groupModel: GroupModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                groupDataModel = groupModel
            )
        )
    }

    override fun onGroupSms(groupModel: GroupModel) {
        this sendGroupSms groupModel
    }

    override fun onGroupEmail(groupModel: GroupModel) {
        this sendGroupEmail groupModel
    }

    override fun onPaymentAdd(groupModel: GroupModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToNewPaymentAmountFragment(
                groupModelData = groupModel
            )
        )
    }

    override fun onPaymentAmountSumSelected(paymentAmountSumModel: PaymentAmountSumModel) {
        TotalPaymentsAmountBottomSheetDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            paymentAmountSumModel = paymentAmountSumModel
        )
    }

    override fun onBalanceOverviewState() {
        filteredList.firstNotNullOfOrNull {
            it as? BalanceOverviewDataModel
        }?.currentBalanceOverviewState = filteredList.firstNotNullOfOrNull {
            it as? BalanceOverviewDataModel
        }?.currentBalanceOverviewState?.other
        binding.paymentListRecyclerView.adapter?.notifyItemChanged(
            filteredList.indexOfFirst {
                it is BalanceOverviewDataModel
            }
        )
    }

    override fun onSaveBalance(balance: Double) {
        (activity as? MainActivity)?.userModel?.balance = balance
        filteredList.firstNotNullOfOrNull {
            it as? BalanceOverviewDataModel
        }?.currentBalance = balance
        uiScope.launch {
            viewModel.updateUserBalance(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel,
                balance = balance
            )
        }
    }

    lateinit var binding: FragmentPaymentsLayoutBinding
    val uiScope = CoroutineScope(Dispatchers.Main)
    val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    val viewModel: PaymentsViewModel by viewModels()
    val itemsList by lazy { arrayListOf<Any>() }
    val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            binding.searchLayout.isVisible = value
        }
    private var appBarIsVisible: Boolean = false
        set(value) {
            field = value
            binding.paymentsAppBarLayout.visibility = if (value) View.VISIBLE else View.GONE
        }
    private var selectedPayments: Int = 0
        set(value) {
            field = value
            binding.selectedPaymentsView.text = String.format(
                resources.getString(R.string.key_payments_selected_value_label),
                value
            )
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPaymentsLayoutBinding.inflate(
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
        initializeNewFilters()
        configureToolbar()
        configureSearchView()
        configureRecyclerView()
        initializePayments()
        configureObservers()
    }

    private fun configureToolbar() {
        binding.paymentsToolbar.setNavigationOnClickListener {
            clearSelectedPayments()
        }
    }

    private fun configureSearchView() {
        binding.searchLayout.onProfileImageClicked {
            redirectToPersonalInformationFragment()
        }
        binding.searchLayout.onSecondaryIconClicked {
            showPaymentsMenuOptionsFragment()
        }
        binding.searchLayout.onQueryListener {
            configurePayments(
                list = itemsList,
                query = it
            )
        }
    }

    private fun showPaymentsMenuOptionsFragment() {
        PaymentsMenuOptionsBottomSheetFragment.show(
            supportFragmentManager = childFragmentManager
        )
    }

    fun redirectToFilterPaymentsFragment() {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToFilterPaymentsFragment()
        )
    }

    fun redirectToQrCodeFragment(
        qrCodeSelectionType: QRCodeSelectionType,
        selectedClients: List<ClientModel>? = null
    ) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToQRCodeFragment(
                qrCodeSelectionType = qrCodeSelectionType,
                selectedClients = selectedClients?.toTypedArray()
            )
        )
    }
    
    private fun configureRecyclerView() {
        configureRecyclerViewAdapterAndLayoutManager()
        addRecyclerViewItemDecoration()
        configureSwipeEvents()
    }

    fun redirectToPdfViewer(pdfFile: File, description: String) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToPdfViewerFragment(
                fileDataModel = FileDataModel(
                    description = description,
                    fileName = pdfFile.name,
                    dateTime = Date().pdfFormattedCurrentDate
                ).also {
                    it.fileData = pdfFile.readBytes()
                }
            )
        )
    }

    private fun configureObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            after(
                millis = 1000
            ) {
                list.filterIsInstance<ClientModel>().apply {
                    searchViewState = isNotEmpty()
                    (activity as? MainActivity)?.paymentsSize = size.takeIf { it > 0 }
                }
            }
            itemsList.clear()
            itemsList.addAll(list)
            configurePayments(
                list = list
            )
        }
        viewModel.deletionLiveData.observe(viewLifecycleOwner) {
            this setSelectedPaymentsAppBarState false
            if (it)
                initializePayments()
        }
        initializeNewPayments()
    }

    fun clearSelectedPayments() {
        this setSelectedPaymentsAppBarState false
        filteredList.forEachIfEach(
            predicate = {
                it is ClientModel
            },
            action = {
                (it as? ClientModel)?.isSelected = false
            }
        )
        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.reloadData()
    }

    private infix fun setSelectedPaymentsAppBarState(state: Boolean) {
        appBarIsVisible = state
        if (!state)
            selectedPayments = 0
    }

    fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
        }
    }

    private fun initializeNewFilters() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<PaymentsFilteringOptionType>>(
            FilterPaymentsFragment.PAYMENTS_FILTERING_OPTION_DATA_EXTRA
        )?.observe(
            viewLifecycleOwner
        ) { paymentsFilteringOptionTypes ->
            paymentsFilteringOptionTypes?.forEachIndexed { index, paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = index
            }
            sharedPreferences?.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
            MainApplication.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
        }
    }

    private fun redirectToPersonalInformationFragment() {
        findNavController().navigate(
            PaymentsFragmentDirections.actionGlobalPersonalInformation()
        )
    }

    fun navigateToPeriodFilterFragment() {
        val payments = filteredList.filterIsInstance<ClientModel>().mapNotNull { it.payments }.flatten()
        findNavController().popBackStack(R.id.periodFilterFragment, true)
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToPeriodFilterFragment(
                periodFilterData = PeriodFilterDataModel(
                    minimumMonthDate = payments.mapNotNull { it.paymentMonthDate }.minOrNull(),
                    maximumMonthDate = payments.mapNotNull { it.paymentMonthDate }.maxOrNull()
                ),
                paymentListData = payments.toTypedArray()
            )
        )
    }

}
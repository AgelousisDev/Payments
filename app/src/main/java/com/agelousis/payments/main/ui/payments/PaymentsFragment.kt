package com.agelousis.payments.main.ui.payments

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.payments.databinding.FragmentPaymentsLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.menuOptions.PaymentsMenuOptionsBottomSheetFragment
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.presenters.GroupPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentAmountSumPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentsFragmentPresenter
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentViewHolder
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.paymentsFiltering.FilterPaymentsFragment
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.periodFilter.models.PeriodFilterDataModel
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.main.ui.shareMessageFragment.ShareMessageBottomSheetFragment
import com.agelousis.payments.main.ui.totalPaymentsAmount.TotalPaymentsAmountDialogFragment
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import com.agelousis.payments.widgets.extensions.clientModelList
import com.agelousis.payments.widgets.extensions.updatePaymentsAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PaymentsFragment : Fragment(), GroupPresenter, PaymentPresenter, PaymentAmountSumPresenter, PaymentsFragmentPresenter {

    override fun onDeletePayments() {
        configureMultipleDeleteAction(
            positions = filteredList.filterIsInstance<ClientModel>().filter { it.isSelected }.mapNotNull { it.personId }
        )
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

    override fun onHistoryGraph() {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToHistoryFragment(
                fromPaymentsRedirection = true
            )
        )
    }

    override fun onGroupSelected(groupModel: GroupModel) {
        (activity as? MainActivity)?.startGroupActivity(
            groupModel = groupModel
        )
    }

    override fun onPaymentSelected(clientModel: ClientModel, adapterPosition: Int) {
        when {
            filteredList.filterIsInstance<ClientModel>().any { it.isSelected } ->
                onPaymentLongPressed(
                    paymentIndex = adapterPosition,
                    isSelected = !clientModel.isSelected
                )
            else ->
                findNavController().navigate(
                    PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                        clientDataModel = clientModel
                    )
                )
        }
    }

    override fun onPaymentShareMessage(clientModel: ClientModel) {
        ShareMessageBottomSheetFragment.show(
            supportFragmentManager = activity?.supportFragmentManager ?: return,
            clientModel = clientModel
        )
    }

    override fun onPaymentLongPressed(paymentIndex: Int, isSelected: Boolean) {
        (filteredList.getOrNull(
            index = paymentIndex
        ) as? ClientModel)?.isSelected = isSelected
        appBarIsVisible = filteredList.filterIsInstance<ClientModel>().any { it.isSelected }
        selectedPayments = filteredList.filterIsInstance<ClientModel>().count { it.isSelected }
        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.restoreItem(
            position = paymentIndex
        )
    }

    override fun onPersonAdd(groupModel: GroupModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                groupDataModel = groupModel
            )
        )
    }

    override fun onGroupLongPressed(groupModel: GroupModel) {
        context?.showSimpleDialog(
            title = resources.getString(R.string.key_sms_label),
            message = String.format(
                resources.getString(R.string.key_send_sms_to_group_value_message),
                groupModel.groupName ?: ""
            ),
            positiveButtonText = resources.getString(R.string.key_send_label)
        ) {
            context?.sendSMSMessage(
                mobileNumbers = itemsList.filterIsInstance<ClientModel>().filter { it.groupId == groupModel.groupId }.mapNotNull { it.phone },
                message = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
            )
        }
    }

    override fun onPaymentAmountSumSelected(paymentAmountSumModel: PaymentAmountSumModel) {
        TotalPaymentsAmountDialogFragment.show(
            supportFragmentManager = childFragmentManager,
            paymentAmountSumModel = paymentAmountSumModel
        )
    }

    private lateinit var binding: FragmentPaymentsLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private val itemsList by lazy { arrayListOf<Any>() }
    private val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            binding.searchLayout.visibility = if (value) View.VISIBLE else View.GONE
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
            it.paymentsAreAvailable = false
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
            (activity as? MainActivity)?.onMaterialMenuOptionSelected(
                materialMenuOption = MaterialMenuOption.PROFILE
            )
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

    fun redirectToQrCodeFragment(qrCodeSelectionType: QRCodeSelectionType) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentsFragmentToQRCodeFragment(
                qrCodeSelectionType = qrCodeSelectionType
            )
        )
    }
    
    private fun configureRecyclerView() {
        binding.paymentListRecyclerView.adapter = PaymentsAdapter(
            list = filteredList,
            groupPresenter = this,
            paymentPresenter = this,
            paymentAmountSumPresenter = this
        )
        binding.paymentListRecyclerView.addItemDecoration(
            object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val adapterPosition = parent.getChildAdapterPosition(view)
                    when(filteredList.getOrNull(adapterPosition)) {
                        is GroupModel -> {
                            if (adapterPosition > 0)
                                outRect.top = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                            outRect.bottom = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        }
                        is ClientModel -> {
                            outRect.left = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                            outRect.right = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                            if (filteredList isLastAt adapterPosition)
                                outRect.bottom = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        }
                        is PaymentAmountSumModel -> {
                            outRect.top = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                            if (filteredList isLastAt adapterPosition)
                                outRect.bottom = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        }
                    }
                }
            }
        )
        binding.paymentListRecyclerView.addOnScrollListener(
            object: RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val isInFirstPosition = (binding.paymentListRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() == 0 && filteredList.filterIsInstance<ClientModel>().mapNotNull { it.payments }.flatten().isNotEmpty()
                    if (!isInFirstPosition && binding.paymentsAreAvailable == true)
                        binding.paymentsAreAvailable = false
                    if (isInFirstPosition && binding.paymentsAreAvailable == false)
                        binding.paymentsAreAvailable = true
                }

            }
        )
        configureSwipeEvents()
    }

    private fun configureSwipeEvents() {
        val swipeItemTouchHelper = ItemTouchHelper(
            SwipeItemTouchHelper(
                context = context ?: return,
                swipeItemType = SwipeItemType.PERSON_ITEM,
                marginStart = resources.getDimension(R.dimen.activity_general_horizontal_margin),
                swipePredicateBlock = {
                    it is GroupViewHolder || it is PaymentViewHolder
                }
            ) innerBlock@ { swipeAction, position ->
                when(swipeAction) {
                    SwipeAction.RIGHT -> {
                        configurePDFAction(
                            position = position
                        )
                        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.restoreItem(
                            position = position
                        )
                    }
                    SwipeAction.LEFT ->
                        configureDeleteAction(
                            position = position
                        )
                }
            }
        )
        swipeItemTouchHelper.attachToRecyclerView(binding.paymentListRecyclerView)
    }

    private fun configurePDFAction(position: Int) {
        uiScope.launch {
            filteredList.getOrNull(index = position)?.asIs<GroupModel> { groupModel ->
                viewModel.initializePayments(
                    context = context ?: return@asIs,
                    userModel = (activity as? MainActivity)?.userModel,
                    groupModel = groupModel
                ) {
                    initializePDFCreation(
                        clients = it
                    )
                }
            }
            filteredList.getOrNull(index = position)?.asIs<ClientModel> { personModel ->
                initializePDFCreation(
                    clients = listOf(personModel)
                )
            }
        }
    }

    private fun initializePDFCreation(
        clients: List<ClientModel>,
        fromMultipleSelection: Boolean = false
    ) {
        PDFHelper.shared.initializePDF(
            context = context ?: return,
            userModel = (activity as? MainActivity)?.userModel,
            clients = clients,
        ) { pdfFile ->
            uiScope.launch {
                viewModel.insertFile(
                    context = context ?: return@launch,
                    userModel = (activity as? MainActivity)?.userModel,
                    file = pdfFile,
                    description = if (clients.isSizeOne)
                        clients.firstOrNull()?.fullName ?: ""
                    else
                        clients.groupBy { it.groupName }.mapNotNull { it.key }.joinToString(
                            separator = " - "
                        )
                )
                if (fromMultipleSelection)
                    clearSelectedPayments()
                uiScope.launch {
                    delay(
                        timeMillis = 1000L
                    )
                    redirectToPdfViewer(
                        pdfFile = pdfFile,
                        description = if (clients.isSizeOne)
                            clients.firstOrNull()?.fullName ?: ""
                        else
                            clients.groupBy { it.groupName }.mapNotNull { it.key }.joinToString(
                                separator = " - "
                            )
                    )
                }
                context?.message(
                    message = resources.getString(R.string.key_file_saved_message)
                )
            }
        }
    }

    private fun redirectToPdfViewer(pdfFile: File, description: String) {
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

    private fun configureDeleteAction(position: Int) {
        context?.showTwoButtonsDialog(
            isCancellable = false,
            title = resources.getString(R.string.key_warning_label),
            message =
                if (filteredList.getOrNull(index = position) is GroupModel)
                    String.format(resources.getString(R.string.key_delete_group_message_value), (filteredList.getOrNull(index = position) as? GroupModel)?.groupName)
                else
                    resources.getString(R.string.key_delete_payment_message),
            negativeButtonBlock = {
                (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.restoreItem(
                    position = position
                )
            },
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deleteItem(
                        context = context ?: return@launch,
                        item = filteredList.getOrNull(index = position)
                    )
                }
            }
        )
    }

    private fun configureMultipleDeleteAction(positions: List<Int>) {
        context?.showTwoButtonsDialog(
            isCancellable = false,
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_delete_selected_payments_message),
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deletePayments(
                        context = context ?: return@launch,
                        personIds = positions
                    )
                }
            }
        )
    }

    private fun configureObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            after(
                millis = 1000
            ) {
                searchViewState = list.filterIsInstance<ClientModel>().isNotEmpty()
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
    }

    private fun clearSelectedPayments() {
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

    private fun configurePayments(list: List<Any>, query: String? = null) {
        filteredList.clear()
        list.filterIsInstance<ClientModel>().takeIf { it.isNotEmpty() }?.let { payments ->
            payments.groupBy { it.groupName ?: "" }.toSortedMap().forEach { map ->
                map.value.filter { it.fullName.lowercase().contains(query?.replace(" ", "")?.lowercase() ?: "") || it.groupName?.lowercase()?.contains(query?.replace(" ", "")?.lowercase() ?: "") == true }
                    .takeIf { it.isNotEmpty() }?.let inner@ { filteredByQueryPayments ->
                        filteredList.add(
                            GroupModel(
                                groupId = filteredByQueryPayments.firstOrNull()?.groupId,
                                groupName = map.key,
                                color = filteredByQueryPayments.firstOrNull()?.groupColor,
                                groupImage = filteredByQueryPayments.firstOrNull()?.groupImage
                            )
                        )
                        filteredList.addAll(
                            filteredByQueryPayments.sortedBy { (it getPaymentsFilteringOptionType MainApplication.paymentsFilteringOptionTypes).position  }.also { personModelList ->
                                if (personModelList.isSizeOne)
                                    personModelList.firstOrNull()?.backgroundDrawable = R.drawable.payment_row_radius_background
                                else {
                                    personModelList.firstOrNull()?.backgroundDrawable = R.drawable.payment_row_header_background
                                    personModelList.lastOrNull()?.backgroundDrawable = R.drawable.payment_row_footer_background
                                }
                            }
                        )
                        filteredByQueryPayments.mapNotNull { it.totalPaymentAmount }.sum().takeIf { !it.isZero }?.let { sum ->
                            filteredList.add(
                                PaymentAmountSumModel(
                                    sum = sum,
                                    color = filteredByQueryPayments.firstOrNull()?.groupColor
                                )
                            )
                        }
                    }
            }
        }
        filteredList.addAll(
            list.filterIsInstance<GroupModel>().filter {
                it.groupName?.lowercase()?.contains(query?.lowercase() ?: "") == true
                        //&& it.groupName != resources.getString(R.string.key_inactive_label)
            }
        )

        if (filteredList.isEmpty())
            query.whenNull {
                filteredList.add(
                    EmptyModel(
                        title = resources.getString(R.string.key_no_clients_title_message),
                        message = resources.getString(R.string.key_no_clients_message),
                        animationJsonIcon = "empty_animation.json"
                    )
                )
            }?.let {
                filteredList.add(
                    EmptyModel(
                        message = String.format(
                            resources.getString(R.string.key_no_results_found_value),
                            it
                        ),
                        imageIconResource = R.drawable.ic_colored_search
                    )
                )
            }
        binding.paymentListRecyclerView.scheduleLayoutAnimation()
        (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.reloadData()
        (activity as? MainActivity)?.historyButtonIsVisible = filteredList.filterIsInstance<ClientModel>().mapNotNull { it.payments }.flatten().isNotEmpty()
        binding.paymentsAreAvailable = (activity as? MainActivity)?.historyButtonIsVisible == true
        sharedPreferences?.clientModelList.takeIf { it == null || it.size != list.filterIsInstance<ClientModel>().size }.apply {
            sharedPreferences?.clientModelList = list.filterIsInstance<ClientModel>()
            context?.updatePaymentsAppWidget()
        }
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
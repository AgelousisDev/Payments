package com.agelousis.payments.main.ui.payments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.payments.databinding.FragmentPaymentsLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.presenters.GroupPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentAmountSumPresenter
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentViewHolder
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.paymentsFiltering.FilterPaymentsFragment
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.periodFilter.models.PeriodFilterDataModel
import com.agelousis.payments.main.ui.shareMessageFragment.ShareMessageBottomSheetFragment
import com.agelousis.payments.main.ui.totalPaymentsAmount.TotalPaymentsAmountDialogFragment
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PaymentsFragment : Fragment(), GroupPresenter, PaymentPresenter, PaymentAmountSumPresenter {

    override fun onGroupSelected(groupModel: GroupModel) {
        (activity as? MainActivity)?.startGroupActivity(
            groupModel = groupModel
        )
    }

    override fun onPaymentSelected(personModel: PersonModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                personDataModel = personModel
            )
        )
    }

    override fun onPaymentLongPressed(personModel: PersonModel) {
        ShareMessageBottomSheetFragment.show(
            supportFragmentManager = activity?.supportFragmentManager ?: return,
            personModel = personModel
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
                mobileNumbers = itemsList.filterIsInstance<PersonModel>().filter { it.groupId == groupModel.groupId }.mapNotNull { it.phone },
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
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private val itemsList by lazy { arrayListOf<Any>() }
    private val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            binding.searchLayout.visibility = if (value) View.VISIBLE else View.GONE
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPaymentsLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSearchView()
        configureRecyclerView()
        initializePayments()
        initializeNewFilters()
    }

    private fun configureSearchView() {
        binding.searchLayout.onProfileImageClicked {
            (activity as? MainActivity)?.binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        binding.searchLayout.onSecondaryIconClicked {
            findNavController().navigate(
                PaymentsFragmentDirections.actionPaymentsFragmentToFilterPaymentsFragment()
            )
        }
        binding.searchLayout.onQueryListener {
            configurePayments(
                list = itemsList,
                query = it
            )
        }
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
                        is PersonModel -> {
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
                        persons = it
                    )
                }
            }
            filteredList.getOrNull(index = position)?.asIs<PersonModel> { personModel ->
                initializePDFCreation(
                    persons = listOf(personModel)
                )
            }
        }
    }

    private fun initializePDFCreation(persons: List<PersonModel>) {
        PDFHelper.shared.initializePDF(
            context = context ?: return,
            userModel = (activity as? MainActivity)?.userModel,
            persons = persons,
        ) { pdfFile ->
            uiScope.launch {
                viewModel.insertFile(
                    context = context ?: return@launch,
                    userModel = (activity as? MainActivity)?.userModel,
                    file = pdfFile,
                    description = if (persons.isSizeOne) persons.firstOrNull()?.fullName ?: "" else persons.firstOrNull()?.groupName ?: ""
                )
                context?.sharePDF(
                    pdfFile = pdfFile
                )
                context?.message(
                    message = resources.getString(R.string.key_file_saved_message)
                )
            }
        }
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

    private fun configureObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            after(
                millis = 1000
            ) {
                searchViewState = list.filterIsInstance<PersonModel>().isNotEmpty()
            }
            itemsList.clear()
            itemsList.addAll(list)
            configurePayments(
                list = list
            )
        }
        viewModel.deletionLiveData.observe(viewLifecycleOwner) {
            if (it)
                initializePayments()
        }
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
            (activity as? MainActivity)?.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
            (activity as? MainActivity)?.paymentsFilteringOptionTypes?.forEachIndexed { index, paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = index
            }
        }
    }

    private fun configurePayments(list: List<Any>, query: String? = null) {
        filteredList.clear()
        list.filterIsInstance<PersonModel>().takeIf { it.isNotEmpty() }?.let { payments ->
            payments.groupBy { it.groupName ?: "" }.toSortedMap().forEach { map ->
                map.value.filter { it.fullName.toLowerCase(Locale.getDefault()).contains(query?.replace(" ", "")?.toLowerCase(Locale.getDefault()) ?: "") || it.groupName?.toLowerCase(Locale.getDefault())?.contains(query?.replace(" ", "")?.toLowerCase(Locale.getDefault()) ?: "") == true }
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
                            filteredByQueryPayments.sortedBy { (it getPaymentsFilteringOptionType (activity as? MainActivity)?.paymentsFilteringOptionTypes).position  }.also { personModelList ->
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
            list.filterIsInstance<GroupModel>().filter { it.groupName?.toLowerCase(Locale.getDefault())?.contains(query?.toLowerCase(Locale.getDefault()) ?: "") == true  && it.groupName != resources.getString(R.string.key_inactive_label) }
        )

        if (filteredList.isEmpty())
            query.whenNull {
                filteredList.add(
                    EmptyModel(
                        title = resources.getString(R.string.key_no_persons_title_message),
                        message = resources.getString(R.string.key_no_persons_message),
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
        (activity as? MainActivity)?.historyButtonIsVisible = filteredList.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten().isNotEmpty()
    }

    fun navigateToPeriodFilterFragment() {
        val payments = filteredList.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten()
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
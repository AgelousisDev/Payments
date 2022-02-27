package com.agelousis.payments.main.ui.payments.extensions

import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.payments.database.UpdateSuccessBlock
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.ui.newPayment.viewHolders.PaymentAmountViewHolder
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.PaymentsFragmentDirections
import com.agelousis.payments.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.payments.main.ui.payments.models.*
import com.agelousis.payments.main.ui.payments.viewHolders.BalanceOverviewViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.ClientViewHolder
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import com.agelousis.payments.utils.models.CalendarDataModel
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import com.agelousis.payments.widgets.extensions.clientModelList
import com.agelousis.payments.widgets.extensions.updatePaymentsAppWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun PaymentsFragment.configureSwipeEvents() {
    val swipeItemTouchHelper = ItemTouchHelper(
        SwipeItemTouchHelper(
            context = context ?: return,
            marginStart = resources.getDimension(R.dimen.activity_general_horizontal_margin),
            swipePredicateBlock = {
                it is GroupViewHolder || it is ClientViewHolder || it is BalanceOverviewViewHolder || it is PaymentAmountViewHolder
            }
        ) innerBlock@ { swipeAction, swipeItemType, position ->
            when(swipeItemType) {
                SwipeItemType.BALANCE_OVERVIEW_ITEM ->
                    disableBalanceOverview()
                SwipeItemType.PAYMENT_AMOUNT ->
                    this deleteItemWith position
                else ->
                    when(swipeAction) {
                        SwipeAction.RIGHT -> {
                            this configurePDFActionWith position
                            (layoutBinding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.updateIn(
                                position = position
                            )
                        }
                        SwipeAction.LEFT ->
                            this deleteItemWith position
                    }
            }
        }
    )
    swipeItemTouchHelper.attachToRecyclerView(layoutBinding.paymentListRecyclerView)
}

infix fun PaymentsFragment.sendGroupSms(groupModel: GroupModel) {
    context?.showSimpleDialog(
        SimpleDialogDataModel(
            title = resources.getString(R.string.key_sms_label),
            message = String.format(
                resources.getString(R.string.key_send_sms_to_group_value_message),
                groupModel.groupName ?: ""
            ),
            positiveButtonText = resources.getString(R.string.key_send_label),
            positiveButtonBlock = {
                context?.sendSMSMessage(
                    mobileNumbers = itemsList.filterIsInstance<ClientModel>()
                        .filter { it.groupId == groupModel.groupId }.mapNotNull { it.phone },
                    message = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
                )
            }
        )
    )
}

infix fun PaymentsFragment.sendGroupEmail(groupModel: GroupModel) {
    context?.showSimpleDialog(
        SimpleDialogDataModel(
            title = resources.getString(R.string.key_email_label),
            message = String.format(
                resources.getString(R.string.key_send_email_to_group_value_message),
                groupModel.groupName ?: ""
            ),
            positiveButtonText = resources.getString(R.string.key_send_label),
            positiveButtonBlock = {
                context?.textEmail(
                    *itemsList.filterIsInstance<ClientModel>()
                        .filter { it.groupId == groupModel.groupId }.mapNotNull { it.email }
                        .toTypedArray(),
                    content = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
                )
            }
        )
    )
}

private infix fun PaymentsFragment.deleteItemWith(
    position: Int
) {
    context?.showTwoButtonsDialog(
        SimpleDialogDataModel(
            isCancellable = false,
            title = resources.getString(R.string.key_warning_label),
            message =
            when {
                filteredList.getOrNull(index = position) is GroupModel ->
                    String.format(resources.getString(R.string.key_delete_group_message_value), (filteredList.getOrNull(index = position) as? GroupModel)?.groupName)
                filteredList.getOrNull(index = position) is PaymentAmountModel ->
                    resources.getString(R.string.key_delete_payment_message)
                else -> resources.getString(R.string.key_delete_client_message)
            },
            negativeButtonBlock = {
                (layoutBinding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.updateIn(
                    position = position
                )
            },
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBackgroundColor = ContextCompat.getColor(context ?: return, R.color.red),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deleteItem(
                        item = filteredList.getOrNull(index = position)
                    )
                }
            }
        )
    )
}

infix fun PaymentsFragment.configureMultipleDeleteActionWith(positions: List<Int>) {
    context?.showTwoButtonsDialog(
        SimpleDialogDataModel(
            isCancellable = false,
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_delete_selected_payments_message),
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBackgroundColor = ContextCompat.getColor(context ?: return, R.color.red),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deletePayments(
                        personIds = positions
                    )
                }
            }
        )
    )
}

private infix fun PaymentsFragment.configurePDFActionWith(position: Int) {
    uiScope.launch {
        filteredList.getOrNull(index = position)?.asIs<GroupModel> { groupModel ->
            viewModel.initializePayments(
                userModel = (activity as? MainActivity)?.userModel,
                groupModel = groupModel
            ) {
                initializePDFCreation(
                    clients = it
                )
            }
        }
        filteredList.getOrNull(index = position)?.asIs<ClientModel> { clientModel ->
            initializePDFCreation(
                clients = listOf(
                    clientModel
                )
            )
        }
    }
}

fun PaymentsFragment.initializePDFCreation(
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

fun PaymentsFragment.disableBalanceOverview() {
    sharedPreferences?.balanceOverviewState = false
    filteredList.indexOfFirst {
        it is BalanceOverviewDataModel
    }.takeIf { it > -1 }?.let { balanceOverviewDataModelIndex ->
        filteredList.removeAt(balanceOverviewDataModelIndex)
        layoutBinding.paymentListRecyclerView.adapter?.notifyItemRemoved(balanceOverviewDataModelIndex)
    }
}

fun PaymentsFragment.configureRecyclerViewAdapterAndLayoutManager() {
    if (context?.isLandscape == true)
        layoutBinding.paymentListRecyclerView.layoutManager = GridLayoutManager(
            context,
            2
        ).also { gridLayoutManager ->
            gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = when(filteredList.getOrNull(index = position)) {
                    is ClientModel,
                    is BalanceOverviewDataModel -> 1
                    else -> 2
                }
            }
        }
    layoutBinding.paymentListRecyclerView.adapter = PaymentsAdapter(
        list = filteredList,
        groupPresenter = this,
        clientPresenter = this,
        paymentPresenter = this,
        paymentAmountSumPresenter = this,
        balanceOverviewPresenter = this,
        vat = (activity as? MainActivity)?.userModel?.vat
    )
}

fun PaymentsFragment.addRecyclerViewItemDecoration() {
    layoutBinding.paymentListRecyclerView.addItemDecoration(
        object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val adapterPosition = parent.getChildAdapterPosition(view)
                when(filteredList.getOrNull(adapterPosition)) {
                    is GroupModel -> {
                        if (adapterPosition > 0)
                            outRect.top = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        if (context?.isLandscape == false)
                            outRect.bottom = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        if (filteredList isLastAt adapterPosition)
                            outRect.bottom = 90.inPixel.toInt()
                    }
                    is ClientModel,
                    is PaymentAmountModel -> {
                        if (context?.isLandscape == true)
                            outRect.top = resources.getDimensionPixelOffset(R.dimen.nav_header_vertical_spacing)
                        outRect.left = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        outRect.right = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        if (filteredList isLastAt adapterPosition)
                            outRect.bottom = 90.inPixel.toInt()
                        else if (filteredList.getOrNull(adapterPosition) is PaymentAmountModel)
                            outRect.bottom = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                    }
                    is PaymentAmountSumModel -> {
                        outRect.top = resources.getDimensionPixelOffset(R.dimen.activity_general_horizontal_margin)
                        if (filteredList isLastAt adapterPosition)
                            outRect.bottom = 90.inPixel.toInt()
                    }
                }
            }
        }
    )
}

fun PaymentsFragment.configurePayments(list: List<Any>, query: String? = null) {
    filteredList.clear()
    list.filterIsInstance<ClientModel>().takeIf { it.isNotEmpty() }?.let { payments ->
        if (sharedPreferences?.balanceOverviewState == true
            && query == null
        )
            filteredList.add(
                BalanceOverviewDataModel.getBalanceOverviewDataModelWith(
                    currentBalance = (activity as? MainActivity)?.userModel?.balance,
                    lastPaymentMonthList = payments.getSixLastPaymentMonths(
                        context = context ?: return@let,
                        independentPaymentAmountModelList = list.filterIsInstance<PaymentAmountModel>()
                    )
                )
            )
        payments.groupBy {
            it.groupName ?: ""
        }.toSortedMap().forEach { map ->
            map.value.filter {
                it.fullName.lowercase().contains(query?.replace(" ", "")?.lowercase() ?: "")
                        || it.groupName?.lowercase()
                    ?.contains(query?.replace(" ", "")?.lowercase() ?: "") == true
            }.takeIf {
                it.isNotEmpty()
            }?.let inner@ { filteredByQueryClients ->
                filteredList.add(
                    GroupModel(
                        groupId = filteredByQueryClients.firstOrNull()?.groupId,
                        groupName = map.key,
                        color = filteredByQueryClients.firstOrNull()?.groupColor,
                        groupImage = filteredByQueryClients.firstOrNull()?.groupImage
                    )
                )
                filteredList.addAll(
                    list.filterIsInstance<PaymentAmountModel>().filter { paymentAmountModel ->
                        paymentAmountModel.groupId == filteredByQueryClients.firstOrNull()?.groupId
                    }
                )
                filteredList.addAll(
                    filteredByQueryClients.sortedBy {
                        (it getClientsFilteringOptionType MainApplication.paymentsFilteringOptionTypes).position
                    }.also { clientModelList ->
                            clientModelList applyPaymentRowBackground (context ?: return@also)
                    }
                )

                filteredByQueryClients.mapNotNull {
                    it.totalPaymentAmount
                }.sum().takeIf {
                    !it.isZero
                }?.let { sum ->
                    filteredList.add(
                        PaymentAmountSumModel(
                            sum = sum + (list.filterIsInstance<PaymentAmountModel>()
                                .filter { paymentAmountModel -> paymentAmountModel.groupId == filteredByQueryClients.firstOrNull()?.groupId }
                                .mapNotNull { it.paymentAmount }.takeIf { it.isNotEmpty() }?.sum()
                                ?: 0.0),
                            color = filteredByQueryClients.firstOrNull()?.groupColor
                        )
                    )
                }
            }
        }
    }
    filteredList.addAll(
        list.filterIsInstance<GroupModel>().filter {
            it.groupName?.lowercase()?.contains(query?.lowercase() ?: "") == true
        }
    )
    if (filteredList.none { filteredPaymentItem ->
            filteredPaymentItem is PaymentAmountModel
                && list.any { genericPaymentItem ->
            genericPaymentItem is PaymentAmountModel
        }
    })
        list.filterIsInstance<PaymentAmountModel>().forEach { paymentAmountModel ->
            filteredList.toList().forEachIndexed { index, item ->
                if (item is GroupModel
                    && paymentAmountModel.groupId == item.groupId
                )
                    filteredList.add(
                        index + 1,
                        paymentAmountModel
                    )
            }
        }

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
    layoutBinding.paymentListRecyclerView.scheduleLayoutAnimation()
    (layoutBinding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.reloadData()
    sharedPreferences?.clientModelList.takeIf { it == null || it.size != list.filterIsInstance<ClientModel>().size }
        .apply {
            sharedPreferences?.clientModelList = list.filterIsInstance<ClientModel>()
            context?.updatePaymentsAppWidget()
        }
}

infix fun PaymentsFragment.createCalendarEventWith(paymentAmountModel: PaymentAmountModel?) {
    (context ?: return) createCalendarEventWith CalendarDataModel(
        calendar = paymentAmountModel?.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.calendar ?: return,
        title = String.format(
            "%s",
            paymentAmountModel.singlePaymentProductsSeparated ?: ""
        ),
        description = String.format(
            resources.getString(R.string.key_calendar_event_amount_value),
            paymentAmountModel.getAmountWithoutVat(
                context = context ?: return,
                vat = (activity as? MainActivity)?.userModel?.vat ?: return
            )
        )
    )
}

fun PaymentsFragment.redirectToGroupModificationFragment(
    groupModel: GroupModel? = null
) {
    findNavController().navigate(
        PaymentsFragmentDirections.actionPaymentsFragmentToGroupModificationFragment(
            groupModel = groupModel
        )
    )
}

fun PaymentsFragment.configureGroup(
    groupModel: GroupModel,
    updateSuccessBlock: UpdateSuccessBlock
) {
    uiScope.launch {
        groupModel.groupImageData = context?.byteArrayFromInternalImage(
            imageName = groupModel.groupImage
        )
        groupModel.groupId?.let {
            viewModel.updateGroup(
                groupModel = groupModel,
                updateSuccessBlock = updateSuccessBlock
            )
        } ?: viewModel.insertGroups(
            userId = (activity as? MainActivity)?.userModel?.id,
            groupModelList = listOf(
                groupModel
            ),
            insertionSuccessBlock = updateSuccessBlock
        )
    }
}
package com.agelousis.payments.main.ui.payments.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.payments.main.ui.payments.models.BalanceOverviewDataModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.payments.viewHolders.BalanceOverviewViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentViewHolder
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun PaymentsFragment.configureSwipeEvents() {
    val swipeItemTouchHelper = ItemTouchHelper(
        SwipeItemTouchHelper(
            context = context ?: return,
            marginStart = resources.getDimension(R.dimen.activity_general_horizontal_margin),
            swipePredicateBlock = {
                it is GroupViewHolder || it is PaymentViewHolder || it is BalanceOverviewViewHolder
            }
        ) innerBlock@ { swipeAction, swipeItemType, position ->
            when(swipeItemType) {
                SwipeItemType.BALANCE_OVERVIEW_ITEM ->
                    disableBalanceOverview()
                else ->
                    when(swipeAction) {
                        SwipeAction.RIGHT -> {
                            this configurePDFActionWith position
                            (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.updateIn(
                                position = position
                            )
                        }
                        SwipeAction.LEFT ->
                            this deletePaymentWith position
                    }
            }
        }
    )
    swipeItemTouchHelper.attachToRecyclerView(binding.paymentListRecyclerView)
}

infix fun PaymentsFragment.sendGroupSms(groupModel: GroupModel) {
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

infix fun PaymentsFragment.sendGroupEmail(groupModel: GroupModel) {
    context?.showSimpleDialog(
        title = resources.getString(R.string.key_email_label),
        message = String.format(
            resources.getString(R.string.key_send_email_to_group_value_message),
            groupModel.groupName ?: ""
        ),
        positiveButtonText = resources.getString(R.string.key_send_label)
    ) {
        context?.textEmail(
            *itemsList.filterIsInstance<ClientModel>().filter { it.groupId == groupModel.groupId }.mapNotNull { it.email }.toTypedArray(),
            content = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
        )
    }
}

private infix fun PaymentsFragment.deletePaymentWith(position: Int) {
    context?.showTwoButtonsDialog(
        isCancellable = false,
        title = resources.getString(R.string.key_warning_label),
        message =
        if (filteredList.getOrNull(index = position) is GroupModel)
            String.format(resources.getString(R.string.key_delete_group_message_value), (filteredList.getOrNull(index = position) as? GroupModel)?.groupName)
        else
            resources.getString(R.string.key_delete_payment_message),
        negativeButtonBlock = {
            (binding.paymentListRecyclerView.adapter as? PaymentsAdapter)?.updateIn(
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

infix fun PaymentsFragment.configureMultipleDeleteActionWith(positions: List<Int>) {
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

private infix fun PaymentsFragment.configurePDFActionWith(position: Int) {
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

fun PaymentsFragment.disableBalanceOverview() {
    sharedPreferences?.balanceOverviewState = false
    filteredList.indexOfFirst {
        it is BalanceOverviewDataModel
    }.takeIf { it > -1 }?.let { balanceOverviewDataModelIndex ->
        filteredList.removeAt(balanceOverviewDataModelIndex)
        binding.paymentListRecyclerView.adapter?.notifyItemRemoved(balanceOverviewDataModelIndex)
    }
}

fun PaymentsFragment.addRecyclerViewItemDecoration() {
    binding.paymentListRecyclerView.addItemDecoration(
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
                    }
                    is ClientModel -> {
                        if (context?.isLandscape == true)
                            outRect.top = resources.getDimensionPixelOffset(R.dimen.nav_header_vertical_spacing)
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
}
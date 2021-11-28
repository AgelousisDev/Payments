package com.agelousis.payments.main.ui.newPayment.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.countrySelector.CountrySelectorDialogFragment
import com.agelousis.payments.main.ui.groupSelector.GroupSelectorDialogFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.models.NotificationDataModel
import com.agelousis.payments.utils.models.SimpleDialogDataModel

fun NewPaymentFragment.configureScrollView() {
    binding.nestedScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
        binding.headerConstraintLayout.elevation = if (binding.nestedScrollView.canScrollVertically(-1)) 8.inPixel else 0.0f
    }
}

fun NewPaymentFragment.configureRecyclerView() {
    binding.paymentAmountRecyclerView.addItemDecoration(
        object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val adapterPosition = parent.getChildAdapterPosition(view)
                if (availablePayments isLastAt adapterPosition)
                    outRect.bottom = 56.inPixel.toInt()
            }
        }
    )
    binding.paymentAmountRecyclerView.adapter = PaymentAmountAdapter(
        paymentModelList = availablePayments,
        vat = (activity as? MainActivity)?.userModel?.vat,
        presenter = this
    )
}

fun NewPaymentFragment.showCountryCodesSelector() {
    CountrySelectorDialogFragment.show(
        supportFragmentManager = childFragmentManager,
        countrySelectorFragmentPresenter = this,
        selectedCountryDataModel = selectedCountryDataModel,
        userModel = (activity as? MainActivity)?.userModel,
        updateGlobalCountryState = false
    )
}

fun NewPaymentFragment.showGroupsSelectionFragment() {
    availableGroups.forEach { groupModel ->
        groupModel.isSelected = groupModel.groupName?.lowercase() == binding.groupDetailsLayout.value?.lowercase()
    }
    GroupSelectorDialogFragment.show(
        supportFragmentManager = childFragmentManager,
        groupModelList = availableGroups,
        groupSelectorFragmentPresenter = this
    )
}

fun NewPaymentFragment.redirectToSMSAppIf(payment: PaymentAmountModel, predicate: () -> Boolean) {
    if (predicate())
        context?.showTwoButtonsDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_sms_label),
                message = resources.getString(R.string.key_send_sms_message),
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
        )
}

fun NewPaymentFragment.scheduleNotification() {
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
            groupImage = currentClientModel?.groupImage ?: args.groupDataModel?.groupImage,
            groupTint = currentClientModel?.groupColor
        )
    }
}

fun NewPaymentFragment.dismissPayment() {
    paymentReadyForDeletionIndexArray.sortDescending()
    paymentReadyForDeletionIndexArray.forEach { paymentReadyForDeletionIndex ->
        (binding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.removeItem(
            position = paymentReadyForDeletionIndex
        )
        (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
    }
    paymentReadyForDeletionIndexArray.clear()
    binding.paymentsAreAvailable = availablePayments.isNotEmpty()
}
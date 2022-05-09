package com.agelousis.payments.main.ui.newPayment.extensions

import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.ItemTouchHelper
import com.agelousis.payments.R
import com.agelousis.payments.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.ui.countrySelector.CountrySelectorBottomSheetDialogFragment
import com.agelousis.payments.main.ui.groupSelector.GroupSelectorBottomSheetDialogFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.payments.main.ui.newPayment.viewHolders.PaymentAmountViewHolder
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.newPayment.enumerations.ContactType
import com.agelousis.payments.main.ui.newPayment.ui.ContactOptionsLayout
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.custom.FabExtendingOnScrollListener
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.models.CalendarDataModel
import com.agelousis.payments.utils.models.NotificationDataModel
import com.agelousis.payments.utils.models.SimpleDialogDataModel

fun NewPaymentFragment.configureScrollView() {
    layoutBinding.nestedScrollView.setOnScrollChangeListener(
        FabExtendingOnScrollListener(
            floatingActionButton = layoutBinding.addPaymentButton
        )
    )
}

fun NewPaymentFragment.configureRecyclerView() {
    configureRecyclerViewMargins()
    configurePaymentsSwipeEvent()
    layoutBinding.paymentAmountRecyclerView applyFloatingButtonBottomMarginWith availablePayments
    layoutBinding.paymentAmountRecyclerView.adapter = PaymentAmountAdapter(
        paymentModelList = availablePayments,
        vat = (activity as? MainActivity)?.userModel?.vat,
        presenter = this
    )
}

fun NewPaymentFragment.configureRecyclerViewMargins() {
    when (context?.isLandscape == true) {
        true ->
            layoutBinding.paymentAmountRecyclerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(
                    bottom =
                    if (availablePayments.isEmpty())
                        90.inPixel.toInt()
                    else
                        0
                )
            }
        false ->
            layoutBinding.paymentAmountRecyclerView.updateLayoutParams<LinearLayout.LayoutParams> {
                updateMargins(
                    bottom = if (availablePayments.isEmpty())
                        90.inPixel.toInt()
                    else
                        0
                )
            }
    }
}

private fun NewPaymentFragment.configurePaymentsSwipeEvent() {
    val swipeItemTouchHelper = ItemTouchHelper(
        SwipeItemTouchHelper(
            context = context ?: return,
            marginStart = resources.getDimension(R.dimen.activity_general_horizontal_margin),
            swipePredicateBlock = {
                it is PaymentAmountViewHolder
            }
        ) innerBlock@ { _, swipeItemType, position ->
            when(swipeItemType) {
                SwipeItemType.PAYMENT_AMOUNT ->
                    this removePaymentAt position
                else -> {}
            }
        }
    )
    swipeItemTouchHelper.attachToRecyclerView(layoutBinding.paymentAmountRecyclerView)
}

private infix fun NewPaymentFragment.removePaymentAt(position: Int) {
    (layoutBinding.paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.removeItem(
        position = position
    )
    configureRecyclerViewMargins()
}

fun NewPaymentFragment.showCountryCodesSelector() {
    CountrySelectorBottomSheetDialogFragment.show(
        supportFragmentManager = childFragmentManager,
        countrySelectorFragmentPresenter = this,
        selectedCountryDataModel = selectedCountryDataModel,
        userModel = (activity as? MainActivity)?.userModel,
        updateGlobalCountryState = false
    )
}

fun NewPaymentFragment.showGroupsSelectionFragment() {
    availableGroups.forEach { groupModel ->
        groupModel.isSelected = groupModel.groupName?.lowercase() == layoutBinding.groupDetailsLayout.value?.lowercase()
    }
    GroupSelectorBottomSheetDialogFragment.show(
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
                            layoutBinding.phoneLayout.value ?: ""
                        ),
                        message = String.format(
                            "%s\n%s",
                            layoutBinding.messageTemplateField.text?.toString() ?: "",
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

infix fun NewPaymentFragment.createCalendarEventWith(paymentAmountModel: PaymentAmountModel?) {
    (context ?: return) createCalendarEventWith CalendarDataModel(
        calendar = paymentAmountModel?.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.calendar ?: return,
        title = String.format(
            "%s %s",
            layoutBinding.firstNameLayout.value ?: return,
            layoutBinding.surnameLayout.value ?: return
        ),
        description = String.format(
            resources.getString(R.string.key_calendar_event_amount_value),
            paymentAmountModel.getAmountWithoutVat(
                context = context ?: return,
                vat = (activity as? MainActivity)?.userModel?.vat ?: return
            )
        ),
        email = layoutBinding.emailLayout.value ?: return
    )
}

private val NewPaymentFragment.contactTypeList: List<ContactType>
    get() {
        val array = arrayListOf<ContactType>()
        array.add(
            ContactType.CALL.also {
                it.isEnabled = args.clientDataModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ContactType.SMS.also {
                it.isEnabled = args.clientDataModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ContactType.WHATS_APP.also {
                it.isEnabled =
                    args.clientDataModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(
                        packageName = it.packageName ?: return@also
                    ) == true
            }
        )
        array.add(
            ContactType.VIBER.also {
                it.isEnabled =
                    args.clientDataModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(
                        packageName = it.packageName ?: return@also
                    ) == true
            }
        )
        array.add(
            ContactType.EMAIL.also {
                it.isEnabled = args.clientDataModel?.email.isNullOrEmpty() == false
            }
        )
        return array
    }

fun NewPaymentFragment.setupContactUI() {
    if (!args.clientDataModel?.email.isNullOrEmpty()
            || !args.clientDataModel?.phone.isNullOrEmpty())
        layoutBinding.contactComposeView.setContent {
            MaterialTheme(
                colorScheme = appColorScheme(),
                typography = Typography
            ) {
                ContactOptionsLayout(
                    contactTypeList = contactTypeList,
                    contactTypeSelectionBlock = this::configureShareMethod
                )
            }
        }
}

private fun NewPaymentFragment.configureShareMethod(contactType: ContactType) {
    when (contactType) {
        ContactType.CALL ->
            context?.call(
                phone = args.clientDataModel?.phone ?: return
            )
        ContactType.SMS ->
            context?.sendSMSMessage(
                mobileNumbers = listOf(
                    args.clientDataModel?.phone?.toRawMobileNumber ?: return
                ),
                message = args.clientDataModel?.messageTemplate ?: ""
            )
        ContactType.WHATS_APP,
        ContactType.VIBER ->
            context?.shareMessage(
                schemeUrl = String.format(
                    contactType.schemeUrl ?: return,
                    args.clientDataModel?.phone?.toRawMobileNumber,
                    args.clientDataModel?.messageTemplate ?: ""
                )
            )
        ContactType.EMAIL ->
            context?.textEmail(
                args.clientDataModel?.email ?: return,
                content = args.clientDataModel?.messageTemplate ?: ""
            )

    }
}
package com.agelousis.payments.main.menuOptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.menuOptions.ui.PaymentsMenuOptionsLayout
import com.agelousis.payments.main.menuOptions.viewModel.PaymentsMenuOptionsViewModel
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currentNavigationFragment
import com.agelousis.payments.utils.extensions.hasPermissions
import com.agelousis.payments.utils.extensions.sendSMSMessage
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment

class PaymentsMenuOptionsBottomSheetFragment: BasicBottomSheetDialogFragment(), PaymentsMenuOptionPresenter {

    companion object {
        fun show(supportFragmentManager: FragmentManager) {
            PaymentsMenuOptionsBottomSheetFragment().show(
                supportFragmentManager,
                Constants.PAYMENTS_MENU_OPTIONS_FRAGMENT_TAG
            )
        }
    }

    override fun onClearPayments() {
        dismiss()
        (activity as? MainActivity)?.triggerPaymentsClearance()
    }

    override fun onCsvExport() {
        dismiss()
        (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.navigateToPeriodFilterFragment()
    }

    override fun onSendSmsGlobally() {
        context?.sendSMSMessage(
            mobileNumbers = (parentFragment as? PaymentsFragment)?.filteredList?.filterIsInstance<ClientModel>()?.mapNotNull { it.phone } ?: return,
            message = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
        )
    }

    override fun onPaymentsOrder() {
        dismiss()
        (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToFilterPaymentsFragment()
    }

    override fun onQrCode(qrCodeSelectionType: QRCodeSelectionType) {
        dismiss()
        when(qrCodeSelectionType) {
            QRCodeSelectionType.GENERATE ->
                (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToQrCodeFragment(
                    qrCodeSelectionType = qrCodeSelectionType
                )
            QRCodeSelectionType.SCAN ->
                if (context?.hasPermissions(android.Manifest.permission.CAMERA) == true)
                    (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToQrCodeFragment(
                        qrCodeSelectionType = qrCodeSelectionType,
                        selectedClients = (parentFragment as? PaymentsFragment)?.filteredList?.filterIsInstance<ClientModel>()
                            ?.takeWhile { clientModel -> clientModel.isSelected }
                    )
                else
                    ActivityCompat.requestPermissions(
                        activity ?: return,
                        arrayOf(
                            android.Manifest.permission.CAMERA
                        ),
                        MainActivity.QR_CODE_CAMERA_PERMISSION_REQUEST_CODE
                    )
        }

    }

    private val viewModel by viewModels<PaymentsMenuOptionsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    viewModel.setupList(
                        context = context ?: return@MaterialTheme,
                        paymentsFragment = this@PaymentsMenuOptionsBottomSheetFragment.parentFragment as? PaymentsFragment
                            ?: return@MaterialTheme
                    )
                    PaymentsMenuOptionsLayout(
                        viewModel = viewModel,
                        presenter = this@PaymentsMenuOptionsBottomSheetFragment
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PaymentsMenuOptionsBottomSheetFragmentPreview() {
        PaymentsMenuOptionsLayout(
            viewModel = viewModel,
            presenter = this@PaymentsMenuOptionsBottomSheetFragment
        )
    }

}
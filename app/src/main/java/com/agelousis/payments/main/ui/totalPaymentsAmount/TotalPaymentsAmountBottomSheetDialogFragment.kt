package com.agelousis.payments.main.ui.totalPaymentsAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.totalPaymentsAmount.ui.TotalPaymentAmountLayoutWith
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColorScheme
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment

class TotalPaymentsAmountBottomSheetDialogFragment: BasicBottomSheetDialogFragment() {

     companion object {

         private const val PAYMENT_AMOUNT_SUM_MODEL_EXTRA = "TotalPaymentsAmountDialogFragment=paymentAmountSumModel"

         fun show(supportFragmentManager: FragmentManager, paymentAmountSumModel: PaymentAmountSumModel) {
             TotalPaymentsAmountBottomSheetDialogFragment().also { fragment ->
                 fragment.arguments = Bundle().also {
                     it.putParcelable(
                         PAYMENT_AMOUNT_SUM_MODEL_EXTRA,
                         paymentAmountSumModel
                     )
                 }
             }.show(
                 supportFragmentManager,
                 Constants.TOTAL_PAYMENTS_AMOUNT_FRAGMENT_TAG
             )
         }

     }

    private val paymentAmountModel by lazy {
        arguments?.getParcelable<PaymentAmountSumModel>(PAYMENT_AMOUNT_SUM_MODEL_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    TotalPaymentAmountLayoutWith(
                        paymentAmountSumModel = paymentAmountModel,
                        vat = (activity as? MainActivity)?.userModel?.vat
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun TotalPaymentsAmountBottomSheetDialogFragmentComposablePreview() {
        TotalPaymentAmountLayoutWith(
            paymentAmountSumModel = paymentAmountModel,
            vat = (activity as? MainActivity)?.userModel?.vat
        )
    }

}
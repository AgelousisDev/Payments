package com.agelousis.payments.main.ui.totalPaymentsAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.totalPaymentsAmount.ui.TotalPaymentAmountLayoutWith
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
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

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    TotalPaymentAmountLayoutWith(
                        paymentAmountSumModel = paymentAmountModel,
                        vat = (activity as? MainActivity)?.userModel?.vat
                    )
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        TotalPaymentAmountLayoutWith(
            paymentAmountSumModel = paymentAmountModel,
            vat = (activity as? MainActivity)?.userModel?.vat
        )
    }

}
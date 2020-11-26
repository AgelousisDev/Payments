package com.agelousis.payments.main.ui.totalPaymentsAmount

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.databinding.TotalPaymentsAmountFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.utils.constants.Constants

class TotalPaymentsAmountDialogFragment: DialogFragment() {

     companion object {

         private const val PAYMENT_AMOUNT_SUM_MODEL_EXTRA = "TotalPaymentsAmountDialogFragment=paymentAmountSumModel"

         fun show(supportFragmentManager: FragmentManager, paymentAmountSumModel: PaymentAmountSumModel) {
             TotalPaymentsAmountDialogFragment().also { fragment ->
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

    private val paymentAmountModel by lazy { arguments?.getParcelable<PaymentAmountSumModel>(PAYMENT_AMOUNT_SUM_MODEL_EXTRA) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        TotalPaymentsAmountFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.paymentAmountSumModel = paymentAmountModel
            it.vat = (activity as? MainActivity)?.userModel?.vat
        }.root

}
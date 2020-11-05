package com.agelousis.payments.main.ui.periodFilter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PeriodFilterFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.after
import com.agelousis.payments.utils.extensions.createFile
import com.agelousis.payments.utils.extensions.toDateWith
import com.agelousis.payments.utils.extensions.toast
import com.agelousis.payments.utils.helpers.PaymentCsvHelper
import java.util.*

class PeriodFilterFragment: Fragment() {

    companion object {
        private const val CREATE_CSV_FILE_REQUEST_CODE = 7
        private const val LOADING_TIME = 5000L
    }

    private val args: PeriodFilterFragmentArgs by navArgs()
    private var binding: PeriodFilterFragmentLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context ?: return).inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PeriodFilterFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.periodFilterDataModel = args.periodFilterData
            it.isLoading = false
        }
        return binding?.root
    }

    fun initializeExportToExcelOperation() {
        (activity as? MainActivity)?.floatingButtonState = false
        binding?.isLoading = true
        after(
            millis = LOADING_TIME
        ) {
            createFile(
                requestCode = CREATE_CSV_FILE_REQUEST_CODE,
                fileName = Constants.PAYMENTS_CSV_FILE
            )
        }
    }

    private fun triggerCsvCreation(uri: Uri) {
        val minimumMonthDate = binding?.periodFilterMinimumPaymentMonthLayout?.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val maximumMonthDate = binding?.periodFilterMaximumPaymentMonthLayout?.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val filteredPayments = args.paymentListData.filter { it.paymentMonthDate ?: Date() in minimumMonthDate..maximumMonthDate }
        PaymentCsvHelper.createPaymentsCsv(
            context = context ?: return,
            uri = uri,
            payments = filteredPayments
        ) {
            context?.toast(
                message = resources.getString(R.string.key_scv_created_successfully)
            )
            findNavController().popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when(requestCode) {
                CREATE_CSV_FILE_REQUEST_CODE ->
                    triggerCsvCreation(
                        uri = data?.data ?: return
                    )
            }
    }

}
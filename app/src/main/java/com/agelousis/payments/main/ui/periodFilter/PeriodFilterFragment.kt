package com.agelousis.payments.main.ui.periodFilter

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PeriodFilterFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.periodFilter.presenter.PeriodFilterFragmentPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import com.agelousis.payments.utils.helpers.PaymentCsvHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PeriodFilterFragment: Fragment(), PeriodFilterFragmentPresenter {

    companion object {
        private const val LOADING_TIME = 5000L
    }

    override fun onPdfInvoice() {
        val minimumMonthDate = binding.periodFilterMinimumPaymentMonthLayout.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val maximumMonthDate = binding.periodFilterMaximumPaymentMonthLayout.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        initializePDFCreation(
            payments = args.paymentListData.filter { it.paymentMonthDate ?: Date() in minimumMonthDate..maximumMonthDate }.sortedBy { it.paymentMonthDate }
        )
    }

    private val args: PeriodFilterFragmentArgs by navArgs()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private lateinit var binding: PeriodFilterFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PeriodFilterFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.periodFilterDataModel = args.periodFilterData
            it.isLoading = false
            it.presenter = this
        }
        return binding.root
    }

    fun initializeExportToExcelOperation() {
        (activity as? MainActivity)?.floatingButtonState = false
        binding.isLoading = true
        after(
            millis = LOADING_TIME
        ) {
            (activity as? MainActivity)?.activityLauncher?.launch(
                input = createDocumentIntentWith(
                    fileName = Constants.PAYMENTS_CSV_FILE,
                    mimeType = Constants.CSV_MIME_TYPE
                )
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK)
                    triggerCsvCreation(
                        uri = result.data?.data ?: return@launch
                    )

                else
                    findNavController().popBackStack()
            }
        }
    }

    private fun triggerCsvCreation(uri: Uri) {
        val minimumMonthDate = binding.periodFilterMinimumPaymentMonthLayout.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val maximumMonthDate = binding.periodFilterMaximumPaymentMonthLayout.dateValue?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
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

    private fun initializePDFCreation(
        payments: List<PaymentAmountModel>
    ) {
        PDFHelper.shared.initializePDF(
            context = context ?: return,
            userModel = (activity as? MainActivity)?.userModel,
            persons = listOf(
                PersonModel(
                    payments = payments
                )
            ),
        ) { pdfFile ->
            uiScope.launch {
                viewModel.insertFile(
                    context = context ?: return@launch,
                    userModel = (activity as? MainActivity)?.userModel,
                    file = pdfFile,
                    description = String.format(
                        "%s - %s",
                        binding.periodFilterMinimumPaymentMonthLayout.dateValue ?: "",
                        binding.periodFilterMaximumPaymentMonthLayout.dateValue ?: ""
                    )
                )
                uiScope.launch {
                    delay(
                        timeMillis = 1000L
                    )
                    redirectToPdfViewer(
                        pdfFile = pdfFile,
                        description = String.format(
                            "%s - %s",
                            binding.periodFilterMinimumPaymentMonthLayout.dateValue ?: "",
                            binding.periodFilterMaximumPaymentMonthLayout.dateValue ?: ""
                        )
                    )
                }
                context?.message(
                    message = resources.getString(R.string.key_file_saved_message)
                )
            }
        }
    }

    private fun redirectToPdfViewer(pdfFile: File, description: String) {
        findNavController().navigate(
            PeriodFilterFragmentDirections.actionPeriodFilterFragmentToPdfViewerFragment(
                fileDataModel = FileDataModel(
                    description = description,
                    fileName = pdfFile.name,
                    dateTime = Date().pdfFormattedCurrentDate
                ).also {
                    it.fileData = pdfFile.readBytes()
                }
            )
        )
    }

}
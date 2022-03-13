package com.agelousis.payments.main.ui.periodFilter

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.periodFilter.presenter.PeriodFilterFragmentPresenter
import com.agelousis.payments.main.ui.periodFilter.ui.PeriodFilterLayout
import com.agelousis.payments.main.ui.periodFilter.viewModel.PeriodFilterViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.helpers.PDFHelper
import com.agelousis.payments.utils.helpers.PaymentCsvHelper
import com.agelousis.payments.views.dateLayout.YearMonthPickerBottomSheetFragment
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerListener
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PeriodFilterFragment: Fragment(), PeriodFilterFragmentPresenter {

    override fun onPdfInvoice() {
        val minimumMonthDate = viewModel.periodFilterMinimumPaymentMonthDate?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val maximumMonthDate = viewModel.periodFilterMaximumPaymentMonthDate?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        initializePDFCreation(
            payments = args.paymentListData.filter { it.paymentMonthDate ?: Date() in minimumMonthDate..maximumMonthDate }.sortedBy { it.paymentMonthDate }
        )
    }

    private val args: PeriodFilterFragmentArgs by navArgs()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<PeriodFilterViewModel>()

    private fun onPeriodFilterMinimumPaymentMonthDate() {
        YearMonthPickerBottomSheetFragment.show(
            supportFragmentManager = childFragmentManager,
            yearMonthPickerDataModel = YearMonthPickerDataModel(
                calendar = Date().calendar,
                yearMonthPickerListener = object: YearMonthPickerListener {
                    override fun onYearMonthSet(year: Int, month: Int) {
                        viewModel.periodFilterMinimumPaymentMonthDate = String.format(
                            "%s %s",
                            resources.getStringArray(R.array.key_months_array).getOrNull(index = month) ?: "",
                            year
                        )
                    }
                }
            )
        )
    }

    private fun onPeriodFilterMaximumPaymentMonthDate() {
        YearMonthPickerBottomSheetFragment.show(
            supportFragmentManager = childFragmentManager,
            yearMonthPickerDataModel = YearMonthPickerDataModel(
                calendar = Date().calendar,
                yearMonthPickerListener = object: YearMonthPickerListener {
                    override fun onYearMonthSet(year: Int, month: Int) {
                        viewModel.periodFilterMaximumPaymentMonthDate = String.format(
                            "%s %s",
                            resources.getStringArray(R.array.key_months_array).getOrNull(index = month) ?: "",
                            year
                        )
                    }
                }
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                setContent {
                    MaterialTheme(
                        typography = Typography,
                        colors = appColors()
                    ) {
                        setupData()
                        PeriodFilterLayout(
                            viewModel = viewModel,
                            periodFilterMinimumPaymentMonthDateBlock = this@PeriodFilterFragment::onPeriodFilterMinimumPaymentMonthDate,
                            periodFilterMaximumPaymentMonthDateBlock = this@PeriodFilterFragment::onPeriodFilterMaximumPaymentMonthDate,
                            periodFilterFragmentPresenter = this@PeriodFilterFragment
                        )
                    }
                }
            }
        }
    }

    private fun setupData() {
        viewModel.periodFilterMinimumPaymentMonthDate = args.periodFilterData?.minimumMonthDateValue
        viewModel.periodFilterMaximumPaymentMonthDate = args.periodFilterData?.maximumMonthDateValue
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun PeriodFilterFragmentPreview() {
        PeriodFilterLayout(
            viewModel = viewModel,
            periodFilterMinimumPaymentMonthDateBlock = {},
            periodFilterMaximumPaymentMonthDateBlock = {},
            periodFilterFragmentPresenter = this@PeriodFilterFragment
        )
    }

    fun initializeExportToExcelOperation() {
        (activity as? MainActivity)?.floatingButtonState = false
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

    private fun triggerCsvCreation(uri: Uri) {
        val minimumMonthDate = viewModel.periodFilterMinimumPaymentMonthDate?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
        val maximumMonthDate = viewModel.periodFilterMaximumPaymentMonthDate?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US) ?: return
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
            clients = listOf(
                ClientModel(
                    payments = payments
                )
            ),
        ) { pdfFile ->
            uiScope.launch {
                viewModel.insertFile(
                    userModel = (activity as? MainActivity)?.userModel,
                    file = pdfFile,
                    description = String.format(
                        "%s - %s",
                        viewModel.periodFilterMinimumPaymentMonthDate ?: "",
                        viewModel.periodFilterMaximumPaymentMonthDate ?: ""
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
                            viewModel.periodFilterMinimumPaymentMonthDate ?: "",
                            viewModel.periodFilterMaximumPaymentMonthDate ?: ""
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
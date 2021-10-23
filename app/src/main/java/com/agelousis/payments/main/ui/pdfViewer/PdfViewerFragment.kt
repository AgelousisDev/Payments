package com.agelousis.payments.main.ui.pdfViewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseBindingFragment
import com.agelousis.payments.databinding.PdfViewerFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.pdfViewer.presenters.PdfViewerPresenter
import com.agelousis.payments.utils.extensions.sharePDF
import com.agelousis.payments.utils.extensions.showSimpleDialog
import com.agelousis.payments.utils.extensions.uiMode
import com.agelousis.payments.utils.helpers.PrinterHelper
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import java.io.File
import java.io.IOException

class PdfViewerFragment: BaseBindingFragment<PdfViewerFragmentLayoutBinding>(
    inflate = PdfViewerFragmentLayoutBinding::inflate
), PdfViewerPresenter {

    private val args: PdfViewerFragmentArgs by navArgs()
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageNumber = 0

    override fun onBindData(binding: PdfViewerFragmentLayoutBinding?) {
        super.onBindData(binding)
        binding?.uiMode = context?.uiMode
        binding?.presenter = this
    }

    override fun onPrint() {
        PrinterHelper.initialize(
            context = context ?: return,
            document = File(
                context?.filesDir ?: return,
                args.fileDataModel.fileName ?: return
            )
        )
    }

    override fun onPreviousPage() {
        showPage(
            index = (currentPage?.index ?: return) - 1
        )
    }

    override fun onNextPage() {
        showPage(
            index = (currentPage?.index ?: return) + 1
        )
    }

    override fun onStart() {
        super.onStart()
        try {
            openRenderer()
            showPage(
                index = currentPageNumber
            )
        } catch (ioException: IOException) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAppBarTitle()
    }

    private fun configureAppBarTitle() {
        (activity as? MainActivity)?.appBarTitle = args.fileDataModel.description ?: return
    }

    @Throws(IOException::class)
    private fun openRenderer() {
        if (context == null) return
        val fileDescriptor = context?.contentResolver?.openFileDescriptor(
            File(
                context?.filesDir ?: return,
                args.fileDataModel.fileName ?: return
            ).toUri(), "r") ?: return

        // This is the PdfRenderer we use to render the PDF.
        pdfRenderer = PdfRenderer(fileDescriptor)
        currentPage = pdfRenderer?.openPage(currentPageNumber)
    }

    private fun showPage(index: Int) {
        if (index < 0 || index >= (pdfRenderer?.pageCount ?: 0))
            return

        currentPage?.close()
        currentPage = pdfRenderer?.openPage(index)

        // Important: the destination bitmap must be ARGB (not RGB).
        val bitmap = Bitmap.createBitmap((currentPage?.width ?: return) * 2, (currentPage?.height ?: return) * 2, Bitmap.Config.ARGB_8888)

        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        binding?.pdfView?.setImageBitmap(bitmap)

        val pageCount = pdfRenderer?.pageCount
        binding?.previousButtonState = index != 0
        binding?.nextButtonState = index + 1 < (pageCount ?: 0)
    }

    @Throws(IOException::class)
    private fun closeRenderer() {
        currentPage?.close()
        pdfRenderer?.close()
    }

    fun sharePDF() {
        File(context?.filesDir ?: return, args.fileDataModel.fileName ?: return).takeIf {
            it.exists()
        }?.let {
            context?.sharePDF(
                pdfFile = it
            )
        } ?: context?.showSimpleDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_warning_label),
                message = resources.getString(R.string.key_file_not_exists_message)
            )
        )
    }

    override fun onStop() {
        super.onStop()
        try {
            closeRenderer()
        } catch (ioException: IOException) {}
    }

}
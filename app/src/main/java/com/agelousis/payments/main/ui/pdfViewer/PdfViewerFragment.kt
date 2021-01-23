package com.agelousis.payments.main.ui.pdfViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PdfViewerFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.pdfViewer.presenters.PdfViewerPresenter
import com.agelousis.payments.utils.extensions.sharePDF
import com.agelousis.payments.utils.extensions.showSimpleDialog
import com.agelousis.payments.utils.helpers.PrinterHelper
import java.io.File

class PdfViewerFragment: Fragment(), PdfViewerPresenter {

    private lateinit var binding: PdfViewerFragmentLayoutBinding
    private val args: PdfViewerFragmentArgs by navArgs()

    override fun onPrint() {
        PrinterHelper.initialize(
            context = context ?: return,
            document = File(
                context?.filesDir ?: return,
                args.fileDataModel.fileName ?: return
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PdfViewerFragmentLayoutBinding.inflate(inflater, container, false).also {
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAppBarTitle()
        configurePDF()
    }

    private fun configureAppBarTitle() {
        (activity as? MainActivity)?.appBarTitle = args.fileDataModel.description ?: return
    }

    private fun configurePDF() {
        binding.pdfView.fromFile(
            File(
                context?.filesDir ?: return,
                args.fileDataModel.fileName ?: return
            )
        )?.load()
    }

    fun sharePDF() {
        File(context?.filesDir ?: return, args.fileDataModel.fileName ?: return).takeIf {
            it.exists()
        }?.let {
            context?.sharePDF(
                pdfFile = it
            )
        } ?: context?.showSimpleDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_file_not_exists_message)
        )
    }

}
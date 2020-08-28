package com.agelousis.monthlyfees.utils.helpers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.pdfFormattedCurrentDate
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.util.*

typealias PDFInitializationSuccessBlock = (path: String) -> Unit
class PDFHelper {

    companion object {
        val shared = PDFHelper()
    }

    private val ubuntuFont by lazy { BaseFont.createFont("assets/fonts/ubuntu_regular.ttf", BaseFont.IDENTITY_H, true) }

    fun initializePDF(context: Context, userModel: UserModel?, payments: List<PersonModel>, pdfInitializationSuccessBlock: PDFInitializationSuccessBlock) {
        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream("${context.filesDir.absolutePath}/${String.format(Constants.PDF_FILE_NAME_FORMAT_VALUE, Date().pdfFormattedCurrentDate)}"))
        document.open()

        addHeaderImage(
            context = context,
            document = document,
            userModel = userModel,
        )
        /*document.add(
            Paragraph(
            context.resources.getString(R.string.key_this_month_label),
            Font(ubuntuFont, 16.0f, Font.BOLD)
        ).also {
            it.alignment = Element.ALIGN_CENTER
        })*/

        /*addUserDetailsContent(
            document = document,
            serviceEntryRequestModel = serviceEntryRequestModel
        )

        document.add(Chunk.NEWLINE)

        document.add(Paragraph(
            context.resources.getString(R.string.key_tools_label),
            Font(ubuntuFont, 14.0f, Font.BOLD)
        ).also {
            it.alignment = Element.ALIGN_CENTER
        })

        addToolsContent(
            document = document,
            serviceEntryRequestModel = serviceEntryRequestModel
        )

        addTotalAmount(
            document = document,
            serviceEntryRequestModel = serviceEntryRequestModel
        )

        addRemarks(
            document = document,
            serviceEntryRequestModel = serviceEntryRequestModel
        )*/

        document.close()
        pdfInitializationSuccessBlock("${context.filesDir.absolutePath}/sample.pdf")
    }

    private fun addHeaderImage(context: Context, document: Document, userModel: UserModel?) {
        val imageByteArray = context.contentResolver.openInputStream(Uri.fromFile(File(context.filesDir, userModel?.profileImage ?: return)))?.readBytes() ?: return
        document.add(
            Chunk(Image.getInstance(imageByteArray), 10.0f, 10.0f)
        )
    }

}
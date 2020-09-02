package com.agelousis.monthlyfees.utils.helpers

import android.content.Context
import android.net.Uri
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.euroFormattedString
import com.agelousis.monthlyfees.utils.extensions.invoiceNumber
import com.agelousis.monthlyfees.utils.extensions.pdfFormattedCurrentDate
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream
import java.util.*

typealias PDFInitializationSuccessBlock = (pdfFile: File) -> Unit
class PDFHelper {

    companion object {
        val shared = PDFHelper()
    }

    private val ubuntuFont by lazy { BaseFont.createFont("assets/fonts/ubuntu_regular.ttf", BaseFont.IDENTITY_H, true) }

    fun initializePDF(context: Context, userModel: UserModel?, persons: List<PersonModel>, pdfInitializationSuccessBlock: PDFInitializationSuccessBlock) {
        val document = Document()
        val pdfFile = File(context.filesDir, String.format(Constants.PDF_FILE_NAME_FORMAT_VALUE, Date().pdfFormattedCurrentDate))
        PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()
        addHeaderImage(
            context = context,
            document = document,
            userModel = userModel,
        )
        addUserDetails(
            context = context,
            document = document,
            userModel = userModel
        )
        document.add(Chunk.NEWLINE)
        document.add(
            Paragraph(
                context.resources.getString(R.string.key_payments_label),
                Font(ubuntuFont, 16.0f, Font.BOLD)
            ).also {
                it.alignment = Element.ALIGN_CENTER
            })
        document.add(Chunk.NEWLINE)
        addPersons(
            context = context,
            document = document,
            persons = persons
        )
        document.close()

        pdfInitializationSuccessBlock(pdfFile)
    }

    private fun addUserDetails(context: Context, document: Document, userModel: UserModel?) {
        String.format("%s %s", userModel?.firstName ?: "", userModel?.lastName ?: "").takeIf { it.isNotEmpty() && it.isNotBlank() }?.let { fullName->
            document.add(
                Paragraph(
                    "${System.lineSeparator()}$fullName",
                    Font(ubuntuFont, 8.0f, Font.BOLD)
                )
            )
        }
        userModel?.address?.takeIf { it.isNotEmpty() }?.let {
            document.add(
                Paragraph(
                    it,
                    Font(ubuntuFont, 8.0f, Font.BOLD)
                )
            )
        }
        userModel?.idCardNumber?.takeIf { it.isNotEmpty() }?.let {
            document.add(
                Paragraph(
                    "${context.resources.getString(R.string.key_id_card_number_label)}: $it",
                    Font(ubuntuFont, 8.0f, Font.BOLD)
                )
            )
        }
        userModel?.socialInsuranceNumber?.takeIf { it.isNotEmpty() }?.let {
            document.add(
                Paragraph(
                    "${context.resources.getString(R.string.key_social_insurance_number_label)}: $it",
                    Font(ubuntuFont, 8.0f, Font.BOLD)
                )
            )
        }
    }

    private fun addHeaderImage(context: Context, document: Document, userModel: UserModel?) {
        val imageByteArray = context.contentResolver.openInputStream(Uri.fromFile(File(context.filesDir, userModel?.profileImage ?: return)))?.readBytes() ?: return
        val image = Image.getInstance(imageByteArray)
        image.scaleAbsolute(60.0f, 60.0f)
        document.add(Paragraph().also {
            it.add(image)
            it.alignment = Element.ALIGN_CENTER
        })
    }

    private fun addPersons(context: Context, document: Document, persons: List<PersonModel>) {
        persons.forEachIndexed { index, personModel ->
            val table = PdfPTable(3)
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_group_label)}: ${personModel.groupName ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_first_name_label)}: ${personModel.firstName ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_surname_label)}: ${personModel.surname ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_phone_label)}: ${personModel.phone ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_parent_name_label)}: ${personModel.parentName ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_parent_phone_label)}: ${personModel.parentPhone ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_email_label)}: ${personModel.email ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_is_active_label)}: ${if (personModel.active == true) context.resources.getString(R.string.key_yes_label) else context.resources.getString(R.string.key_no_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_is_free_label)}: ${if (personModel.free == true) context.resources.getString(R.string.key_yes_label) else context.resources.getString(R.string.key_no_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_total_payments_label)}: ${personModel.totalPaymentAmount.euroFormattedString ?: context.resources.getString(R.string.key_no_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_invoice_number_label)}: ${personModel.paymentId.invoiceNumber ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = false
                )
            )
            table.addCell(
                getCell(
                    text = "",
                    withBorder = false
                )
            )
            document.add(table)

            document.add(Chunk.NEWLINE)

            addPayments(
                context = context,
                document = document,
                payments = personModel.payments
            )

            document.add(Chunk.NEWLINE)
            if (index < persons.size - 1)
                document.add(Chunk(LineSeparator()))
        }
    }

    private fun addPayments(context: Context, document: Document, payments: List<PaymentAmountModel>?) {
        payments?.forEach { paymentAmountModel ->
            val table = PdfPTable(3)
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_amount_label)}: ${paymentAmountModel.paymentAmount?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = true
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_start_date_label)}: ${paymentAmountModel.startDate ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = true
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_end_date_label)}: ${paymentAmountModel.endDate ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = true
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_execution_date_label)}: ${paymentAmountModel.paymentDate ?: context.resources.getString(R.string.key_empty_field_label)}",
                    withBorder = true
                )
            )
            table.addCell(
                getCell(
                    text = "${context.resources.getString(R.string.key_skip_payment_label)}: ${if (paymentAmountModel.skipPayment == true) context.resources.getString(R.string.key_yes_label) else context.resources.getString(R.string.key_no_label)}",
                    withBorder = true
                )
            )
            table.addCell(
                getCell(
                    text = "",
                    withBorder = true
                )
            )
            document.add(table)
            document.add(Chunk.NEWLINE)
        }
    }

    private fun getCell(text: String, withBorder: Boolean = true) =
        PdfPCell().also {
            if (!withBorder) it.border = 0
            it.addElement(Phrase(text).also { phrase ->
                phrase.font = Font(ubuntuFont, 9.0f, Font.NORMAL)
            })
        }

}
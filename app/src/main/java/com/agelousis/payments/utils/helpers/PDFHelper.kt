package com.agelousis.payments.utils.helpers

import android.content.Context
import android.net.Uri
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
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
    private val blueColor by lazy { BaseColor(40, 53, 146) }
    private val lightGreyColor by lazy { BaseColor(243, 243, 243) }

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
                context.resources.getString(R.string.key_invoice_label),
                Font(ubuntuFont, 16.0f, Font.BOLD, blueColor)
            ).also {
                it.alignment = Element.ALIGN_LEFT
            })
        document.add(Chunk.NEWLINE)
        addPersons(
            context = context,
            userModel = userModel,
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

    private fun addPersons(context: Context, userModel: UserModel?, document: Document, persons: List<PersonModel>) {
        persons.forEach { personModel ->
            document.add(
                Paragraph(
                    "${personModel.fullName}\n${context.resources.getString(R.string.key_phone_label)}: ${personModel.phone}",
                    Font(ubuntuFont, 14.0f, Font.BOLD, BaseColor.GRAY)
                ).also {
                    it.alignment = Element.ALIGN_LEFT
                })

            document.add(Chunk.NEWLINE)
            document.add(Chunk(LineSeparator()))

            addPayments(
                context = context,
                document = document,
                personModel = personModel
            )

            document.add(Chunk.NEWLINE)

            addPaymentFooter(
                context = context,
                document = document,
                personModel = personModel,
                userModel = userModel
            )

            document.add(Chunk.NEWLINE)
        }
    }

    private fun addPayments(context: Context, document: Document, personModel: PersonModel) {
        val table = PdfPTable(6)
        addPaymentHeaderCells(
            context = context,
            table = table
        )
        personModel.payments?.forEachIndexed { index, paymentAmountModel ->
            table.addCell(
                getCell(
                    text = (index + 1).toString(),
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
            table.addCell(
                getCell(
                    text = paymentAmountModel.paymentMonth ?: context.resources.getString(R.string.key_empty_field_label),
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
            table.addCell(
                getCell(
                    text = paymentAmountModel.paymentDate ?: context.resources.getString(R.string.key_empty_field_label),
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
            table.addCell(
                getCell(
                    text = personModel.personId.invoiceNumber?.let { String.format("%s-%d", it, paymentAmountModel.paymentId ?: 0) } ?: context.resources.getString(R.string.key_empty_field_label) ,
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
            table.addCell(
                getCell(
                    text = paymentAmountModel.paymentNote ?: context.resources.getString(R.string.key_empty_field_label),
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
            table.addCell(
                getCell(
                    text = if (!paymentAmountModel.paymentAmount.isZero) paymentAmountModel.paymentAmount?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label) else String.format("${MainApplication.currencySymbol ?: "€"}%s", "0"),
                    withBorder = false,
                    textColor = BaseColor.GRAY,
                    backgroundColor = if (index % 2 == 0) BaseColor.WHITE else lightGreyColor
                )
            )
        }
        document.add(table)
    }

    private fun addPaymentHeaderCells(context: Context, table: PdfPTable) {
        table.addCell(
            getCell(
                text = "",
                withBorder = false,
                textSize = 12.0f
            )
        )
        table.addCell(
            getCell(
                text = context.resources.getString(R.string.key_payment_month_label),
                withBorder = false,
                textSize = 12.0f,
                textColor = blueColor
            )
        )
        table.addCell(
            getCell(
                text = context.resources.getString(R.string.key_execution_date_label),
                withBorder = false,
                textSize = 12.0f,
                textColor = blueColor
            )
        )
        table.addCell(
            getCell(
                text = context.resources.getString(R.string.key_invoice_number_label),
                withBorder = false,
                textSize = 12.0f,
                textColor = blueColor
            )
        )
        table.addCell(
            getCell(
                text = context.resources.getString(R.string.key_note_label),
                withBorder = false,
                textSize = 12.0f,
                textColor = blueColor
            )
        )
        table.addCell(
            getCell(
                text = context.resources.getString(R.string.key_total_price_label),
                withBorder = false,
                textSize = 12.0f,
                textColor = blueColor
            )
        )
    }

    private fun addPaymentFooter(context: Context, document: Document, personModel: PersonModel, userModel: UserModel?) {
        document.add(
            Paragraph(
                "${context.resources.getString(R.string.key_subtotal_label)}          ${personModel.totalPaymentAmount?.getAmountWithoutVat(vat = userModel?.vat)?.takeIf { !it.isZero }?.let { it.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label) } ?: String.format("${MainApplication.currencySymbol ?: "€"}%s", "0")}",
                Font(ubuntuFont, 14.0f, Font.BOLD, blueColor)
            ).also {
                it.alignment = Element.ALIGN_RIGHT
            })
        document.add(
            Paragraph(
                "${context.resources.getString(R.string.key_vat_label)}                  ${personModel.totalPaymentAmount?.getVatAmount(vat = userModel?.vat)?.takeIf { !it.isZero }?.let { it.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label) } ?: String.format("${MainApplication.currencySymbol ?: "€"}%s", "0")}",
                Font(ubuntuFont, 14.0f, Font.BOLD, blueColor)
            ).also {
                it.alignment = Element.ALIGN_RIGHT
            })
        document.add(
            Paragraph(
                personModel.totalPaymentAmount?.takeIf { !it.isZero }?.let { it.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label) } ?: String.format("${MainApplication.currencySymbol ?: "€"}%s", "0"),
                Font(ubuntuFont, 20.0f, Font.BOLD, blueColor)
            ).also {
                it.alignment = Element.ALIGN_RIGHT
            })
    }

    private fun getCell(text: String, withBorder: Boolean = true, textSize: Float = 9.0f, textColor: BaseColor = BaseColor.BLACK, backgroundColor: BaseColor = BaseColor.WHITE) =
        PdfPCell().also {
            if (!withBorder) it.border = 0
            it.addElement(
                Phrase(text).also { phrase ->
                    phrase.font = Font(ubuntuFont, textSize, Font.NORMAL, textColor)
                }
            )
            it.verticalAlignment = Element.ALIGN_CENTER
            it.horizontalAlignment = Element.ALIGN_CENTER
            it.backgroundColor = backgroundColor
        }

}
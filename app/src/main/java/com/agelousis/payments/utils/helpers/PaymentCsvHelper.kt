package com.agelousis.payments.utils.helpers

import android.content.Context
import android.net.Uri
import com.agelousis.payments.R
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.euroFormattedString
import java.io.FileWriter

typealias CsvClosure = () -> Unit

object PaymentCsvHelper {

    fun createPaymentsCsv(context: Context, uri: Uri, payments: List<PaymentAmountModel>, csvClosure: CsvClosure) {
        context.contentResolver?.openFileDescriptor(uri, "w")?.use {
            val fileWriter = FileWriter(it.fileDescriptor)
            fileWriter.append(
                String.format(
                    "%s,%s,%s,%s,%s",
                    SQLiteHelper.ID,
                    SQLiteHelper.PAYMENT_AMOUNT,
                    SQLiteHelper.PAYMENT_MONTH,
                    SQLiteHelper.PAYMENT_DATE,
                    SQLiteHelper.PAYMENT_NOTE
                )
            )
            fileWriter.append("\n")

            payments.forEachIndexed { index, payment ->
                fileWriter.append((index + 1).toString())
                fileWriter.append(",")
                fileWriter.append(payment.paymentAmount?.euroFormattedString?.replace(",", ".") ?: context.resources.getString(R.string.key_empty_field_label))
                fileWriter.append(",")
                fileWriter.append(payment.paymentMonth ?: context.resources.getString(R.string.key_empty_field_label))
                fileWriter.append(",")
                fileWriter.append(payment.paymentDate ?: context.resources.getString(R.string.key_empty_field_label))
                fileWriter.append(",")
                fileWriter.append(payment.paymentNote?.replace(",", ".") ?: context.resources.getString(R.string.key_empty_field_label))
                fileWriter.append("\n")
            }

            fileWriter.flush()
            fileWriter.close()
            csvClosure()
        }
    }

}
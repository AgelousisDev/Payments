package com.agelousis.payments.utils.helpers

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import com.agelousis.payments.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object PrinterHelper {

    fun initialize(context: Context, document: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "${context.resources.getString(R.string.app_name)} ${document.name}"
        printManager.print(
            jobName,
            this createAdapterFrom document,
            null
        )
    }

    private infix fun createAdapterFrom(document: File) =
        object: PrintDocumentAdapter() {
            override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?, cancellationSignal: CancellationSignal?, callback: WriteResultCallback?) {
                FileInputStream(document).use { fileInputStream ->
                    FileOutputStream(destination?.fileDescriptor).use { fileOutputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        do {
                            bytesRead = fileInputStream.read(buffer)
                            if (bytesRead ==  -1)
                                break
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        while(bytesRead > 0)
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    }
                }
            }

            override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback?, extras: Bundle?) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val pdi = PrintDocumentInfo.Builder(document.name).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()
                callback?.onLayoutFinished(pdi, true)
            }
        }
}
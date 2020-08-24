package com.agelousis.monthlyfees.utils.helpers

import android.content.Context
import com.agelousis.monthlyfees.utils.constants.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

typealias CSVInitializationSuccessBlock = () -> Unit

object CSVHelper {

    suspend fun initialize(context: Context, csvInitializationSuccessBlock: CSVInitializationSuccessBlock) {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, Constants.CSV_FILE)
            if (!file.exists())
                file.createNewFile()
            //val csvWriter = CSVF
        }
    }

}
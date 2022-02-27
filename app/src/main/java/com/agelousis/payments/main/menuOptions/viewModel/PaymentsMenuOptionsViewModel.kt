package com.agelousis.payments.main.menuOptions.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.ClientModel

class PaymentsMenuOptionsViewModel: ViewModel() {
    var optionsList by mutableStateOf(
        emptyList<Any>()
    )

    fun setupList(
        context: Context,
        paymentsFragment: PaymentsFragment
    ) {
        val clientModelList = paymentsFragment.filteredList.filterIsInstance<ClientModel>()
        optionsList = listOf(
            HeaderModel(
                dateTime = null,
                header = context.resources.getString(R.string.key_options_label),
                headerBackgroundColor = ContextCompat.getColor(context, android.R.color.transparent)
            ),
            PaymentsMenuOptionType.CLIENTS_ORDER.also { paymentsMenuOptionType ->
                paymentsMenuOptionType.isEnabled = clientModelList.isNotEmpty()
            },
            PaymentsMenuOptionType.CLEAR_CLIENTS.also { paymentsMenuOptionType ->
                paymentsMenuOptionType.isEnabled = clientModelList.isNotEmpty()
            },
            PaymentsMenuOptionType.CSV_EXPORT.also { paymentsMenuOptionType ->
                paymentsMenuOptionType.isEnabled = clientModelList.mapNotNull { it.payments }.flatten().isNotEmpty()
            },
            PaymentsMenuOptionType.SEND_SMS_GLOBALLY.also { paymentsMenuOptionType ->
                paymentsMenuOptionType.isEnabled = clientModelList.mapNotNull { it.phone }.isNotEmpty()
            },
            PaymentsMenuOptionType.QR_CODE_GENERATOR.also { paymentsMenuOptionType ->
                paymentsMenuOptionType.isEnabled = MainApplication.firebaseToken != null
            },
            PaymentsMenuOptionType.SCAN_QR_CODE.also {
                it.isEnabled = paymentsFragment.filteredList.filterIsInstance<ClientModel>()
                    .takeWhile { clientModel -> clientModel.isSelected }.isNotEmpty() == true
            }
        )
    }

}
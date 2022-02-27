package com.agelousis.payments.main.menuOptions.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.menuOptions.viewModel.PaymentsMenuOptionsViewModel
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.ui.BottomSheetNavigationLine
import com.agelousis.payments.ui.rows.HeaderRowLayout
import com.agelousis.payments.ui.rows.PaymentsMenuOptionRowLayout

@Composable
fun PaymentsMenuOptionsLayout(
    viewModel: PaymentsMenuOptionsViewModel,
    presenter: PaymentsMenuOptionPresenter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetNavigationLine()
        LazyColumn {
            items(
                items = viewModel.optionsList
            ) { optionItem ->
                when(optionItem) {
                    is HeaderModel ->
                        HeaderRowLayout(
                            headerModel = optionItem
                        )
                    is PaymentsMenuOptionType ->
                        PaymentsMenuOptionRowLayout(
                            paymentsMenuOptionType = optionItem,
                            presenter = presenter
                        )
                }
                if (optionItem !is HeaderModel
                    && optionItem != PaymentsMenuOptionType.SCAN_QR_CODE
                )
                    Divider()
            }
        }
    }
}
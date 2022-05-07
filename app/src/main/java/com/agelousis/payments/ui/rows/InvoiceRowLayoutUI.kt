package com.agelousis.payments.ui.rows

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.files.enumerations.InvoiceRowState
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.main.ui.files.viewModel.InvoicesViewModel
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun InvoiceRowLayout(
    invoiceDataModel: InvoiceDataModel,
    viewModel: InvoicesViewModel,
    modifier: Modifier = Modifier
) {
    var invoiceRowState by remember {
        mutableStateOf(value = InvoiceRowState.NORMAL)
    }
    if (viewModel.selectedInvoiceModelList.isEmpty())
        invoiceRowState = InvoiceRowState.NORMAL
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true
                ),
                onLongClick = {
                    invoiceRowState = invoiceRowState.other
                    when(invoiceRowState) {
                        InvoiceRowState.NORMAL ->
                            viewModel.selectedInvoiceModelList.remove(invoiceDataModel)
                        InvoiceRowState.SELECTED ->
                            viewModel.selectedInvoiceModelList.add(invoiceDataModel)
                    }
                },
                onClick = {
                    if (viewModel.selectedInvoiceModelList.isNotEmpty()) {
                        invoiceRowState = invoiceRowState.other
                        when(invoiceRowState) {
                            InvoiceRowState.NORMAL ->
                                viewModel.selectedInvoiceModelList.remove(invoiceDataModel)
                            InvoiceRowState.SELECTED ->
                                viewModel.selectedInvoiceModelList.add(invoiceDataModel)
                        }
                    }
                    else
                        viewModel onInvoiceDataModel invoiceDataModel
                }
            ),
        shape = RoundedCornerShape(
            size = 16.dp
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = invoiceRowState.backgroundColor)
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_colored_pdf),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        size = 40.dp
                    )
                    .padding(
                        top = 16.dp
                    )
            )
            Text(
                text = invoiceDataModel.description ?: "",
                style = textViewTitleLabelFont,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp
                    )
            )
            Text(
                text = invoiceDataModel.showingDate ?: "",
                style = textViewLabelFont,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            )
        }
    }
}

@Preview
@Composable
fun FileRowLayoutPreview() {
    InvoiceRowLayout(
        invoiceDataModel = InvoiceDataModel(
            description = "Sample Invoice",
            fileName = "sample_invoice.pdf",
            dateTime = "2022_10_11_13_30_00"
        ),
        viewModel = InvoicesViewModel()
    )
}
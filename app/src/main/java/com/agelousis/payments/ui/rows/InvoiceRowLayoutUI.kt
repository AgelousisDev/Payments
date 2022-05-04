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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.files.enumerations.InvoiceRowState
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.main.ui.files.presenter.InvoicePresenter
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun InvoiceRowLayout(
    invoiceDataModel: InvoiceDataModel,
    invoicePresenter: InvoicePresenter
) {
    var invoiceRowState by remember {
        mutableStateOf(value = InvoiceRowState.NORMAL)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    radius = 16.dp
                ),
                onLongClick = {
                    invoiceRowState = invoiceRowState.other
                    invoicePresenter.onInvoiceLongPressed(
                        adapterPosition = 0
                    )
                },
                onClick = {
                    invoicePresenter.onInvoiceSelected(
                        invoiceDataModel = invoiceDataModel,
                        0
                    )
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
        invoicePresenter = object: InvoicePresenter {}
    )
}
package com.agelousis.payments.main.ui.totalPaymentsAmount.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.ui.textViewTitleFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun TotalPaymentAmountLayoutWith(
    paymentAmountSumModel: PaymentAmountSumModel?,
    vat: Int?
) {
    paymentAmountSumModel ?: return
    vat ?: return
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.colorAccent),
                    shape = RoundedCornerShape(
                        size = 16.dp
                    )
                )
                .height(
                    height = 80.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_payment
                ),
                contentDescription = null,
                modifier = Modifier.size(
                    size = 42.dp
                ),
                colorFilter = ColorFilter.tint(
                    color = colorResource(
                        id = R.color.white
                    )
                )
            )
            Text(
                text = stringResource(
                    id = R.string.key_total_payments_label
                ),
                style = textViewTitleFont,
                color = colorResource(
                    id = R.color.white
                ),
                modifier = Modifier.padding(
                    start = 16.dp
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = paymentAmountSumModel.getFormattedPaymentAmountWithoutVat(
                    context = context,
                    vat = vat
                ),
                style = textViewTitleLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier.padding(
                    top = 8.dp
                )
            )
            Text(
                text = paymentAmountSumModel.getFormattedPaymentVatAmount(
                    context = context,
                    vat = vat
                ),
                style = textViewTitleLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier.padding(
                    top = 8.dp
                )
            )
            Text(
                text = paymentAmountSumModel.getFormattedPaymentsSum(
                    context = context
                ),
                style = textViewTitleFont,
                color = paymentAmountSumModel.color?.let {
                    Color(paymentAmountSumModel.color)
                } ?: colorResource(
                    id = R.color.colorAccent
                ),
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 16.dp
                )
            )
        }
    }
}
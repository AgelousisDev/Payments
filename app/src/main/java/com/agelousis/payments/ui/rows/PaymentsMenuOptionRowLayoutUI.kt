package com.agelousis.payments.ui.rows

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.agelousis.payments.R
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun PaymentsMenuOptionRowLayout(
    paymentsMenuOptionType: PaymentsMenuOptionType,
    presenter: PaymentsMenuOptionPresenter
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(
                alpha = if (paymentsMenuOptionType.isEnabled) 1.0f else 0.5f
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                enabled = paymentsMenuOptionType.isEnabled,
            ) {
                when (paymentsMenuOptionType) {
                    PaymentsMenuOptionType.CLEAR_CLIENTS -> presenter.onClearPayments()
                    PaymentsMenuOptionType.CSV_EXPORT -> presenter.onCsvExport()
                    PaymentsMenuOptionType.SEND_SMS_GLOBALLY -> presenter.onSendSmsGlobally()
                    PaymentsMenuOptionType.CLIENTS_ORDER -> presenter.onPaymentsOrder()
                    PaymentsMenuOptionType.QR_CODE_GENERATOR -> presenter.onQrCode(
                        QRCodeSelectionType.GENERATE
                    )
                    PaymentsMenuOptionType.SCAN_QR_CODE -> presenter.onQrCode(QRCodeSelectionType.SCAN)
                }
            }
    ) {
        val (paymentsMenuIconConstrainedReference, paymentsMenuLabelConstrainedReference, paymentsMenuSubtitleConstrainedReference)
                = createRefs()
        Image(
            painter = painterResource(
                id = paymentsMenuOptionType.icon
            ),
            contentDescription = null,
            modifier = Modifier
                .size(
                    width = 24.dp,
                    height = 24.dp
                )
                .constrainAs(paymentsMenuIconConstrainedReference) {
                    top.linkTo(parent.top, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                    if (paymentsMenuOptionType.subtitle == null)
                        bottom.linkTo(
                            parent.bottom,
                            16.dp
                        )
                }
        )
        Text(
            text = stringResource(
                id = paymentsMenuOptionType.title
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            ),
            modifier = Modifier
                .constrainAs(paymentsMenuLabelConstrainedReference) {
                    top.linkTo(paymentsMenuIconConstrainedReference.top)
                    start.linkTo(paymentsMenuIconConstrainedReference.end, 16.dp)
                    bottom.linkTo(paymentsMenuIconConstrainedReference.bottom)
                }
        )
        if (paymentsMenuOptionType.subtitle != null)
            Text(
                text = stringResource(
                    id = paymentsMenuOptionType.subtitle ?: return@ConstraintLayout
                ),
                style = textViewLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier
                    .constrainAs(paymentsMenuSubtitleConstrainedReference) {
                        width = Dimension.fillToConstraints
                        top.linkTo(paymentsMenuLabelConstrainedReference.bottom, 8.dp)
                        start.linkTo(paymentsMenuIconConstrainedReference.end, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
            )
    }
}
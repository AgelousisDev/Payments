package com.agelousis.payments.main.ui.periodFilter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.*
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.periodFilter.presenter.PeriodFilterFragmentPresenter
import com.agelousis.payments.main.ui.periodFilter.viewModel.PeriodFilterViewModel
import com.agelousis.payments.compose.BasicButton
import com.agelousis.payments.compose.textViewTitleLabelFont
import com.agelousis.payments.views.dateLayout.YearMonthPickerFieldLayout
import com.agelousis.payments.views.extensions.setMonthYearValue
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel

typealias PeriodFilterMinimumPaymentMonthDateBlock = () -> Unit

@Composable
fun PeriodFilterLayout(
    viewModel: PeriodFilterViewModel,
    periodFilterMinimumPaymentMonthDateBlock: PeriodFilterMinimumPaymentMonthDateBlock,
    periodFilterMaximumPaymentMonthDateBlock: PeriodFilterMinimumPaymentMonthDateBlock,
    periodFilterFragmentPresenter: PeriodFilterFragmentPresenter,
) {
    Column {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset("date_animation.json")
        )
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
            restartOnPlay = false
        )
        LottieAnimation(
            composition = composition,
            progress = {
                       progress
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = 170.dp
                )
        )
        Text(
            text = stringResource(
                id = R.string.key_from_label
            ),
            color = colorResource(
                id = R.color.grey
            ),
            style = textViewTitleLabelFont,
            modifier = Modifier
                .padding(
                    start = 16.dp
                )
        )
        AndroidView(
            factory = { context ->
                YearMonthPickerFieldLayout(
                    context = context
                ).also { yearMonthPickerFieldLayout ->
                    yearMonthPickerFieldLayout.binding.dataModel = PersonDetailsViewDataModel(
                        label =  context.resources.getString(R.string.key_minimum_payment_month_label),
                        showLine = true,
                        endIcon = R.drawable.ic_payment_month
                    )
                }
            },
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = periodFilterMinimumPaymentMonthDateBlock
                )
        ) { yearMonthPickerFieldLayout ->
            yearMonthPickerFieldLayout setMonthYearValue viewModel.periodFilterMinimumPaymentMonthDate
        }
        Text(
            text = stringResource(
                id = R.string.key_to_label
            ),
            color = colorResource(
                id = R.color.grey
            ),
            style = textViewTitleLabelFont,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                )
        )
        AndroidView(
            factory = { context ->
                YearMonthPickerFieldLayout(
                    context = context
                ).also { yearMonthPickerFieldLayout ->
                    yearMonthPickerFieldLayout.binding.dataModel = PersonDetailsViewDataModel(
                        label =  context.resources.getString(R.string.key_maximum_payment_month_label),
                        showLine = true,
                        endIcon = R.drawable.ic_payment_month
                    )
                }
            },
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = periodFilterMaximumPaymentMonthDateBlock
                )
        ) { yearMonthPickerFieldLayout ->
            yearMonthPickerFieldLayout setMonthYearValue viewModel.periodFilterMaximumPaymentMonthDate
        }
        BasicButton(
            text = stringResource(
                id = R.string.key_export_invoice_label
            ),
            roundedCornerShapePercent = 50,
            modifier = {
                padding(
                    top = 16.dp
                )
            },
            basicButtonBlock = {
                periodFilterFragmentPresenter.onPdfInvoice()
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PeriodFilterLayoutPreview() {
    PeriodFilterLayout(
        viewModel = PeriodFilterViewModel(),
        periodFilterMinimumPaymentMonthDateBlock = {},
        periodFilterMaximumPaymentMonthDateBlock = {},
        periodFilterFragmentPresenter = object: PeriodFilterFragmentPresenter {}
    )
}
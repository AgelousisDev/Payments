package com.agelousis.payments.main.ui.periodFilter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.periodFilter.models.PeriodFilterDataModel
import com.agelousis.payments.main.ui.periodFilter.presenter.PeriodFilterFragmentPresenter
import com.agelousis.payments.ui.textViewTitleLabelFont
import com.agelousis.payments.utils.extensions.calendar
import com.agelousis.payments.views.dateLayout.YearMonthPickerBottomSheetFragment
import com.agelousis.payments.views.dateLayout.YearMonthPickerFieldLayout
import com.agelousis.payments.views.dateLayout.interfaces.YearMonthPickerListener
import com.agelousis.payments.views.dateLayout.models.YearMonthPickerDataModel
import com.agelousis.payments.views.extensions.setMonthYearValue
import com.agelousis.payments.views.personDetailsLayout.models.PersonDetailsViewDataModel
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import java.util.*

@Composable
fun PeriodFilterLayout(
    periodFilterDataModel: PeriodFilterDataModel?,
    periodFilterFragmentPresenter: PeriodFilterFragmentPresenter,
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { context ->
                LottieAnimationView(context).apply {
                    setAnimation("date_animation.json")
                    repeatMode = LottieDrawable.RESTART
                    repeatCount = LottieDrawable.INFINITE
                    playAnimation()
                }
            },
            modifier = Modifier.height(
                height = 170.dp
            )
        )
        Text(
            text = stringResource(
                id = R.string.key_from_label
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.grey
            ),
            modifier = Modifier
                .padding(
                    start = 16.dp
                ).fillMaxWidth()
        )
        AndroidView(
            factory = { context ->
                YearMonthPickerFieldLayout(
                    context = context
                )
            },
            Modifier.clickable {
                YearMonthPickerBottomSheetFragment.show(
                    supportFragmentManager = childFragmentManager,
                    yearMonthPickerDataModel = YearMonthPickerDataModel(
                        calendar = Date().calendar,
                        yearMonthPickerListener = yearMonthPickerListener
                    )
                )
            }
        ) { yearMonthPickerFieldLayout ->
            yearMonthPickerFieldLayout.setMonthYearValue(
                date = periodFilterDataModel?.minimumMonthDateValue
            )
            yearMonthPickerFieldLayout.binding.dataModel = PersonDetailsViewDataModel(
                label = yearMonthPickerFieldLayout.resources.getString(
                    R.string.key_minimum_payment_month_label
                ),
                showLine = true,
                icon = R.drawable.ic_payment_month
            )
        }
        Text(
            text = stringResource(
                id = R.string.key_to_label
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.grey
            ),
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                ).fillMaxWidth()
        )
        AndroidView(
            factory = { context ->
                YearMonthPickerFieldLayout(
                    context = context
                )
            }
        ) { yearMonthPickerFieldLayout ->
            yearMonthPickerFieldLayout.setMonthYearValue(
                date = periodFilterDataModel?.maximumMonthDateValue
            )
            yearMonthPickerFieldLayout.binding.dataModel = PersonDetailsViewDataModel(
                label = yearMonthPickerFieldLayout.resources.getString(
                    R.string.key_maximum_payment_month_label
                ),
                showLine = true,
                icon = R.drawable.ic_payment_month
            )
        }
        Button(
            onClick = {
                periodFilterFragmentPresenter.onPdfInvoice()
            },
            shape = RoundedCornerShape(
                size = 24.dp
            ),
            modifier = Modifier.padding(
                top = 16.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(
                        id = R.drawable.ic_invoice
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(
                            size = 25.dp
                        )
                )
                Text(
                    text = stringResource(
                        id = R.string.key_export_invoice_label
                    ),
                    style = textViewTitleLabelFont,
                    modifier = Modifier.padding(
                        start = 8.dp
                    )
                )
            }
        }
    }
}
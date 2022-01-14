package com.agelousis.payments.main.ui.history.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.ui.horizontalMargin
import com.agelousis.payments.ui.textViewTitleFont
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun DashboardLayout(
    viewModel: PaymentsViewModel
) {
    val paymentItems by remember {
        viewModel.paymentsMutableStateList
    }
    Column {
        Text(
            text = stringResource(
                id = R.string.key_dashboard_label
            ),
            style = textViewTitleFont,
            modifier = horizontalMargin
        )
        LazyVerticalGrid(
            cells = GridCells.Fixed(
                count = 2
            ),
            contentPadding = PaddingValues(
                all = 16.dp
            )
        ) {
            items(
                items = paymentItems
            ) {
                StatisticCardLayout(
                    paymentItems = paymentItems
                )
            }
        }
    }
}

@Composable
fun StatisticCardLayout(
    paymentItems: List<Any>
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        modifier = Modifier
            .height(
                height = 100.dp
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = 10.dp,
        onClick = {

        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(
                        size = 70.dp
                    )
                    .background(
                        color = colorResource(
                            id = R.color.orange
                        )
                    )
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_person
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        colorResource(
                            id = R.color.white
                        )
                    )
                )
                Column(
                    modifier = horizontalMargin
                ) {
                    Text(
                        text = "",
                        style = textViewTitleLabelFont,
                        modifier = Modifier.padding(
                            all = 8.dp
                        )
                    )
                    Text(
                        text = stringResource(
                            id = R.string.key_dashboard_label
                        ),
                        style = textViewTitleLabelFont,
                        modifier = Modifier.padding(
                            all = 8.dp
                        )
                    )
                }
            }
        }
    }
}
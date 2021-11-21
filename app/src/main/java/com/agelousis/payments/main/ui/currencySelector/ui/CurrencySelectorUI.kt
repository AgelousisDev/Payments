package com.agelousis.payments.main.ui.currencySelector.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.ui.textViewTitleFont

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CurrencySelectorLayout(
    currencyTypes: List<CurrencyType>,
    currencyPresenter: CurrencySelectorFragmentPresenter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.colorAccent),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
                .height(
                    height = 80.dp
                ).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    id = R.string.key_select_currency_label
                ),
                style = textViewTitleFont,
                color = colorResource(
                    id = R.color.white
                )
            )
        }
        LazyVerticalGrid(
            contentPadding = PaddingValues(
                all = 16.dp
            ),
            cells = GridCells.Adaptive(70.dp),
            modifier = Modifier.background(
                color = colorResource(
                    id = R.color.nativeBackgroundColor
                ),
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ).fillMaxWidth().wrapContentHeight()
        ) {
            items(currencyTypes.size) { index ->
                CurrencyType(
                    currencyType = currencyTypes[index],
                    currencyPresenter = currencyPresenter
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun CurrencyType(
    currencyType: CurrencyType,
    currencyPresenter: CurrencySelectorFragmentPresenter
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        modifier = Modifier
            .size(
                width = 70.dp,
                height = 50.dp
            ).padding(
                all = 4.dp
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = if (currencyType.isSelected) 16.dp else 1.dp,
        onClick = {
            currencyPresenter.onCurrencySelected(
                currencyType = currencyType
            )
        },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = currencyType.icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (currencyType.isSelected) 0.5f else 1.0f
            )
            Text(
                text = currencyType.symbol,
                style = textViewTitleFont,
                color = colorResource(
                    id = R.color.white
                ),
                modifier = Modifier.shadow(
                    elevation = 8.dp
                )
            )
        }
    }
}
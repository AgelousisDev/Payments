package com.agelousis.payments.main.ui.colorSelector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.ui.BottomSheetNavigationLine
import com.agelousis.payments.ui.textViewTitleLabelFont

@Composable
fun ColorSelectorLayout(
    colorDataModelList: List<ColorDataModel>,
    colorSelectorPresenter: ColorSelectorPresenter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetNavigationLine()
        Text(
            text = stringResource(
                id = R.string.key_pick_color_label
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            )
        )
        LazyVerticalGrid(
            contentPadding = PaddingValues(
                all = 16.dp
            ),
            cells = GridCells.Adaptive(
                minSize = 100.dp
            )
        ) {
            items(colorDataModelList.size) { index ->
                ColorData(
                    colorDataModel = colorDataModelList[index],
                    colorSelectorPresenter = colorSelectorPresenter
                )
            }
        }
    }
}

@Composable
fun ColorData(
    colorDataModel: ColorDataModel,
    colorSelectorPresenter: ColorSelectorPresenter
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        modifier = Modifier
            .height(
                height = 70.dp
            )
            .padding(
                all = 10.dp
            ),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(
            color = colorDataModel.color
        ),
        elevation = 10.dp,
        onClick = {
            colorSelectorPresenter.onColorSelected(
                color = colorDataModel.color
            )
        },
    ) {
        if (colorDataModel.selected)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_check
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(
                        size = 25.dp
                    ),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(
                        color = colorResource(
                            id = R.color.white
                        )
                    )
                )
            }
    }
}
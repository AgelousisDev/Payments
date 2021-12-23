package com.agelousis.payments.main.ui.colorSelector.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
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


@ExperimentalFoundationApi
@ExperimentalMaterialApi
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
                minSize = 50.dp
            ),
            modifier = Modifier.wrapContentSize()
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

@ExperimentalMaterialApi
@Composable
fun ColorData(
    colorDataModel: ColorDataModel,
    colorSelectorPresenter: ColorSelectorPresenter
) {
    Card(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        modifier = Modifier
            .size(
                width = 50.dp,
                height = 50.dp
            ).padding(
                all = 10.dp
            ),
        shape = RoundedCornerShape(25.dp),
        backgroundColor = Color(
            color = colorDataModel.color
        ),
        elevation = 8.dp,
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
                    contentScale = ContentScale.Crop
                )
            }
    }
}
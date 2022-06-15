package com.agelousis.payments.main.ui.colorSelector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.compose.BottomSheetNavigationLine
import com.agelousis.payments.compose.textViewTitleLabelFont

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
            columns = GridCells.Adaptive(
                minSize = 100.dp
            ),
            contentPadding = PaddingValues(
                all = 16.dp
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
        onClick = {
            colorSelectorPresenter.onColorSelected(
                color = colorDataModel.color
            )
        },
        modifier = Modifier
            .height(
                height = 70.dp
            )
            .padding(
                all = 10.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(
                color = colorDataModel.color
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
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

@Preview
@Composable
fun ColorSelectorLayoutPreview() {
    ColorSelectorLayout(
        colorDataModelList = listOf(),
        colorSelectorPresenter = object: ColorSelectorPresenter {}
    )
}
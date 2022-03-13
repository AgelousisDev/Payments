package com.agelousis.payments.userSelection.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.ui.*
import com.agelousis.payments.utils.extensions.bitmap

typealias UserSelectionBlock = (userModel: UserModel) -> Unit

@Composable
fun UserSelectionLayout(
    userModelList: List<UserModel>?,
    userSelectionBlock: UserSelectionBlock
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetNavigationLine()
        Text(
            text = stringResource(
                id = R.string.key_select_user_from_backup_label
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(
                start = 16.dp
            )
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                )
        ) {
            items(
                items = userModelList ?: listOf()
            ) { userModel: UserModel ->
                Column(
                    modifier = Modifier
                        .width(
                            width = 100.dp
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true)
                        ) {
                            userSelectionBlock(userModel)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                size = 42.dp
                            )
                            .background(
                                color = if (userModel.profileImageData == null) colorResource(id = R.color.colorAccent) else Color.Transparent,
                                shape = RoundedCornerShape(size = if (userModel.profileImageData == null) 21.dp else 0.dp)
                            )
                    ) {
                        if (userModel.profileImageData == null)
                            Text(
                                text = userModel.username?.firstOrNull()?.uppercase() ?: "",
                                style = textViewTitleFont,
                                color = colorResource(
                                    id = R.color.white
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 5.dp
                                    )
                            )
                        if (userModel.profileImageData != null)
                            userModel.profileImageData?.bitmap?.let { profileImageBitmap ->
                                Image(
                                    bitmap = profileImageBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                    }
                    MarqueeText(
                        text = userModel.username ?: "",
                        style = textViewTitleLabelFont,
                        color = colorResource(
                            id = R.color.dayNightTextOnBackground
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }
        }
    }
}
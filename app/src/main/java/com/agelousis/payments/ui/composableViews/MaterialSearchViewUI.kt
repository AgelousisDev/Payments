package com.agelousis.payments.ui.composableViews

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.agelousis.payments.R
import com.agelousis.payments.ui.textViewTitleLabelFont
import com.agelousis.payments.views.searchLayout.SearchQueryChangeBlock
import com.agelousis.payments.views.searchLayout.enumerations.MaterialSearchViewIconState
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import java.io.File

@Composable
fun MaterialSearchViewLayout(
    modifier: Modifier = Modifier,
    materialSearchViewDataModel: MaterialSearchViewDataModel,
    searchQueryChangeBlock: SearchQueryChangeBlock,
) {
    val context = LocalContext.current
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Card(
        interactionSource = remember { MutableInteractionSource() },
        shape = RoundedCornerShape(
            size = 30.dp
        ),
        onClick = {
            focusRequester.requestFocus()
        },
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (searchIconConstrainedReference, searchFieldConstrainedReference,
                secondaryIconConstrainedReference, profileIconConstrainedReference) = createRefs()
            var materialSearchViewIconState by remember {
                mutableStateOf(value = MaterialSearchViewIconState.SEARCH)
            }
            var searchFieldValue by remember {
                mutableStateOf(value = "")
            }
            val infiniteAlphaTransition = rememberInfiniteTransition()
            val alpha by infiniteAlphaTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )

            IconButton(
                onClick = {
                    when(materialSearchViewIconState) {
                        MaterialSearchViewIconState.SEARCH ->
                            focusRequester.requestFocus()
                        else -> {
                            searchFieldValue = ""
                            focusRequester.freeFocus()
                            keyboardController?.hide()
                            materialSearchViewIconState = MaterialSearchViewIconState.SEARCH
                            searchQueryChangeBlock(if (searchFieldValue.length > 1) searchFieldValue else null)
                        }
                    }
                },
                modifier = Modifier
                    .constrainAs(searchIconConstrainedReference) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                        width = Dimension.value(dp = 25.dp)
                        height = Dimension.value(dp = 25.dp)
                    }
            ) {
                Crossfade(targetState = materialSearchViewIconState.icon) {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = stringResource(id = R.string.key_search)
                    )
                }
            }

            TextField(
                value = searchFieldValue,
                onValueChange = {
                    searchFieldValue = it

                    if (materialSearchViewIconState != MaterialSearchViewIconState.CLOSE && it.isNotEmpty())
                        materialSearchViewIconState = MaterialSearchViewIconState.CLOSE

                    if (materialSearchViewIconState != MaterialSearchViewIconState.SEARCH && it.isEmpty())
                        materialSearchViewIconState = MaterialSearchViewIconState.SEARCH

                    searchQueryChangeBlock(if (it.length > 1) it else null)
                },
                placeholder = {
                    Text(
                        text = materialSearchViewDataModel.hint ?: "",
                        style = textViewTitleLabelFont
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusRequester.freeFocus()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .constrainAs(searchFieldConstrainedReference) {
                        start.linkTo(searchIconConstrainedReference.end, 16.dp)
                        top.linkTo(parent.top)
                        end.linkTo(
                            if (materialSearchViewDataModel.secondaryImageResourceId != null)
                                secondaryIconConstrainedReference.start
                            else
                                profileIconConstrainedReference.start,
                            16.dp
                        )
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .focusRequester(
                        focusRequester = focusRequester
                    )
                    .alpha(
                        alpha = if (searchFieldValue.isEmpty()) alpha else 1f

                    )
            )

            if (materialSearchViewDataModel.secondaryImageResourceId != null)
                IconButton(
                    onClick = {
                        materialSearchViewDataModel.presenter?.onSecondaryIconClicked()
                    },
                    modifier = Modifier
                        .constrainAs(secondaryIconConstrainedReference) {
                            top.linkTo(parent.top)
                            end.linkTo(profileIconConstrainedReference.start, 16.dp)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.value(dp = 30.dp)
                            height = Dimension.value(dp = 30.dp)
                        }
                ) {
                    Icon(
                        painter = painterResource(id = materialSearchViewDataModel.secondaryImageResourceId),
                        contentDescription = null
                    )
                }

            IconButton(
                onClick = {
                    materialSearchViewDataModel.presenter?.onProfileImageClicked()
                },
                enabled = materialSearchViewDataModel.profileImagePath != null,
                modifier = Modifier
                    .constrainAs(profileIconConstrainedReference) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.value(dp = 30.dp)
                        height = Dimension.value(dp = 30.dp)
                    }
            ) {
                Image(
                    painter = materialSearchViewDataModel.profileImagePath?.let {
                        rememberAsyncImagePainter(
                            model = File(context.filesDir, it)
                        )
                    } ?: painterResource(id = R.drawable.ic_person),
                    contentDescription = null
                )
            }

        }
    }
}

@Preview
@Composable
fun MaterialSearchViewLayoutPreview() {
    MaterialSearchViewLayout(
        materialSearchViewDataModel = MaterialSearchViewDataModel(
            hint = "Search here...",
            profileImagePath = null,
            presenter = null
        )
    ) {

    }
}
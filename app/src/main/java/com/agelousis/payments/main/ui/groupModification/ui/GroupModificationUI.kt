package com.agelousis.payments.main.ui.groupModification.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.groupModification.GroupModificationState
import com.agelousis.payments.main.ui.groupModification.viewModel.GroupModificationViewModel
import com.agelousis.payments.ui.BasicButton
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.loadImageBitmap
import com.agelousis.payments.utils.extensions.saveImage

@Composable
fun GroupModificationLayout(
    viewModel: GroupModificationViewModel
) {
    ConstraintLayout {
        val (
            groupColorConstrainedReference,
            groupCardInfoConstrainedReference
        ) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = 75.dp
                )
                .background(
                    color = viewModel.groupColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
                .constrainAs(groupColorConstrainedReference) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable {
                    viewModel.groupModificationFragmentPresenter?.onColorPalette()
                }
        )
        Card(
            interactionSource = remember { MutableInteractionSource() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(groupCardInfoConstrainedReference) {
                    top.linkTo(
                        groupColorConstrainedReference.bottom,
                        (-32).dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            GroupInfoLayout(
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun GroupInfoLayout(
    viewModel: GroupModificationViewModel
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri ->
        loadImageBitmap(
            imageUri = uri
        ) { groupBitmap ->
            viewModel.groupBitmap = groupBitmap
            viewModel.groupImageName = context.saveImage(
                bitmap = groupBitmap,
                fileName = "${Constants.GROUP_IMAGE_NAME}_${System.currentTimeMillis()}"
            )
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    ConstraintLayout {
        val (
            groupNameTextFieldConstrainedReference,
            groupImageConstrainedReference,
            groupBoxColorLabelConstrainedReference,
            addGroupButtonConstrainedReference
        ) = createRefs()
        OutlinedTextField(
            value = viewModel.groupName,
            onValueChange = {
                viewModel.groupName = it
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.key_group_name_label
                    ),
                    style = textViewTitleLabelFont
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = colorResource(id = R.color.dayNightTextOnBackground)
            ),
            modifier = Modifier
                .constrainAs(groupNameTextFieldConstrainedReference) {
                    top.linkTo(parent.top, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
        )
        if (viewModel.groupBitmap != null)
            Image(
                bitmap = viewModel.groupBitmap?.asImageBitmap() ?: return@ConstraintLayout,
                contentDescription = null,
                modifier = Modifier
                    .size(
                        size = 40.dp
                    )
                    .background(
                        color = viewModel.groupColor,
                        shape = RoundedCornerShape(
                            size = 20.dp
                        )
                    )
                    .constrainAs(groupImageConstrainedReference) {
                        top.linkTo(groupNameTextFieldConstrainedReference.bottom, 16.dp)
                        start.linkTo(parent.start, 16.dp)
                    }
                    .clickable {
                        launcher.launch("image/*")
                    }
            )
        else
            Image(
                painter = painterResource(
                    id = R.drawable.ic_group
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        size = 40.dp
                    )
                    .constrainAs(groupImageConstrainedReference) {
                        top.linkTo(groupNameTextFieldConstrainedReference.bottom, 16.dp)
                        start.linkTo(parent.start, 16.dp)
                    }
                    .clickable {
                        launcher.launch("image/*")
                    },
                colorFilter = ColorFilter.tint(
                    color = colorResource(
                        id = R.color.dayNightTextOnBackground
                    )
                )
            )
        Text(
            text = stringResource(
                id = R.string.key_group_icon_label
            ),
            style = textViewLabelFont,
            modifier = Modifier
                .constrainAs(groupBoxColorLabelConstrainedReference) {
                    start.linkTo(groupImageConstrainedReference.end, 16.dp)
                    top.linkTo(groupImageConstrainedReference.top)
                    bottom.linkTo(groupImageConstrainedReference.bottom)
                }
        )
        BasicButton(
            text = stringResource(
                id = when(viewModel.groupModificationState) {
                    GroupModificationState.UPDATE -> R.string.key_modify_group
                    else -> R.string.key_add_group_label
                }
            ),
            isEnabled = viewModel.groupName.isNotEmpty(),
            roundedCornerShapePercent = 50,
            buttonColor = viewModel.groupColor,
            modifier = {
                fillMaxWidth()
                .constrainAs(addGroupButtonConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(groupImageConstrainedReference.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            }
        ) {
            viewModel.groupModificationFragmentPresenter?.onGroupAdd()
        }
    }
}

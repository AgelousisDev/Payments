package com.agelousis.payments.main.ui.groupModification.ui

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.groupModification.GroupModificationState
import com.agelousis.payments.main.ui.groupModification.viewModel.GroupModificationViewModel
import com.agelousis.payments.ui.ColorAccent
import com.agelousis.payments.ui.textViewLabelFont
import com.agelousis.payments.ui.textViewTitleLabelFont
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.fromVector
import com.agelousis.payments.utils.extensions.saveImage

@Composable
fun GroupModificationLayout(
    viewModel: GroupModificationViewModel
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri ->
        if (Build.VERSION.SDK_INT < 28)
            viewModel.groupBitmap = MediaStore.Images
                .Media.getBitmap(context.contentResolver, uri)
        else {
            val source = ImageDecoder
                .createSource(context.contentResolver, uri ?: return@rememberLauncherForActivityResult)
            viewModel.groupBitmap = ImageDecoder.decodeBitmap(source)
        }
        viewModel.groupImageName = context.saveImage(
            bitmap = viewModel.groupBitmap,
            fileName = "${Constants.GROUP_IMAGE_NAME}_${System.currentTimeMillis()}"
        )
    }
    ConstraintLayout {
        val (
            groupImageConstrainedReference,
            groupCardInfoConstrainedReference
        ) = createRefs()
        viewModel.groupBitmap?.let { groupBitmap ->
            Image(
                bitmap = groupBitmap.asImageBitmap(),
                contentDescription = viewModel.groupName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = 150.dp
                    )
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .constrainAs(groupImageConstrainedReference) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        launcher.launch("image/*")
                    },
                contentScale = if (viewModel.groupBitmap?.asImageBitmap() != null) ContentScale.Crop else ContentScale.Fit
            )
        } ?: run {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_group
                ),
                contentDescription = viewModel.groupName,
                modifier = Modifier
                    .size(
                        size = 50.dp
                    )
                    .constrainAs(groupImageConstrainedReference) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        launcher.launch("image/*")
                    }
                    .background(ColorAccent)
            )
        }
        Card(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            shape = RoundedCornerShape(12.dp),
            elevation = 10.dp,
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(groupCardInfoConstrainedReference) {
                    top.linkTo(groupImageConstrainedReference.bottom, if (viewModel.groupBitmap != null) (-32).dp else 0  .dp)
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
    val keyboardController = LocalSoftwareKeyboardController.current
    ConstraintLayout {
        val (
            groupNameTextFieldConstrainedReference,
            groupBoxColorConstrainedReference,
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
                keyboardType = KeyboardType.Number
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
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                }
        )
        Box(
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
                .constrainAs(groupBoxColorConstrainedReference) {
                    top.linkTo(groupNameTextFieldConstrainedReference.bottom, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                }
                .clickable {
                    viewModel.groupModificationFragmentPresenter?.onColorPalette()
                }
        )
        Text(
            text = stringResource(
                id = R.string.key_group_color_label
            ),
            style = textViewLabelFont,
            modifier = Modifier
                .constrainAs(groupBoxColorLabelConstrainedReference) {
                    start.linkTo(groupBoxColorConstrainedReference.end, 16.dp)
                    top.linkTo(groupBoxColorConstrainedReference.top)
                    bottom.linkTo(groupBoxColorConstrainedReference.bottom)
                }
        )
        Button(
            onClick = {
                viewModel.groupModificationFragmentPresenter?.onGroupAdd()
            },
            enabled = viewModel.groupName.isNotEmpty(),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 16.dp
                )
                .constrainAs(addGroupButtonConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(groupBoxColorConstrainedReference.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = stringResource(
                    id = when(viewModel.groupModificationState) {
                        GroupModificationState.UPDATE -> R.string.key_modify_group
                        else -> R.string.key_add_group_label
                    }
                ),
                style = textViewTitleLabelFont
            )
        }
    }
}

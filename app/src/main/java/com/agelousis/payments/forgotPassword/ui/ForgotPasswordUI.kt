package com.agelousis.payments.forgotPassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.agelousis.payments.R
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.agelousis.payments.ui.textViewTitleLabelFont
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agelousis.payments.ui.horizontalMargin
import com.agelousis.payments.ui.textViewTitleFont

typealias UpdatePasswordBlock = () -> Unit

@ExperimentalComposeUiApi
@Composable
fun ForgotPasswordUI(
    updatePasswordBlock: UpdatePasswordBlock
) {
    val viewModel: ForgotPasswordViewModel = viewModel()
    var pinValue by remember { mutableStateOf(value = TextFieldValue()) }
    var newPasswordValue by remember { mutableStateOf(value = TextFieldValue()) }
    var repeatNewPasswordValue by remember { mutableStateOf(value = TextFieldValue()) }
    var updatePasswordState by remember { mutableStateOf(value = false) }
    var pinVisibility: Boolean by remember { mutableStateOf(false) }
    var newPasswordVisibility: Boolean by remember { mutableStateOf(false) }
    var repeatNewPasswordVisibility: Boolean by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(
            color = colorResource(
                id = R.color.secondaryNativeBackgroundColor
            ),
            shape = RoundedCornerShape(
                size = 16.dp
            )
        )
    ) {
        Text(
            text = stringResource(
                id = R.string.key_forgot_password_label
            ),
            style = textViewTitleFont,
            modifier = horizontalMargin,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            )
        )
        OutlinedTextField(
            value = pinValue,
            onValueChange = {
                pinValue = it
                viewModel.pinLiveData.value = it.text
                updatePasswordState = it.text.isNotEmpty()
                        && newPasswordValue.text.isNotEmpty()
                        && repeatNewPasswordValue.text.isNotEmpty()
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.key_pin_label
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
            visualTransformation = if (pinVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                     pinVisibility = !pinVisibility
                }) {
                    Icon(imageVector = if (pinVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = colorResource(id = R.color.dayNightTextOnBackground)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
        )
        OutlinedTextField(
            value = newPasswordValue,
            onValueChange = {
                newPasswordValue = it
                viewModel.newPasswordLiveData.value = it.text
                updatePasswordState = it.text == repeatNewPasswordValue.text
                        && pinValue.text.isNotEmpty()
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.key_new_password_hint
                    ),
                    style = textViewTitleLabelFont
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            visualTransformation = if (newPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    newPasswordVisibility = !newPasswordVisibility
                }) {
                    Icon(imageVector = if (newPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = colorResource(id = R.color.dayNightTextOnBackground)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        )
        OutlinedTextField(
            value = repeatNewPasswordValue,
            onValueChange = {
                repeatNewPasswordValue = it
                viewModel.repeatNewPasswordLiveData.value = it.text
                updatePasswordState = it.text == newPasswordValue.text
                        && pinValue.text.isNotEmpty()
            },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.key_repeat_new_password_hint
                    ),
                    style = textViewTitleLabelFont
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            visualTransformation = if (repeatNewPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    repeatNewPasswordVisibility = !repeatNewPasswordVisibility
                }) {
                    Icon(imageVector = if (repeatNewPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = colorResource(id = R.color.dayNightTextOnBackground)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        )
        Button(
            onClick = updatePasswordBlock,
            modifier = horizontalMargin.fillMaxWidth(),
            enabled = updatePasswordState,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = stringResource(
                    id = R.string.key_change_password_label
                ),
                style = textViewTitleLabelFont
            )
        }
    }
}

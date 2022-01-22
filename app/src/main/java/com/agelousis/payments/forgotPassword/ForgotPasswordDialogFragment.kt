package com.agelousis.payments.forgotPassword

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.agelousis.payments.R
import com.agelousis.payments.forgotPassword.ui.ForgotPasswordUI
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordDialogFragment: DialogFragment() {

    companion object {

        private const val USER_ID_EXTRA = "ForgotPasswordBottomSheetFragment=userIdExtra"

        fun show(supportFragmentManager: FragmentManager, userId: Int) {
            ForgotPasswordDialogFragment().also { forgotPasswordBottomSheetFragment ->
                forgotPasswordBottomSheetFragment.arguments = Bundle().also { bundle ->
                    bundle.putInt(USER_ID_EXTRA, userId)
                }
            }.show(
                supportFragmentManager,
                Constants.FORGOT_PASSWORD_FRAGMENT_TAG
            )
        }

    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<ForgotPasswordViewModel>()
    private val userId by lazy { arguments?.getInt(USER_ID_EXTRA) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    ForgotPasswordUI(
                        updatePasswordBlock = this@ForgotPasswordDialogFragment::onChangePassword
                    )
                }
            }
        }
    }

    private fun onChangePassword() {
        uiScope.launch {
            viewModel.updatePassword(
                userId = userId ?: return@launch,
                pin = viewModel.pinLiveData.value ?: return@launch
            )
        }
    }

    @Preview
    @Composable
    fun ForgotPasswordDialogFragmentComposablePreview() {
        ForgotPasswordUI(
            updatePasswordBlock = this@ForgotPasswordDialogFragment::onChangePassword
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
    }

    private fun addObservers() {
        viewModel.updatePasswordLiveData.observe(viewLifecycleOwner) {
            if (it) {
                context?.toast(
                    message = resources.getString(R.string.key_password_changed_successfully_message)
                )
                dismiss()
            }
            else
                context?.toast(
                    message = resources.getString(R.string.key_invalid_pin_message)
                )
        }
    }

}
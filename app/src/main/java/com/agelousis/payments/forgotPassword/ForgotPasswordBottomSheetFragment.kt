package com.agelousis.payments.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.databinding.ForgotPasswordFragmentLayoutBinding
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.toast
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordBottomSheetFragment: BasicBottomSheetDialogFragment(), ForgotPasswordPresenter {

    companion object {

        private const val USER_ID_EXTRA = "ForgotPasswordBottomSheetFragment=userIdExtra"

        fun show(supportFragmentManager: FragmentManager, userId: Int) {
            ForgotPasswordBottomSheetFragment().also { forgotPasswordBottomSheetFragment ->
                forgotPasswordBottomSheetFragment.arguments = Bundle().also { bundle ->
                    bundle.putInt(USER_ID_EXTRA, userId)
                }
            }.show(
                supportFragmentManager,
                Constants.FORGOT_PASSWORD_FRAGMENT_TAG
            )
        }

    }

    override fun onChangePassword() {
        uiScope.launch {
            viewModel.updatePassword(
                context = context ?: return@launch,
                userId = userId ?: return@launch,
                pin = viewModel.pinLiveData.value ?: return@launch
            )
        }
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(ForgotPasswordViewModel::class.java) }
    private val userId by lazy { arguments?.getInt(USER_ID_EXTRA) }
    private lateinit var binding: ForgotPasswordFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ForgotPasswordFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.viewModel = viewModel
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
    }

    private fun addObservers() {
        viewModel.pinLiveData.observe(viewLifecycleOwner) {
            binding.buttonState = !it.isNullOrEmpty() &&
                    !viewModel.newPasswordLiveData.value.isNullOrEmpty() &&
                    !viewModel.repeatNewPasswordLiveData.value.isNullOrEmpty()
        }
        viewModel.newPasswordLiveData.observe(viewLifecycleOwner) {
            binding.buttonState = it == viewModel.repeatNewPasswordLiveData.value && !viewModel.pinLiveData.value.isNullOrEmpty()
        }
        viewModel.repeatNewPasswordLiveData.observe(viewLifecycleOwner) {
            binding.buttonState = it == viewModel.newPasswordLiveData.value && !viewModel.pinLiveData.value.isNullOrEmpty()
        }
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
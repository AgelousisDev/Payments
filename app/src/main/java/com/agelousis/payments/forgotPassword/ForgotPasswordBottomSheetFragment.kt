package com.agelousis.payments.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.databinding.ForgotPasswordFragmentLayoutBinding
import com.agelousis.payments.forgotPassword.viewModels.ForgotPasswordViewModel
import com.agelousis.payments.utils.constants.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordBottomSheetFragment: BottomSheetDialogFragment(), ForgotPasswordPresenter {

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
                userId = userId ?: return@launch
            )
        }
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(ForgotPasswordViewModel::class.java) }
    private val userId by lazy { arguments?.getInt(USER_ID_EXTRA) }
    private var binding: ForgotPasswordFragmentLayoutBinding? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ForgotPasswordFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.viewModel = viewModel
            it.presenter = this
        }
        return binding?.root
    }

    private fun addObservers() {
        viewModel.newPasswordLiveData.observe(viewLifecycleOwner) {
            binding?.buttonState = it == viewModel.repeatNewPasswordLiveData.value
        }
        viewModel.repeatNewPasswordLiveData.observe(viewLifecycleOwner) {
            binding?.buttonState = it == viewModel.newPasswordLiveData.value
        }
        viewModel.updatePasswordLiveData.observe(viewLifecycleOwner) {
            dismiss()
        }
    }

}
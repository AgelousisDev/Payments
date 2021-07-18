package com.agelousis.payments.main.ui.personalInformation

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseBindingFragment
import com.agelousis.payments.databinding.ProfilePictureFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.personalInformation.presenter.PersonalInformationPresenter

class ProfilePictureFragment: BaseBindingFragment<ProfilePictureFragmentLayoutBinding>(
    inflate = ProfilePictureFragmentLayoutBinding::inflate
), PersonalInformationPresenter {

    override fun onProfilePicturePressed() {
        findNavController().popBackStack()
    }

    override fun onBindData(binding: ProfilePictureFragmentLayoutBinding?) {
        super.onBindData(binding)
        binding?.userModel = (activity as? MainActivity)?.userModel
        binding?.presenter = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.shared_image_transition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        (activity as? MainActivity)?.binding?.appBarMain?.bottomAppBar?.performHide()
    }

}
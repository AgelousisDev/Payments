package com.agelousis.payments.guide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.agelousis.payments.databinding.GuideBasicFragmentLayoutBinding
import com.agelousis.payments.guide.models.GuideModel

class GuideBasicFragment: Fragment() {

    companion object {
        const val GUIDE_MODEL_EXTRA = "GuideBasicFragment=guideModelExtra"

        infix fun instance(guideModel: GuideModel?) =
            GuideBasicFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelable(GUIDE_MODEL_EXTRA, guideModel)
                }
            }
    }

    private lateinit var binding: GuideBasicFragmentLayoutBinding
    private val guideModel by lazy { arguments?.getParcelable<GuideModel>(GUIDE_MODEL_EXTRA) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GuideBasicFragmentLayoutBinding.inflate(inflater, container, false).also {
            it.guideModel = guideModel
        }
        return binding.root
    }

}
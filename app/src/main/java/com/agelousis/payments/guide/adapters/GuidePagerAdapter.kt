package com.agelousis.payments.guide.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.agelousis.payments.guide.fragments.GuideBasicFragment
import com.agelousis.payments.guide.models.GuideModel

class GuidePagerAdapter(supportFragmentManager: FragmentManager, private val guideModelList: List<GuideModel>): FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = guideModelList.size

    override fun getItem(position: Int) =
        GuideBasicFragment instance guideModelList.getOrNull(index = position)

}
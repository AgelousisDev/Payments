package com.agelousis.payments.guide.adapters

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agelousis.payments.guide.fragments.GuideBasicFragment
import com.agelousis.payments.guide.models.GuideModel

class GuidePagerAdapter(fragmentActivity: FragmentActivity, private val guideModelList: List<GuideModel>): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = guideModelList.size

    override fun createFragment(position: Int) = GuideBasicFragment instance guideModelList.getOrNull(index = position)

}
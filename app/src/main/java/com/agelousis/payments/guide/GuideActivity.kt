package com.agelousis.payments.guide

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.agelousis.payments.databinding.ActivityGuideBinding
import com.agelousis.payments.guide.adapters.GuidePagerAdapter
import com.agelousis.payments.guide.controller.GuideController
import com.agelousis.payments.guide.fragments.GuideBasicFragment
import com.agelousis.payments.guide.presenters.GuideActivityPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.custom.SlideTransformer
import com.agelousis.payments.utils.extensions.addTabDots
import com.agelousis.payments.utils.extensions.isFirstTime
import com.agelousis.payments.utils.extensions.isLandscape

class GuideActivity : AppCompatActivity(), GuideActivityPresenter {

    override fun onSkip() {
        skipGuide()
    }

    private lateinit var binding: ActivityGuideBinding
    private val guideModelList by lazy { GuideController getGuideItems this }

    override fun onBackPressed() {
        skipGuide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater).also {
            it.presenter = this
        }
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        binding.viewPager.apply {
            offscreenPageLimit = guideModelList.size
            if (!isLandscape)
                setPageTransformer(
                    SlideTransformer(
                        offscreenPageLimit = guideModelList.size
                    )
                )
            adapter = GuidePagerAdapter(
                fragmentActivity = this@GuideActivity,
                guideModelList = guideModelList
            )
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    binding.dotsLayout.addTabDots(
                        currentPage = position,
                        totalPages = guideModelList.size
                    )
                    supportFragmentManager.fragments.forEachIndexed { index, fragment ->
                        (fragment as? GuideBasicFragment)?.binding?.guideDescriptionIsVisible = index == position
                    }
                }
            })
        }
        binding.dotsLayout.addTabDots(
            currentPage = 0,
            totalPages = guideModelList.size
        )
    }

    private fun skipGuide() {
        getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).isFirstTime = false
        finish()
    }

}
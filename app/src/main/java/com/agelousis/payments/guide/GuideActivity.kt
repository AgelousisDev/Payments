package com.agelousis.payments.guide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.agelousis.payments.databinding.ActivityGuideBinding
import com.agelousis.payments.guide.adapters.GuidePagerAdapter
import com.agelousis.payments.guide.controller.GuideController
import com.agelousis.payments.guide.presenters.GuideActivityPresenter
import com.agelousis.payments.utils.extensions.addTabDots

class GuideActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, GuideActivityPresenter {

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        binding?.dotsLayout?.addTabDots(
            currentPage = position,
            totalPages = guideModelList.size
        )
    }

    override fun onSkip() {
        finish()
    }

    private var binding: ActivityGuideBinding? = null
    private val guideModelList by lazy { GuideController.guideItems }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater).also {
            it.presenter = this
        }
        setContentView(binding?.root)
        setupView()
    }

    private fun setupView() {
        binding?.viewPager?.adapter = GuidePagerAdapter(
            supportFragmentManager = supportFragmentManager,
            guideModelList = guideModelList
        )
        binding?.viewPager?.addOnPageChangeListener(this)
        binding?.dotsLayout?.addTabDots(
            currentPage = 0,
            totalPages = guideModelList.size
        )
    }

}
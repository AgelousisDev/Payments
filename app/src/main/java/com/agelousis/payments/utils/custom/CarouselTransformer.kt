package com.agelousis.payments.utils.custom

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.agelousis.payments.R
import kotlin.math.abs

class CarouselTransformer(context: Context, nextItemWidth: Float): ViewPager2.PageTransformer {

    private val currentItemHorizontalMarginPx = context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
    private val pageTranslationX = nextItemWidth + currentItemHorizontalMarginPx

    override fun transformPage(page: View, position: Float) {
        page.translationX = -pageTranslationX * position
        // Next line scales the item's height. You can remove it if you don't want this effect
        page.scaleY = 1 - (0.25f * abs(position))
        // If you want a fading effect uncomment the next line:
        // page.alpha = 0.25f + (1 - abs(position))
    }

}
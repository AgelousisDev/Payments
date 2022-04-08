package com.agelousis.payments.utils.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FloatingButtonSlideBehavior(context: Context, attributeSet: AttributeSet): FloatingActionButton.Behavior(context, attributeSet) {

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (dyConsumed > 0) {
            val margin = (child.layoutParams as? CoordinatorLayout.LayoutParams ?: return).bottomMargin
            child.animate().translationY(child.height.toFloat() + margin).setInterpolator(LinearInterpolator()).setDuration(1000).start()
        } else if (dyConsumed < 0)
            child.animate().translationY(0f).setInterpolator(LinearInterpolator()).setDuration(1000).start()
    }

}
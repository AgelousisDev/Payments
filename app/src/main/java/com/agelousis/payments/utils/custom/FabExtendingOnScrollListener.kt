package com.agelousis.payments.utils.custom

import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class FabExtendingOnScrollListener(
    private val floatingActionButton: ExtendedFloatingActionButton
) : NestedScrollView.OnScrollChangeListener {
    override fun onScrollChange(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY == 0)
            floatingActionButton.extend()
        if (scrollY != 0
            && floatingActionButton.isExtended
        )
            floatingActionButton.shrink()
    }
}
package com.agelousis.payments.views.bottomSheet

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.agelousis.payments.utils.extensions.isLandscape
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BasicBottomSheetDialogFragment: BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixLandscapeModeHeight {
            resources.isLandscape
        }
    }

    private fun fixLandscapeModeHeight(predicate: () -> Boolean) {
        if (predicate())
            view?.viewTreeObserver?.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val dialog = dialog as? BottomSheetDialog
                    val bottomSheet = dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) ?: return
                    val behavior = BottomSheetBehavior.from(bottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.peekHeight = 0
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
    }

}
package com.agelousis.payments.utils.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.R

class LoaderDialog: DialogFragment() {

    companion object {
        fun show(supportFragmentManager: FragmentManager) {
            LoaderDialog().show(
                supportFragmentManager,
                Constants.LOADER_DIALOG_TAG
            )
        }

        fun hide(supportFragmentManager: FragmentManager) {
            (supportFragmentManager.findFragmentByTag(Constants.LOADER_DIALOG_TAG) as? DialogFragment)?.dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        isCancelable = false
        return inflater.inflate(R.layout.loader_dialog_layout, container, false)
    }

}
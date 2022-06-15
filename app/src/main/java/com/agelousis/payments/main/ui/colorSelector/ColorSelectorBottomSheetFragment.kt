package com.agelousis.payments.main.ui.colorSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.colorSelector.ui.ColorSelectorLayout
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment

class ColorSelectorBottomSheetFragment: BasicBottomSheetDialogFragment(), ColorSelectorPresenter {

    companion object {

        fun show(
            supportFragmentManager: FragmentManager,
            colorSelectorPresenter: ColorSelectorPresenter,
            selectedColor: Int?
        ) {
            ColorSelectorBottomSheetFragment().also { colorSelectorBottomSheetFragment ->
                colorSelectorBottomSheetFragment.colorSelectorPresenter = colorSelectorPresenter
                colorSelectorBottomSheetFragment.selectedColor = selectedColor
            }.show(
                supportFragmentManager,
                Constants.COLOR_SELECTOR_FRAGMENT_TAG
            )
        }

    }

    override fun onColorSelected(color: Int) {
        colorSelectorPresenter?.onColorSelected(
            color = color
        )
        dismiss()
    }

    private val colorDataModelList by lazy {
        Constants.Colors.colorPickerColors.map {
            ColorDataModel(
                color = it,
                selected = it == selectedColor
            )
        }
    }
    private var colorSelectorPresenter: ColorSelectorPresenter? = null
    private var selectedColor: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    ColorSelectorLayout(
                        colorDataModelList = colorDataModelList,
                        colorSelectorPresenter = this@ColorSelectorBottomSheetFragment
                    )
                }
            }
        }
    }

}
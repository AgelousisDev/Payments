package com.agelousis.payments.main.ui.colorSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.colorSelector.ui.ColorSelectorLayout
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
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

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    ColorSelectorLayout(
                        colorDataModelList = colorDataModelList,
                        colorSelectorPresenter = this@ColorSelectorBottomSheetFragment
                    )
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        ColorSelectorLayout(
            colorDataModelList = colorDataModelList,
            colorSelectorPresenter = this@ColorSelectorBottomSheetFragment
        )
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        (binding.colorPickerRecyclerView.layoutManager as? FlexboxLayoutManager)?.also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.colorPickerRecyclerView.adapter = ColorSelectorAdapter(
            colorDataModelList = colorDataModelList,
            colorSelectorPresenter = this
        )
    }*/

}
package com.agelousis.payments.main.ui.colorSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.databinding.ColorSelectorFragmentLayoutBinding
import com.agelousis.payments.main.ui.colorSelector.adapters.ColorSelectorAdapter
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class ColorSelectorBottomSheetFragment: BasicBottomSheetDialogFragment(), ColorSelectorPresenter {

    companion object {

        fun show(
            supportFragmentManager: FragmentManager,
            colorSelectorPresenter: ColorSelectorPresenter
        ) {
            ColorSelectorBottomSheetFragment().also { colorSelectorBottomSheetFragment ->
                colorSelectorBottomSheetFragment.colorSelectorPresenter = colorSelectorPresenter
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

    private lateinit var binding: ColorSelectorFragmentLayoutBinding
    private var colorSelectorPresenter: ColorSelectorPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ColorSelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            colorDataModelList = Constants.Colors.colorPickerColors.map {
                ColorDataModel(
                    color = it
                )
            },
            colorSelectorPresenter = this
        )
    }

}
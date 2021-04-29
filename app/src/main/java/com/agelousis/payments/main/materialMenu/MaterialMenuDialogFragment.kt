package com.agelousis.payments.main.materialMenu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.databinding.MaterialMenuFragmentLayoutBinding
import com.agelousis.payments.main.materialMenu.adapters.MaterialMenuOptionsAdapter
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.materialMenu.models.MaterialMenuDataModel
import com.agelousis.payments.main.materialMenu.presenters.MaterialMenuFragmentPresenter
import com.agelousis.payments.utils.constants.Constants

class MaterialMenuDialogFragment: DialogFragment(), MaterialMenuFragmentPresenter {

    companion object {

        fun show(supportFragmentManager: FragmentManager, materialMenuDataModel: MaterialMenuDataModel, materialMenuFragmentPresenter: MaterialMenuFragmentPresenter) {
            MaterialMenuDialogFragment().also {
                it.materialMenuDataModel = materialMenuDataModel
                it.materialMenuFragmentPresenter = materialMenuFragmentPresenter
            }.show(
                supportFragmentManager,
                Constants.MATERIAL_MENU_FRAGMENT_TAG
            )
        }

    }

    override fun onMaterialMenuOptionSelected(materialMenuOption: MaterialMenuOption) {
        dismiss()
        materialMenuFragmentPresenter?.onMaterialMenuOptionSelected(
            materialMenuOption = materialMenuOption
        )
    }

    override fun onMaterialMenuProfileIconClicked() {
        materialMenuFragmentPresenter?.onMaterialMenuProfileIconClicked()
    }

    override fun onMaterialMenuDismiss() {
        dismiss()
    }

    private lateinit var binding: MaterialMenuFragmentLayoutBinding
    private var materialMenuDataModel: MaterialMenuDataModel? = null
    private var materialMenuFragmentPresenter: MaterialMenuFragmentPresenter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MaterialMenuFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.materialMenuDataModel = materialMenuDataModel
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        binding.materialMenuRecyclerView.adapter = MaterialMenuOptionsAdapter(
            materialMenuOptionList = materialMenuDataModel?.materialMenuOptionList ?: return,
            materialMenuFragmentPresenter = this
        )
    }

}
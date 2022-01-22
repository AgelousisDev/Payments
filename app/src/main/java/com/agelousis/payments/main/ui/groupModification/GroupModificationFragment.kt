package com.agelousis.payments.main.ui.groupModification

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseBindingFragment
import com.agelousis.payments.databinding.GroupModificationFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.colorSelector.ColorSelectorBottomSheetFragment
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.groupModification.presenter.GroupModificationFragmentPresenter
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*

class GroupModificationFragment: BaseBindingFragment<GroupModificationFragmentLayoutBinding>(
    inflate = GroupModificationFragmentLayoutBinding::inflate
), GroupModificationFragmentPresenter, ColorSelectorPresenter {

    companion object {
        const val GROUP_MODEL_EXTRA = "GroupModificationFragment=groupModelExtra"
    }

    override fun onColorPalette() {
        ColorSelectorBottomSheetFragment.show(
            supportFragmentManager = childFragmentManager,
            colorSelectorPresenter = this,
            selectedColor = selectedGroupColor
        )
    }

    override fun onGroupAdd() {
        setFragmentResult(
            GROUP_MODEL_EXTRA,
            bundleOf(
                GROUP_MODEL_EXTRA to selectedGroupModel
            )
        )
        findNavController().popBackStack()
    }

    override fun onGroupImage() {
        (activity as? MainActivity)?.activityLauncher?.launch(
            input = galleryIntent
        ) { result ->
            result.data?.data?.let { imageUri ->
                loadImageBitmap(
                    imageUri = imageUri
                ) { bitmap ->
                    selectedGroupModel.groupImage?.let {
                        context?.deleteInternalFile(
                            fileName = it
                        )
                    }
                    selectedGroupModel.groupImage = context?.saveImage(
                        bitmap = bitmap,
                        fileName = "${Constants.GROUP_IMAGE_NAME}_${System.currentTimeMillis()}"
                    )
                    binding?.groupModel = selectedGroupModel
                }
            }
        }
    }

    override fun onColorSelected(color: Int) {
        selectedGroupColor = color
    }

    override fun onBindData(binding: GroupModificationFragmentLayoutBinding?) {
        super.onBindData(binding)
        binding?.groupModel = args.groupModel
        binding?.presenter = this
    }

    private val args: GroupModificationFragmentArgs by navArgs()
    private val selectedGroupModel by lazy {
        args.groupModel ?: GroupModel()
    }
    private var addGroupButtonState: Boolean = false
        set(value) {
            field = value
            binding?.addGroupButton?.visibility = if (value) View.VISIBLE else View.GONE
        }
    private var selectedGroupColor: Int? = null
        set(value) {
            field = value
            value?.let {
                selectedGroupModel.color = it
                binding?.groupModel = selectedGroupModel
            }
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        (activity as? MainActivity)?.appBarTitle =
            if (args.groupModel != null)
                resources.getString(R.string.key_modify_group)
            else
                resources.getString(R.string.key_add_group_label)
        binding?.groupField?.doAfterTextChanged {
            addGroupButtonState = it?.isNotEmpty() == true
            selectedGroupModel.groupName = it?.toString()
        }
    }

}
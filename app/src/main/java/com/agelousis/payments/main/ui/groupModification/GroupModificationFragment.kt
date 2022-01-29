package com.agelousis.payments.main.ui.groupModification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.main.ui.colorSelector.ColorSelectorBottomSheetFragment
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.groupModification.presenter.GroupModificationFragmentPresenter
import com.agelousis.payments.main.ui.groupModification.ui.GroupModificationLayout
import com.agelousis.payments.main.ui.groupModification.viewModel.GroupModificationViewModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.ui.ColorAccent
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import java.io.File

class GroupModificationFragment: BasicBottomSheetDialogFragment(), GroupModificationFragmentPresenter, ColorSelectorPresenter {

    companion object {
        const val GROUP_MODEL_EXTRA = "GroupModificationFragment=groupModelExtra"
    }

    override fun onColorPalette() {
        ColorSelectorBottomSheetFragment.show(
            supportFragmentManager = childFragmentManager,
            colorSelectorPresenter = this,
            selectedColor = viewModel.groupColor.hashCode()
        )
    }

    override fun onGroupAdd() {
        selectedGroupModel.groupImage = viewModel.groupImageName
        selectedGroupModel.color = viewModel.groupColor.hashCode()
        selectedGroupModel.groupName = viewModel.groupName
        setFragmentResult(
            GROUP_MODEL_EXTRA,
            bundleOf(
                GROUP_MODEL_EXTRA to selectedGroupModel
            )
        )
        dismiss()
    }

    override fun onColorSelected(color: Int) {
        viewModel.groupColor = Color(
            color = color
        )
    }

    private val args: GroupModificationFragmentArgs by navArgs()
    private val viewModel by viewModels<GroupModificationViewModel>()
    private val selectedGroupModel by lazy {
        args.groupModel ?: GroupModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    initializeViewModelData()
                    GroupModificationLayout(viewModel = viewModel)
                }
            }
        }
    }

    private fun initializeViewModelData() {
        viewModel.groupModificationState = if (args.groupModel != null) GroupModificationState.UPDATE else GroupModificationState.ADD
        viewModel.groupImageName = args.groupModel?.groupImage
        viewModel.groupBitmap = args.groupModel?.groupImage?.let { groupImage ->
            File(context?.filesDir, groupImage).bitmap
        }
        viewModel.groupColor = args.groupModel?.color?.let { groupColor ->
            Color(
                color = groupColor
            )
        } ?: ColorAccent
        viewModel.groupName = args.groupModel?.groupName ?: ""
        viewModel.groupModificationFragmentPresenter = this@GroupModificationFragment
    }

    @Preview
    @Composable
    fun GroupModificationFragmentUIPreview() {
        initializeViewModelData()
        GroupModificationLayout(viewModel = viewModel)
    }

}
package com.agelousis.payments.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseActivity
import com.agelousis.payments.databinding.ActivityGroupBinding
import com.agelousis.payments.group.presenter.GroupActivityPresenter
import com.agelousis.payments.main.ui.colorSelector.ColorSelectorBottomSheetFragment
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*

class GroupActivity : BaseActivity(), GroupActivityPresenter, ColorSelectorPresenter {

    companion object {
        const val GROUP_MODEL_EXTRA = "GroupActivity=groupModelExtra"
    }

    override fun onColorPalette() {
        ColorSelectorBottomSheetFragment.show(
            supportFragmentManager = supportFragmentManager,
            colorSelectorPresenter = this,
            selectedColor = uiBarColor
        )
    }

    override fun onGroupAdd() {
        binding.rootLayout.circularUnReveal {
            setResult(
                Activity.RESULT_OK,
                Intent().also {
                    it.putExtra(
                        GROUP_MODEL_EXTRA,
                        selectedGroupModel
                    )
                }
            )
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onGroupImage() {
        activityLauncher?.launch(
            input = galleryIntent
        ) { result ->
            result.data?.data?.let { imageUri ->
                loadImageBitmap(
                    imageUri = imageUri
                ) { bitmap ->
                    selectedGroupModel.groupImage?.let {
                        deleteInternalFile(
                            fileName = it
                        )
                    }
                    selectedGroupModel.groupImage = saveImage(
                        bitmap = bitmap,
                        fileName = "${Constants.GROUP_IMAGE_NAME}_${System.currentTimeMillis()}"
                    )
                    binding.groupModel = selectedGroupModel
                }
            }
        }
    }

    override fun onColorSelected(color: Int) {
        uiBarColor = color
    }

    private lateinit var binding: ActivityGroupBinding
    private var uiBarColor: Int? = null
        set(value) {
            field = value
            value?.let {
                window?.statusBarColor = it
                window?.navigationBarColor = it
                selectedGroupModel.color = it
                binding.groupModel = selectedGroupModel
            }
        }
    private var addGroupButtonState: Boolean = false
        set(value) {
            field = value
            binding.addGroupButton.visibility = if (value) View.VISIBLE else View.GONE
        }
    private var onTouchCenterX: Float? = null
    private var onTouchCenterY: Float? = null
    private val selectedGroupModel by lazy { intent?.extras?.getParcelable(GROUP_MODEL_EXTRA) ?: GroupModel() }

    override fun onBackPressed() {
       binding.rootLayout.circularUnReveal(
            centerX = onTouchCenterX?.toInt(),
            centerY = onTouchCenterY?.toInt()
        ) {
            super.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        window?.isEdgeToEdge = true
        binding = ActivityGroupBinding.inflate(
            layoutInflater
        ).also {
            it.groupModel = selectedGroupModel
            it.presenter = this
        }
        setContentView(
            binding.root
        )
        setupUI()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.root.applyWindowInsets(
            withTop = true,
            withBottom = !isEdgeToEdgeEnabled
        )
        if (!isEdgeToEdgeEnabled)
            binding.rootLayout.applyWindowInsets(
                withBottom = true
            )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchCenterX = event.x
                onTouchCenterY = event.y
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setupUI() {
        binding.rootLayout.circularReveal {
            uiBarColor = selectedGroupModel.color ?: ContextCompat.getColor(this, R.color.colorAccent)
        }
        binding.groupField.doAfterTextChanged {
            addGroupButtonState = it?.isNotEmpty() == true
            selectedGroupModel.groupName = it?.toString()
        }
    }

}
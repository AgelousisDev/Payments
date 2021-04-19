package com.agelousis.payments.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.databinding.ActivityGroupBinding
import com.agelousis.payments.group.presenter.GroupActivityPresenter
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import dev.sasikanth.colorsheet.ColorPickerListener
import dev.sasikanth.colorsheet.ColorSheet

class GroupActivity : AppCompatActivity(), GroupActivityPresenter, ColorPickerListener {

    companion object {
        const val GROUP_IMAGE_REQUEST_CODE = 4
        const val GROUP_MODEL_EXTRA = "GroupActivity=groupModelExtra"
    }

    override fun onColorPalette() {
        ColorSheet().colorPicker(
            colors = Constants.Colors.colorPickerColors,
            listener = this
        ).show(supportFragmentManager)
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

    override fun onGroupImage() =
        openGallery(
            requestCode = GROUP_IMAGE_REQUEST_CODE
        )

    override fun invoke(color: Int) {
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
       binding. rootLayout.circularUnReveal(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when(requestCode) {
                GROUP_IMAGE_REQUEST_CODE -> {
                    data?.data?.let { imageUri ->
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
    }

}
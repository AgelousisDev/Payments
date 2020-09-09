package com.agelousis.payments.group

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
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
import com.agelousis.payments.utils.extensions.circularReveal
import com.agelousis.payments.utils.extensions.circularUnReveal
import dev.sasikanth.colorsheet.ColorPickerListener
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity(), GroupActivityPresenter, ColorPickerListener {

    companion object {
        const val GROUP_SELECTION_REQUEST_CODE = 3
        const val GROUP_MODEL_EXTRA = "GroupActivity=groupModelExtra"
    }

    override fun onColorPalette() {
        ColorSheet().colorPicker(
            colors = Constants.Colors.colorPickerColors,
            listener = this
        ).show(supportFragmentManager)
    }

    override fun onGroupAdd() {
        rootLayout.circularUnReveal {
            setResult(
                Activity.RESULT_OK,
                Intent().also {
                    it.putExtra(
                        GROUP_MODEL_EXTRA,
                        selectedGroupModel?.also { groupModel ->
                            groupModel.color = uiBarColor ?: ContextCompat.getColor(this, R.color.colorAccent)
                            groupModel.groupName = groupField.text?.toString()
                        } ?: GroupModel(
                            color = uiBarColor ?: ContextCompat.getColor(this, R.color.colorAccent),
                            groupName = groupField.text?.toString()
                        )
                    )
                }
            )
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun invoke(color: Int) {
        uiBarColor = color
    }

    private var uiBarColor: Int? = null
        set(value) {
            field = value
            value?.let {
                window?.statusBarColor = it
                rootLayout.setBackgroundColor(it)
                groupTextInputLayout.hintTextColor = ColorStateList.valueOf(it)
                window?.navigationBarColor = it
            }
        }
    private var addGroupButtonState: Boolean = false
        set(value) {
            field = value
            addGroupButton.visibility = if (value) View.VISIBLE else View.GONE
        }
    private var onTouchCenterX: Float? = null
    private var onTouchCenterY: Float? = null
    private val selectedGroupModel by lazy { intent?.extras?.getParcelable<GroupModel>(GROUP_MODEL_EXTRA) }

    override fun onBackPressed() {
        rootLayout.circularUnReveal(
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
        setContentView(
            ActivityGroupBinding.inflate(
                layoutInflater
            ).also {
                it.groupModel = selectedGroupModel
                it.presenter = this
            }.root
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
        rootLayout.circularReveal {
            uiBarColor = selectedGroupModel?.color ?: ContextCompat.getColor(this, R.color.colorAccent)
        }
        groupField.doAfterTextChanged {
            addGroupButtonState = it?.isNotEmpty() == true
        }
    }

}
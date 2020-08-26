package com.agelousis.monthlyfees.group

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.ActivityGroupBinding
import com.agelousis.monthlyfees.group.presenter.GroupActivityPresenter
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.after
import com.agelousis.monthlyfees.utils.extensions.circularReveal
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
        setResult(
            Activity.RESULT_OK,
            Intent().also {
                it.putExtra(
                    GROUP_MODEL_EXTRA,
                    GroupModel(
                        color = uiBarColor ?: ContextCompat.getColor(this, R.color.colorAccent),
                        groupName = groupField.text?.toString()
                    )
                )
            }
        )
        finish()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(
            ActivityGroupBinding.inflate(
                layoutInflater
            ).also {
                it.presenter = this
            }.root
        )
        setupUI()
    }

    private fun setupUI() {
        rootLayout.circularReveal()
        after(
            millis = 400
        ) {
            uiBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }
        groupField.doAfterTextChanged {
            addGroupButtonState = it?.isNotEmpty() == true
        }
    }

}
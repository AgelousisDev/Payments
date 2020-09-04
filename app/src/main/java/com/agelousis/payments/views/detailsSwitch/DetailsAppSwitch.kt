package com.agelousis.payments.views.detailsSwitch

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.agelousis.payments.R
import com.agelousis.payments.databinding.DetailsAppSwitchLayoutBinding
import com.agelousis.payments.views.detailsSwitch.interfaces.AppSwitchListener
import com.agelousis.payments.views.detailsSwitch.models.DetailsAppSwitchDataModel
import kotlinx.android.synthetic.main.details_app_switch_layout.view.*

class DetailsAppSwitch(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    private var appSwitchListener: AppSwitchListener? = null
    var isChecked: Boolean = false
        set(value) {
            field = value
            detailsAppSwitch.isChecked = value
        }
        get() = detailsAppSwitch.isChecked

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.DetailsAppSwitch, 0, 0)
            val binding = DetailsAppSwitchLayoutBinding.inflate(
                LayoutInflater.from(
                    context
                ),
                null,
                false
            )
            binding.detailsAppSwitchDataModel = DetailsAppSwitchDataModel(
                label = attributes.getString(R.styleable.DetailsAppSwitch_appSwitchLabel),
                showLine = attributes.getBoolean(R.styleable.DetailsAppSwitch_appSwitchShowLine, false)
            )
            attributes.recycle()
            addView(binding.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        detailsAppSwitch.setOnCheckedChangeListener { _, isChecked ->
            appSwitchListener?.onAppSwitchValueChanged(
                isChecked = isChecked
            )
        }
    }

}
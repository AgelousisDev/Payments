package com.agelousis.monthlyfees.views.searchLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.agelousis.monthlyfees.databinding.MaterialSearchViewLayoutBinding
import com.agelousis.monthlyfees.utils.extensions.infiniteAlphaAnimation
import kotlinx.android.synthetic.main.material_search_view_layout.view.*

typealias SearchQueryChangesBlock = (String?) -> Unit
class MaterialSearchView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var binding: MaterialSearchViewLayoutBinding? = null

    init {
        initAttributesAndView()
    }

    private fun initAttributesAndView() {
        binding = MaterialSearchViewLayoutBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )
        addView(binding?.root)
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        searchField.infiniteAlphaAnimation(
            state = true
        )
    }

    fun onQueryListener(searchQueryChangesBlock: SearchQueryChangesBlock) {
        searchField.doOnTextChanged { text, _, _, _ ->
            searchField.infiniteAlphaAnimation(
                state = text?.length == 0
            )
            searchQueryChangesBlock(if (text?.length ?: 0 > 1) text?.toString() else null)
        }
    }

    fun onProfileImageClicked(onClickListener: OnClickListener) {
        profileImageView.setOnClickListener(onClickListener)
    }

}
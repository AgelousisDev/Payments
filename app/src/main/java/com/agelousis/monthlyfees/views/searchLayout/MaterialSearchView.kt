package com.agelousis.monthlyfees.views.searchLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.agelousis.monthlyfees.databinding.MaterialSearchViewLayoutBinding
import kotlinx.android.synthetic.main.material_search_view_layout.view.*

typealias SearchQueryChangesBlock = (String?) -> Unit
class MaterialSearchView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var binding: MaterialSearchViewLayoutBinding? = null

    init {
        initAttributesAndView(attributeSet = attributeSet)
    }

    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        binding = MaterialSearchViewLayoutBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )
        addView(binding?.root)
    }

    fun onQueryListener(searchQueryChangesBlock: SearchQueryChangesBlock) {
        searchField.doOnTextChanged { text, _, _, _ ->
            searchQueryChangesBlock(text?.toString() ?: return@doOnTextChanged)
        }
    }

    fun onProfileImageClicked(onClickListener: OnClickListener) {
        profileImageView.setOnClickListener(onClickListener)
    }

}
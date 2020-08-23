package com.agelousis.monthlyfees.views.searchLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.agelousis.monthlyfees.databinding.MaterialSearchViewLayoutBinding
import com.agelousis.monthlyfees.utils.extensions.infiniteAlphaAnimation
import com.agelousis.monthlyfees.utils.extensions.setAnimatedImageResourceId
import com.agelousis.monthlyfees.views.searchLayout.enumerations.MaterialSearchViewIconState
import kotlinx.android.synthetic.main.material_search_view_layout.view.*

typealias SearchQueryChangesBlock = (String?) -> Unit
class MaterialSearchView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    var binding: MaterialSearchViewLayoutBinding? = null
    private var searchViewIconState = MaterialSearchViewIconState.SEARCH
        set(value) {
            field = value
            searchIcon.setAnimatedImageResourceId(
                resourceId = value.icon
            )
            searchIcon.setOnClickListener(when(value) {
                MaterialSearchViewIconState.SEARCH -> this::onSearchIcon
                MaterialSearchViewIconState.CLOSE -> this::onDeleteQuery
            })
        }

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

    private fun onDeleteQuery(p0: View) {
        searchField.text?.clear()
    }

    private fun onSearchIcon(p0: View) {
        searchField.requestFocus()
    }

    fun onQueryListener(searchQueryChangesBlock: SearchQueryChangesBlock) {
        searchField.doOnTextChanged { text, _, _, _ ->
            searchField.infiniteAlphaAnimation(
                state = text?.length == 0
            )
            searchViewIconState = if (text?.isNotEmpty() == true) MaterialSearchViewIconState.CLOSE else MaterialSearchViewIconState.SEARCH
            searchQueryChangesBlock(if (text?.length ?: 0 > 1) text?.toString() else null)
        }
    }

    fun onProfileImageClicked(onClickListener: OnClickListener) {
        profileImageView.setOnClickListener(onClickListener)
    }

}
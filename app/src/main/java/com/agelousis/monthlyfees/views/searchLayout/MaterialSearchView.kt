package com.agelousis.monthlyfees.views.searchLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.MaterialSearchViewLayoutBinding
import com.agelousis.monthlyfees.utils.extensions.infiniteAlphaAnimation
import com.agelousis.monthlyfees.utils.extensions.initializeField
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
        initAttributesAndView(attributeSet = attributeSet)
    }

    private fun initAttributesAndView(attributeSet: AttributeSet?) {
        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.MaterialSearchView, 0, 0)
            binding = MaterialSearchViewLayoutBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            )
            binding?.hint = attributes.getString(R.styleable.MaterialSearchView_searchHint)
            attributes.recycle()
            addView(binding?.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        searchField.infiniteAlphaAnimation(
            state = true
        )
        searchIcon.setOnClickListener(this::onSearchIcon)
    }

    private fun onDeleteQuery(p0: View) {
        searchField.text?.clear()
    }

    private fun onSearchIcon(p0: View) {
        context?.initializeField(
            appCompatEditText = searchField
        )
    }

    fun onQueryListener(searchQueryChangesBlock: SearchQueryChangesBlock) {
        searchField.doOnTextChanged { text, _, _, _ ->
            searchField.infiniteAlphaAnimation(
                state = text?.length == 0
            )
            if (searchViewIconState != MaterialSearchViewIconState.CLOSE && !text.isNullOrEmpty())
                searchViewIconState = MaterialSearchViewIconState.CLOSE
            if (searchViewIconState != MaterialSearchViewIconState.SEARCH && text.isNullOrEmpty())
                searchViewIconState = MaterialSearchViewIconState.SEARCH
            searchQueryChangesBlock(if (text?.length ?: 0 > 1) text?.toString() else null)
        }
    }

    fun onProfileImageClicked(onClickListener: OnClickListener) {
        profileImageView.setOnClickListener(onClickListener)
    }

}
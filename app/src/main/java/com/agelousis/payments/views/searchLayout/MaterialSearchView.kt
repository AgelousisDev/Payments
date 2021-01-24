package com.agelousis.payments.views.searchLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.agelousis.payments.R
import com.agelousis.payments.databinding.MaterialSearchViewLayoutBinding
import com.agelousis.payments.utils.extensions.infiniteAlphaAnimation
import com.agelousis.payments.utils.extensions.initializeField
import com.agelousis.payments.utils.extensions.setAnimatedImageResourceId
import com.agelousis.payments.views.searchLayout.enumerations.MaterialSearchViewIconState

typealias SearchQueryChangesBlock = (String?) -> Unit
class MaterialSearchView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    lateinit var binding: MaterialSearchViewLayoutBinding
    private var searchViewIconState = MaterialSearchViewIconState.SEARCH
        set(value) {
            field = value
            binding.searchIcon.setAnimatedImageResourceId(
                resourceId = value.icon
            )
            binding.searchIcon.setOnClickListener(when(value) {
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
            binding.hint = attributes.getString(R.styleable.MaterialSearchView_searchHint)
            binding.secondaryImageResourceId = attributes.getResourceId(R.styleable.MaterialSearchView_secondaryIconResource, 0)
            attributes.recycle()
            addView(binding.root)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        setupUI()
    }

    private fun setupUI() {
        binding.searchField.infiniteAlphaAnimation(
            state = true
        )
        binding.searchIcon.setOnClickListener(this::onSearchIcon)
    }

    private fun onDeleteQuery(p0: View) {
        binding.searchField.text?.clear()
    }

    private fun onSearchIcon(p0: View) {
        context?.initializeField(
            appCompatEditText = binding.searchField
        )
    }

    fun onQueryListener(searchQueryChangesBlock: SearchQueryChangesBlock) {
        binding.searchField.doOnTextChanged { text, _, _, _ ->
            binding.searchField.infiniteAlphaAnimation(
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
        binding.profileImageView.setOnClickListener(onClickListener)
    }

    fun onSecondaryIconClicked(onClickListener: OnClickListener) {
        binding.secondaryActionIcon.setOnClickListener(onClickListener)
    }

}
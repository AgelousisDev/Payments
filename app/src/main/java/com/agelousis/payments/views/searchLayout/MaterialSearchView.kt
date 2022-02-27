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
import com.agelousis.payments.views.searchLayout.enumerations.MaterialSearchViewIconState
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter

typealias SearchQueryChangesBlock = (String?) -> Unit
class MaterialSearchView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet), MaterialSearchViewPresenter {

    override fun onDeleteQuery() {
        binding.searchField.text?.clear()
    }

    override fun onSearchIcon() {
        context?.initializeField(
            appCompatEditText = binding.searchField
        )
    }

    override fun onProfileImageClicked() {
        presenter?.onProfileImageClicked()
    }

    override fun onSecondaryIconClicked() {
        presenter?.onSecondaryIconClicked()
    }

    lateinit var binding: MaterialSearchViewLayoutBinding
    var presenter: MaterialSearchViewPresenter? = null

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
            binding.materialSearchViewDataModel = MaterialSearchViewDataModel(
                hint = attributes.getString(R.styleable.MaterialSearchView_searchHint),
                secondaryImageResourceId = attributes.getResourceId(R.styleable.MaterialSearchView_secondaryIconResource, 0),
                presenter = this
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
        binding.searchField.infiniteAlphaAnimation(
            state = true
        )
    }

    fun onQueryListener(searchQueryChangeBlock: SearchQueryChangesBlock) {
        binding.searchField.doOnTextChanged { text, _, _, _ ->
            binding.searchField.infiniteAlphaAnimation(
                state = text?.length == 0
            )
            if (binding.materialSearchViewDataModel?.materialSearchViewIconState != MaterialSearchViewIconState.CLOSE && !text.isNullOrEmpty())
                binding.materialSearchViewDataModel = binding.materialSearchViewDataModel?.also { materialSearchViewDataModel ->
                    materialSearchViewDataModel.materialSearchViewIconState = MaterialSearchViewIconState.CLOSE
                }
            if (binding.materialSearchViewDataModel?.materialSearchViewIconState != MaterialSearchViewIconState.SEARCH && text.isNullOrEmpty())
                binding.materialSearchViewDataModel = binding.materialSearchViewDataModel?.also { materialSearchViewDataModel ->
                    materialSearchViewDataModel.materialSearchViewIconState = MaterialSearchViewIconState.SEARCH
                }
            searchQueryChangeBlock(if (text?.length ?: 0 > 1) text?.toString() else null)
        }
    }

}
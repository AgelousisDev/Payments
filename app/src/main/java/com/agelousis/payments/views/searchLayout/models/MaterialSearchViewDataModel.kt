package com.agelousis.payments.views.searchLayout.models

import com.agelousis.payments.views.searchLayout.enumerations.MaterialSearchViewIconState
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter

data class MaterialSearchViewDataModel(
    var hint: String?,
    var profileImagePath: String? = null,
    var materialSearchViewIconState: MaterialSearchViewIconState = MaterialSearchViewIconState.SEARCH,
    val secondaryImageResourceId: Int? = null,
    val presenter: MaterialSearchViewPresenter?
)
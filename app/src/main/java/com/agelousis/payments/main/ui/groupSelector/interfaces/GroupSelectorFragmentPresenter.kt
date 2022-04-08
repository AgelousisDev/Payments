package com.agelousis.payments.main.ui.groupSelector.interfaces

import com.agelousis.payments.main.ui.payments.models.GroupModel

interface GroupSelectorFragmentPresenter {
    fun onGroupSelected(groupModel: GroupModel)
}
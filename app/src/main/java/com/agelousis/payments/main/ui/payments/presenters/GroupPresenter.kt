package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.GroupModel

interface GroupPresenter {
    fun onGroupSelected(groupModel: GroupModel)
    fun onGroupLongPressed(groupModel: GroupModel)
}
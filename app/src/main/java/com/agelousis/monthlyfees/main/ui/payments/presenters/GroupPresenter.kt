package com.agelousis.monthlyfees.main.ui.payments.presenters

import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel

interface GroupPresenter {
    fun onGroupSelected(groupModel: GroupModel)
}
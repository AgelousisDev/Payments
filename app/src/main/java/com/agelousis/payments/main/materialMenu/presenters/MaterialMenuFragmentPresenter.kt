package com.agelousis.payments.main.materialMenu.presenters

import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption

interface MaterialMenuFragmentPresenter {
    fun onMaterialMenuOptionSelected(materialMenuOption: MaterialMenuOption) {}
    fun onMaterialMenuProfileIconClicked() {}
    fun onMaterialMenuDismiss() {}
}
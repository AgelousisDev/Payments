package com.agelousis.payments.main.materialMenu.models

import android.os.Parcelable
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import kotlinx.parcelize.Parcelize

@Parcelize
data class MaterialMenuDataModel(val profileImagePath: String?,
                                 val profileName: String?,
                                 val materialMenuOptionList: List<MaterialMenuOption>?
): Parcelable
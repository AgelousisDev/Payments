package com.agelousis.payments.views.personDetailsLayout.models

import androidx.annotation.DrawableRes
import com.agelousis.payments.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.payments.views.personDetailsLayout.enumerations.PersonDetailFieldType

data class PersonDetailsViewDataModel(val label: String? = null,
                                      val value: String? = null,
                                      val showLine: Boolean? = null,
                                      val imeOptionsType: ImeOptionsType? = null,
                                      val type: PersonDetailFieldType? = null,
                                      var customParams: List<String>? = null,
                                      @DrawableRes val icon: Int? = null
)
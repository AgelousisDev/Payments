package com.agelousis.monthlyfees.views.personDetailsLayout.models

import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.ImeOptionsType
import com.agelousis.monthlyfees.views.personDetailsLayout.enumerations.PersonDetailFieldType

data class PersonDetailsViewDataModel(val label: String? = null,
                                      val value: String? = null,
                                      val showLine: Boolean? = null,
                                      val isEnabled: Boolean? = null,
                                      val imeOptionsType: ImeOptionsType? = null,
                                      val type: PersonDetailFieldType? = null

)
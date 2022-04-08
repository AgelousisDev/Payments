package com.agelousis.payments.main.ui.files.models

import java.util.*

data class HeaderModel(
    val dateTime: Date?,
    val header: String,
    val headerBackgroundColor: Int? = null
)
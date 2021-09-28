package com.agelousis.payments.utils.models

import android.net.Uri

data class ContactDataModel(
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val email: String?,
    val photoUri: Uri?
)
package com.agelousis.payments.main.ui.newPayment

import android.graphics.Bitmap

data class ContactModel(val firstName: String?,
                        val lastName: String?,
                        val phoneNumber: String?,
                        val email: String?,
                        val photo: Bitmap?
)
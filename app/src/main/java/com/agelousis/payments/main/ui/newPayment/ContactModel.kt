package com.agelousis.payments.main.ui.newPayment

import android.net.Uri

data class ContactModel(val firstName: String?,
                        val lastName: String?,
                        val phoneNumber: String?,
                        val email: String?,
                        val photoUri: Uri?,
                        var photo: String? = null,
) {

    var photoImageData: ByteArray? = null

}
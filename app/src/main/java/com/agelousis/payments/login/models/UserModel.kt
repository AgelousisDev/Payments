package com.agelousis.payments.login.models

import android.os.Parcelable
import com.agelousis.payments.utils.extensions.ifLet
import com.agelousis.payments.utils.extensions.second
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(var id: Int? = null,
                     var username: String? = null,
                     var password: String? = null,
                     var biometrics: Boolean? = null,
                     var profileImage: String? = null,
                     var address: String? = null,
                     var idCardNumber: String? = null,
                     var socialInsuranceNumber: String? = null,
                     var firstName: String? = null,
                     var lastName: String? = null,
                     var vat: Int? = null,
                     var defaultPaymentAmount: Double? = null,
                     var defaultMessageTemplate: String? = null,
                     var passwordPin: String? = null
): Parcelable {

    @IgnoredOnParcel
    var profileImageData: ByteArray? = null

    val fullName
        get() = ifLet(firstName, lastName) { String.format("%s %s", it.first(), it.second()) } ?: username

}
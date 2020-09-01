package com.agelousis.monthlyfees.login.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
                     var lastName: String? = null
): Parcelable
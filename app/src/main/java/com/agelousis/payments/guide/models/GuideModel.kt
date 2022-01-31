package com.agelousis.payments.guide.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class GuideModel(@DrawableRes val icon: Int,
                      val title: Int,
                      val subtitleArray: List<String>
): Parcelable
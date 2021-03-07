package com.agelousis.payments.guide.controller

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.guide.models.GuideModel
import com.agelousis.payments.login.enumerations.UIMode
import com.agelousis.payments.utils.extensions.uiMode

object GuideController {

    infix fun getGuideItems(context: Context): List<GuideModel> {
        val isDarkMode = context.uiMode == UIMode.DARK_MODE
        return listOf(
            GuideModel(
                icon = if (isDarkMode) context.resources.getIdentifier("ic_guide_login_dark", "drawable", context.packageName) else context.resources.getIdentifier("ic_guide_login_light", "drawable", context.packageName),
                title = R.string.key_offline_login_label,
                subtitle = R.string.key_create_user_guide_message
            ),
            GuideModel(
                icon = if (isDarkMode) context.resources.getIdentifier("ic_guide_payments_dark", "drawable", context.packageName) else context.resources.getIdentifier("ic_guide_payments_light", "drawable", context.packageName),
                title = R.string.key_payments_label,
                subtitle = R.string.key_payments_grouped_guide_message
            ),
            GuideModel(
                icon = if (isDarkMode) context.resources.getIdentifier("ic_guide_new_payment_dark", "drawable", context.packageName) else context.resources.getIdentifier("ic_guide_new_payment_light", "drawable", context.packageName),
                title = R.string.key_your_client_label,
                subtitle = R.string.key_add_payment_guide_message
            ),
            GuideModel(
                icon =  if (isDarkMode) context.resources.getIdentifier("ic_guide_personal_information_dark", "drawable", context.packageName) else context.resources.getIdentifier("ic_guide_personal_information_light", "drawable", context.packageName),
                title = R.string.key_personal_information_label,
                subtitle = R.string.key_edit_personal_information_guide_message
            ),
            GuideModel(
                icon =  if (isDarkMode) context.resources.getIdentifier("ic_guide_payments_order_dark", "drawable", context.packageName) else context.resources.getIdentifier("ic_guide_payments_order_light", "drawable", context.packageName),
                title = R.string.key_payments_order_label,
                subtitle = R.string.key_edit_payments_order_guide_message
            )
        )
    }

}
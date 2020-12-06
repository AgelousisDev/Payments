package com.agelousis.payments.guide.controller

import com.agelousis.payments.R
import com.agelousis.payments.guide.models.GuideModel

object GuideController {

    val guideItems =
        listOf(
            GuideModel(
                icon = R.drawable.ic_china,
                title = R.string.key_offline_login_label,
                subtitle = R.string.key_create_user_guide_message
            ),
            GuideModel(
                icon = R.drawable.ic_sweden,
                title = R.string.key_payments_label,
                subtitle = R.string.key_payments_grouped_guide_message
            ),
            GuideModel(
                icon = R.drawable.ic_united_kingdom,
                title = R.string.key_your_client_label,
                subtitle = R.string.key_add_payment_guide_message
            ),
            GuideModel(
                icon = R.drawable.ic_switzerland,
                title = R.string.key_personal_information_label,
                subtitle = R.string.key_edit_personal_information_guide_message
            )
        )

}
package com.agelousis.payments.guide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import com.agelousis.payments.R
import com.agelousis.payments.guide.models.GuideModel
import com.agelousis.payments.guide.ui.GuideActivityLayout
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme

class GuideActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = appColorScheme(),
                typography = Typography
            ) {
                GuideActivityLayout(
                    guideModelList = getGuideModelList()
                )
            }
        }
    }

    @Composable
    fun getGuideModelList() =
        listOf(
            GuideModel(
                icon = if (isSystemInDarkTheme()) R.drawable.ic_guide_login_dark else R.drawable.ic_guide_login_light,
                title = R.string.key_offline_login_label,
                subtitleArray = stringArrayResource(R.array.key_create_user_guide_message_array).toList()
            ),
            GuideModel(
                icon = if (isSystemInDarkTheme()) R.drawable.ic_guide_payments_dark else R.drawable.ic_guide_payments_light,
                title = R.string.key_payments_label,
                subtitleArray = stringArrayResource(R.array.key_payments_grouped_guide_message_array).toList()
            ),
            GuideModel(
                icon = if (isSystemInDarkTheme()) R.drawable.ic_guide_new_payment_dark else R.drawable.ic_guide_new_payment_light,
                title = R.string.key_your_client_label,
                subtitleArray = stringArrayResource(R.array.key_add_payment_guide_message_array).toList()
            ),
            GuideModel(
                icon =  if (isSystemInDarkTheme()) R.drawable.ic_guide_personal_information_dark else R.drawable.ic_guide_personal_information_light,
                title = R.string.key_personal_information_label,
                subtitleArray = stringArrayResource(R.array.key_edit_personal_information_guide_message_array).toList()
            ),
            GuideModel(
                icon =  if (isSystemInDarkTheme()) R.drawable.ic_guide_payments_order_dark else R.drawable.ic_guide_payments_order_light,
                title = R.string.key_clients_order_label,
                subtitleArray = stringArrayResource(R.array.key_edit_payments_order_guide_message_array).toList()
            )
        )

    @Preview
    @Composable
    fun GuideActivityUI() {
        GuideActivityLayout(
            guideModelList = getGuideModelList()
        )
    }

}
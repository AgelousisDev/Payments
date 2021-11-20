package com.agelousis.payments.main.ui.shareMessageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType
import com.agelousis.payments.main.ui.shareMessageFragment.presenters.ShareMessagePresenter
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment

class ShareMessageBottomSheetFragment: BasicBottomSheetDialogFragment(), ShareMessagePresenter {

    override fun onShareMessageTypeSelected(shareMessageType: ShareMessageType) {
        dismiss()
        configureShareMethod(
            shareMessageType = shareMessageType
        )
    }

    companion object {

        private const val CLIENT_MODEL_EXTRA = "ShareMessageBottomSheetFragment=personModelExtra"

        fun show(supportFragmentManager: FragmentManager, clientModel: ClientModel) {
            ShareMessageBottomSheetFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelable(CLIENT_MODEL_EXTRA, clientModel)
                }
            }.show(
                supportFragmentManager, Constants.SHARE_MESSAGE_FRAGMENT_TAG
            )
        }

    }

    private val clientModel by lazy {
        arguments?.getParcelable<ClientModel>(CLIENT_MODEL_EXTRA)
    }
    private val shareMessageTypeList by lazy {
        val array = arrayListOf<ShareMessageType>()
        array.add(
            ShareMessageType.CALL.also {
                it.isEnabled = clientModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ShareMessageType.SMS.also {
                it.isEnabled = clientModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ShareMessageType.WHATS_APP.also {
                it.isEnabled = clientModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = it.packageName ?: return@also) == true
            }
        )
        array.add(
            ShareMessageType.VIBER.also {
                it.isEnabled = clientModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = it.packageName ?: return@also) == true
            }
        )
        array.add(
            ShareMessageType.EMAIL.also {
                it.isEnabled = clientModel?.email.isNullOrEmpty() == false
            }
        )
        array
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(typography = Typography) {
                    ShareMessageOptionsScreen()
                }
            }
        }
    }

    @Composable
    fun ShareMessageOptionsScreen() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(
                thickness = 2.5.dp,
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 8.dp
                ).width(
                    width = 20.dp
                ).background(
                    color = colorResource(id = R.color.grey),
                    shape = RoundedCornerShape(50)
                )
            )
            Text(
                text = stringResource(id = R.string.key_contact_share_message_label),
                style = MaterialTheme.typography.h3,
                color = colorResource(id = R.color.dayNightTextOnBackground),
            )
            LazyRow(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    shareMessageTypeList
                ) { shareMessageType ->
                    Box(
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 16.dp
                        ).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            enabled = shareMessageType.isEnabled,
                        ) {
                            onShareMessageTypeSelected(
                                shareMessageType = shareMessageType
                            )
                        }
                    ) {
                        Image(
                            painter = painterResource(
                                id = shareMessageType.icon
                            ),
                            alpha = if (shareMessageType.isEnabled) 1.0f else 0.2f,
                            contentDescription = null,
                            modifier = Modifier.size(
                                size = 40.dp
                            )
                        )
                    }
                }
            }
        }
    }

    private fun configureShareMethod(shareMessageType: ShareMessageType) {
        when(shareMessageType) {
            ShareMessageType.CALL ->
                context?.call(
                    phone = clientModel?.phone ?: return
                )
            ShareMessageType.SMS ->
                context?.sendSMSMessage(
                    mobileNumbers = listOf(
                        clientModel?.phone?.toRawMobileNumber ?: return
                    ),
                    message = clientModel?.messageTemplate ?: ""
                )
            ShareMessageType.WHATS_APP, ShareMessageType.VIBER ->
                context?.shareMessage(
                    schemeUrl = String.format(
                        shareMessageType.schemeUrl ?: return,
                        clientModel?.phone?.toRawMobileNumber,
                        clientModel?.messageTemplate ?: ""
                    )
                )
            ShareMessageType.EMAIL ->
                context?.textEmail(
                    clientModel?.email ?: return,
                    content = clientModel?.messageTemplate ?: ""
                )

        }
    }

    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        ShareMessageOptionsScreen()
    }

}
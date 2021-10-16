package com.agelousis.payments.main.ui.shareMessageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.databinding.ShareMessageFragmentLayoutBinding
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.shareMessageFragment.adapters.ShareMessageAdapter
import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType
import com.agelousis.payments.main.ui.shareMessageFragment.presenters.ShareMessagePresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class ShareMessageBottomSheetFragment: BasicBottomSheetDialogFragment(), ShareMessagePresenter {

    override fun onShareMessageTypeSelected(shareMessageType: ShareMessageType) {
        dismiss()
        configureShareMethod(
            shareMessageType = shareMessageType
        )
    }

    companion object {

        private const val PERSON_MODEL_EXTRA = "ShareMessageBottomSheetFragment=personModelExtra"

        fun show(supportFragmentManager: FragmentManager, clientModel: ClientModel) {
            ShareMessageBottomSheetFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelable(PERSON_MODEL_EXTRA, clientModel)
                }
            }.show(
                supportFragmentManager, Constants.SHARE_MESSAGE_FRAGMENT_TAG
            )
        }

    }

    private lateinit var binding: ShareMessageFragmentLayoutBinding
    private val personModel by lazy {
        arguments?.getParcelable<ClientModel>(PERSON_MODEL_EXTRA)
    }
    private val shareMessageTypeList by lazy {
        val array = arrayListOf<ShareMessageType>()
        array.add(
            ShareMessageType.CALL.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ShareMessageType.SMS.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ShareMessageType.WHATS_APP.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = it.packageName ?: return@also) == true
            }
        )
        array.add(
            ShareMessageType.VIBER.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = it.packageName ?: return@also) == true
            }
        )
        array.add(
            ShareMessageType.EMAIL.also {
                it.isEnabled = personModel?.email.isNullOrEmpty() == false
            }
        )
        array
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ShareMessageFragmentLayoutBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureShareMessageRecyclerView()
    }

    private fun configureShareMessageRecyclerView() {
        binding.shareMessageRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.shareMessageRecyclerView.adapter = ShareMessageAdapter(
            shareMessageTypeList = shareMessageTypeList,
            presenter = this
        )
    }

    private fun configureShareMethod(shareMessageType: ShareMessageType) {
        when(shareMessageType) {
            ShareMessageType.CALL ->
                context?.call(
                    phone = personModel?.phone ?: return
                )
            ShareMessageType.SMS ->
                context?.sendSMSMessage(
                    mobileNumbers = listOf(
                        personModel?.phone?.toRawMobileNumber ?: return
                    ),
                    message = personModel?.messageTemplate ?: ""
                )
            ShareMessageType.WHATS_APP, ShareMessageType.VIBER ->
                context?.shareMessage(
                    schemeUrl = String.format(
                        shareMessageType.schemeUrl ?: return,
                        personModel?.phone?.toRawMobileNumber,
                        personModel?.messageTemplate ?: ""
                    )
                )
            ShareMessageType.EMAIL ->
                context?.textEmail(
                    personModel?.email ?: return,
                    content = personModel?.messageTemplate ?: ""
                )

        }
    }

}
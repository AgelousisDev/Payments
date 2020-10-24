package com.agelousis.payments.main.ui.shareMessageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.shareMessageFragment.adapters.ShareMessageAdapter
import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType
import com.agelousis.payments.main.ui.shareMessageFragment.presenters.ShareMessagePresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.share_message_fragment_layout.*

class ShareMessageBottomSheetFragment: BottomSheetDialogFragment(), ShareMessagePresenter {

    override fun onShareMessageTypeSelected(shareMessageType: ShareMessageType) {
        dismiss()
        configureShareMethod(
            shareMessageType = shareMessageType
        )
    }

    companion object {

        private const val PERSON_MODEL_EXTRA = "ShareMessageBottomSheetFragment=personModelExtra"

        fun show(supportFragmentManager: FragmentManager, personModel: PersonModel) {
            ShareMessageBottomSheetFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putParcelable(PERSON_MODEL_EXTRA, personModel)
                }
            }.show(
                supportFragmentManager, Constants.SHARE_MESSAGE_FRAGMENT_TAG
            )
        }

    }

    private val personModel by lazy {
        arguments?.getParcelable<PersonModel>(PERSON_MODEL_EXTRA)
    }
    private val shareMessageTypeList by lazy {
        val array = arrayListOf<ShareMessageType>()
        array.add(
            ShareMessageType.SMS.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false
            }
        )
        array.add(
            ShareMessageType.WHATS_APP.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = Constants.WHATS_APP_PACKAGE_NAME) == true
            }
        )
        array.add(
            ShareMessageType.VIBER.also {
                it.isEnabled = personModel?.phone.isNullOrEmpty() == false && context?.packageManager?.isPackageInstalled(packageName = Constants.VIBER_PACKAGE_NAME) == true
            }
        )
        array.add(
            ShareMessageType.EMAIL.also {
                it.isEnabled = personModel?.email.isNullOrEmpty() == false
            }
        )
        array
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.share_message_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureShareMessageRecyclerView()
    }

    private fun configureShareMessageRecyclerView() {
        shareMessageRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        shareMessageRecyclerView.adapter = ShareMessageAdapter(
            shareMessageTypeList = shareMessageTypeList,
            presenter = this
        )
    }

    private fun configureShareMethod(shareMessageType: ShareMessageType) {
        when(shareMessageType) {
            ShareMessageType.SMS ->
                context?.sendSMSMessage(
                    mobileNumber = personModel?.phone ?: return,
                    message = "Hello, How are you?"
                )
            ShareMessageType.WHATS_APP, ShareMessageType.VIBER ->
                context?.shareMessage(
                    schemeUrl = String.format(
                        shareMessageType.schemeUrl ?: return,
                        personModel?.phone,
                        "Hello World"
                    )
                )
            ShareMessageType.EMAIL ->
                context?.textEmail(
                    email = personModel?.email ?: return,
                    content = "Hello World"
                )

        }
    }

}
package com.agelousis.payments.main.ui.shareMessageFragment.presenters

import com.agelousis.payments.main.ui.shareMessageFragment.enumerations.ShareMessageType

interface ShareMessagePresenter {
    fun onShareMessageTypeSelected(shareMessageType: ShareMessageType)
}
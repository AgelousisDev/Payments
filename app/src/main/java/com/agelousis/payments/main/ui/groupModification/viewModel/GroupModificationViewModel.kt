package com.agelousis.payments.main.ui.groupModification.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.agelousis.payments.main.ui.groupModification.GroupModificationState
import com.agelousis.payments.main.ui.groupModification.presenter.GroupModificationFragmentPresenter
import com.agelousis.payments.compose.ColorAccent

class GroupModificationViewModel: ViewModel() {

    var groupModificationFragmentPresenter: GroupModificationFragmentPresenter? = null

    var groupImageName: String? = null
    var groupBitmap by mutableStateOf<Bitmap?>(value = null)
    var groupColor by mutableStateOf(ColorAccent)
    var groupName by mutableStateOf(value = "")
    var groupModificationState by mutableStateOf<GroupModificationState?>(value = null)
}
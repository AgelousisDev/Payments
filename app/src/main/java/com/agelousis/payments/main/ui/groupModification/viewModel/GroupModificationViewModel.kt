package com.agelousis.payments.main.ui.groupModification.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GroupModificationViewModel: ViewModel() {
    val groupImageBitmapMutableState by mutableStateOf<String?>(value = null)
}
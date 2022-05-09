package com.agelousis.payments.main.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.main.ui.files.ui.InvoicesLayout
import com.agelousis.payments.main.ui.files.viewModel.InvoicesViewModel
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter
import java.io.File

class InvoicesFragment: Fragment(), MaterialSearchViewPresenter {

    override fun onProfileImageClicked() {
        redirectToPersonalInformationFragment()
    }

    fun onDeleteInvoices() {
        viewModel.invoicesDeletionState = true
        /*
        if (clearAllState) {
            selectedFilePositions.clear()
            selectedFilePositions.addAll(
                fileList
            )
        }
        configureDeleteAction(
            clearAllState = clearAllState
        )*/
    }

    private fun onInvoiceSelected(invoiceDataModel: InvoiceDataModel) {
        File(context?.filesDir ?: return, invoiceDataModel.fileName ?: return).takeIf {
            it.exists()
        }?.let {
            findNavController().navigate(
                InvoicesFragmentDirections.actionInvoicesFragmentToPdfViewerFragment(
                    invoiceDataModel = invoiceDataModel
                )
            )
        } ?: context?.showSimpleDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_warning_label),
                message = resources.getString(R.string.key_file_not_exists_message)
            )
        )
    }

    private val viewModel: InvoicesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    configureViewModelProperties()
                    InvoicesLayout(
                        viewModel = viewModel,
                        materialSearchViewPresenter = this@InvoicesFragment
                    )
                }
            }
        }
    }

    private fun configureViewModelProperties() {
        viewModel.updateInvoicesState = true
        viewModel.invoicesLiveData.value = null
        viewModel.selectedInvoiceModelList.clear()
        viewModel.invoiceDataModelBlock = this::onInvoiceSelected
    }

    private fun redirectToPersonalInformationFragment() {
        (activity as? MainActivity)?.binding?.appBarMain?.bottomNavigationView?.selectedItemId =
            R.id.personalInformationFragment
    }

}
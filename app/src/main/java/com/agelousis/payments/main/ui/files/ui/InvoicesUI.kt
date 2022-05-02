package com.agelousis.payments.main.ui.files.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.viewModel.InvoicesViewModel
import com.agelousis.payments.ui.composableViews.MaterialSearchViewLayout
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter

@Composable
fun InvoicesLayout(
    viewModel: InvoicesViewModel,
    materialSearchViewPresenter: MaterialSearchViewPresenter
) {
    val context = LocalContext.current
    val selectedInvoiceDataModelList by viewModel.selectedInvoicesLiveData.observeAsState()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topAppBarConstrainedReference, searchViewConstrainedReference) = createRefs()

        MaterialSearchViewLayout(
            modifier = Modifier
                .constrainAs(searchViewConstrainedReference) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(
                        if (selectedInvoiceDataModelList?.isNotEmpty() == true)
                            topAppBarConstrainedReference.bottom
                        else
                            parent.top,
                        16.dp
                    )
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            materialSearchViewDataModel = MaterialSearchViewDataModel(
                hint = stringResource(id = R.string.key_search_in_invoices_label),
                profileImagePath = (context as? MainActivity)?.userModel?.profileImage,
                presenter = materialSearchViewPresenter
            )
        ) {

        }
    }
}
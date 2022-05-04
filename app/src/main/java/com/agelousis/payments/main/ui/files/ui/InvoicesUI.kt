package com.agelousis.payments.main.ui.files.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.viewModel.InvoicesViewModel
import com.agelousis.payments.ui.composableViews.MaterialSearchViewLayout
import com.agelousis.payments.ui.textViewValueLabelFont
import com.agelousis.payments.utils.extensions.after
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter
import kotlinx.coroutines.launch

@Composable
fun InvoicesLayout(
    viewModel: InvoicesViewModel,
    materialSearchViewPresenter: MaterialSearchViewPresenter
) {
    val context = LocalContext.current
    var updateFilesState by remember {
        mutableStateOf(value = true)
    }
    val selectedInvoiceDataModelList by viewModel.selectedInvoicesLiveData.observeAsState()

    InitializeInvoices(
        state = updateFilesState,
        viewModel = viewModel
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topAppBarConstrainedReference, searchViewConstrainedReference,
            lazyColumnConstrainedReference) = createRefs()

        InvoicesAppBarLayout(
            state = selectedInvoiceDataModelList?.isNotEmpty() == true,
            viewModel = viewModel,
            modifier = Modifier
                .constrainAs(topAppBarConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(dp = 55.dp)
                }
                .background(
                    color = colorResource(id = R.color.nativeBackgroundColor)
                )
        )

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
        ) { query ->

        }

        LazyColumn(
            modifier = Modifier
                .constrainAs(lazyColumnConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(searchViewConstrainedReference.bottom, 4.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp
            )
        ) {

        }
    }
}

@Composable
private fun InitializeInvoices(
    state: Boolean,
    viewModel: InvoicesViewModel
) {
    if (!state)
        return
    val context = LocalContext.current
    val invoiceDataModelList by viewModel.invoicesLiveData.observeAsState()
    LaunchedEffect(
        key1 = Unit
    ) {
        viewModel.initializeInvoices(
            userId = (context as? MainActivity)?.userModel?.id
        )
    }
    (context as? MainActivity)?.invoicesSize = invoiceDataModelList?.size
    (context as? MainActivity)?.floatingButtonState = invoiceDataModelList?.isNotEmpty() == true
    viewModel.createFilesIfRequired(
        context = context,
        invoices = invoiceDataModelList ?: listOf()
    )
    configureFileList(
        invoices = files
    )
}

@Composable
private fun InvoicesAppBarLayout(
    state: Boolean,
    viewModel: InvoicesViewModel,
    modifier: Modifier
) {
    if (state)
        ConstraintLayout(
            modifier = modifier
        ) {
            val (closeIconConstrainedReference, selectedInvoicesLabelConstrainedReference,
                deleteIconConstrainedReference) = createRefs()

            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(closeIconConstrainedReference) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.value(dp = 25.dp)
                            height = Dimension.value(dp = 25.dp)
                        }
                )
            }

            Text(
                text = stringResource(id = R.string.key_files_selected_value_label, viewModel.selectedInvoicesLiveData.value?.size ?: 0),
                style = textViewValueLabelFont,
                modifier = Modifier
                    .constrainAs(selectedInvoicesLabelConstrainedReference) {
                        top.linkTo(parent.top)
                        end.linkTo(deleteIconConstrainedReference.start, 16.dp)
                        bottom.linkTo(parent.bottom)
                    }
            )

            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = colorResource(id = R.color.dayNightTextOnBackground),
                    modifier = Modifier
                        .constrainAs(deleteIconConstrainedReference) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end, 16.dp)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.value(dp = 25.dp)
                            height = Dimension.value(dp = 25.dp)
                        }
                )
            }
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoicesLayoutPreview() {
    InvoicesLayout(
        viewModel = InvoicesViewModel(),
        materialSearchViewPresenter = object : MaterialSearchViewPresenter {}
    )
}
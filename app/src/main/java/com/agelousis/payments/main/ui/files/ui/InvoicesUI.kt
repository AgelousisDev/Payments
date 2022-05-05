package com.agelousis.payments.main.ui.files.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.enumerations.InvoiceRowState
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
import com.agelousis.payments.main.ui.files.viewModel.InvoicesViewModel
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.ui.GridLazyColumnRow
import com.agelousis.payments.ui.composableViews.MaterialSearchViewLayout
import com.agelousis.payments.ui.composableViews.SimpleDialog
import com.agelousis.payments.ui.rows.EmptyRowLayout
import com.agelousis.payments.ui.rows.HeaderRowLayout
import com.agelousis.payments.ui.rows.InvoiceRowLayout
import com.agelousis.payments.ui.textViewValueLabelFont
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter
import kotlinx.coroutines.launch

@Composable
fun InvoicesLayout(
    viewModel: InvoicesViewModel,
    materialSearchViewPresenter: MaterialSearchViewPresenter
) {
    val context = LocalContext.current
    val selectedInvoiceDataModelList by viewModel.selectedInvoicesLiveData.observeAsState()

    InitializeInvoices(
        viewModel = viewModel
    )

    InvoicesDeletionDialog(
        viewModel = viewModel
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topAppBarConstrainedReference, searchViewConstrainedReference,
            mainContentConstrainedReference) = createRefs()

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
            viewModel.searchQuery = query
        }

        if (viewModel.itemsFilteredList.isEmpty())
            EmptyRowLayout(
                emptyModel = EmptyModel(
                    title = if (viewModel.searchQuery.isNullOrEmpty())
                        stringResource(id = R.string.key_no_files_title_message)
                    else
                        null,
                    message = if (viewModel.searchQuery.isNullOrEmpty())
                        stringResource(id = R.string.key_no_files_message)
                    else
                        stringResource(
                            id = R.string.key_no_results_found_value,
                            viewModel.searchQuery ?: ""
                        ),
                    imageIconResource = if (viewModel.searchQuery.isNullOrEmpty()) R.drawable.ic_colored_pdf else R.drawable.ic_colored_search
                ),
                modifier = Modifier
                    .constrainAs(mainContentConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            )
        else
            LazyColumn(
                modifier = Modifier
                    .constrainAs(mainContentConstrainedReference) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(searchViewConstrainedReference.bottom, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp
                )
            ) {
                items(
                    items = viewModel.itemsFilteredList
                ) { invoiceItem ->
                    when(invoiceItem) {
                        is HeaderModel ->
                            HeaderRowLayout(
                                headerModel = invoiceItem,
                                modifier = Modifier
                                    .animateItemPlacement()
                            )
                        is List<*> ->
                            GridLazyColumnRow(
                                items = invoiceItem,
                                columnCount = 2,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) { invoiceDataModel ->
                                InvoiceRowLayout(
                                    invoiceDataModel = invoiceDataModel as? InvoiceDataModel ?: return@GridLazyColumnRow,
                                    invoiceSelectionBlock = {

                                    },
                                    invoiceLongClickBlock = { selectedInvoiceDataModel, invoiceRowState ->
                                        when(invoiceRowState) {
                                            InvoiceRowState.NORMAL -> {
                                                val selectedInvoices = viewModel.selectedInvoicesLiveData.value?.toMutableList()
                                                selectedInvoices?.remove(selectedInvoiceDataModel)
                                                viewModel.selectedInvoicesLiveData.value = selectedInvoices
                                            }
                                            InvoiceRowState.SELECTED ->
                                                viewModel.selectedInvoicesLiveData.value = listOf(
                                                    *viewModel.selectedInvoicesLiveData.value?.toTypedArray() ?: arrayOf(),
                                                    selectedInvoiceDataModel
                                                )
                                        }
                                    },
                                    modifier = Modifier
                                        .animateItemPlacement()
                                )
                            }
                    }
                }
            }
    }
}

@Composable
private fun InitializeInvoices(
    viewModel: InvoicesViewModel
) {
    val context = LocalContext.current
    val invoiceDataModelList by viewModel.invoicesLiveData.observeAsState()
    if (viewModel.updateInvoicesState)
        LaunchedEffect(
            key1 = invoiceDataModelList
        ) {
            viewModel.initializeInvoices(
                userId = (context as? MainActivity)?.userModel?.id
            )
            viewModel.updateInvoicesState = false
        }

    if (invoiceDataModelList?.isNotEmpty() == true) {
        (context as? MainActivity)?.invoicesSize = invoiceDataModelList?.size
        (context as? MainActivity)?.floatingButtonState = invoiceDataModelList?.isNotEmpty() == true
        viewModel.createFilesIfRequired(
            context = context,
            invoices = invoiceDataModelList ?: listOf()
        )
        configureFileList(
            context = context,
            viewModel = viewModel,
        )
    }
}

private fun configureFileList(
    context: Context,
    viewModel: InvoicesViewModel
) {
    val filteredList = arrayListOf<Any>()
    viewModel.invoicesLiveData.value?.groupBy { it.fileDate.yearMonth }?.toSortedMap(compareByDescending { it })?.forEach { map ->
        map.value.filter {
            it.description?.lowercase()?.contains(viewModel.searchQuery?.lowercase() ?: "") == true
        }.takeIf {
            it.isNotEmpty()
        }?.let inner@ { filteredByQueryList ->
                val header = if (map.key?.isSameYearAndMonthWithCurrentDate == true) context.resources.getString(R.string.key_this_month_label) else map.key?.monthFormattedString
                filteredList.add(
                    HeaderModel(
                        dateTime = map.value.firstOrNull()?.fileDate,
                        header = header ?: context.resources.getString(R.string.key_empty_field_label),
                        headerBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    )
                )
                filteredList.add(
                    filteredByQueryList.sortedByDescending { it.fileDate }
                )
            }
    }

    /*if (filteredList.isEmpty())
        query.whenNull {
            filteredList.add(
                EmptyModel(
                    title = resources.getString(R.string.key_no_files_title_message),
                    message = resources.getString(R.string.key_no_files_message),
                    imageIconResource = R.drawable.ic_colored_pdf
                )
            )
        }?.let {
            filteredList.add(
                EmptyModel(
                    message = String.format(
                        resources.getString(R.string.key_no_results_found_value),
                        it
                    ),
                    imageIconResource = R.drawable.ic_colored_search
                )
            )
        }
    binding?.filesListRecyclerView?.scheduleLayoutAnimation()
    (binding?.filesListRecyclerView?.adapter as? FilesAdapter)?.reloadData()
     */
    viewModel.itemsFilteredList = filteredList
}

@Composable
private fun InvoicesAppBarLayout(
    state: Boolean,
    viewModel: InvoicesViewModel,
    modifier: Modifier
) {
    val animationState = remember {
        MutableTransitionState(
            initialState = false
        )
    }
    animationState.targetState = state
    AnimatedVisibility(
        visibleState = animationState,
        enter = slideInVertically(),
        exit = slideOutVertically(),
        modifier = modifier
    ) {
        ConstraintLayout {
            val (closeIconConstrainedReference, selectedInvoicesLabelConstrainedReference,
                deleteIconConstrainedReference) = createRefs()

            IconButton(
                onClick = {
                    viewModel.selectedInvoicesLiveData.value = listOf()
                },
                modifier = Modifier
                    .constrainAs(closeIconConstrainedReference) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.value(dp = 25.dp)
                        height = Dimension.value(dp = 25.dp)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(
                    id = R.string.key_files_selected_value_label,
                    viewModel.selectedInvoicesLiveData.value?.size ?: 0
                ),
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
                    viewModel.invoicesDeletionState = true
                },
                modifier = Modifier
                    .constrainAs(deleteIconConstrainedReference) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.value(dp = 25.dp)
                        height = Dimension.value(dp = 25.dp)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = colorResource(id = R.color.dayNightTextOnBackground)
                )
            }
        }
    }
}

@Composable
fun InvoicesDeletionDialog(
    viewModel: InvoicesViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fileDeletionState by viewModel.fileDeletionLiveData.observeAsState()
    if (fileDeletionState == true) {
        viewModel.invoicesDeletionState = false
        viewModel.updateInvoicesState = true
        viewModel.fileDeletionLiveData.value = false
    }
    SimpleDialog(
        show = viewModel.invoicesDeletionState,
        simpleDialogDataModel = SimpleDialogDataModel(
            title = stringResource(id = R.string.key_warning_label),
            message = stringResource(
                id = if (viewModel.selectedInvoicesLiveData.value.isNullOrEmpty())
                    R.string.key_clear_all_invoices_question
                else
                    R.string.key_delete_selected_invoices_message
            ),
            positiveButtonText = stringResource(
                id = if (viewModel.selectedInvoicesLiveData.value.isNullOrEmpty())
                    R.string.key_clear_label
                else
                    R.string.key_delete_label
            ),
            positiveButtonBlock = {
                coroutineScope.launch {
                    viewModel.deleteInvoices(
                        context = context,
                        invoiceDataModelList = viewModel.selectedInvoicesLiveData.value ?: viewModel.invoicesLiveData.value ?: return@launch
                    )
                }
            },
            dismissBlock = {
                viewModel.invoicesDeletionState = false
            }
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoicesLayoutPreview() {
    InvoicesLayout(
        viewModel = InvoicesViewModel(),
        materialSearchViewPresenter = object : MaterialSearchViewPresenter {}
    )
}
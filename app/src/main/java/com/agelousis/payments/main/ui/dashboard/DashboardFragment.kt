package com.agelousis.payments.main.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.dashboard.presenter.DashboardPresenter
import com.agelousis.payments.main.ui.dashboard.ui.DashboardLayout
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment: Fragment(), DashboardPresenter {

    companion object {
        val shared
            get() = DashboardFragment()
    }

    override fun onDashboardPage(bottomNavigationMenuItemId: Int) {
        (activity as? MainActivity)?.binding?.appBarMain?.bottomNavigationView?.selectedItemId = bottomNavigationMenuItemId
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<DashboardViewModel>()

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
                    typography = Typography,
                    colors = appColors()
                ) {
                    DashboardLayout(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
        requestData()
    }

    private fun addObservers() {
        viewModel.groupModelListLiveData.observe(
            viewLifecycleOwner
        ) {
            uiScope.launch {
                viewModel.fetchInvoices(
                    context = context ?: return@launch,
                    userId = (activity as? MainActivity)?.userModel?.id
                )
            }
        }
        viewModel.fileDataModelListLiveData.observe(
            viewLifecycleOwner
        ) { fileDataModelList ->
            viewModel.initializeDashboardDataWith(
                groupModelList = viewModel.groupModelListLiveData.value ?: listOf(),
                clientModelList = viewModel.clientModelList ?: listOf(),
                paymentAmountModeList = viewModel.paymentAmountModelList ?: listOf(),
                fileDataModelList = fileDataModelList
            )
        }
    }

    private fun requestData() {
        viewModel.dashboardPresenter = this
        viewModel.clientModelList = (parentFragment as? HistoryFragment)?.clientModelList
        viewModel.paymentAmountModelList = listOf(
            *(parentFragment as? HistoryFragment)?.clientModelList?.asSequence()?.mapNotNull {
                it.payments
            }?.flatten()?.toList()?.toTypedArray() ?: arrayOf(),
            *(parentFragment as? HistoryFragment)?.paymentAmountModelList?.toTypedArray() ?: arrayOf()
        )
        uiScope.launch {
            viewModel.fetchGroups(
                context = context ?: return@launch,
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

    @Preview
    @Composable
    fun DashboardFragmentLayoutPreview() {
        DashboardLayout(
            viewModel = viewModel
        )
    }

}
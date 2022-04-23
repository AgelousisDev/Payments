package com.agelousis.payments.main.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.dashboard.presenter.DashboardPresenter
import com.agelousis.payments.main.ui.dashboard.ui.HistoryLayout
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment: Fragment(), DashboardPresenter {

    override fun onDashboardPage(bottomNavigationMenuItemId: Int) {
        (activity as? MainActivity)?.binding?.appBarMain?.bottomNavigationView?.selectedItemId = bottomNavigationMenuItemId
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    HistoryLayout(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestData()
        addObservers()
    }

    private fun addObservers() {
        viewModel.groupModelListLiveData.observe(
            viewLifecycleOwner
        ) {
            uiScope.launch {
                viewModel.fetchInvoices(
                    userId = (activity as? MainActivity)?.userModel?.id
                )
            }
        }
        viewModel.fileDataModelListLiveData.observe(
            viewLifecycleOwner
        ) {
            viewModel.initializeDashboardDataWith()
        }
    }

    private fun requestData() {
        viewModel.dashboardPresenter = this
        uiScope.launch {
            viewModel.initializePayments(
                userModel = (activity as? MainActivity)?.userModel
            )
            viewModel fetchGroups (activity as? MainActivity)?.userModel?.id
        }
    }

}
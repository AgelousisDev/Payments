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
import com.agelousis.payments.main.ui.dashboard.ui.DashboardLayout
import com.agelousis.payments.main.ui.dashboard.viewModel.DashboardViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment: Fragment() {

    companion object {
        val shared
            get() = DashboardFragment()
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
                    requestData()
                }
            }
        }
    }

    private fun requestData() {
        uiScope.launch {
            uiScope.launch outer@ {
                viewModel.fetchGroups(
                    context = context ?: return@outer,
                    userId = (activity as? MainActivity)?.userModel?.id
                ) { groupModelList ->
                    uiScope.launch inner@ {
                        viewModel.fetchInvoices(
                            context = context ?: return@inner,
                            userId = (activity as? MainActivity)?.userModel?.id
                        ) { fileDataModelList ->
                            viewModel.initializeDashboardDataWith(
                                groupModelList = groupModelList,
                                clientModelList = (parentFragment as? HistoryFragment)?.clientModelList ?: listOf(),
                                paymentAmountModeList = listOf(
                                    *(parentFragment as? HistoryFragment)?.clientModelList?.asSequence()?.mapNotNull {
                                        it.payments
                                    }?.flatten()?.toList()?.toTypedArray() ?: arrayOf(),
                                    *(parentFragment as? HistoryFragment)?.paymentAmountModelList?.toTypedArray() ?: arrayOf()
                                ),
                                fileDataModelList = fileDataModelList
                            )
                        }
                    }
                }
            }
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
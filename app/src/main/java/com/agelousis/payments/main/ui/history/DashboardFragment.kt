package com.agelousis.payments.main.ui.history

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
import com.agelousis.payments.main.ui.history.ui.DashboardLayout
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors

class DashboardFragment: Fragment() {

    companion object {
        val shared
            get() = DashboardFragment()
    }

    private val viewModel by viewModels<PaymentsViewModel>()

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

    @Preview
    @Composable
    fun DashboardFragmentLayoutPreview() {
        DashboardLayout(
            viewModel = viewModel
        )
    }

}
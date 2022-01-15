package com.agelousis.payments.main.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseViewBindingFragment
import com.agelousis.payments.databinding.HistoryFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.dashboard.adapters.ChartPagerAdapter
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.github.mikephil.charting.data.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HistoryFragment: BaseViewBindingFragment<HistoryFragmentLayoutBinding>(
    inflate = HistoryFragmentLayoutBinding::inflate
) {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by viewModels<PaymentsViewModel>()
    val clientModelList by lazy {
        arrayListOf<ClientModel>()
    }
    val paymentAmountModelList by lazy {
        arrayListOf<PaymentAmountModel>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePayments()
        addObservers()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { data ->
            (activity as? MainActivity)?.floatingButtonState = data.filterIsInstance<ClientModel>().isNotEmpty()
            if (data.filterIsInstance<ClientModel>().isEmpty()) {
                binding?.emptyModel = EmptyModel(
                    title = resources.getString(R.string.key_no_clients_title_message),
                    message = resources.getString(R.string.key_add_clients_from_home_message),
                    animationJsonIcon = "empty_animation.json"
                )
                return@observe
            }
            clientModelList.clear()
            paymentAmountModelList.clear()
            clientModelList.addAll(data.filterIsInstance<ClientModel>())
            paymentAmountModelList.addAll(data.filterIsInstance<PaymentAmountModel>())
            configureViewPager()
        }
    }

    private fun configureViewPager() {
        binding?.chartViewPager?.apply {
            val chartPagerAdapter = ChartPagerAdapter(
                fragment = this@HistoryFragment
            )
            adapter = chartPagerAdapter
            offscreenPageLimit = 3
            TabLayoutMediator(
                binding?.materialTabLayout ?: return@apply,
                this
            ) { tab, index ->
                tab.setIcon(
                    chartPagerAdapter.getPageIcon(
                        position = index
                    )
                )
            }.attach()
        }
    }

    private fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
        }
    }

    fun switchChart() {
        binding?.chartViewPager?.currentItem = when (binding?.chartViewPager?.currentItem) {
            2 -> (binding?.chartViewPager?.currentItem ?: 0) - 1
            1 -> sequence<Int> { (0 until 3).random() }.firstOrNull {
                it != 1
            } ?: 0
            else -> (binding?.chartViewPager?.currentItem ?: 0) + 1
        }
    }

}
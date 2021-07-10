package com.agelousis.payments.main.ui.history

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.agelousis.payments.R
import com.agelousis.payments.databinding.HistoryFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.history.adapters.ChartPagerAdapter
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.github.mikephil.charting.data.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HistoryFragment: Fragment() {

    private lateinit var binding: HistoryFragmentLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    val clientModelList = arrayListOf<ClientModel>()
    private val chartPagerAdapter by lazy {
        ChartPagerAdapter(
            fragment = this@HistoryFragment
        )
    }
    private var indicatorWidth = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HistoryFragmentLayoutBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePayments()
        addObservers()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { payments ->
            (activity as? MainActivity)?.floatingButtonState = payments.filterIsInstance<ClientModel>().isNotEmpty()
            if (payments.filterIsInstance<ClientModel>().isEmpty()) {
                binding.emptyModel = EmptyModel(
                    title = resources.getString(R.string.key_no_clients_title_message),
                    message = resources.getString(R.string.key_add_clients_from_home_message),
                    animationJsonIcon = "empty_animation.json"
                )
                return@observe
            }
            clientModelList.clear()
            clientModelList.addAll(payments.filterIsInstance<ClientModel>())
            configureViewPager()
        }
    }

    private fun configureViewPager() {
        binding.chartViewPager.apply {
            adapter = chartPagerAdapter
            offscreenPageLimit = 2
            TabLayoutMediator(
                binding.materialTabLayout,
                this
            ) { tab, index ->
                tab.setIcon(
                    chartPagerAdapter.getPageIcon(
                        position = index
                    )
                )
            }.attach()
            binding.materialTabLayout.post {
                indicatorWidth = binding.materialTabLayout.width / binding.materialTabLayout.tabCount

                //Assign new width
                binding.indicator.updateLayoutParams<FrameLayout.LayoutParams> {
                    width = indicatorWidth
                }
            }
            registerOnPageChangeCallback(
                object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        binding.indicator.updateLayoutParams<FrameLayout.LayoutParams> {
                            //Multiply positionOffset with indicatorWidth to get translation
                            val translationOffset =  (positionOffset + position) * indicatorWidth
                            leftMargin = translationOffset.toInt()
                        }
                    }
                }
            )
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
        binding.chartViewPager.currentItem = if (binding.chartViewPager.currentItem == 0) 1 else 0
    }

}
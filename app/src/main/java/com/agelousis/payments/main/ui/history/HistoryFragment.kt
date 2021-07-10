package com.agelousis.payments.main.ui.history

import android.os.Bundle
import android.view.*
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
import com.agelousis.payments.utils.extensions.addTabDots
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HistoryFragment: Fragment() {

    private lateinit var binding: HistoryFragmentLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    val clientModelList = arrayListOf<ClientModel>()
    private var firstTimeChartsLoaded = true

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
            adapter = ChartPagerAdapter(
                fragment = this@HistoryFragment
            )
            offscreenPageLimit = 2
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    if (firstTimeChartsLoaded)
                        postDelayed({
                            firstTimeChartsLoaded = false
                            binding.dotsLayout.addTabDots(
                                currentPage = position,
                                totalPages = 2
                            )
                        }, 1000)
                    else
                        binding.dotsLayout.addTabDots(
                            currentPage = position,
                            totalPages = 2
                        )
                }
            })
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
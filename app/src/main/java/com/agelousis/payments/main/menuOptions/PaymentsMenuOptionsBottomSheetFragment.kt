package com.agelousis.payments.main.menuOptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currentNavigationFragment
import com.agelousis.payments.utils.extensions.firstOrNullWithType
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import kotlinx.android.synthetic.main.payments_menu_options_fragment_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentsMenuOptionsBottomSheetFragment: BasicBottomSheetDialogFragment(), PaymentsMenuOptionPresenter {

    companion object {
        fun show(supportFragmentManager: FragmentManager) {
            PaymentsMenuOptionsBottomSheetFragment().show(
                supportFragmentManager,
                Constants.PAYMENTS_MENU_OPTIONS_FRAGMENT_TAG
            )
        }
    }

    override fun onClearPayments() {
        dismiss()
        (activity as? MainActivity)?.triggerPaymentsClearance()
    }

    override fun onCsvExport() {
        dismiss()
        (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.navigateToPeriodFilterFragment()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private val optionList by lazy {
        arrayListOf(
            HeaderModel(
                dateTime = null,
                header = resources.getString(R.string.key_options_label)
            ),
            PaymentsMenuOptionType.CLEAR_PAYMENTS,
            PaymentsMenuOptionType.CSV_EXPORT
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.payments_menu_options_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        initializePayments()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.CLEAR_PAYMENTS
                }
            )?.isEnabled = list.filterIsInstance<PersonModel>().isNotEmpty()
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.CSV_EXPORT
                }
            )?.isEnabled = list.filterIsInstance<PersonModel>().mapNotNull { it.payments }.flatten().isNotEmpty()
            (menuOptionsRecyclerView.adapter as? PaymentsMenuOptionAdapter)?.reloadData()
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

    private fun configureRecyclerView() {
        menuOptionsRecyclerView.adapter = PaymentsMenuOptionAdapter(
            list = optionList,
            paymentsMenuOptionPresenter = this
        )
        menuOptionsRecyclerView.addItemDecoration(
            DividerItemRecyclerViewDecorator(
                context = context ?: return,
                margin = resources.getDimension(R.dimen.activity_general_horizontal_margin).toInt()
            ) {
                optionList.getOrNull(index = it) !is HeaderModel && optionList.getOrNull(index = it) != PaymentsMenuOptionType.CSV_EXPORT
            }
        )
    }

}
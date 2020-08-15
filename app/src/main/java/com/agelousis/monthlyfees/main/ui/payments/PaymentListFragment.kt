package com.agelousis.monthlyfees.main.ui.payments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import com.agelousis.monthlyfees.main.ui.payments.presenters.GroupPresenter
import com.agelousis.monthlyfees.main.ui.payments.viewModels.PaymentListViewModel
import com.agelousis.monthlyfees.utils.extensions.whenNull
import kotlinx.android.synthetic.main.fragment_payment_list_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PaymentListFragment : Fragment(R.layout.fragment_payment_list_layout), GroupPresenter {

    override fun onGroupSelected(groupModel: GroupModel) {
        findNavController().navigate(
            PaymentListFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                groupDataModel = groupModel
            )
        )
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentListViewModel::class.java) }
    private val itemsList by lazy { arrayListOf<Any>() }
    private val filteredList by lazy { arrayListOf<Any>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureObservers()
        initializePayments()
    }
    
    private fun configureRecyclerView() {
        paymentListRecyclerView.adapter = PaymentsAdapter(
            list = filteredList,
            groupPresenter = this
        )
    }

    private fun configureObservers() =
        viewModel.paymentsLiveData.observe(viewLifecycleOwner, Observer {
            itemsList.clear()
            itemsList.addAll(it)
            configurePayments(
                list = it
            )
        })

    fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel ?: return@launch
            )
        }
    }

    private fun configurePayments(list: List<Any>, query: String? = null) {
        filteredList.clear()
        list.filterIsInstance<PersonModel>().takeIf { it.isNotEmpty() }?.let { payments ->
            payments.groupBy { it.groupName ?: "" }.toSortedMap().forEach { map ->
                map.value.filter { it.firstName?.toLowerCase(Locale.getDefault())?.contains(query?.toLowerCase(Locale.getDefault()) ?: "") == true }
                    .takeIf { it.isNotEmpty() }?.let { filteredByQueryPayments ->
                        filteredList.add(
                            GroupModel(
                                groupId = filteredByQueryPayments.firstOrNull()?.groupId,
                                groupName = map.key
                            )
                        )
                        filteredList.addAll(
                            filteredByQueryPayments.also {
                                it.lastOrNull()?.showLine = false
                            }
                        )
                    }
            }
        } ?: filteredList.addAll(list)

        if (filteredList.isEmpty())
            query.whenNull {
                filteredList.add(
                    EmptyModel(
                        text = resources.getString(R.string.key_no_entries_message),
                        imageIconResource = R.drawable.ic_empy
                    )
                )
            }?.let {
                filteredList.add(
                    EmptyModel(
                        text = String.format(
                            resources.getString(R.string.key_no_results_found_value),
                            it
                        ),
                        imageIconResource = R.drawable.ic_search
                    )
                )
            }
        paymentListRecyclerView.scheduleLayoutAnimation()
        (paymentListRecyclerView.adapter as? PaymentsAdapter)?.reloadData()
    }

}
package com.agelousis.monthlyfees.main.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.custom.enumerations.SwipeAction
import com.agelousis.monthlyfees.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.monthlyfees.databinding.FragmentPaymentsLayoutBinding
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.enumerations.SwipeItemType
import com.agelousis.monthlyfees.main.ui.payments.adapters.PaymentsAdapter
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import com.agelousis.monthlyfees.main.ui.payments.presenters.GroupPresenter
import com.agelousis.monthlyfees.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.PaymentViewHolder
import com.agelousis.monthlyfees.main.ui.payments.viewModels.PaymentViewModel
import com.agelousis.monthlyfees.utils.extensions.*
import com.agelousis.monthlyfees.utils.helpers.PDFHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_payments_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PaymentsFragment : Fragment(), GroupPresenter, PaymentPresenter {

    override fun onGroupSelected(groupModel: GroupModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                groupDataModel = groupModel
            )
        )
    }

    override fun onPaymentSelected(personModel: PersonModel) {
        findNavController().navigate(
            PaymentsFragmentDirections.actionPaymentListFragmentToNewPaymentFragment(
                personDataModel = personModel
            )
        )
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentViewModel::class.java) }
    private val itemsList by lazy { arrayListOf<Any>() }
    private val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            searchLayout.visibility = if (value) View.VISIBLE else View.GONE
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPaymentsLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSearchView()
        configureRecyclerView()
        initializePayments()
    }

    private fun configureSearchView() {
        searchLayout.onProfileImageClicked {
            (activity as? MainActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        searchLayout.onQueryListener {
            configurePayments(
                list = itemsList,
                query = it
            )
        }
    }
    
    private fun configureRecyclerView() {
        paymentListRecyclerView.adapter = PaymentsAdapter(
            list = filteredList,
            groupPresenter = this,
            paymentPresenter = this
        )
        configureSwipeEvents()
    }

    private fun configureSwipeEvents() {
        val swipeItemTouchHelper = ItemTouchHelper(
            SwipeItemTouchHelper(
                context = context ?: return,
                swipeItemType = SwipeItemType.PERSON_ITEM,
                swipePredicateBlock = {
                    it is GroupViewHolder || it is PaymentViewHolder
                }
            ) innerBlock@ { swipeAction, position ->
                when(swipeAction) {
                    SwipeAction.RIGHT -> {
                        configurePDFAction(
                            position = position
                        )
                        (paymentListRecyclerView.adapter as? PaymentsAdapter)?.restoreItem(
                            position = position
                        )
                    }
                    SwipeAction.LEFT ->
                        configureDeleteAction(
                            position = position
                        )
                }
            }
        )
        swipeItemTouchHelper.attachToRecyclerView(paymentListRecyclerView)
    }

    private fun configurePDFAction(position: Int) {
        uiScope.launch {
            filteredList.getOrNull(index = position)?.asIs<GroupModel> { groupModel ->
                viewModel.initializePayments(
                    context = context ?: return@asIs,
                    userModel = (activity as? MainActivity)?.userModel,
                    groupModel = groupModel
                ) {
                    initializePDFCreation(
                        persons = it
                    )
                }
            }
            filteredList.getOrNull(index = position)?.asIs<PersonModel> { personModel ->
                initializePDFCreation(
                    persons = listOf(personModel)
                )
            }
        }
    }

    private fun initializePDFCreation(persons: List<PersonModel>) {
        PDFHelper.shared.initializePDF(
            context = context ?: return,
            userModel = (activity as? MainActivity)?.userModel,
            persons = persons,
        ) { pdfFile ->
            uiScope.launch {
                viewModel.insertFile(
                    context = context ?: return@launch,
                    userModel = (activity as? MainActivity)?.userModel,
                    file = pdfFile,
                    description = if (persons.isSizeOne) persons.firstOrNull()?.fullName ?: "" else persons.firstOrNull()?.groupName ?: ""
                )
                context?.message(
                    message = resources.getString(R.string.key_file_saved_message)
                )
            }
        }
    }

    private fun configureDeleteAction(position: Int) {
        context?.showTwoButtonsDialog(
            title = resources.getString(R.string.key_warning_label),
            message =
                if (filteredList.getOrNull(index = position) is GroupModel)
                    String.format(resources.getString(R.string.key_delete_group_message_value), (filteredList.getOrNull(index = position) as? GroupModel)?.groupName)
                else
                    resources.getString(R.string.key_delete_payment_message),
            negativeButtonBlock = {
                (paymentListRecyclerView.adapter as? PaymentsAdapter)?.restoreItem(
                    position = position
                )
            },
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deleteItem(
                        context = context ?: return@launch,
                        item = filteredList.getOrNull(index = position)
                    )
                }
            }
        )
    }

    private fun configureObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            after(
                millis = 1000
            ) {
                searchViewState = list.filterIsInstance<PersonModel>().isNotEmpty()
            }
            itemsList.clear()
            itemsList.addAll(list)
            configurePayments(
                list = list
            )
        }
        viewModel.deletionLiveData.observe(viewLifecycleOwner) {
            if (it)
                initializePayments()
        }
    }

    fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
        }
    }

    private fun configurePayments(list: List<Any>, query: String? = null) {
        filteredList.clear()
        list.filterIsInstance<PersonModel>().takeIf { it.isNotEmpty() }?.let { payments ->
            payments.groupBy { it.groupName ?: "" }.toSortedMap().forEach { map ->
                map.value.filter { it.firstName?.toLowerCase(Locale.getDefault())?.contains(query?.toLowerCase(Locale.getDefault()) ?: "") == true || it.groupName?.toLowerCase(Locale.getDefault())?.contains(query?.toLowerCase(Locale.getDefault()) ?: "") == true }
                    .takeIf { it.isNotEmpty() }?.let inner@ { filteredByQueryPayments ->
                        filteredList.add(
                            GroupModel(
                                groupId = filteredByQueryPayments.firstOrNull()?.groupId,
                                groupName = map.key,
                                color = filteredByQueryPayments.firstOrNull()?.groupColor
                            )
                        )
                        filteredList.addAll(
                            filteredByQueryPayments.also { personModelList ->
                                personModelList.lastOrNull()?.showLine = false
                            }
                        )
                    }
            }
        }
        filteredList.addAll(
            list.filterIsInstance<GroupModel>()
        )

        if (filteredList.isEmpty())
            query.whenNull {
                filteredList.add(
                    EmptyModel(
                        text = resources.getString(R.string.key_no_entries_message),
                        imageIconResource = R.drawable.ic_empty
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
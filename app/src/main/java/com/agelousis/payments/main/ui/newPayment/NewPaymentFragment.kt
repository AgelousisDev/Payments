package com.agelousis.payments.main.ui.newPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.agelousis.payments.R
import com.agelousis.payments.database.DatabaseTriggeringType
import com.agelousis.payments.databinding.FragmentNewPaymentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.ui.newPayment.adapters.PaymentAmountAdapter
import com.agelousis.payments.main.ui.newPayment.enumerations.PaymentAmountRowState
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.newPayment.viewModels.NewPaymentViewModel
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.utils.extensions.ifLet
import com.agelousis.payments.utils.extensions.message
import com.agelousis.payments.utils.extensions.showListDialog
import kotlinx.android.synthetic.main.fragment_new_payment_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewPaymentFragment: Fragment(), NewPaymentPresenter {

    override fun onPaymentAmount(paymentAmountModel: PaymentAmountModel?) {
        fillCurrentPersonModel()
        paymentAmountUpdateIndex = paymentAmountModel?.let { availablePayments.indexOf(it) }
        findNavController().navigate(
            NewPaymentFragmentDirections.actionNewPaymentFragmentToNewPaymentAmountFragment(
                paymentAmountDataModel = paymentAmountModel,
            )
        )
    }

    override fun onPaymentAmountLongPressed(adapterPosition: Int) {
        availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState = availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState?.otherState ?: PaymentAmountRowState.NORMAL
        (paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
        when((activity as? MainActivity)?.floatingButtonType) {
            FloatingButtonType.NORMAL ->
                if (availablePayments.any { it.paymentAmountRowState == PaymentAmountRowState.CAN_BE_DISMISSED })
                    (activity as? MainActivity)?.setFloatingButtonAsPaymentRemovalButton()
            FloatingButtonType.NEGATIVE ->
                if (availablePayments.all { it.paymentAmountRowState == PaymentAmountRowState.NORMAL })
                    (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
        }
        if (availablePayments.getOrNull(index = adapterPosition)?.paymentAmountRowState == PaymentAmountRowState.CAN_BE_DISMISSED)
            paymentReadyForDeletionIndexArray.add(adapterPosition)
        else
            paymentReadyForDeletionIndexArray.remove(adapterPosition)
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(NewPaymentViewModel::class.java) }
    private val args: NewPaymentFragmentArgs by navArgs()
    private val databaseTriggeringType by lazy {
        args.groupDataModel?.let { DatabaseTriggeringType.INSERT } ?: DatabaseTriggeringType.UPDATE
    }
    private val availableGroups by lazy { arrayListOf<GroupModel>() }
    private val availablePayments by lazy { ArrayList(args.personDataModel?.payments ?: listOf()) }
    private var binding: FragmentNewPaymentLayoutBinding? = null
    private var currentPersonModel: PersonModel? = null
    private var paymentReadyForDeletionIndexArray = arrayListOf<Int>()
    private var paymentAmountUpdateIndex: Int? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureObservers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context ?: return).inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPaymentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.groupModel = args.groupDataModel
            it.personModel = args.personDataModel
            it.presenter = this
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
        initializeGroups()
        initializeNewPayments()
    }

    private fun setupUI() {
        groupDetailsLayout.setOnDetailsPressed {
            context?.showListDialog(
                title = resources.getString(R.string.key_select_group_label),
                items = availableGroups.mapNotNull { it.groupName }
            ) {
                groupDetailsLayout.errorState = false
                groupDetailsLayout.value = availableGroups.getOrNull(index = it)?.groupName
            }
        }
        activeAppSwitchLayout.setOnClickListener {
            activeAppSwitchLayout.isChecked = !activeAppSwitchLayout.isChecked
        }
        freeAppSwitchLayout.setOnClickListener {
            freeAppSwitchLayout.isChecked = !freeAppSwitchLayout.isChecked
        }
        currentPersonModel?.let {
            binding?.personModel = it
        }
    }

    private fun configureObservers() {
        viewModel.groupsLiveData.observe(viewLifecycleOwner) {
            availableGroups.clear()
            availableGroups.addAll(it)
        }
        viewModel.paymentInsertionStateLiveData.observe(viewLifecycleOwner) { paymentInsertionState ->
            if (paymentInsertionState) {
                currentPersonModel = null
                findNavController().popBackStack()
            }
        }
    }

    private fun initializeNewPayments() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<PaymentAmountModel>(NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA)
            ?.observe(viewLifecycleOwner, { paymentAmountModel ->
                paymentAmountUpdateIndex?.let { index ->
                    availablePayments.set(
                        index = index,
                        paymentAmountModel
                    )
                } ?: availablePayments.add(paymentAmountModel)
                (paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.reloadData()
            })
    }

    private fun configureRecyclerView() {
        paymentAmountRecyclerView.adapter = PaymentAmountAdapter(
            paymentModelList = availablePayments,
            vat = (activity as? MainActivity)?.userModel?.vat,
            presenter = this
        )
    }

    private fun initializeGroups() {
        uiScope.launch {
            viewModel.initializeGroups(
                context = context ?: return@launch,
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

    fun checkInputFields() {
        fillCurrentPersonModel()
        ifLet(
            currentPersonModel?.groupName,
            currentPersonModel?.firstName,
            currentPersonModel?.surname,
            currentPersonModel?.phone,
            currentPersonModel?.parentName,
            currentPersonModel?.email,
            currentPersonModel?.payments
        ) {
            checkDatabasePaymentAction()
        } ?: run {
            binding?.groupDetailsLayout?.errorState = binding?.groupDetailsLayout?.value == null
            binding?.firstNameLayout?.errorState = binding?.firstNameLayout?.value == null
            binding?.surnameLayout?.errorState = binding?.surnameLayout?.value == null
            binding?.phoneLayout?.errorState = binding?.phoneLayout?.value == null
            binding?.parentNameLayout?.errorState = binding?.parentNameLayout?.value == null
            binding?.emailLayout?.errorState = binding?.emailLayout?.value == null
            if (availablePayments.isEmpty())
                context?.message(
                    message = resources.getString(R.string.key_add_payment_message)
                )
        }
    }

    private fun checkDatabasePaymentAction() {
        uiScope.launch {
            when(databaseTriggeringType) {
                DatabaseTriggeringType.INSERT ->
                    viewModel.addPayment(
                        context = this@NewPaymentFragment.context ?: return@launch,
                        userId = (activity as? MainActivity)?.userModel?.id,
                        personModel = currentPersonModel ?: return@launch
                    )
                DatabaseTriggeringType.UPDATE ->
                    viewModel.updatePayment(
                        context = this@NewPaymentFragment.context ?: return@launch,
                        userId = (activity as? MainActivity)?.userModel?.id,
                        personModel = currentPersonModel ?: return@launch
                    )
            }
        }
    }

    private fun fillCurrentPersonModel() {
        currentPersonModel = PersonModel(
            paymentId = args.personDataModel?.paymentId,
            groupId = availableGroups.firstOrNull { it.groupName?.toLowerCase(Locale.getDefault()) == binding?.groupDetailsLayout?.value?.toLowerCase(Locale.getDefault()) }?.groupId,
            groupName = binding?.groupDetailsLayout?.value,
            firstName = binding?.firstNameLayout?.value,
            surname = binding?.surnameLayout?.value,
            phone = binding?.phoneLayout?.value,
            parentName = binding?.parentNameLayout?.value,
            parentPhone = binding?.parentPhoneLayout?.value,
            email = binding?.emailLayout?.value,
            active = binding?.activeAppSwitchLayout?.isChecked,
            free = binding?.freeAppSwitchLayout?.isChecked,
            payments = availablePayments
        )
    }

    fun dismissPayment() {
        paymentReadyForDeletionIndexArray.forEach { paymentReadyForDeletionIndex ->
            (paymentAmountRecyclerView.adapter as? PaymentAmountAdapter)?.removeItem(
                position = paymentReadyForDeletionIndex
            )
            (activity as? MainActivity)?.returnFloatingButtonBackToNormal()
        }
    }

}
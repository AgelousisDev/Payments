package com.agelousis.payments.main.menuOptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.payments.databinding.PaymentsMenuOptionsFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currentNavigationFragment
import com.agelousis.payments.utils.extensions.firstOrNullWithType
import com.agelousis.payments.utils.extensions.hasPermissions
import com.agelousis.payments.utils.extensions.sendSMSMessage
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
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

    override fun onSendSmsGlobally() {
        context?.sendSMSMessage(
            mobileNumbers = personModelList.mapNotNull { it.phone },
            message = (activity as? MainActivity)?.userModel?.defaultMessageTemplate ?: ""
        )
    }

    override fun onPaymentsOrder() {
        dismiss()
        (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToFilterPaymentsFragment()
    }

    override fun onQrCode(qrCodeSelectionType: QRCodeSelectionType) {
        dismiss()
        when(qrCodeSelectionType) {
            QRCodeSelectionType.GENERATE ->
                (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToQrCodeFragment(
                    qrCodeSelectionType = qrCodeSelectionType
                )
            QRCodeSelectionType.SCAN ->
                if (context?.hasPermissions(android.Manifest.permission.CAMERA) == true)
                    (activity?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.redirectToQrCodeFragment(
                        qrCodeSelectionType = qrCodeSelectionType
                    )
                else
                    ActivityCompat.requestPermissions(
                        activity ?: return,
                        arrayOf(
                            android.Manifest.permission.CAMERA
                        ),
                        MainActivity.QR_CODE_CAMERA_PERMISSION_REQUEST_CODE
                    )
        }

    }

    private lateinit var binding: PaymentsMenuOptionsFragmentLayoutBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private val personModelList by lazy { arrayListOf<ClientModel>() }
    private val optionList by lazy {
        arrayListOf(
            HeaderModel(
                dateTime = null,
                header = resources.getString(R.string.key_options_label),
                headerBackgroundColor = context?.let { ContextCompat.getColor(it, android.R.color.transparent) }
            ),
            PaymentsMenuOptionType.CLIENTS_ORDER,
            PaymentsMenuOptionType.CLEAR_CLIENTS,
            PaymentsMenuOptionType.CSV_EXPORT,
            PaymentsMenuOptionType.SEND_SMS_GLOBALLY,
            PaymentsMenuOptionType.QR_CODE_GENERATOR,
            PaymentsMenuOptionType.SCAN_QR_CODE.also {
                it.isEnabled = true
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PaymentsMenuOptionsFragmentLayoutBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        initializePayments()
        addObservers()
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            personModelList.clear()
            personModelList.addAll(
                list.filterIsInstance<ClientModel>()
            )
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.CLIENTS_ORDER
                }
            )?.isEnabled = personModelList.isNotEmpty()
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.CLEAR_CLIENTS
                }
            )?.isEnabled = personModelList.isNotEmpty()
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.CSV_EXPORT
                }
            )?.isEnabled = personModelList.mapNotNull { it.payments }.flatten().isNotEmpty()
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.SEND_SMS_GLOBALLY
                }
            )?.isEnabled = personModelList.mapNotNull { it.phone }.isNotEmpty()
            optionList.firstOrNullWithType(
                typeBlock = {
                    it as? PaymentsMenuOptionType
                },
                predicate = {
                    it == PaymentsMenuOptionType.QR_CODE_GENERATOR
                }
            )?.isEnabled = MainApplication.firebaseToken != null
            (binding.menuOptionsRecyclerView.adapter as? PaymentsMenuOptionAdapter)?.reloadData()
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
        binding.menuOptionsRecyclerView.adapter = PaymentsMenuOptionAdapter(
            list = optionList,
            paymentsMenuOptionPresenter = this
        )
        binding.menuOptionsRecyclerView.addItemDecoration(
            DividerItemRecyclerViewDecorator(
                context = context ?: return,
                margin = resources.getDimension(R.dimen.activity_general_horizontal_margin).toInt()
            ) {
                optionList.getOrNull(index = it) !is HeaderModel && optionList.getOrNull(index = it) != PaymentsMenuOptionType.SCAN_QR_CODE
            }
        )
    }

}
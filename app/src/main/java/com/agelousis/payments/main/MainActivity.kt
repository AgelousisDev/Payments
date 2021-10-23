package com.agelousis.payments.main

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseActivity
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.databinding.ActivityMainBinding
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.group.GroupActivity
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.enumerations.FloatingButtonPosition
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.materialMenu.models.MaterialMenuDataModel
import com.agelousis.payments.main.ui.clientsSelector.ClientsSelectorDialogFragment
import com.agelousis.payments.main.ui.files.FilesFragment
import com.agelousis.payments.main.ui.history.HistoryFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.paymentsFiltering.FilterPaymentsFragment
import com.agelousis.payments.main.ui.pdfViewer.PdfViewerFragment
import com.agelousis.payments.main.ui.periodFilter.PeriodFilterFragment
import com.agelousis.payments.main.ui.personalInformation.PersonalInformationFragment
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.receivers.NotificationDataReceiver
import com.agelousis.payments.receivers.interfaces.NotificationListener
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.agelousis.payments.utils.models.SimpleDialogDataModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener, View.OnClickListener, NotificationListener {

    companion object {
        const val USER_MODEL_EXTRA = "MainActivity=userModelExtra"
        const val FIREBASE_NOTIFICATION_DATA_EXTRA = "MainActivity=firebaseNotificatonDataExtra"
        const val QR_CODE_CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    override fun onNotificationReceived(firebaseNotificationData: FirebaseNotificationData) {
        binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack()
        makeSoundNotification()
        ClientsSelectorDialogFragment.show(
            supportFragmentManager = supportFragmentManager,
            clientModelList = firebaseNotificationData.clientModelList
        )
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        binding.appBarMain.bottomAppBar.performShow()
        ((binding.appBarMain.floatingButton.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? HideBottomViewOnScrollBehavior)?.slideUp(
            binding.appBarMain.floatingButton
        )
        bottomNavigationViewIsVisible = destination.id == R.id.paymentsFragment
                || destination.id == R.id.filesFragment
                || destination.id == R.id.historyFragment
                || destination.id == R.id.personalInformationFragment
        bottomAppBarContentInsetState = destination.id != R.id.paymentsFragment
                && destination.id != R.id.filesFragment
                && destination.id != R.id.historyFragment
                && destination.id != R.id.personalInformationFragment
        when(destination.id) {
            R.id.personalInformationFragment -> {
                navigationIcon = null
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.paymentsFragment -> {
                navigationIcon = null
                appBarTitle = ""
                floatingButtonImage = R.drawable.ic_add_group
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.newPaymentFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_client_info_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.newPaymentAmountFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.filesFragment -> {
                navigationIcon = null
                appBarTitle = ""
                floatingButtonState = false
                floatingButtonImage = R.drawable.ic_delete
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.red
            }
            R.id.periodFilterFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_filter_period_label)
                floatingButtonImage = R.drawable.ic_table
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.historyFragment -> {
                navigationIcon = null
                floatingButtonImage = R.drawable.ic_left_and_right
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = false
            }
            R.id.pdfViewerFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_invoice_label)
                floatingButtonImage = R.drawable.ic_share
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.filterPaymentsFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_clients_order_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                floatingButtonState = true
            }
            R.id.QRCodeFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_qr_code_label)
                floatingButtonState = false
            }
            R.id.profilePictureFragment ->
                floatingButtonState = false
        }
    }

    override fun onClick(p0: View?) {
        currentFocus?.let {
            hideKeyboard(
                view = it
            )
        }
        when(binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().currentDestination?.id) {
            R.id.personalInformationFragment ->
                (supportFragmentManager.currentNavigationFragment as? PersonalInformationFragment)?.playProfileSuccessAnimation()
            R.id.paymentsFragment -> startGroupActivity()
            R.id.newPaymentFragment ->
                when(floatingButtonType) {
                    FloatingButtonType.NORMAL ->
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.checkInputFields()
                    FloatingButtonType.NEGATIVE -> {
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.dismissPayment()
                        returnFloatingButtonBackToNormal()
                    }
                }
            R.id.newPaymentAmountFragment ->
                (supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.checkInputFields()
            R.id.periodFilterFragment ->
                (supportFragmentManager.currentNavigationFragment as? PeriodFilterFragment)?.initializeExportToExcelOperation()
            R.id.pdfViewerFragment ->
                (supportFragmentManager.currentNavigationFragment as? PdfViewerFragment)?.sharePDF()
            R.id.filterPaymentsFragment ->
                (supportFragmentManager.currentNavigationFragment as? FilterPaymentsFragment)?.saveFiltersAndDismiss()
            R.id.historyFragment ->
                (supportFragmentManager.currentNavigationFragment as? HistoryFragment)?.switchChart()
            R.id.filesFragment ->
                (supportFragmentManager.currentNavigationFragment as? FilesFragment)?.onDeleteInvoices(
                    clearAllState = true
                )
        }
    }

    lateinit var binding: ActivityMainBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val dbManager by lazy { DBManager(context = this) }
    val userModel by lazy { intent?.extras?.getParcelable<UserModel>(USER_MODEL_EXTRA) }
    private val materialMenuDataModel by lazy {
        MaterialMenuDataModel(
            profileImagePath = userModel?.profileImage,
            profileName = userModel?.fullName ?: resources.getString(R.string.key_empty_field_label),
            materialMenuOptionList = listOf(
                MaterialMenuOption.HOME,
                MaterialMenuOption.PROFILE,
                MaterialMenuOption.INVOICES,
                MaterialMenuOption.HISTORY,
                MaterialMenuOption.GUIDE
            )
        )
    }
    var appBarTitle: String? = null
        set(value) {
            field = value
            value?.let {
                binding.appBarMain.bottomAppBarTitle.text = it
            }
        }
    @DrawableRes private var floatingButtonImage: Int = R.drawable.ic_add
        set(value) {
            field = value
            binding.appBarMain.floatingButton.hide()
            binding.appBarMain.floatingButton.setImageResource(value)
            binding.appBarMain.floatingButton.show()
        }
    var floatingButtonState: Boolean = true
        set(value) {
            field = value
            if (value)
                binding.appBarMain.floatingButton.show()
            else binding.appBarMain.floatingButton.hide()
        }
    var floatingButtonType = FloatingButtonType.NORMAL
    private var floatingButtonPosition = FloatingButtonPosition.END
        set(value) {
            field = value
            when(value) {
                FloatingButtonPosition.CENTER ->
                    binding.appBarMain.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                FloatingButtonPosition.END ->
                    binding.appBarMain.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            }
        }
    private var floatingButtonTint: Int = 0
        set(value) {
            field = value
            binding.appBarMain.floatingButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, value))
        }
    var historyButtonIsVisible = false
        set(value) {
            field = value
            materialMenuDataModel.materialMenuOptionList?.firstOrNull { it == MaterialMenuOption.HISTORY }?.isEnabled = value
        }
    private val notificationIntentFilter by lazy { IntentFilter(Constants.SHOW_NOTIFICATION_INTENT_ACTION) }
    private val notificationDataReceiver by lazy {
        NotificationDataReceiver().also {
            it.notificationListener = this
        }
    }
    private var bottomNavigationViewIsVisible = false
        set(value) {
            field = value
            binding.appBarMain.bottomNavigationView.isVisible = value
        }
    private var bottomAppBarContentInsetState = false
        set(value) {
            field = value
            binding.appBarMain.bottomAppBar.setContentInsetsAbsolute(
                if (value)
                    resources.getDimensionPixelSize(R.dimen.activity_general_horizontal_margin)
                else
                    0,
                0
            )
        }
    private var navigationIcon: Drawable? = null
        set(value) {
            field = value
            binding.appBarMain.bottomAppBar.navigationIcon = value
        }

    override fun onBackPressed() {
        when (binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().currentDestination?.id) {
            R.id.historyFragment ->
                showSimpleDialog(
                    SimpleDialogDataModel(
                        title = resources.getString(R.string.key_logout_label),
                        message = resources.getString(R.string.key_logout_message),
                        icon = R.drawable.ic_logout,
                        positiveButtonBlock = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                )
            R.id.newPaymentFragment ->
                showNewPersonUnsavedFieldsWarning()
            R.id.newPaymentAmountFragment ->
                showNewPaymentUnsavedFieldsWarning()
            else -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().previousBackStackEntry?.savedStateHandle?.remove<PaymentAmountModel>(
                    NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA
                )
                super.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.isEdgeToEdge = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupUI()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        configureNavigationController()
        NavigationUI.setupWithNavController(
            binding.appBarMain.bottomNavigationView,
            binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController()
        )
        binding.appBarMain.contentMain.root.applyWindowInsets(
            withTop = true,
            withBottom = !isEdgeToEdgeEnabled
        )
        if (!isEdgeToEdgeEnabled)
            binding.appBarMain.bottomAppBar.applyWindowInsets(
                withBottom = true
            )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R
            || !isEdgeToEdgeEnabled
        )
            window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            )
        else if (isEdgeToEdgeEnabled)
            binding.appBarMain.contentMain.navHostFragmentContainerView.applyAnimationOnKeyboard()
    }

    override fun onResume() {
        registerReceiver(
            notificationDataReceiver,
            notificationIntentFilter
        )
        super.onResume()
    }

    private fun configureNavigationController() {
        binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().addOnDestinationChangedListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBarMain.bottomAppBar)
        binding.appBarMain.bottomAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupUI() {
        binding.appBarMain.floatingButton.setOnClickListener(this)
    }

    private fun configureGroup(groupModel: GroupModel, successBlock: () -> Unit) {
        uiScope.launch {
            groupModel.groupImageData = this@MainActivity byteArrayFromInternalImage groupModel.groupImage
            groupModel.groupId?.let {
                dbManager.updateGroup(
                    groupModel = groupModel,
                    updateSuccessBlock = successBlock
                )
            } ?: dbManager.insertGroups(
                userId = userModel?.id,
                groupModelList = listOf(
                    groupModel
                ),
                insertionSuccessBlock = successBlock
            )
        }
    }

    fun initializeDatabaseExport() {
        showTwoButtonsDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_export_database_label),
                message = resources.getString(R.string.key_export_message),
                icon = R.drawable.ic_export,
                positiveButtonText = resources.getString(R.string.key_proceed_label),
                positiveButtonBlock = {
                    activityLauncher?.launch(
                        input = createDocumentIntentWith(
                            fileName = Constants.EXPORT_DATABASE_FILE_NAME,
                            mimeType = Constants.GENERAL_MIME_TYPE
                        )
                    ) { result ->
                        alterFile(
                            uri = result.data?.data,
                            file = getDatabasePath(SQLiteHelper.DB_NAME)
                        )
                    }
                }
            )
        )
    }

    fun triggerPaymentsClearance() {
        showTwoButtonsDialog(
            SimpleDialogDataModel(
                title = resources.getString(R.string.key_warning_label),
                message = resources.getString(R.string.key_clear_all_payments_message),
                icon = R.drawable.ic_clear_all,
                positiveButtonText = resources.getString(R.string.key_clear_label),
                positiveButtonBackgroundColor = ContextCompat.getColor(this, R.color.red),
                positiveButtonBlock = {
                    uiScope.launch {
                        dbManager.clearPayments(
                            userId = userModel?.id
                        ) {
                            (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.initializePayments()
                        }
                    }
                }
            )
        )
    }

    private fun showNewPersonUnsavedFieldsWarning() {
        if ((supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.fieldsHaveChanged == true)
            showTwoButtonsDialog(
                SimpleDialogDataModel(
                    title = resources.getString(R.string.key_warning_label),
                    message = resources.getString(R.string.key_unsaved_changes_message),
                    negativeButtonText = resources.getString(R.string.key_discard_label),
                    negativeButtonBlock = {
                        super.onBackPressed()
                    },
                    positiveButtonText = resources.getString(R.string.key_save_label),
                    positiveButtonBlock = {
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.checkInputFields()
                    }
                )
            )
        else
            super.onBackPressed()
    }

    private fun showNewPaymentUnsavedFieldsWarning() {
        if ((supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.fieldsHaveChanged == true)
            showTwoButtonsDialog(
                SimpleDialogDataModel(
                    title = resources.getString(R.string.key_warning_label),
                    message = resources.getString(R.string.key_unsaved_changes_message),
                    negativeButtonText = resources.getString(R.string.key_discard_label),
                    negativeButtonBlock = {
                        super.onBackPressed()
                    },
                    positiveButtonText = resources.getString(R.string.key_save_label),
                    positiveButtonBlock = {
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.checkInputFields()
                    }
                )
            )
        else
            super.onBackPressed()
    }

    fun setFloatingButtonAsPaymentRemovalButton() {
        floatingButtonType = FloatingButtonType.NEGATIVE
        floatingButtonImage = R.drawable.ic_delete
        binding.appBarMain.floatingButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
    }

    fun returnFloatingButtonBackToNormal() {
        floatingButtonType = FloatingButtonType.NORMAL
        floatingButtonImage = R.drawable.ic_check
        binding.appBarMain.floatingButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
    }

    fun startGroupActivity(groupModel: GroupModel? = null) {
        activityLauncher?.launch(
            input = Intent(this, GroupActivity::class.java).also {
                it.putExtra(GroupActivity.GROUP_MODEL_EXTRA, groupModel)
            }
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                configureGroup(
                    groupModel = result.data?.extras?.getParcelable(GroupActivity.GROUP_MODEL_EXTRA) ?: return@launch
                ) {
                    (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.initializePayments()
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            QR_CODE_CAMERA_PERMISSION_REQUEST_CODE ->
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.redirectToQrCodeFragment(
                        qrCodeSelectionType = QRCodeSelectionType.SCAN
                    )
        }
    }

    override fun onPause() {
        unregisterReceiver(
            notificationDataReceiver
        )
        super.onPause()
    }

}
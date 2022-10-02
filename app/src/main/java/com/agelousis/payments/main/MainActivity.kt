package com.agelousis.payments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
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
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.enumerations.FloatingButtonPosition
import com.agelousis.payments.main.ui.clientsSelector.ClientsSelectorDialogFragment
import com.agelousis.payments.main.ui.files.InvoicesFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.extensions.redirectToGroupModificationFragment
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
        bottomAppBarState = true
        ((binding.appBarMain.floatingButton.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? HideBottomViewOnScrollBehavior)?.slideUp(
            binding.appBarMain.floatingButton
        )
        bottomNavigationViewIsVisible = destination.id == R.id.paymentsFragment
                || destination.id == R.id.invoicesFragment
                || destination.id == R.id.historyFragment
                || destination.id == R.id.personalInformationFragment
        bottomAppBarContentInsetState = destination.id != R.id.paymentsFragment
                && destination.id != R.id.invoicesFragment
                && destination.id != R.id.historyFragment
                && destination.id != R.id.personalInformationFragment
        statusBarColor = when(destination.id) {
            R.id.personalInformationFragment ->
                ContextCompat.getColor(this, R.color.colorPrimaryLighter)
            else ->
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
        when(destination.id) {
            R.id.personalInformationFragment -> {
                navigationIcon = null
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonState = true
            }
            R.id.paymentsFragment -> {
                navigationIcon = null
                appBarTitle = ""
                floatingButtonImage = R.drawable.ic_add_group
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonState = true
            }
            R.id.groupModificationFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                floatingButtonState = false
            }
            R.id.newPaymentFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_client_info_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonState = true
            }
            R.id.newPaymentAmountFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonState = true
            }
            R.id.invoicesFragment -> {
                navigationIcon = null
                appBarTitle = ""
                floatingButtonImage = R.drawable.ic_delete
                floatingButtonPosition = FloatingButtonPosition.CENTER
            }
            R.id.periodFilterFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_filter_period_label)
                floatingButtonImage = R.drawable.ic_table
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonState = true
            }
            R.id.historyFragment -> {
                navigationIcon = null
                floatingButtonImage = R.drawable.ic_left_and_right
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonState = false
            }
            R.id.pdfViewerFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = ""
                floatingButtonImage = R.drawable.ic_share
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonState = true
            }
            R.id.filterPaymentsFragment -> {
                navigationIcon = getDrawableFromAttribute(
                    attributeId = R.attr.homeAsUpIndicator
                )
                appBarTitle = resources.getString(R.string.key_clients_order_label)
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
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
            R.id.paymentsFragment ->
                (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.redirectToGroupModificationFragment()
            R.id.newPaymentFragment ->
                (supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.checkInputFields()
            R.id.newPaymentAmountFragment ->
                (supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.checkInputFields()
            R.id.periodFilterFragment ->
                (supportFragmentManager.currentNavigationFragment as? PeriodFilterFragment)?.initializeExportToExcelOperation()
            R.id.pdfViewerFragment ->
                (supportFragmentManager.currentNavigationFragment as? PdfViewerFragment)?.sharePDF()
            R.id.filterPaymentsFragment ->
                (supportFragmentManager.currentNavigationFragment as? FilterPaymentsFragment)?.saveFiltersAndDismiss()
            R.id.invoicesFragment ->
                (supportFragmentManager.currentNavigationFragment as? InvoicesFragment)?.onDeleteInvoices()
        }
    }

    lateinit var binding: ActivityMainBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)
    val userModel by lazy {
        intent?.extras?.getParcelable<UserModel?>(USER_MODEL_EXTRA)
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
            else
                binding.appBarMain.floatingButton.hide()
        }
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
    var paymentsSize: Int? = null
        set(value) {
            field = value
            binding.appBarMain.bottomNavigationView.getOrCreateBadge(R.id.paymentsFragment).apply {
                isVisible = value != null
                number = value ?: return@apply
            }
        }
    var invoicesSize: Int? = null
        set(value) {
            field = value
            binding.appBarMain.bottomNavigationView.getOrCreateBadge(R.id.invoicesFragment).apply {
                isVisible = value != null
                number = value ?: return@apply
            }
        }
    var bottomAppBarState: Boolean = true
        set(value) {
            field = value
            if (value)
                binding.appBarMain.bottomAppBar.performShow()
            else
                binding.appBarMain.bottomAppBar.performHide()
        }
    private var statusBarColor: Int? = null
        set(value) {
            field = value
            window?.statusBarColor = value ?: return
        }

    @SuppressLint("UnsafeOptInUsageError")
    private fun configureBackAction() {
        if (BuildCompat.isAtLeastT())
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                when (binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().currentDestination?.id) {
                    R.id.historyFragment ->
                        showLogoutDialog()
                    R.id.newPaymentFragment ->
                        showNewPersonUnsavedFieldsWarning()
                    R.id.newPaymentAmountFragment ->
                        showNewPaymentUnsavedFieldsWarning()
                }
            }
        else
            onBackPressedDispatcher.addCallback(
                this, // lifecycle owner
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        when (binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().currentDestination?.id) {
                            R.id.historyFragment ->
                                showLogoutDialog()
                            R.id.newPaymentFragment ->
                                showNewPersonUnsavedFieldsWarning()
                            R.id.newPaymentAmountFragment ->
                                showNewPaymentUnsavedFieldsWarning()
                        }
                    }
                }
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.isEdgeToEdge = true
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBackAction()
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
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupUI() {
        binding.appBarMain.floatingButton.setOnClickListener(this)
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
                        DBManager.clearPayments(
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
                        onBackPressedDispatcher.onBackPressed()
                    },
                    positiveButtonText = resources.getString(R.string.key_save_label),
                    positiveButtonBlock = {
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.checkInputFields()
                    }
                )
            )
        else
            onBackPressedDispatcher.onBackPressed()
    }

    private fun showNewPaymentUnsavedFieldsWarning() {
        if ((supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.fieldsHaveChanged == true)
            showTwoButtonsDialog(
                SimpleDialogDataModel(
                    title = resources.getString(R.string.key_warning_label),
                    message = resources.getString(R.string.key_unsaved_changes_message),
                    negativeButtonText = resources.getString(R.string.key_discard_label),
                    negativeButtonBlock = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    positiveButtonText = resources.getString(R.string.key_save_label),
                    positiveButtonBlock = {
                        (supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.checkInputFields()
                    }
                )
            )
        else
            onBackPressedDispatcher.onBackPressed()
    }

    private fun showLogoutDialog() {
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
package com.agelousis.payments.main

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.agelousis.payments.R
import com.agelousis.payments.base.BaseActivity
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.databinding.ActivityMainBinding
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.group.GroupActivity
import com.agelousis.payments.guide.GuideActivity
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.enumerations.FloatingButtonPosition
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.materialMenu.MaterialMenuDialogFragment
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.materialMenu.models.MaterialMenuDataModel
import com.agelousis.payments.main.materialMenu.presenters.MaterialMenuFragmentPresenter
import com.agelousis.payments.main.ui.clientsSelector.ClientsSelectorDialogFragment
import com.agelousis.payments.main.ui.files.FilesFragment
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
import com.agelousis.payments.profilePicture.ProfilePictureActivity
import com.agelousis.payments.receivers.NotificationDataReceiver
import com.agelousis.payments.receivers.interfaces.NotificationListener
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener, View.OnClickListener, MaterialMenuFragmentPresenter, NotificationListener {

    companion object {
        const val USER_MODEL_EXTRA = "MainActivity=userModelExtra"
        const val FIREBASE_NOTIFICATION_DATA_EXTRA = "MainActivity=firebaseNotificatonDataExtra"
        const val QR_CODE_CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMaterialMenuOptionSelected(materialMenuOption: MaterialMenuOption) {
        when(materialMenuOption) {
            MaterialMenuOption.HOME -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack()
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_paymentsFragment)
            }
            MaterialMenuOption.PROFILE -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.personalInformationFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_personalInformation)
            }
            MaterialMenuOption.INVOICES -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.filesFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_filesFragment)
            }
            MaterialMenuOption.HISTORY -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.historyFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_historyFragment)
            }
            MaterialMenuOption.GUIDE ->
                showGuide()
        }
    }

    override fun onMaterialMenuProfileIconClicked() {
        showProfilePicture()
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
        when(destination.id) {
            R.id.personalInformationFragment -> {
                appBarTitle = resources.getString(R.string.key_profile_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.paymentsFragment -> {
                appBarTitle = ""
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_add_group
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.colorAccent
            }
            R.id.newPaymentFragment -> {
                appBarTitle = resources.getString(R.string.key_client_info_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.newPaymentAmountFragment -> {
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.filesFragment -> {
                appBarTitle = resources.getString(R.string.key_invoices_label)
                floatingButtonImage = R.drawable.ic_delete
                floatingButtonState = false
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.red
            }
            R.id.periodFilterFragment -> {
                appBarTitle = resources.getString(R.string.key_filter_period_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_table
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.historyFragment -> {
                appBarTitle = resources.getString(R.string.key_history_label)
                floatingButtonState = false
            }
            R.id.pdfViewerFragment -> {
                appBarTitle = resources.getString(R.string.key_invoice_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_share
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.filterPaymentsFragment -> {
                appBarTitle = resources.getString(R.string.key_clients_order_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
            }
            R.id.QRCodeFragment -> {
                appBarTitle = resources.getString(R.string.key_qr_code_label)
                floatingButtonState = false
            }
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
            R.id.filesFragment ->
                (supportFragmentManager.currentNavigationFragment as? FilesFragment)?.configureDeleteAction()
            R.id.pdfViewerFragment ->
                (supportFragmentManager.currentNavigationFragment as? PdfViewerFragment)?.sharePDF()
            R.id.filterPaymentsFragment ->
                (supportFragmentManager.currentNavigationFragment as? FilterPaymentsFragment)?.saveFiltersAndDismiss()
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

    override fun onBackPressed() {
        when(supportFragmentManager.currentNavigationFragment) {
            is PaymentsFragment ->
                showSimpleDialog(
                    title = resources.getString(R.string.key_logout_label),
                    message = resources.getString(R.string.key_logout_message),
                    icon = R.drawable.ic_logout
                ) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            is NewPaymentFragment ->
                showNewPersonUnsavedFieldsWarning()
            is NewPaymentAmountFragment ->
                showNewPaymentUnsavedFieldsWarning()
            else -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().previousBackStackEntry?.savedStateHandle?.remove<PaymentAmountModel>(NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA)
                super.onBackPressed()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isLandscape)
            window?.hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupUI()
        addInactiveGroup()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        configureNavigationController()
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
        //binding.appBarMain.bottomAppBar.replaceMenu(R.menu.activity_menu_main)
        binding.appBarMain.bottomAppBar.setNavigationOnClickListener {
            showMaterialMenuFragment()
        }
    }

    private fun showMaterialMenuFragment() {
        MaterialMenuDialogFragment.show(
            supportFragmentManager = supportFragmentManager,
            materialMenuDataModel = materialMenuDataModel,
            materialMenuFragmentPresenter = this
        )
    }

    private fun setupUI() {
        binding.appBarMain.floatingButton.setOnClickListener(this)
    }

    private fun addInactiveGroup() {
        uiScope.launch {
            dbManager.insertGroups(
                userId = userModel?.id,
                groupModelList = listOf(
                    GroupModel(
                        groupName = resources.getString(R.string.key_inactive_label),
                        color = ContextCompat.getColor(this@MainActivity, R.color.grey)
                    )
                )
            ) {}
        }
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
    }

    fun triggerPaymentsClearance() {
        showTwoButtonsDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_clear_all_payments_message),
            icon = R.drawable.ic_clear_all,
            positiveButtonText = resources.getString(R.string.key_clear_label),
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
    }

    private fun showGuide() {
        startActivity(Intent(this, GuideActivity::class.java))
    }

    private fun showNewPersonUnsavedFieldsWarning() {
        if ((supportFragmentManager.currentNavigationFragment as? NewPaymentFragment)?.fieldsHaveChanged == true)
            showTwoButtonsDialog(
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
        else
            super.onBackPressed()
    }

    private fun showNewPaymentUnsavedFieldsWarning() {
        if ((supportFragmentManager.currentNavigationFragment as? NewPaymentAmountFragment)?.fieldsHaveChanged == true)
            showTwoButtonsDialog(
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

    fun showProfilePicture() {
        startActivity(
            Intent(
                this,
                ProfilePictureActivity::class.java
            ).also {
                it.putExtra(USER_MODEL_EXTRA, userModel)
            }
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
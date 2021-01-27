package com.agelousis.payments.main

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.agelousis.payments.R
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.databinding.ActivityMainBinding
import com.agelousis.payments.databinding.NavHeaderMainBinding
import com.agelousis.payments.group.GroupActivity
import com.agelousis.payments.guide.GuideActivity
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.enumerations.FloatingButtonPosition
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.menuOptions.PaymentsMenuOptionsBottomSheetFragment
import com.agelousis.payments.main.ui.history.HistoryFragment
import com.agelousis.payments.main.ui.files.FilesFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.paymentsFiltering.FilterPaymentsFragment
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.pdfViewer.PdfViewerFragment
import com.agelousis.payments.main.ui.periodFilter.PeriodFilterFragment
import com.agelousis.payments.main.ui.personalInformation.PersonalInformationFragment
import com.agelousis.payments.profilePicture.ProfilePictureActivity
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener, View.OnClickListener {

    companion object {
        const val USER_MODEL_EXTRA = "MainActivity=userModelExtra"
        const val EXPORT_FILE_REQUEST_CODE = 1
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigationHome -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack()
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_paymentsFragment)
            }
            R.id.navigationProfile -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.personalInformationFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_personalInformation)
            }
            R.id.navigationFiles -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.filesFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_filesFragment)
            }
            R.id.navigationGraph -> {
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().popBackStack(R.id.historyFragment, true)
                binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().navigate(R.id.action_global_historyFragment)
            }
            R.id.navigationGuide ->
                showGuide()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when(destination.label ?: "") {
            in PersonalInformationFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_profile_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
            }
            in PaymentsFragment::class.java.name -> {
                appBarTitle = ""
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_add_group
                floatingButtonPosition = FloatingButtonPosition.CENTER
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = true
            }
            in NewPaymentFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_person_info_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
            }
            in NewPaymentAmountFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
            }
            in FilesFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_files_label)
                floatingButtonImage = R.drawable.ic_delete
                floatingButtonState = false
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.red
                menuOptionsIsVisible = false
            }
            in PeriodFilterFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_filter_period_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_table
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
            }
            in HistoryFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_history_label)
                floatingButtonState = false
                menuOptionsIsVisible = false
            }
            in PdfViewerFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_pdf_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_share
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
            }
            in FilterPaymentsFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_filter_payments_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                floatingButtonPosition = FloatingButtonPosition.END
                floatingButtonTint = R.color.colorAccent
                menuOptionsIsVisible = false
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
    private var menuOptionsIsVisible = false
        set(value) {
            field = value
            binding.appBarMain.bottomAppBar.menu?.findItem(R.id.menuSettings)?.isVisible = value
        }
    var historyButtonIsVisible = false
        set(value) {
            field = value
            binding.navigationView.menu.findItem(R.id.navigationGraph)?.isVisible = value
        }
    var paymentsFilteringOptionTypes =
        listOf(
            PaymentsFilteringOptionType.DATE,
            PaymentsFilteringOptionType.FREE,
            PaymentsFilteringOptionType.CHARGE,
            PaymentsFilteringOptionType.INACTIVE
        )

    override fun onBackPressed() {
        unCheckAllMenuItems(
            menu = binding.navigationView.menu
        )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupNavigationView()
        setupUI()
        addInactiveGroup()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        configureNavigationController()
    }

    private fun configureNavigationController() {
        binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController().addOnDestinationChangedListener(this)
        ifLet(
            binding.navigationView,
            binding.appBarMain.contentMain.navHostFragmentContainerView.findNavController()
        ) {
            NavigationUI.setupWithNavController(it.first() as NavigationView, it.second() as NavController)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBarMain.bottomAppBar)
        binding.appBarMain.bottomAppBar.replaceMenu(R.menu.activity_menu_main)
    }

    private fun setupNavigationView() {
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.bottomAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationView.getHeaderView(0)?.let {
            val navHeaderBinding = NavHeaderMainBinding.bind(it)
            navHeaderBinding.navigationViewProfileImageView.loadImagePath(
                fileName = userModel?.profileImage
            )
            navHeaderBinding.navigationViewProfileImageView.setOnClickListener {
                showProfilePicture()
            }
            navHeaderBinding.navigationViewProfileUsername.text = userModel?.fullName ?: resources.getString(R.string.key_empty_field_label)
        }
    }

    private fun setupUI() {
        binding.appBarMain.floatingButton.setOnClickListener(this)
    }

    private fun addInactiveGroup() {
        uiScope.launch {
            dbManager.insertGroup(
                userId = userModel?.id,
                groupModel = GroupModel(
                    groupName = resources.getString(R.string.key_inactive_label),
                    color = ContextCompat.getColor(this@MainActivity, R.color.grey)
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
            } ?: dbManager.insertGroup(
                    userId = userModel?.id,
                    groupModel = groupModel,
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
                saveFile(
                    requestCode = EXPORT_FILE_REQUEST_CODE,
                    fileName = Constants.EXPORT_DATABASE_FILE_NAME,
                    mimeType = Constants.GENERAL_MIME_TYPE
                )
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

    private fun unCheckAllMenuItems(menu: Menu?) {
        for (i in 0 until (menu?.size() ?: 0)) {
            val item = menu?.getItem(i)
            if(item?.hasSubMenu() == true)
                unCheckAllMenuItems(item.subMenu)
            else
                item?.isChecked = false
        }
    }

    private fun showPaymentsMenuOptionsFragment() {
        PaymentsMenuOptionsBottomSheetFragment.show(
            supportFragmentManager = supportFragmentManager
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

    fun startGroupActivity(groupModel: GroupModel? = null) =
        startActivityForResult(
            Intent(this, GroupActivity::class.java).also {
                it.putExtra(GroupActivity.GROUP_MODEL_EXTRA, groupModel)
            },
            GroupActivity.GROUP_SELECTION_REQUEST_CODE
        )

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when(requestCode) {
                EXPORT_FILE_REQUEST_CODE ->
                    alterFile(
                        uri = data?.data,
                        file = getDatabasePath(SQLiteHelper.DB_NAME)
                    )
                GroupActivity.GROUP_SELECTION_REQUEST_CODE ->
                    configureGroup(
                        groupModel = data?.extras?.getParcelable(GroupActivity.GROUP_MODEL_EXTRA) ?: return
                    ) {
                        (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.initializePayments()
                    }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu_main, menu)
        menu?.findItem(R.id.menuSettings)?.isVisible = menuOptionsIsVisible
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSettings ->
                showPaymentsMenuOptionsFragment()
        }
        return super.onOptionsItemSelected(item)
    }

}
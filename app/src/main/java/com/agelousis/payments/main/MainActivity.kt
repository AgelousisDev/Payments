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
import com.agelousis.payments.group.GroupActivity
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.enumerations.FloatingButtonType
import com.agelousis.payments.main.ui.files.FilesFragment
import com.agelousis.payments.main.ui.newPayment.NewPaymentFragment
import com.agelousis.payments.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.periodFilter.PeriodFilterFragment
import com.agelousis.payments.main.ui.personalInformation.PersonalInformationFragment
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
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
                navHostFragmentContainerView.findNavController().popBackStack()
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_paymentsFragment)
            }
            R.id.navigationProfile -> {
                navHostFragmentContainerView.findNavController().popBackStack(R.id.personalInformationFragment, true)
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_personalInformation)
            }
            R.id.navigationFiles -> {
                navHostFragmentContainerView.findNavController().popBackStack(R.id.filesFragment, true)
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_filesFragment)
            }
            R.id.navigationExport ->
                initializeDatabaseExport()
            R.id.navigationClearPayments ->
                triggerPaymentsClearance()
            R.id.navigationExcelExport ->
                (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.navigateToPeriodFilterFragment()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when(destination.label ?: "") {
            in PersonalInformationFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_profile_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                clearPaymentsMenuItemIsVisible = false
                exportToExcelMenuItemIsVisible = false
            }
            in PaymentsFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.app_name)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_add
                clearPaymentsMenuItemIsVisible = true
                exportToExcelMenuItemIsVisible = true
            }
            in NewPaymentFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_person_info_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                clearPaymentsMenuItemIsVisible = false
                exportToExcelMenuItemIsVisible = false
            }
            in NewPaymentAmountFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                clearPaymentsMenuItemIsVisible = false
                exportToExcelMenuItemIsVisible = false
            }
            in FilesFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_files_label)
                floatingButtonState = false
                clearPaymentsMenuItemIsVisible = false
                exportToExcelMenuItemIsVisible = false
            }
            in PeriodFilterFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_filter_period_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
                clearPaymentsMenuItemIsVisible = false
                exportToExcelMenuItemIsVisible = false
            }
        }
    }

    override fun onClick(p0: View?) {
        when(navHostFragmentContainerView.findNavController().currentDestination?.id) {
            R.id.personalInformationFragment ->
                uiScope.launch {
                    (supportFragmentManager.currentNavigationFragment as? PersonalInformationFragment)?.updateUser {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
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
        }
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val dbManager by lazy { DBManager(context = this) }
    val userModel by lazy { intent?.extras?.getParcelable<UserModel>(USER_MODEL_EXTRA) }
    private var appBarTitle: String? = null
        set(value) {
            field = value
            value?.let {
                bottomAppBarTitle.text = it
            }
        }
    @DrawableRes private var floatingButtonImage: Int = R.drawable.ic_add
        set(value) {
            field = value
            floatingButton.hide()
            floatingButton.setImageResource(value)
            floatingButton.show()
        }
    private var floatingButtonState: Boolean = true
        set(value) {
            field = value
            if (value)
                floatingButton.show()
            else floatingButton.hide()
        }
    var floatingButtonType = FloatingButtonType.NORMAL
    var clearPaymentsMenuItemIsVisible = false
        set(value) {
            field = value
            navigationView?.menu?.findItem(R.id.navigationClearPayments)?.isVisible = value
        }
    var exportToExcelMenuItemIsVisible = false
        set(value) {
            field = value
            navigationView?.menu?.findItem(R.id.navigationExcelExport)?.isVisible = value
        }

    override fun onBackPressed() {
        unCheckAllMenuItems(
            menu = navigationView.menu
        )
        if (supportFragmentManager.currentNavigationFragment is PaymentsFragment)
            showSimpleDialog(
                title = resources.getString(R.string.key_logout_label),
                message = resources.getString(R.string.key_logout_message)
            ) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        else {
            navHostFragmentContainerView.findNavController().previousBackStackEntry?.savedStateHandle?.remove<PaymentAmountModel>(NewPaymentAmountFragment.PAYMENT_AMOUNT_DATA_EXTRA)
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        navHostFragmentContainerView.findNavController().addOnDestinationChangedListener(this)
        NavigationUI.setupWithNavController(navigationView, navHostFragmentContainerView.findNavController())
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(bottomAppBar)
    }

    private fun setupNavigationView() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, bottomAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.getHeaderView(0).navigationViewProfileImageView.loadImagePath(
            fileName = userModel?.profileImage
        )
        navigationView.getHeaderView(0).navigationViewProfileUsername.text = userModel?.username ?: resources.getString(R.string.key_empty_field_label)
    }

    private fun setupUI() {
        floatingButton.setOnClickListener(this)
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

    private fun initializeDatabaseExport() {
        showTwoButtonsDialog(
            title = resources.getString(R.string.key_export_label),
            message = resources.getString(R.string.key_export_message),
            positiveButtonText = resources.getString(R.string.key_proceed_label),
            positiveButtonBlock = {
                saveFile(
                    requestCode = EXPORT_FILE_REQUEST_CODE,
                    fileName = SQLiteHelper.DB_NAME,
                    mimeType = Constants.GENERAL_MIME_TYPE
                )
            }
        )
    }

    private fun triggerPaymentsClearance() {
        showTwoButtonsDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_clear_all_payments_message),
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

    private fun unCheckAllMenuItems(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if(item.hasSubMenu())
                unCheckAllMenuItems(item.subMenu)
            else
                item.isChecked = false
        }
    }

    fun setFloatingButtonAsPaymentRemovalButton() {
        floatingButtonType = FloatingButtonType.NEGATIVE
        floatingButtonImage = R.drawable.ic_delete
        floatingButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
    }

    fun returnFloatingButtonBackToNormal() {
        floatingButtonType = FloatingButtonType.NORMAL
        floatingButtonImage = R.drawable.ic_check
        floatingButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
    }

    fun startGroupActivity(groupModel: GroupModel? = null) =
        startActivityForResult(
            Intent(this, GroupActivity::class.java).also {
                it.putExtra(GroupActivity.GROUP_MODEL_EXTRA, groupModel)
            },
            GroupActivity.GROUP_SELECTION_REQUEST_CODE
        )

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

}
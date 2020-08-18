package com.agelousis.monthlyfees.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.login.LoginActivity
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.newPayment.NewPaymentFragment
import com.agelousis.monthlyfees.main.ui.newPaymentAmount.NewPaymentAmountFragment
import com.agelousis.monthlyfees.main.ui.payments.PaymentsFragment
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.personalInformation.PersonalInformationFragment
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.*
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigationHome -> {
                navHostFragmentContainerView.findNavController().popBackStack()
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_paymentListFragment)
            }
            R.id.navigationProfile -> {
                navHostFragmentContainerView.findNavController().popBackStack(R.id.personalInformationFragment, true)
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_personalInformation)
            }
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
            }
            in PaymentsFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.app_name)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_add
            }
            in NewPaymentFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_person_info_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
            }
            in NewPaymentAmountFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_add_payment_label)
                floatingButtonState = true
                floatingButtonImage = R.drawable.ic_check
            }
        }
    }

    override fun onClick(p0: View?) {
        when(navHostFragmentContainerView.findNavController().currentDestination?.id) {
            R.id.personalInformationFragment ->
                uiScope.launch {
                    (supportFragmentManager.currentNavigationFragment as? PersonalInformationFragment)?.updateUser { newUserModel ->
                        sharedPreferences.userModel = sharedPreferences.userModel?.also {
                            it.biometrics = newUserModel?.biometrics
                        }
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            R.id.paymentListFragment ->
                showGroupInputDialog(
                    inputHint = resources.getString(R.string.key_group_name_label),
                    positiveButtonText = resources.getString(R.string.key_add_label),
                    inputGroupDialogBlock = {
                        insertGroup(
                            groupModel = it
                        )
                    }
                )
        }
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val dbManager by lazy { DBManager(context = this) }
    private val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) }
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
            floatingButton.setImageResource(value)
        }
    private var floatingButtonState: Boolean = true
        set(value) {
            field = value
            if (value)
                floatingButton.show()
            else floatingButton.hide()
        }

    override fun onBackPressed() {
        if (supportFragmentManager.currentNavigationFragment is PaymentsFragment)
            showSimpleDialog(
                title = resources.getString(R.string.key_logout_label),
                message = resources.getString(R.string.key_logout_message)
            ) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        else
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupNavigationView()
        setupUI()
        /*uiScope.launch {
            dbManager.insertPayment(
                userId = userModel?.id,
                paymentModel = PaymentModel(
                    1, 1, "FirstGroup", "Vagelis", "76565657", "Agelousis", "65327653276", "agelousakos@gmail.com", true, false, PaymentAmountModel(12.0, "12-12-2000", true, "uwgdjwudwegugywe")
                )
            ) {

            }
        }*/
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
            path = userModel?.profileImage
        )
        navigationView.getHeaderView(0).navigationViewProfileUsername.text = userModel?.username ?: resources.getString(R.string.key_empty_field_label)
    }

    private fun setupUI() {
        floatingButton.setOnClickListener(this)
    }

    private fun insertGroup(groupModel: GroupModel) {
        uiScope.launch {
            dbManager.insertGroup(
                userId = userModel?.id,
                groupModel = groupModel
            ) {
                (supportFragmentManager.currentNavigationFragment as? PaymentsFragment)?.initializePayments()
            }
        }
    }

}
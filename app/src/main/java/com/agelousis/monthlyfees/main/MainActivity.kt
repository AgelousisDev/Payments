package com.agelousis.monthlyfees.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.login.LoginActivity
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.PaymentListFragment
import com.agelousis.monthlyfees.main.ui.settings.SettingsFragment
import com.agelousis.monthlyfees.utils.extensions.currentNavigationFragment
import com.agelousis.monthlyfees.utils.extensions.loadImagePath
import com.agelousis.monthlyfees.utils.extensions.showSimpleDialog
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener {

    companion object {
        const val USER_MODEL_EXTRA = "MainActivity=userModelExtra"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigationHome -> {
                navHostFragmentContainerView.findNavController().popBackStack()
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_paymentListFragment)
            }
            R.id.navigationSettings -> {
                navHostFragmentContainerView.findNavController().popBackStack(R.id.settingsFragment, true)
                navHostFragmentContainerView.findNavController().navigate(R.id.action_global_settingsFragment)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when(destination.label ?: "") {
            in SettingsFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.key_settings_label)
                floatingButtonState = false
            }
            in PaymentListFragment::class.java.name -> {
                appBarTitle = resources.getString(R.string.app_name)
                floatingButtonState = true
            }
        }
    }

    val userModel by lazy { intent?.extras?.getParcelable<UserModel>(USER_MODEL_EXTRA) }
    private var appBarTitle: String? = null
        set(value) {
            field = value
            value?.let {
                bottomAppBarTitle.text = it
            }
        }
    private var floatingButtonState: Boolean = true
        set(value) {
            field = value
            if (value)
                addGroupButton.show()
            else addGroupButton.hide()
        }

    override fun onBackPressed() {
        if (supportFragmentManager.currentNavigationFragment is PaymentListFragment)
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

}
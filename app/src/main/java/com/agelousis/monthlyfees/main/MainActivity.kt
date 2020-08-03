package com.agelousis.monthlyfees.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.utils.extensions.loadImagePath
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val USER_MODEL_EXTRA = "MainActivity=userModelExtra"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    private val userModel by lazy { intent?.extras?.getParcelable<UserModel>(USER_MODEL_EXTRA) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupNavigationView()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupNavigationView() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.getHeaderView(0).navigationViewProfileImageView.loadImagePath(
            path = userModel?.profileImage
        )
        navigationView.getHeaderView(0).navigationViewProfileUsername.text = userModel?.username ?: resources.getString(R.string.key_empty_field_label)
    }

}
package com.agelousis.payments.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.biometrics.BiometricsHelper
import com.agelousis.payments.biometrics.BiometricsListener
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.databinding.ActivityLoginBinding
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.login.presenter.LoginPresenter
import com.agelousis.payments.login.viewModel.LoginViewModel
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.userSelection.UserSelectionFragment
import com.agelousis.payments.userSelection.presenters.UserSelectionPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity(), LoginPresenter, BiometricsListener, UserSelectionPresenter {

    private var binding: ActivityLoginBinding? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val userModel by lazy { UserModel() }
    private val sharedPreferences by lazy {
        getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
    }
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private var dbManager: DBManager? = null
    private var signInState = SignInState.SIGN_UP

    companion object {
        const val PROFILE_SELECT_REQUEST_CODE = 1
        const val IMPORT_FILE_REQUEST_CODE = 2
    }

    override fun onProfileSelect() =
        openGallery(
            requestCode = PROFILE_SELECT_REQUEST_CODE
        )

    override fun onSignIn() {
        when (signInState) {
            SignInState.SIGN_UP ->
                showBiometricsAlert(
                    predicate = {
                        hasBiometrics && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    }
                ) { biometricsState ->
                    userModel.username = usernameField.text?.toString()
                    userModel.password = passwordField.text?.toString()
                    userModel.biometrics = biometricsState
                    dbManager?.userModel = userModel
                    if (keepMeSignedInCheckBox.isChecked)
                        sharedPreferences.userModel = userModel
                    else sharedPreferences.userModel = null
                    uiScope.launch {
                        dbManager?.searchUser(
                            userModel = userModel
                        ) { userModel ->
                            this@LoginActivity.userModel.id = userModel?.id
                            showMainActivity(
                                userModel = this@LoginActivity.userModel
                            )
                        }
                    }
                }
            SignInState.LOGIN ->
                uiScope.launch {
                    dbManager?.searchUser(
                        userModel = UserModel(
                            username = usernameField.text?.toString(),
                            password = passwordField.text?.toString()
                        )
                    ) { userModel ->
                        if (userModel == null)
                            Toast.makeText(
                                this@LoginActivity,
                                resources.getString(R.string.key_wrong_credentials_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        else {
                            if (keepMeSignedInCheckBox.isChecked)
                                sharedPreferences.userModel = userModel
                            else
                                sharedPreferences.userModel = null
                            showMainActivity(
                                userModel = userModel
                            )
                        }
                    }
                }
        }
    }

    override fun onImport() {
        initializeDatabaseImport()
    }

    override fun onBiometricsSucceed() {
        Handler(Looper.getMainLooper()).postDelayed({
            uiScope.launch {
                dbManager?.searchUser(
                    userModel = sharedPreferences.userModel ?: return@launch
                ) { userModel ->
                    if (userModel == null)
                        Toast.makeText(
                            this@LoginActivity,
                            resources.getString(R.string.key_wrong_credentials_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        showMainActivity(
                            userModel = userModel
                        )
                }
            }
        },
            500
        )
    }

    override fun onUserSelected(userModel: UserModel) {
        signInState = SignInState.LOGIN
        saveImage(
            fileName = userModel.profileImage,
            byteArray = userModel.profileImageData
        )
        binding?.signInState = signInState
        binding?.userModel = userModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            layoutInflater
        ).also {
            it.userModel = sharedPreferences.userModel
            it.signInState = signInState
            it.loginButtonState = sharedPreferences.userModel?.username?.isNotEmpty() == true && sharedPreferences.userModel?.password?.isNotEmpty() == true
            it.presenter = this
        }
        setContentView(
            binding?.root
        )
        dbManager = DBManager(
            context = this
        )
        addObservers()
        configureLoginState()
        setupUI()
    }

    private fun showBiometricsAlert(predicate: () -> Boolean, closure: (Boolean) -> Unit) {
        if (predicate())
            showTwoButtonsDialog(
                title = resources.getString(R.string.key_biometrics_label),
                message = resources.getString(R.string.key_enable_biometrics_label),
                negativeButtonBlock = {
                    closure(false)
                },
                positiveButtonBlock = {
                    closure(true)
                }
            )
        else closure(false)
    }

    private fun showBiometrics() {
        if (sharedPreferences.userModel?.biometrics == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Date().isValidProductDate)
            BiometricsHelper(
                biometricsListener = this
            ).showBiometricsPrompt(
                context = this
            )
    }

    private fun showMainActivity(userModel: UserModel) {
        startActivity(Intent(this, MainActivity::class.java).also {
            it.putExtra(MainActivity.USER_MODEL_EXTRA, userModel)
        })
        finish()
    }

    private fun addObservers() {
        viewModel.usersLiveData.observe(this) { users ->
            UserSelectionFragment.show(
                supportFragmentManager = supportFragmentManager,
                users = ArrayList(users)
            )
        }
        viewModel.groupsLiveData.observe(this) { groups ->
            groups.forEach { group ->
                saveImage(
                    fileName = group.groupImage ?: return@forEach,
                    byteArray = group.groupImageData ?: return@forEach
                )
            }
        }
    }

    private fun configureLoginState() {
        uiScope.launch {
            dbManager?.checkUsers {
                signInState = if (it.isNotEmpty()) SignInState.LOGIN else SignInState.SIGN_UP
                binding?.signInState = signInState
                profileImageView.isEnabled = signInState == SignInState.SIGN_UP
                if (signInState == SignInState.LOGIN)
                    showBiometrics()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        usernameField.doAfterTextChanged {
            binding?.loginButtonState = it?.isNotEmpty() == true && passwordField.text?.isNotEmpty() == true
        }
        passwordField.doAfterTextChanged {
            binding?.loginButtonState = it?.isNotEmpty() == true && usernameField.text?.isNotEmpty() == true
        }
        importLayout.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    importLabel.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                    importLine.background?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    importLabel.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    importLine.background?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.grey), PorterDuff.Mode.SRC_IN)
                }
            }
            false
        }
        checkProductDate()
    }

    private fun initializeUsers() =
        uiScope.launch {
            viewModel.initializeUsers(
                context = this@LoginActivity
            )
        }

    private fun initializeGroups() =
        uiScope.launch {
            viewModel.initializeGroups(
                context = this@LoginActivity
            )
        }

    private fun initializeDatabaseImport() {
        showTwoButtonsDialog(
            title = resources.getString(R.string.key_import_label),
            message = resources.getString(R.string.key_import_message),
            positiveButtonText = resources.getString(R.string.key_proceed_label),
            positiveButtonBlock = {
                searchFile(
                    requestCode = IMPORT_FILE_REQUEST_CODE,
                    mimeType = Constants.GENERAL_MIME_TYPE
                )
            }
        )
    }

    private fun makeDatabaseImport(uri: Uri?) {
        if (isDBFile(
                uri = uri
            ) || uri?.isGoogleDrive == true)
            replaceDatabase(
                byteArray = contentResolver.openInputStream(uri ?: return)?.readBytes()
            ) {
                when(it) {
                    true -> {
                        message(
                            message = resources.getString(R.string.key_database_successfully_imported_message)
                        )
                        after(
                            millis = 1000
                        ) {
                            initializeUsers()
                            initializeGroups()
                        }
                    }
                    false ->
                        message(
                            message = resources.getString(R.string.key_invalid_database_file_message)
                        )

                }
            }
        else
            message(
                message = resources.getString(R.string.key_invalid_database_file_message)
            )
    }

    private fun checkProductDate() {
        if (!Date().isValidProductDate)
            showSimpleDialog(
                isCancellable = false,
                title = resources.getString(R.string.key_warning_label),
                message = resources.getString(R.string.key_license_expired_message)
            ) {
                this@LoginActivity.finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        when(requestCode) {
            PROFILE_SELECT_REQUEST_CODE ->
                data?.data?.let { imageUri ->
                    profileImageView.loadImageBitmap(
                        imageUri = imageUri
                    ) { bitmap ->
                        userModel.profileImage = saveProfileImage(
                            bitmap = bitmap
                        )
                        userModel.profileImageData = bitmap?.byteArray
                    }
                    profileImageView.setBackgroundResource(0)
                    profileImageView.loadImageUri(
                        imageUri = imageUri
                    )
                }
            IMPORT_FILE_REQUEST_CODE ->
                makeDatabaseImport(
                    uri = data?.data
                )
        }
    }

}
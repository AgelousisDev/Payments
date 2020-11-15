package com.agelousis.payments.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.agelousis.payments.R
import com.agelousis.payments.biometrics.BiometricsHelper
import com.agelousis.payments.biometrics.BiometricsListener
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.databinding.ActivityLoginBinding
import com.agelousis.payments.login.enumerations.UIMode
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

class LoginActivity : AppCompatActivity(), LoginPresenter, BiometricsListener, UserSelectionPresenter, GestureDetector.OnGestureListener {

    private var binding: ActivityLoginBinding? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val userModel by lazy { UserModel() }
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private var dbManager: DBManager? = null
    private var signInState = SignInState.SIGN_UP
    private var mDetector: GestureDetectorCompat? = null

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
                        hasBiometrics
                    }
                ) { biometricsState ->
                    userModel.username = usernameField.text?.toString()
                    userModel.password = passwordField.text?.toString()
                    userModel.biometrics = biometricsState
                    userModel.vat = 0
                    userModel.defaultPaymentAmount = 0.0
                    userModel.defaultMessageTemplate = resources.getString(R.string.key_default_message_template_value)
                    dbManager?.userModel = userModel
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
                            cardView animateBackgroundColor ContextCompat.getColor(this@LoginActivity, R.color.red)
                        else {
                            showMainActivity(
                                userModel = userModel
                            )
                        }
                    }
                }
        }
    }

    override fun onSignUp() {
        signInState = SignInState.SIGN_UP
        binding?.signInState = signInState
        binding?.userModel = null
        binding?.loginButtonState = false
    }

    override fun onImport() {
        initializeDatabaseImport()
    }

    override fun onBiometrics() {
        binding?.biometricsActive = true
        showBiometrics(
            biometrics = true
        )
    }

    override fun onBiometricsSucceed() {
        uiScope.launch {
            dbManager?.searchUser(
                userModel = UserModel(
                    username = usernameField.text?.toString(),
                    password = passwordField.text?.toString()
                )
            ) { userModel ->
                if (userModel == null)
                    cardView animateBackgroundColor ContextCompat.getColor(this@LoginActivity, R.color.red)
                else
                    showMainActivity(
                        userModel = userModel
                    )
            }
        }
    }

    override fun onBiometricsCancelled() {
        binding?.biometricsActive = false
    }

    override fun onUserSelected(userModel: UserModel) {
        signInState = SignInState.LOGIN
        binding?.signInState = signInState
        binding?.userModel = userModel
        binding?.biometricsActive = userModel.biometrics == true
        binding?.loginButtonState = userModel.username?.isNotEmpty() == true && userModel.password?.isNotEmpty() == true
        showBiometrics(
            biometrics = userModel.biometrics == true
        )
    }

    override fun onUsersSelect() {
        viewModel.usersLiveData.value?.takeIf { it.isNotEmpty() }?.let {
            showUserSelectionFragment(
                users = it
            )
        }
    }

    override fun onUIModeChanged(uiMode: UIMode) {
        isNightMode = uiMode == UIMode.DARK_MODE
    }

    override fun onBackPressed() {
        when(signInState) {
            SignInState.SIGN_UP -> {
                signInState = SignInState.LOGIN
                binding?.signInState = signInState
            }
            else ->
                super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            layoutInflater
        ).also {
            it.signInState = signInState
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
        initializeUsers()
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

    private fun showBiometrics(biometrics: Boolean = false) {
        if (biometrics && Date().isValidProductDate)
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
            showUserSelectionFragment(
                users = users
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

    private fun showUserSelectionFragment(users: List<UserModel>) {
        if (supportFragmentManager.findFragmentByTag(Constants.USER_SELECTION_FRAGMENT_TAG) == null)
            UserSelectionFragment.show(
                supportFragmentManager = supportFragmentManager,
                users = ArrayList(users),
            )
    }

    private fun configureLoginState() {
        uiScope.launch {
            dbManager?.checkUsers {
                signInState = if (it.isNotEmpty()) SignInState.LOGIN else SignInState.SIGN_UP
                binding?.signInState = signInState
                profileImageView.isEnabled = signInState == SignInState.SIGN_UP
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
        mDetector = GestureDetectorCompat(this, this)
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
                    loadImageBitmap(
                        imageUri = imageUri
                    ) { bitmap ->
                        userModel.profileImage?.let {
                            deleteInternalFile(
                                fileName = it
                            )
                        }
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        return mDetector?.onTouchEvent(ev) == true
    }

    override fun onDown(event: MotionEvent) = true

    override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (event2.y + 100.0 < event1.y)
            showUserSelectionFragment(
                users = viewModel.usersLiveData.value?.takeIf { it.isNotEmpty() } ?: return true
            )
        return true
    }

    override fun onLongPress(event: MotionEvent) {}

    override fun onScroll(event1: MotionEvent, event2: MotionEvent, distanceX: Float, distanceY: Float) = true

    override fun onShowPress(event: MotionEvent) {}

    override fun onSingleTapUp(event: MotionEvent) = true

}
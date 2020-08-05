package com.agelousis.monthlyfees.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.biometrics.BiometricsHelper
import com.agelousis.monthlyfees.biometrics.BiometricsListener
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.databinding.ActivityLoginBinding
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.login.presenter.LoginPresenter
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.utils.constants.Constants
import com.agelousis.monthlyfees.utils.extensions.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), LoginPresenter, BiometricsListener {

    private var binding: ActivityLoginBinding? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val userModel by lazy { UserModel() }
    private val sharedPreferences by lazy {
        getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
    }
    private var dbManager: DBManager? = null
    private var signInState = SignInState.SIGN_UP

    companion object {
        const val PROFILE_SELECT_REQUEST_CODE = 1
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

    override fun onBiometricsSucceed() {
        Handler(Looper.getMainLooper()).postDelayed({
            showMainActivity(
                userModel = sharedPreferences.userModel ?: return@postDelayed
            )
            },
            500
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            layoutInflater
        ).also {
            it.userModel = sharedPreferences.userModel
            it.signInState = signInState
            it.presenter = this
        }
        setContentView(
            binding?.root
        )
        dbManager = DBManager(
            context = this
        )
        configureLoginState()
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
        if (sharedPreferences.userModel?.biometrics == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        when(requestCode) {
            PROFILE_SELECT_REQUEST_CODE ->
                data?.data?.let { imageUri ->
                    userModel.profileImage = saveProfileImage(
                        byteArray = contentResolver.openInputStream(imageUri)?.readBytes()
                    )
                    profileImageView.setBackgroundResource(0)
                    profileImageView.loadImageUri(
                        imageUri = imageUri
                    )
                }
        }
    }

}
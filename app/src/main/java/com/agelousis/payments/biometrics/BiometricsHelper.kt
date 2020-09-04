package com.agelousis.payments.biometrics

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

@RequiresApi(Build.VERSION_CODES.P)
class BiometricsHelper(private val biometricsListener: BiometricsListener): BiometricPrompt.AuthenticationCallback() {

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        biometricsListener.onBiometricsSucceed()
    }

    fun showBiometricsPrompt(context: Context) {
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.resources.getString(R.string.key_biometric_authentication_title))
            .setSubtitle(context.resources.getString(R.string.key_biometric_authentication_message))
            .setNegativeButtonText(context.resources.getString(R.string.key_cancel_label))
            .setConfirmationRequired(true)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()
        val biometricPrompt = BiometricPrompt(context as? AppCompatActivity ?: return, executor, this)
        biometricPrompt.authenticate(promptInfo)
    }

}
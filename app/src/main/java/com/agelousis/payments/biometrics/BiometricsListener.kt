package com.agelousis.payments.biometrics

interface BiometricsListener {
    fun onBiometricsCancelled()
    fun onBiometricsSucceed()
}
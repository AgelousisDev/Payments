package com.agelousis.payments.firebase

import com.google.firebase.messaging.FirebaseMessaging

typealias FirebaseTokenSuccessBlock = (token: String) -> Unit
class FirebaseInstanceHelper {

    companion object {
        val shared = FirebaseInstanceHelper()
    }

    fun initializeFirebaseToken(firebaseTokenSuccessBlock: FirebaseTokenSuccessBlock) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            firebaseTokenSuccessBlock(it.result ?: return@addOnCompleteListener)
        }
    }

}
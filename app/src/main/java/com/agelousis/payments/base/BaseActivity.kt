package com.agelousis.payments.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

typealias PermissionResultBlock = (isGranted: Boolean) -> Unit
open class BaseActivity: AppCompatActivity() {

    var activityLauncher: BaseActivityResult<Intent, ActivityResult>? = null
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionResultBlock?.invoke(isGranted)
    }
    var permissionResultBlock: PermissionResultBlock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLauncher = BaseActivityResult.registerForActivityResult<Intent, ActivityResult>(
            caller = this
        )
    }

}
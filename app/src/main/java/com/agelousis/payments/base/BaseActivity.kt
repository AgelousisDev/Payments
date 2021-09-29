package com.agelousis.payments.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

typealias PermissionResultBlock = (isGranted: Boolean) -> Unit
open class BaseActivity: AppCompatActivity() {

    var activityLauncher: BaseActivityResult<Intent, ActivityResult>? = null
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionResultBlock?.invoke(isGranted)
        permissionResultBlock = null
    }
    private var permissionResultBlock: PermissionResultBlock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLauncher = BaseActivityResult.registerForActivityResult<Intent, ActivityResult>(
            caller = this
        )
    }

    fun requestPermission(permission: String, permissionResultBlock: PermissionResultBlock) {
        this.permissionResultBlock = permissionResultBlock
        permissionLauncher.launch(
            permission
        )
    }

}
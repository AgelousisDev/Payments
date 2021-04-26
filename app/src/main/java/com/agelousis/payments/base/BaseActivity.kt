package com.agelousis.payments.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
    var activityLauncher: BaseActivityResult<Intent, ActivityResult>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLauncher = BaseActivityResult.registerForActivityResult<Intent, ActivityResult>(
            caller = this
        )
    }

}
package com.agelousis.payments.profilePicture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agelousis.payments.databinding.ActivityProfilePictureBinding
import com.agelousis.payments.main.MainActivity

class ProfilePictureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityProfilePictureBinding.inflate(
                layoutInflater
            ).also {
                it.userModel = intent?.extras?.getParcelable(MainActivity.USER_MODEL_EXTRA)
            }.root
        )
    }

}
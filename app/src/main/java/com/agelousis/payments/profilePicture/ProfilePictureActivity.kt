package com.agelousis.payments.profilePicture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agelousis.payments.databinding.ActivityProfilePictureBinding
import com.agelousis.payments.main.MainActivity
import com.thefuntasty.hauler.setOnDragDismissedListener

class ProfilePictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilePictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePictureBinding.inflate(layoutInflater)
        binding.userModel = intent?.extras?.getParcelable(MainActivity.USER_MODEL_EXTRA)
        setContentView(
            binding.root
        )
        setupUI()
    }

    private fun setupUI() {
        binding.haulerView.setOnDragDismissedListener {
            finish()
        }
    }

}
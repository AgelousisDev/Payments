package com.agelousis.payments.main.ui.personalInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.compose.extraHorizontalMargin
import java.io.File

class ProfilePictureFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                ProfileImageView()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        (activity as? MainActivity)?.binding?.appBarMain?.bottomAppBar?.performHide()
    }

    @Composable
    fun ProfileImageView() {
        Box(
            modifier = Modifier
                .clickable {
                    findNavController().popBackStack()
                }
                .fillMaxSize()
        ) {
            Image(
                if ((activity as? MainActivity)?.userModel?.profileImage != null)
                    rememberAsyncImagePainter(
                        model = File(context?.filesDir, (activity as? MainActivity)?.userModel?.profileImage ?: return)
                    )
                else
                    painterResource(
                        id = R.drawable.ic_person
                    ),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = extraHorizontalMargin.fillMaxSize()
            )
        }
    }

    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        ProfileImageView()
    }

}
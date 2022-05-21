package com.agelousis.payments.main.ui.qrCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agelousis.payments.compose.Typography
import com.agelousis.payments.compose.appColorScheme
import com.agelousis.payments.main.ui.qrCode.ui.QRCodeLayout
import com.agelousis.payments.main.ui.qrCode.viewModel.QRCodeViewModel

class QRCodeFragment: Fragment()  {

    private val args: QRCodeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    colorScheme = appColorScheme(),
                    typography = Typography
                ) {
                    val viewModel by viewModels<QRCodeViewModel>()
                    viewModel.selectedClientModelList = args.selectedClients?.toList()
                    QRCodeLayout(
                        qrCodeViewModel = viewModel,
                        qrCodeSelectionType = args.qrCodeSelectionType,
                        navController = findNavController()
                    )
                }
            }
        }
    }

}
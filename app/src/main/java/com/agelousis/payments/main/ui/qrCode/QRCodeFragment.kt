package com.agelousis.payments.main.ui.qrCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.agelousis.payments.R
import com.agelousis.payments.databinding.QrCodeFragmentLayoutBinding
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.utils.helpers.QRCodeHelper
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.itextpdf.xmp.impl.Base64

class QRCodeFragment: Fragment() {

    private lateinit var binding: QrCodeFragmentLayoutBinding
    private val args: QRCodeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context ?: return).inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = QrCodeFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.qrCodeSelectionType = args.qrCodeSelectionType
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureQrCodeSelection()
    }

    private fun configureQrCodeSelection() {
        when(args.qrCodeSelectionType) {
            QRCodeSelectionType.GENERATE ->
                generateQRCode()
            QRCodeSelectionType.SCAN ->
                startQRCodeScanning()
            else ->
                return
        }
    }

    private fun generateQRCode() {
        (QRCodeHelper instanceWith (context ?: return))
            .setContent(Base64.encode(args.qrCodeContent ?: return))
            .setErrorCorrectionLevel(ErrorCorrectionLevel.L)
            .setMargin(2)
            .qrCode?.let { qrCodeBitmap ->
                binding.qrCodeBitmap = qrCodeBitmap
            }
    }

    private fun startQRCodeScanning() {

    }

}
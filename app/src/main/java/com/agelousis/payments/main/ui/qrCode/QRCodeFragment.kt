package com.agelousis.payments.main.ui.qrCode

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.QrCodeFragmentLayoutBinding
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.viewModels.PaymentsViewModel
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.utils.extensions.loaderState
import com.agelousis.payments.utils.helpers.QRCodeHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.itextpdf.xmp.impl.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRCodeFragment: Fragment(), ZXingScannerView.ResultHandler {

    override fun handleResult(p0: Result?) {
        destinationFirebaseToken = Base64.decode(p0?.text)
    }

    private lateinit var binding: QrCodeFragmentLayoutBinding
    private val args: QRCodeFragmentArgs by navArgs()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(PaymentsViewModel::class.java) }
    private var destinationFirebaseToken: String? = null
        set(value) {
            field = value
            initializePayments()
        }

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
        addObservers()
    }

    override fun onResume() {
        super.onResume()
        if (args.qrCodeSelectionType == QRCodeSelectionType.SCAN) {
            binding.qrCodeScanner.startCamera()
            binding.qrCodeScanner.setResultHandler(this)
        }
    }

    private fun configureQrCodeSelection() {
        when(args.qrCodeSelectionType) {
            QRCodeSelectionType.GENERATE ->
                generateQRCode()
            QRCodeSelectionType.SCAN -> {
                (activity as? MainActivity)?.binding?.appBarMain?.bottomAppBar?.performHide()
                startQRCodeScanning()
            }
            else -> {}
        }
    }

    private fun addObservers() {
        viewModel.paymentsLiveData.observe(viewLifecycleOwner) { list ->
            this requestClientData list.filterIsInstance<ClientModel>()
        }
        viewModel.firebaseResponseLiveData.observe(viewLifecycleOwner) {
            loaderState = false
            findNavController().popBackStack()
        }
        viewModel.firebaseErrorLiveData.observe(viewLifecycleOwner) {
            loaderState = false
            findNavController().popBackStack()
        }
    }

    private fun initializePayments() {
        uiScope.launch {
            viewModel.initializePayments(
                context = context ?: return@launch,
                userModel = (activity as? MainActivity)?.userModel
            )
        }
    }

    private fun generateQRCode() {
        (QRCodeHelper instanceWith (context ?: return))
            .setContent(Base64.encode(MainApplication.firebaseToken ?: return))
            .setErrorCorrectionLevel(ErrorCorrectionLevel.L)
            .setMargin(2)
            .qrCode?.let { qrCodeBitmap ->
                binding.qrCodeBitmap = qrCodeBitmap
            }
    }

    private fun startQRCodeScanning() {
        binding.qrCodeScanner.setFormats(
            listOf(
                BarcodeFormat.QR_CODE
            )
        )
        binding.qrCodeScanner.setAutoFocus(true)
        binding.qrCodeScanner.setLaserColor(ContextCompat.getColor(context ?: return, R.color.colorAccent))
        //binding.qrCodeScanner.setMaskColor(ContextCompat.getColor(context ?: return, R.color.colorAccent))
        if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true))
            binding.qrCodeScanner.setAspectTolerance(0.5f)
    }

    private infix fun requestClientData(clientModelList: List<ClientModel>) {
        loaderState = true
        viewModel.sendClientDataRequestNotification(
            firebaseMessageModel = FirebaseMessageModel(
                firebaseToken = destinationFirebaseToken ?: return,
                FirebaseNotificationData(
                    clientModelList = clientModelList
                )
            )
        )
    }

    override fun onPause() {
        super.onPause()
        if (args.qrCodeSelectionType == QRCodeSelectionType.SCAN)
            binding.qrCodeScanner.stopCamera()
    }

}
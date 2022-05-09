package com.agelousis.payments.main.ui.qrCode.ui

import com.agelousis.payments.R
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.compose.textViewValueLabelFont
import com.agelousis.payments.compose.utils.OnLifecycleEvent
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.main.ui.qrCode.utils.QRCodeAnalyzer
import com.agelousis.payments.main.ui.qrCode.viewModel.QRCodeViewModel
import com.agelousis.payments.network.responses.FirebaseResponseModel
import com.agelousis.payments.utils.extensions.toast
import com.agelousis.payments.utils.helpers.QRCodeHelper
import com.airbnb.lottie.compose.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.itextpdf.xmp.impl.Base64
import me.dm7.barcodescanner.zxing.ZXingScannerView

@Composable
fun QRCodeLayout(
    qrCodeViewModel: QRCodeViewModel,
    qrCodeSelectionType: QRCodeSelectionType,
    navController: NavController
) {
    when(LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE ->
            QRCodeLandscapeLayout(
                qrCodeViewModel = qrCodeViewModel,
                qrCodeSelectionType = qrCodeSelectionType,
                navController = navController
            )
        else ->
            QRCodePortraitLayout(
                qrCodeViewModel = qrCodeViewModel,
                qrCodeSelectionType = qrCodeSelectionType,
                navController = navController
            )
    }
}

@Composable
fun QRCodeLandscapeLayout(
    qrCodeViewModel: QRCodeViewModel,
    qrCodeSelectionType: QRCodeSelectionType,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleEvent = onLifecycleEvent()
    var loaderState by remember {
        mutableStateOf(value = false)
    }
    val firebaseResponse by qrCodeViewModel.firebaseResponseLiveData.observeAsState()
    firebaseResponse?.let {
        loaderState = false
        configureFirebaseResponse(
            context = context,
            firebaseResponseModel = it,
            navController = navController
        )
    }

    val firebaseError by qrCodeViewModel.firebaseErrorLiveData.observeAsState()
    if (firebaseError != null) {
        loaderState = false
        navController.popBackStack()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (zxingScannerViewConstrainedReference, scanQRCodeLottieAnimationConstrainedReference,
            qrCodeDescriptionConstrainedReference, qrCodeImageConstrainedReference, loaderConstrainedReference) = createRefs()

        if (qrCodeSelectionType == QRCodeSelectionType.SCAN)
            AndroidView(
                factory = { context ->
                    ZXingScannerView(context)
                },
                modifier = Modifier
                    .constrainAs(zxingScannerViewConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            ) { zXingScannerView ->
                when(lifecycleEvent) {
                    Lifecycle.Event.ON_CREATE -> {
                        (context as? MainActivity)?.binding?.appBarMain?.bottomAppBar?.performHide()
                        startQRCodeScanning(
                            zXingScannerView = zXingScannerView
                        )
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        zXingScannerView.startCamera()
                        zXingScannerView.setResultHandler { result ->
                            loaderState = true
                            qrCodeViewModel.sendClientDataRequestNotification(
                                firebaseMessageModel = FirebaseMessageModel(
                                    firebaseToken = Base64.decode(result?.text ?: return@setResultHandler),
                                    FirebaseNotificationData(
                                        clientModelList = qrCodeViewModel.selectedClientModelList ?: return@setResultHandler
                                    )
                                )
                            )
                        }
                    }
                    Lifecycle.Event.ON_PAUSE ->
                        zXingScannerView.stopCamera()
                    else -> {}
                }
            }

        if (qrCodeSelectionType == QRCodeSelectionType.GENERATE) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset("scan_qr_code_animation.json")
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever,
                restartOnPlay = false
            )
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .constrainAs(scanQRCodeLottieAnimationConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.percent(percent = 0.5f)
                        height = Dimension.fillToConstraints
                    }
            )

            Text(
                text = stringResource(id = R.string.key_share_id_and_receive_payments_message),
                style = textViewValueLabelFont,
                modifier = Modifier
                    .constrainAs(qrCodeDescriptionConstrainedReference) {
                        start.linkTo(scanQRCodeLottieAnimationConstrainedReference.end, 16.dp)
                        top.linkTo(parent.top, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            getGenerateQRCodeWithFirebaseToken(
                context = context
            )?.let { qrCodeBitmap ->
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(qrCodeImageConstrainedReference) {
                            start.linkTo(scanQRCodeLottieAnimationConstrainedReference.end)
                            top.linkTo(qrCodeDescriptionConstrainedReference.bottom)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )
            }
        }

        if (loaderState)
            CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(loaderConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
    }
}

@Composable
fun QRCodePortraitLayout(
    qrCodeViewModel: QRCodeViewModel,
    qrCodeSelectionType: QRCodeSelectionType,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFeature = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val lifecycleEvent = onLifecycleEvent()
    var loaderState by remember {
        mutableStateOf(value = false)
    }
    val firebaseResponse by qrCodeViewModel.firebaseResponseLiveData.observeAsState()
    firebaseResponse?.let {
        loaderState = false
        configureFirebaseResponse(
            context = context,
            firebaseResponseModel = it,
            navController = navController
        )
    }

    val firebaseError by qrCodeViewModel.firebaseErrorLiveData.observeAsState()
    if (firebaseError != null) {
        loaderState = false
        navController.popBackStack()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (zxingScannerViewConstrainedReference, scanQRCodeLottieAnimationConstrainedReference,
            qrCodeDescriptionConstrainedReference, qrCodeImageConstrainedReference, loaderConstrainedReference) = createRefs()

        if (qrCodeSelectionType == QRCodeSelectionType.SCAN)
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = androidx.camera.core.Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(
                            Size(
                            previewView.width,
                            previewView.height
                        )
                        )
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer { result ->
                            result
                        }
                    )
                    try {
                        cameraProviderFeature.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier
                    .constrainAs(zxingScannerViewConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            ) { zXingScannerView ->
                /*when(lifecycleEvent) {
                    Lifecycle.Event.ON_RESUME -> {
                        (context as? MainActivity)?.binding?.appBarMain?.bottomAppBar?.performHide()
                        startQRCodeScanning(
                            zXingScannerView = zXingScannerView
                        )
                        zXingScannerView.startCamera()
                        zXingScannerView.setResultHandler { result ->
                            loaderState = true
                            qrCodeViewModel.sendClientDataRequestNotification(
                                firebaseMessageModel = FirebaseMessageModel(
                                    firebaseToken = Base64.decode(result?.text ?: return@setResultHandler),
                                    FirebaseNotificationData(
                                        clientModelList = qrCodeViewModel.selectedClientModelList ?: return@setResultHandler
                                    )
                                )
                            )
                        }
                    }
                    Lifecycle.Event.ON_PAUSE ->
                        zXingScannerView.stopCamera()
                    else -> {}
                }*/
            }

        if (qrCodeSelectionType == QRCodeSelectionType.GENERATE) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset("scan_qr_code_animation.json")
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever,
                restartOnPlay = false
            )
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .constrainAs(scanQRCodeLottieAnimationConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(dp = 150.dp)
                    }
            )

            Text(
                text = stringResource(id = R.string.key_share_id_and_receive_payments_message),
                style = textViewValueLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier
                    .constrainAs(qrCodeDescriptionConstrainedReference) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(scanQRCodeLottieAnimationConstrainedReference.bottom, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            getGenerateQRCodeWithFirebaseToken(
                    context = context
            )?.let { qrCodeBitmap ->
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(qrCodeImageConstrainedReference) {
                            start.linkTo(parent.start)
                            top.linkTo(qrCodeDescriptionConstrainedReference.bottom, 32.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
            }
        }

        if (loaderState)
            CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(loaderConstrainedReference) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
    }
}

@Composable
private fun onLifecycleEvent(): Lifecycle.Event? {
    var lifecycleEvent by remember {
        mutableStateOf<Lifecycle.Event?>(value = null)
    }
    OnLifecycleEvent { _, event ->
        lifecycleEvent = event
    }
    return lifecycleEvent
}

private fun configureFirebaseResponse(
    context: Context,
    firebaseResponseModel: FirebaseResponseModel,
    navController: NavController
) {
    if (!firebaseResponseModel.isSuccessful)
        context.toast(
            message = context.resources.getString(com.agelousis.payments.R.string.key_large_client_data_notification_warning_message)
        )
    navController.popBackStack()

}

private fun startQRCodeScanning(
    zXingScannerView: ZXingScannerView
) {
    zXingScannerView.setFormats(
        listOf(
            BarcodeFormat.QR_CODE
        )
    )
    zXingScannerView.setAutoFocus(true)
    zXingScannerView.setLaserColor(ContextCompat.getColor(zXingScannerView.context, com.agelousis.payments.R.color.colorAccent))
    //binding.qrCodeScanner.setMaskColor(ContextCompat.getColor(context ?: return, R.color.colorAccent))
    if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true))
        zXingScannerView.setAspectTolerance(0.5f)
}

private fun getGenerateQRCodeWithFirebaseToken(
    context: Context
) = (QRCodeHelper instanceWith context)
        .setContent(Base64.encode(MainApplication.firebaseToken))
        .setErrorCorrectionLevel(ErrorCorrectionLevel.L)
        .setMargin(2)
        .qrCode


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QRCodeLayoutPreview() {
    QRCodeLayout(
        qrCodeSelectionType = QRCodeSelectionType.GENERATE,
        qrCodeViewModel = QRCodeViewModel(),
        navController = NavController(LocalContext.current)
    )
}
package com.agelousis.payments.main.ui.qrCode.ui

import com.agelousis.payments.R
import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.compose.textViewValueLabelFont
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType
import com.agelousis.payments.main.ui.qrCode.viewModel.QRCodeViewModel
import com.agelousis.payments.network.responses.FirebaseResponseModel
import com.agelousis.payments.utils.extensions.toast
import com.agelousis.payments.utils.helpers.QRCodeHelper
import com.airbnb.lottie.compose.*
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.itextpdf.xmp.impl.Base64
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.delay

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
    var loaderState by remember {
        mutableStateOf(value = false)
    }
    val firebaseResponse by qrCodeViewModel.firebaseResponseLiveData.observeAsState()
    if (firebaseResponse != null) {
        loaderState = false
        qrCodeViewModel.firebaseResponseLiveData.value = null
        configureFirebaseResponse(
            context = context,
            firebaseResponseModel = firebaseResponse,
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
            .padding(
                bottom = 90.dp
            )
    ) {
        val (qrCodeGeneratedImageConstrainedReference, qrCodeDescriptionConstrainedReference,
            qrCodeLottieAnimationConstrainedReference, loaderConstrainedReference) = createRefs()

        if (qrCodeSelectionType == QRCodeSelectionType.SCAN) {
            val qrCodeScannerLauncher = rememberLauncherForActivityResult(
                contract = ScanContract()
            ) { scanIntentResult ->
                loaderState = true
                scanIntentResult.contents?.let { firebaseTokenEncoded ->
                    qrCodeViewModel.sendClientDataRequestNotification(
                        firebaseMessageModel = FirebaseMessageModel(
                            firebaseToken = Base64.decode(firebaseTokenEncoded),
                            FirebaseNotificationData(
                                clientModelList = qrCodeViewModel.selectedClientModelList ?: return@rememberLauncherForActivityResult
                            )
                        )
                    )
                } ?: navController.popBackStack()
            }
            LaunchedEffect(
                key1 = Unit
            ) {
                delay(
                    timeMillis = 1000
                )
                qrCodeScannerLauncher.launch(ScanOptions())
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
            getGenerateQRCodeWithFirebaseToken(
                context = context
            )?.let { qrCodeBitmap ->
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(qrCodeGeneratedImageConstrainedReference) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.percent(percent = 0.5f)
                            height = Dimension.fillToConstraints
                        }
                )
            }

            Text(
                text = stringResource(id = R.string.key_share_id_and_receive_payments_message),
                style = textViewValueLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier
                    .constrainAs(qrCodeDescriptionConstrainedReference) {
                        start.linkTo(qrCodeGeneratedImageConstrainedReference.end, 16.dp)
                        top.linkTo(parent.top, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            LottieAnimation(
                composition = composition,
                progress = {
                    progress
                },
                modifier = Modifier
                    .constrainAs(qrCodeLottieAnimationConstrainedReference) {
                        start.linkTo(qrCodeGeneratedImageConstrainedReference.end)
                        top.linkTo(qrCodeDescriptionConstrainedReference.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
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
    var loaderState by remember {
        mutableStateOf(value = false)
    }
    val firebaseResponse by qrCodeViewModel.firebaseResponseLiveData.observeAsState()
    if (firebaseResponse != null) {
        loaderState = false
        qrCodeViewModel.firebaseResponseLiveData.value = null
        configureFirebaseResponse(
            context = context,
            firebaseResponseModel = firebaseResponse,
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
        val (scanQRCodeLottieAnimationConstrainedReference,
            qrCodeDescriptionConstrainedReference, qrCodeImageConstrainedReference, loaderConstrainedReference) = createRefs()

        if (qrCodeSelectionType == QRCodeSelectionType.SCAN) {
            val qrCodeScannerLauncher = rememberLauncherForActivityResult(
                contract = ScanContract()
            ) { scanIntentResult ->
                loaderState = true
                scanIntentResult.contents?.let { firebaseTokenEncoded ->
                    qrCodeViewModel.sendClientDataRequestNotification(
                        firebaseMessageModel = FirebaseMessageModel(
                            firebaseToken = Base64.decode(firebaseTokenEncoded),
                            FirebaseNotificationData(
                                clientModelList = qrCodeViewModel.selectedClientModelList ?: return@rememberLauncherForActivityResult
                            )
                        )
                    )
                } ?: navController.popBackStack()
            }
            LaunchedEffect(
                key1 = Unit
            ) {
                delay(
                    timeMillis = 1000
                )
                qrCodeScannerLauncher.launch(ScanOptions())
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
                progress = {
                    progress
                },
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

private fun configureFirebaseResponse(
    context: Context,
    firebaseResponseModel: FirebaseResponseModel?,
    navController: NavController
) {
    if (firebaseResponseModel?.isSuccessful == false)
        context.toast(
            message = context.resources.getString(R.string.key_large_client_data_notification_warning_message)
        )
    navController.popBackStack()

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
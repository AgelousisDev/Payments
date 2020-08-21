package com.agelousis.monthlyfees.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.biometric.BiometricManager
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.custom.picasso.CircleTransformation
import com.agelousis.monthlyfees.database.SQLiteHelper
import com.agelousis.monthlyfees.databinding.GroupInputDialogViewBinding
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.personalInformation.OptionPresenter
import com.agelousis.monthlyfees.utils.constants.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.NoSuchElementException
import kotlin.random.Random.Default.nextInt

typealias PositiveButtonBlock = () -> Unit
typealias InputGroupDialogBlock = (GroupModel) -> Unit
typealias ItemPositionDialogBlock = (Int) -> Unit

fun <T> T?.whenNull(receiver: () -> Unit): T? {
    return if (this == null) {
        receiver.invoke()
        null
    } else this
}

inline fun <T> Iterable<T>.forEachIfEach(predicate: (T) -> Boolean, action: (T) -> Unit) {
    for (element in this)
        if (predicate(element))
            action(element)
}

inline fun <T, J> Iterable<T>.firstOrNullWithType(typeBlock: (T) -> J?, predicate: (J?) -> Boolean): J? {
    for (element in this) {
        val block = typeBlock(element)
        if (predicate(block))
            return typeBlock(element)
    }
    return null
}

fun <T> List<T>.second(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[1]
}
fun <T> List<T>.third(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[2]
}

fun <T> Array<out T>.secondOrNull(): T? = if (isEmpty()) null else this[1]

inline fun <K, T> ifLet(vararg elements: T?, closure: (List<T>) -> K): K? {
    return if (elements.all { it != null })
        closure(elements.filterNotNull())
    else null
}

fun AppCompatActivity.openGallery(requestCode: Int) =
    startActivityForResult(Intent(
        Intent.ACTION_PICK
    ).also {
        it.type = Constants.IMAGE_MIME_TYPE
    }, requestCode)

fun Fragment.openGallery(requestCode: Int) =
    startActivityForResult(Intent(
        Intent.ACTION_PICK
    ).also {
        it.type = Constants.IMAGE_MIME_TYPE
    }, requestCode)

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.saveProfileImage(byteArray: ByteArray?) =
    byteArray?.let { bytes ->
        val newFile = File(filesDir, "${Constants.PROFILE_IMAGE_NAME}_${System.currentTimeMillis()}")
        if (!newFile.exists())
            newFile.createNewFile()
        FileOutputStream(newFile).use {
            it.write(bytes)
        }
        newFile.name
    }

var SharedPreferences.userModel: UserModel?
    set(value) {
        edit().also {
            it.putString(SQLiteHelper.USERNAME, value?.username)
            it.putString(SQLiteHelper.PASSWORD, value?.password)
            it.putBoolean(SQLiteHelper.BIOMETRICS, value?.biometrics == true)
            it.putString(SQLiteHelper.PROFILE_IMAGE, value?.profileImage)
            it.apply()
        }
    }
    get() =
        ifLet(
            getString(SQLiteHelper.USERNAME, null),
            getString(SQLiteHelper.PASSWORD, null),
            getBoolean(SQLiteHelper.BIOMETRICS, false),
            getString(SQLiteHelper.PROFILE_IMAGE, null)
        ) {
            UserModel(
                username = it.first().toString(),
                password = it.second().toString(),
                biometrics = it.third().toString().toBoolean(),
                profileImage = it.last().toString()
            )
        }

fun Context.showTwoButtonsDialog(title: String, message: String, isCancellable: Boolean? = null, negativeButtonBlock: PositiveButtonBlock? = null,
                                 positiveButtonText: String? = null, positiveButtonBlock: PositiveButtonBlock) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancellable ?: true)
        .setNegativeButton(resources.getString(R.string.key_cancel_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            negativeButtonBlock?.invoke()
        }
        .setPositiveButton(positiveButtonText ?: resources.getString(R.string.key_ok_label)) { _, _ ->
            positiveButtonBlock()
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
}

fun Context.showSimpleDialog(title: String, message: String, isCancellable: Boolean = true, positiveButtonBlock: PositiveButtonBlock? = null) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setCancelable(isCancellable)
        .setMessage(message)
        .setPositiveButton(resources.getString(R.string.key_ok_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            positiveButtonBlock?.invoke()
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
}

fun Context.showGroupInputDialog(inputHint: String, isCancellable: Boolean? = null, negativeButtonBlock: PositiveButtonBlock? = null,
                                 positiveButtonText: String? = null, inputGroupDialogBlock: InputGroupDialogBlock) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
    val binding = GroupInputDialogViewBinding.inflate(
        LayoutInflater.from(this),
        null,
        false
    )
    binding.map = mapOf(
        "inputHint" to inputHint
    )
    materialAlertDialogBuilder.setView(binding.root)
    materialAlertDialogBuilder.apply {
        setCancelable(isCancellable ?: true)
        setNegativeButton(resources.getString(R.string.key_cancel_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            negativeButtonBlock?.invoke()
        }
        setPositiveButton(positiveButtonText ?: resources.getString(R.string.key_ok_label)) { _, _ ->
            inputGroupDialogBlock(
                GroupModel(
                groupName = binding.groupField.text?.toString()
            ))
        }
    }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).alpha = 0.5f
    materialDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    binding.groupField.doAfterTextChanged {
        materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it?.isNotEmpty() == true && it.isNotBlank()
        materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).alpha = if (it?.isNotEmpty() == true && it.isNotBlank()) 1.0f else 0.5f
    }
}

fun Context.showListDialog(title: String, items: List<String>, isCancellable: Boolean = true, itemPositionDialogBlock: ItemPositionDialogBlock) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setCancelable(isCancellable)
        .setItems(items.toTypedArray()) { p0, p1 ->
            p0.dismiss()
            itemPositionDialogBlock(p1)
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
}

val Context.hasBiometrics: Boolean
    get() = BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

val Double?.euroFormattedString: String?
    get() {
        val unwrappedAmount = this ?: return null
        val pattern = "€ #,##0"
        val locale = Locale.getDefault()
        val numberFormatter = NumberFormat.getNumberInstance(locale)
        val decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(pattern)
        return decimalFormatter.format(unwrappedAmount)
    }

fun Context.message(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.infiniteAlphaAnimation(state: Boolean) {
    if (state) {
        if (animation == null)
            startAnimation(
                AlphaAnimation(0.0f, 1.0f).also {
                    it.repeatMode = AlphaAnimation.REVERSE
                    it.repeatCount = AlphaAnimation.INFINITE
                    it.duration = 1500
                    it.interpolator = LinearInterpolator()
                }
            )
    }
    else {
        if (animation != null) {
            alpha = 1.0f
            clearAnimation()
        }
    }
}

val Context.randomColor: Int
    get() = Color.argb(255, nextInt(256), nextInt(256), nextInt(256))

val Int.getContrastColor: Int
    get() {
        val luminance = ( 0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.green(this)) / 255
        val d = if (luminance > 0.5) 0 else 255
        return  Color.rgb(d, d, d)
    }

fun after(millis: Long, runnable: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        runnable,
        millis
    )
}

fun Drawable.fromVector(padding: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(padding, padding, canvas.width - padding, canvas.height - padding)
    this.draw(canvas)
    return bitmap
}

@BindingAdapter("picassoImageUri")
fun AppCompatImageView.loadImageUri(imageUri: Uri?) {
    imageUri?.let {
        Picasso.get().load(it).placeholder(R.drawable.ic_person).resize(width, height)
            .transform(CircleTransformation()).centerCrop().into(this)
    }
}

@BindingAdapter("picassoImagePath")
fun AppCompatImageView.loadImagePath(path: String?) {
    path?.let {
        Picasso.get().load(File(context.filesDir, it)).placeholder(R.drawable.ic_person)
            .resize(60.px, 60.px).transform(CircleTransformation()).centerCrop().into(this)
    }
}

@BindingAdapter("picassoImageFromInternalFiles")
fun setPicassoImageFromInternalFiles(appCompatImageView: AppCompatImageView, fileName: String?) {
    fileName?.let {
        Picasso.get().load(File(appCompatImageView.context.filesDir, it)).transform(CircleTransformation()).into(appCompatImageView)
    }
}

@BindingAdapter("switchStateChanged")
fun switchStateChanged(switchMaterial: SwitchMaterial, optionPresenter: OptionPresenter) {
    switchMaterial.setOnCheckedChangeListener { _, isChecked ->
        optionPresenter.onBiometricsState(
            state = isChecked
        )
    }
}

@BindingAdapter("srcCompat")
fun setSrcCompat(appCompatImageView: AppCompatImageView, resourceId: Int?) {
    resourceId?.let {
        appCompatImageView.setImageResource(it)
    }
}

@BindingAdapter("backgroundDrawableTintColor")
fun setBackgroundDrawableTintColor(viewGroup: ViewGroup, color: Int?) {
    color?.let {
        viewGroup.background?.setTint(it)
    }
}
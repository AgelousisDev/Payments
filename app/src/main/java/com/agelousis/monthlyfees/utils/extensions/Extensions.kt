package com.agelousis.monthlyfees.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.biometric.BiometricManager
import androidx.databinding.BindingAdapter
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.custom.picasso.CircleTransformation
import com.agelousis.monthlyfees.database.SQLiteHelper
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.utils.constants.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream

typealias PositiveButtonBlock = () -> Unit

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

inline fun <K, T: Any> ifLet(vararg elements: T?, closure: (List<T>) -> K): K? {
    return if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
    else null
}

fun AppCompatActivity.openGallery(requestCode: Int) =
    startActivityForResult(Intent(
        Intent.ACTION_PICK
    ).also {
        it.type = Constants.IMAGE_MIME_TYPE
    }, requestCode)

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.saveProfileImage(byteArray: ByteArray?) =
    byteArray?.let { bytes ->
        val newFile = File(filesDir, Constants.PROFILE_IMAGE_NAME)
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
                username = it.first() as String,
                password = it.second() as String,
                biometrics = it.third() as Boolean,
                profileImage = it.last() as String
            )
        }

fun Context.showTwoButtonsDialog(title: String, message: String, isCancellable: Boolean? = null, negativeButtonBlock: PositiveButtonBlock? = null,
                                 positiveButtonBlock: PositiveButtonBlock) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancellable ?: true)
        .setNegativeButton(resources.getString(R.string.key_cancel_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            negativeButtonBlock?.invoke()
        }
        .setPositiveButton(resources.getString(R.string.key_ok_label)) { _, _ ->
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

val Context.hasBiometrics: Boolean
    get() = BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

fun AppCompatImageView.loadImageUri(imageUri: Uri?) {
    imageUri?.let {
        Picasso.get().load(it).placeholder(R.drawable.ic_person).resize(width, height)
            .transform(CircleTransformation()).centerCrop().into(this)
    }
}

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
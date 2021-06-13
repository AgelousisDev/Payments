package com.agelousis.payments.utils.extensions

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.provider.OpenableColumns
import android.telephony.TelephonyManager
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.biometric.BiometricManager
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.database.getStringOrNull
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.BuildConfig
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.custom.picasso.CircleTransformation
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.login.enumerations.UIMode
import com.agelousis.payments.main.ui.countrySelector.enumerations.CountryDataModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.custom.ImprovedBulletSpan
import com.agelousis.payments.utils.custom.LoaderDialog
import com.agelousis.payments.utils.models.CalendarDataModel
import com.agelousis.payments.utils.models.NotificationDataModel
import com.agelousis.payments.utils.receivers.NotificationReceiver
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

typealias PositiveButtonBlock = () -> Unit
typealias ItemPositionDialogBlock = (Int) -> Unit
typealias CompletionSuccessBlock = (Boolean) -> Unit
typealias CircularAnimationCompletionBlock = () -> Unit
typealias BitmapBlock = (Bitmap?) -> Unit

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

infix fun <T> Iterable<T>.isLastAt(position: Int) =
    this.count() - 1 == position

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

val galleryIntent
    get() = Intent(
        Intent.ACTION_GET_CONTENT
    ).also {
        it.type = Constants.IMAGE_MIME_TYPE
    }

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.inPixel: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

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

fun Context.saveProfileImage(bitmap: Bitmap?) =
    bitmap?.let {
        val newFile = File(filesDir, "${Constants.PROFILE_IMAGE_NAME}_${System.currentTimeMillis()}")
        if (!newFile.exists())
            newFile.createNewFile()
        it.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(newFile))
        newFile.name
    }

fun Context.saveImage(bitmap: Bitmap?, fileName: String?) =
    bitmap?.let {
        val newFile = File(filesDir, "${fileName ?: return@let null}_${System.currentTimeMillis()}")
        if (!newFile.exists())
            newFile.createNewFile()
        it.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(newFile))
        newFile.name
    }

fun Context.saveImage(fileName: String?, byteArray: ByteArray?) {
    byteArray?.let { bytes ->
        val newFile = File(filesDir, fileName ?: return@let)
        if (!newFile.exists())
            newFile.createNewFile()
        FileOutputStream(newFile).use {
            it.write(bytes)
        }
    }
}

fun Context.deleteInternalFile(fileName: String?) {
    File(filesDir, fileName ?: return).delete()
}

infix fun Context.byteArrayFromInternalImage(imageName: String?) =
    imageName?.let {
        val bitmap = BitmapFactory.decodeFile(File(filesDir, it).absolutePath)
        val bytesArray = bitmap.byteArray
        bitmap.recycle()
        bytesArray
    }

fun Context.showTwoButtonsDialog(title: String, message: String, icon: Int? = null, isCancellable: Boolean? = null, negativeButtonText: String? = null, negativeButtonBlock: PositiveButtonBlock? = null,
                                 positiveButtonText: String? = null, positiveButtonBlock: PositiveButtonBlock) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon ?: 0)
        .setCancelable(isCancellable ?: true)
        .setNegativeButton(negativeButtonText ?: resources.getString(R.string.key_cancel_label)) { dialogInterface, _ ->
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

fun Context.showSimpleDialog(title: String, message: String, icon: Int? = null, isCancellable: Boolean = true, positiveButtonText: String? = null, positiveButtonBlock: PositiveButtonBlock? = null) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog)
        .setTitle(title)
        .setCancelable(isCancellable)
        .setMessage(message)
        .setIcon(icon ?: 0)
        .setPositiveButton(positiveButtonText ?: resources.getString(R.string.key_ok_label)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            positiveButtonBlock?.invoke()
        }
    val materialDialog = materialAlertDialogBuilder.create()
    materialDialog.show()
    materialDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
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
    get() = BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

val Double?.euroFormattedString: String?
    get() {
        val unwrappedAmount = this ?: return null
        val pattern = "${MainApplication.currencySymbol ?: "€"}#,##0.00"
        val decimalFormatter = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
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

fun AppCompatImageView.setAnimatedImageResourceId(resourceId: Int?) {
    resourceId?.let {
        post {
            animate().alpha(0.0f).setInterpolator(LinearInterpolator()).setListener(
                object: Animator.AnimatorListener {
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {
                        setImageResource(it)
                        animate().alpha(1.0f).interpolator = LinearInterpolator()
                    }
                }
            )
        }
    }
}

fun Context.initializeField(appCompatEditText: AppCompatEditText) {
    appCompatEditText.requestFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
        appCompatEditText,
        InputMethodManager.SHOW_FORCED
    )
}

fun createDocumentIntentWith(fileName: String, mimeType: String) = Intent(Intent.ACTION_CREATE_DOCUMENT).also {
    it.addCategory(Intent.CATEGORY_OPENABLE)
    it.type = mimeType
    it.putExtra(Intent.EXTRA_TITLE, fileName)
}

fun AppCompatActivity.alterFile(uri: Uri?, file: File) {
    contentResolver.openFileDescriptor(uri ?: return, "w")?.use { parcelFileDescriptor ->
        FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
            it.write(file.readBytes())
        }
    }
}

infix fun Context.getContentIntent(mimeType: String) = Intent(Intent.ACTION_GET_CONTENT).also {
    it.type = mimeType
    it.addCategory(Intent.CATEGORY_OPENABLE)
}

fun Context.isDBFile(uri: Uri?) =
    uri?.let {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(it)) == Constants.BIN_FILE_EXTENSION ||
                (this getDisplayName it)?.endsWith(Constants.EXPORT_DATABASE_FILE_NAME_EXTENSION) == true ||
                (this getDisplayName it)?.endsWith(SQLiteHelper.DB_NAME_EXTENSION) == true
    } ?: false

infix fun Context.getDisplayName(uri: Uri): String? {
    val cursor = contentResolver.query(uri, null, null, null, null)
    return if (cursor?.moveToFirst() == true && cursor.count > 0) {
        val fileExtension = cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor.close()
        fileExtension
    }
    else null
}

val Uri.isGoogleDrive: Boolean
    get() = toString().contains(Constants.GOOGLE_DRIVE_URI)

fun Context.replaceDatabase(byteArray: ByteArray?, completionSuccessBlock: CompletionSuccessBlock) =
    byteArray?.let { unwrappedByteArray ->
        FileOutputStream(this.getDatabasePath(SQLiteHelper.DB_NAME)).use {
            it.write(unwrappedByteArray)
            completionSuccessBlock(true)
        }
    } ?: completionSuccessBlock(false)

fun View.circularReveal(circularAnimationCompletionBlock: CircularAnimationCompletionBlock) {
    if (viewTreeObserver.isAlive)
        viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val finalRadius: Float = max(width.toFloat(), height.toFloat())
                // create the animator for this view (the start radius is zero)
                val circularReveal = ViewAnimationUtils.createCircularReveal(this@circularReveal, width - (width / 5), height - (height / 5), 0f, finalRadius)
                circularReveal.duration = 500
                // make the view visible and start the animation
                visibility = View.VISIBLE
                circularReveal.addListener(
                    onEnd = {
                        circularAnimationCompletionBlock()
                    }
                )
                circularReveal.start()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
}

fun View.circularUnReveal(centerX: Int? = null, centerY: Int? = null, circularAnimationCompletionBlock: CircularAnimationCompletionBlock) {
    val finalRadius = max(width.toFloat(), height.toFloat()) * 1.1f
    val circularReveal = ViewAnimationUtils.createCircularReveal(this@circularUnReveal, centerX ?: width - 72, centerY ?: 72, finalRadius, 0.0f)
    circularReveal.duration = 500
    circularReveal.addListener(
        onEnd = {
            visibility = View.INVISIBLE
            circularAnimationCompletionBlock()
        }
    )
    circularReveal.start()
}

val Date.monthFormattedString: String
    get() = SimpleDateFormat(Constants.MONTH_DATE_FORMAT, Locale.getDefault()).format(this)

val Date.isSameYearAndMonthWithCurrentDate: Boolean
    get() {
        val firstCalendar = Calendar.getInstance()
        firstCalendar.time = this
        val secondCalendar = Calendar.getInstance()
        secondCalendar.time = Date()
        return (firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) && firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH))
    }

val Date.isDatePassed
    get() = Date().after(this)

val Date.pdfFormattedCurrentDate: String
    get() = SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault()).format(this)

val Date.yearMonth: Date?
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        val dateString = String.format("%d %d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        return with(SimpleDateFormat("yyyy MM", Locale.getDefault())) {
            parse(dateString)
        }
    }

val Date.isValidProductDate: Boolean
    get() {
        if (BuildConfig.VALID_PRODUCT_DATE.isEmpty())
            return true
        val firstCalendar = Calendar.getInstance()
        firstCalendar.time = this
        val productDate = SimpleDateFormat(Constants.FILE_DATE_FORMAT, Locale.getDefault()).parse(BuildConfig.VALID_PRODUCT_DATE) ?: return false
        val secondCalendar = Calendar.getInstance()
        secondCalendar.time = productDate
        return firstCalendar.get(Calendar.YEAR) <= secondCalendar.get(Calendar.YEAR) &&
                firstCalendar.get(Calendar.MONTH) <= secondCalendar.get(Calendar.MONTH) &&
                firstCalendar.get(Calendar.DAY_OF_YEAR) < secondCalendar.get(Calendar.DAY_OF_YEAR)
    }

infix fun Date.formattedDateWith(pattern: String): String? =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun String.toDateWith(pattern: String, locale: Locale? = null): Date? =
    SimpleDateFormat(pattern, locale ?: Locale.getDefault()).parse(this)

val Date.calendar: Calendar
    get() = Calendar.getInstance().also {
        it.time = this
    }

val Date.defaultTimeCalendar: Calendar
    get() = Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 10)
    }

inline fun <reified J> Any.asIs(block: (J) -> Unit) {
    if (this is J)
        block(this)
}

fun Context.openPDF(pdfFile: File) {
    val pdfUri = FileProvider.getUriForFile(
        this,
        "$packageName.provider", pdfFile)
    startActivity(Intent().also {
        it.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        it.action = Intent.ACTION_VIEW
        it.data = pdfUri
    })
}

fun Context.sharePDF(pdfFile: File) {
    val pdfUri = FileProvider.getUriForFile(
        this,
        "$packageName.provider", pdfFile)
    startActivity(Intent().also {
        it.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        it.action = Intent.ACTION_SEND
        it.type = Constants.PDF_MIME_TYPE
        it.putExtra(Intent.EXTRA_STREAM, pdfUri)
    })
}

val <T> Iterable<T>.isSizeOne: Boolean
    get() {
        var counter = 0
        for (element in this) {
            counter++
            if (counter > 1) break
        }
        return counter == 1
    }

val Int?.invoiceNumber: String?
    get() = this?.let { String.format("P%04d", it) }

fun Context.hasPermissions(vararg permissions: String): Boolean {
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun Context.call(phone: String) = startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))

fun Context.textEmail(email: String, content: String? = null) {
    startActivity(Intent(Intent.ACTION_SEND).also {
        it.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        it.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        it.putExtra(Intent.EXTRA_TEXT, content)
        it.type = "text/plain"
    })
}

fun loadImageBitmap(imageUri: Uri?, bitmapBlock: BitmapBlock) {
    imageUri?.let {
        Picasso.get().load(it).resize(200.px, 200.px).transform(CircleTransformation()).centerCrop().into(object: Target {
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmapBlock(bitmap)
            }
        })
    }
}

val Bitmap.byteArray: ByteArray?
    get() {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

fun AppCompatImageView.loadImageUri(imageUri: Uri?) {
    imageUri?.let {
        Picasso.get().load(it).placeholder(R.drawable.ic_person).resize(width, height)
            .transform(CircleTransformation()).centerCrop().into(this)
    }
}

val Int?.isZero
    get() = this == 0

val Double?.isZero
    get() = this == 0.0

fun Double?.getAmountWithoutVat(vat: Int?) =
    ifLet(this, vat) {
        it.first().toDouble() - (it.first().toDouble() * it.second().toInt()) / 100
    }

fun Double?.getVatAmount(vat: Int?) =
    ifLet(this, vat) {
        (it.first().toDouble() * it.second().toInt()) / 100
    }

val Int?.percentageEnclosed
    get() = this?.let { String.format("(%d%%)", it) }

fun View.animateAlpha(toAlpha: Float) {
    animate().alpha(toAlpha).interpolator = LinearInterpolator()
}

fun PackageManager.isPackageInstalled(packageName: String) =
    try {
        this.getPackageInfo(packageName, 0)
        true
    }
    catch (e: PackageManager.NameNotFoundException) {
        false
    }

fun Context.sendSMSMessage(mobileNumbers: List<String>, message: String) {
    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${mobileNumbers.joinToString(
        separator = ";"
    )}")).also { intent ->
        intent.putExtra("sms_body", message)
    })
}

fun Context.shareMessage(schemeUrl: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(schemeUrl)))
}

val String.toRawMobileNumber
    get() = this.replace("\\s".toRegex(), "")

fun Date.toCalendar(plusMonths: Int? = null): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.YEAR, if (calendar.get(Calendar.MONTH) == 11) calendar.get(Calendar.YEAR) + 1 else calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, if (calendar.get(Calendar.MONTH) == 11) 0 else calendar.get(Calendar.MONTH) + (plusMonths ?: 0))
    return calendar
}

inline fun <reified T : Enum<*>> valueEnumOrNull(name: String?): T? =
    T::class.java.enumConstants?.firstOrNull { it.name == name }

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

val greetingLabel
    get() = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 3 until 13 -> R.string.key_good_morning_label
        in 13 until 19 -> R.string.key_good_afternoon_label
        in 19 until 24, in 0 until 3 -> R.string.key_good_evening_label
        else -> R.string.key_good_morning_label
    }

infix fun Context.scheduleNotification(notificationDataModel: NotificationDataModel) {
    val notificationIntent = Intent(this, NotificationReceiver::class.java)
    notificationIntent.putExtra(
        "bundle" ,
        Bundle().also {
            it.putParcelable(NotificationReceiver.NOTIFICATION_DATA_MODEL_EXTRA, notificationDataModel)
        }
    )
    val pendingIntent = PendingIntent.getBroadcast(this, getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).notificationRequestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = getSystemService(Context. ALARM_SERVICE) as? AlarmManager
    alarmManager?.setAlarmClock(AlarmManager.AlarmClockInfo(notificationDataModel.calendar.timeInMillis, pendingIntent), pendingIntent)
}

infix fun Context.createCalendarEventWith(calendarDataModel: CalendarDataModel) {
    calendarDataModel.calendar.set(Calendar.HOUR_OF_DAY, 10)
    val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.Events.TITLE, calendarDataModel.title) // Simple title
        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarDataModel.calendar.timeInMillis) // Only date part is considered when ALL_DAY is true; Same as DTSTART
        .putExtra(CalendarContract.Events.EVENT_LOCATION, (getSystemService(Service.TELEPHONY_SERVICE) as? TelephonyManager)?.networkCountryIso?.uppercase())
        .putExtra(CalendarContract.Events.DESCRIPTION, calendarDataModel.description) // Description
        .putExtra(Intent.EXTRA_EMAIL, calendarDataModel.email)
    startActivity(insertCalendarIntent)
}

infix fun CardView.animateBackgroundColor(endColor: Int) {
    if ((tag as? Pair<*, *>)?.second as? Boolean == true) return
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), cardBackgroundColor.defaultColor, endColor)
    colorAnimation.duration = 750
    colorAnimation.repeatCount = 1
    colorAnimation.repeatMode = ValueAnimator.REVERSE
    colorAnimation.addUpdateListener {
        setCardBackgroundColor(it.animatedValue as? Int ?: return@addUpdateListener)
    }
    colorAnimation.addListener(object: Animator.AnimatorListener {
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {
            tag = "isAnimating" to true
        }
        override fun onAnimationEnd(animation: Animator?) {
            tag = "isAnimating" to false
        }
    })
    colorAnimation.start()
}

val SharedPreferences.notificationRequestCode: Int
    get() {
        val requestCode = getInt(Constants.SHARED_PREFERENCES_NOTIFICATION_REQUEST_CODE_KEY, 0)
        edit(
            commit = true
        ) {
            putInt(Constants.SHARED_PREFERENCES_NOTIFICATION_REQUEST_CODE_KEY, requestCode + 1)
        }
        return requestCode
    }

val Context?.uiMode: UIMode
    get() = when(this?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_NO -> UIMode.LIGHT_MODE
        Configuration.UI_MODE_NIGHT_YES -> UIMode.DARK_MODE
        else -> UIMode.LIGHT_MODE
    }

var SharedPreferences.isNightMode: Boolean
    set(value) {
        edit(
            commit = true
        ) {
            putBoolean(Constants.DARK_MODE_KEY, value)
        }
    }
    get() = getBoolean(Constants.DARK_MODE_KEY, false)

var isNightMode: Boolean = false
    set(value) {
        field = value
        AppCompatDelegate.setDefaultNightMode(
            when(value) {
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

/*fun AppCompatActivity.searchContact(readContactsPermissionRequestCode: Int, contactsSelectorRequestCode: Int) {
    if (hasPermissions(android.Manifest.permission.READ_CONTACTS))
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK
            ).also {
                   it.type = ContactsContract.Contacts.CONTENT_TYPE
            },
            contactsSelectorRequestCode
        )
    else
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_CONTACTS
            ),
            readContactsPermissionRequestCode
        )
}*/

/*infix fun Context.contactModelFrom(uri: Uri?) =
    uri?.let { unwrappedUri ->
        val cursor = contentResolver.query(unwrappedUri, null, null, null, null) ?: return@let null
        if (cursor.count <= 0)
            return@let null
        cursor.moveToFirst()
        val id = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        val phoneNumber = if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == "1") {
            val contentCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,"${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?", arrayOf(id), null)
            contentCursor?.moveToFirst()
            val phone = contentCursor?.getStringOrNull(contentCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contentCursor?.close()
            phone
        }
        else null
        val emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,  null,"${ContactsContract.CommonDataKinds.Email.CONTACT_ID}=?", arrayOf(id), null)
        val email = if ((emailCursor?.count ?: 0) > 0 && emailCursor?.moveToFirst() == true) {
            val email = emailCursor.getStringOrNull(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            emailCursor.close()
            email
        }
        else null

        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id?.toLong() ?: 0L)
        val displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
        val photoUri = try {
            contentResolver.openAssetFileDescriptor(displayPhotoUri, "r")?.let {
                displayPhotoUri
            }
        } catch (e: IOException) {
            null
        }
        val contactModel = ContactModel(
            firstName = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))?.takeIf { it.contains(" ") }?.let {
                it.split(" ").firstOrNull()
            } ?: cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
            lastName = cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))?.takeIf { it.contains(" ") }?.let {
                it.split(" ").second()
            } ?: cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
            phoneNumber = phoneNumber,
            email = email,
            photoUri = photoUri
        )
        cursor.close()
        contactModel
    }
 */

val Resources.isLandscape
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

var SharedPreferences.currencySymbol: String?
    set(value) {
        edit(
            commit = true
        ) {
            putString(Constants.SHARED_PREFERENCES_CURRENCY_SYMBOL_KEY, value)
        }
    }
    get() = getString(Constants.SHARED_PREFERENCES_CURRENCY_SYMBOL_KEY, null)

var SharedPreferences.countryDataModel: CountryDataModel?
    set(value) {
        edit(
            commit = true
        ) {
            putString(Constants.SHARED_PREFERENCES_COUNTRY_DATA_KEY, Gson().toJson(value))
        }
    }
    get() = Gson().fromJson(getString(Constants.SHARED_PREFERENCES_COUNTRY_DATA_KEY, null), CountryDataModel::class.java)

var SharedPreferences.paymentsFilteringOptionTypes: List<PaymentsFilteringOptionType>?
    set(value) {
        edit(
            commit = true
        ) {
            putString(Constants.SHARED_PREFERENCES_PAYMENTS_FILTERING_DATA_KEY, Gson().toJson(value))
        }
    }
    get() = Gson().fromJson<List<PaymentsFilteringOptionType>>(getString(Constants.SHARED_PREFERENCES_PAYMENTS_FILTERING_DATA_KEY, null), object: TypeToken<List<PaymentsFilteringOptionType>>() {}.type)

fun ViewGroup.addTabDots(currentPage: Int, totalPages: Int) {
    val dots = arrayOfNulls<TextView>(totalPages)
    removeAllViews()
    for(count in dots.indices) {
        dots[count] = TextView(context)
        dots[count]?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dots[count]?.gravity = Gravity.CENTER
        dots[count]?.text = resources.getString(R.string.dot_value).html
        dots[count]?.textSize = 25.0f
        dots[count]?.setTextColor(ContextCompat.getColor(context,
            R.color.color_inactive))
        addView(dots[count])
    }
    if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(ContextCompat.getColor(context,
        R.color.steel))
}

val String.html: Spanned?
    get() = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)

var SharedPreferences.isFirstTime: Boolean
    set(value) {
        edit(
            commit = true
        ) {
            putBoolean(Constants.SHARED_PREFERENCES_FIRST_TIME_KEY, value)
        }
    }
    get() = getBoolean(Constants.SHARED_PREFERENCES_FIRST_TIME_KEY, true)

fun AppCompatImageView.setPicassoGroupImageInternalFile(fileName: String?) {
    fileName?.let {
        Picasso.get().load(File(context.filesDir, it)).transform(CircleTransformation()).into(this)
    }
}

val Context.isLandscape
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun RecyclerView.canScrollVertically(scrollVerticallyBlock: (canScrollVertically: Boolean) -> Unit) {
    addOnScrollListener(
        object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollVerticallyBlock(
                    recyclerView.canScrollVertically(1)
                )
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        }
    )
}

fun Window.hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setDecorFitsSystemWindows(false)
        insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    else
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

var AppCompatActivity.loaderState: Boolean
    get() = supportFragmentManager.fragments.any { it is LoaderDialog && (it as? LoaderDialog)?.isVisible == true }
    set(value) {
        if (value)
            LoaderDialog.show(
                supportFragmentManager = supportFragmentManager
            )
        else
            LoaderDialog.hide(
                supportFragmentManager = supportFragmentManager
            )
    }

var Fragment.loaderState: Boolean
    get() = childFragmentManager.fragments.any { it is LoaderDialog && (it as? LoaderDialog)?.isVisible == true }
    set(value) {
        if (value)
            LoaderDialog.show(
                supportFragmentManager = childFragmentManager
            )
        else
            LoaderDialog.hide(
                supportFragmentManager = childFragmentManager
            )
    }

fun Context.makeSoundNotification() {
    try {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(this, notification)
        ringtone.play()
    } catch (e: Exception) {}
}

var SharedPreferences.currentUserId: Int?
    set(value) = edit(
        commit = true
    ) {
        putInt(
            Constants.SHARED_PREFERENCES_CURRENT_USER_ID_KEY,
            value ?: 0
        )
    }
    get() = getInt(
        Constants.SHARED_PREFERENCES_CURRENT_USER_ID_KEY,
        0
    )

@BindingAdapter("picassoImagePath")
fun AppCompatImageView.loadImagePath(fileName: String?) {
    fileName?.let {
        Picasso.get().load(File(context.filesDir, it)).placeholder(R.drawable.ic_person)
            .resize(60.px, 60.px).transform(CircleTransformation()).centerCrop().into(this)
    }
}

@BindingAdapter("picassoImageFromInternalFiles")
fun setPicassoImageFromInternalFiles(appCompatImageView: AppCompatImageView, fileName: String?) {
    fileName?.let {
        Picasso.get().load(File(appCompatImageView.context.filesDir, it)).placeholder(R.drawable.ic_person)
            .transform(CircleTransformation()).into(appCompatImageView)
    } ?: appCompatImageView.setImageResource(R.drawable.ic_person)
}

@BindingAdapter("picassoGroupImageFromInternalFiles")
fun setPicassoGroupImageFromInternalFiles(appCompatImageView: AppCompatImageView, fileName: String?) {
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

@BindingAdapter("backgroundDrawableTintColor")
fun setBackgroundDrawableTintColor(view: View, color: Int?) {
    color?.let {
        view.background?.setTint(it)
    }
}

@BindingAdapter("backgroundViewColor")
fun setBackgroundViewColor(view: View, color: Int?) {
    color?.let {
        view.setBackgroundColor(ContextCompat.getColor(view.context, it))
    }
}

@BindingAdapter("backgroundViewContextColor")
fun setBackgroundViewContextColor(viewGroup: ViewGroup, resourceId: Int?) {
    resourceId?.let {
        viewGroup.setBackgroundColor(resourceId)
    }
}

@BindingAdapter("backgroundViewTintColor")
fun setBackgroundViewTintColor(view: View, color: Int?) {
    color?.let {
        view.background?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(view.context, it), PorterDuff.Mode.SRC_IN)
    }
}

@BindingAdapter("imageFromByteArray")
fun setImageFromByteArray(appCompatImageView: AppCompatImageView, byteArray: ByteArray?) {
    byteArray?.let {
        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
        appCompatImageView.setImageBitmap(bitmap)
    }
}

@BindingAdapter("viewBackground")
fun setViewBackground(viewGroup: ViewGroup, resourceId: Int?) {
    resourceId?.let {
        viewGroup.setBackgroundResource(resourceId)
    }
}

@BindingAdapter("textViewColorByPaymentDate")
fun setTextViewColorByPaymentDate(materialTextView: MaterialTextView, payments: List<PaymentAmountModel?>?) {
    payments?.takeIf { it.isNotEmpty() } ?: return
    if (payments.mapNotNull {
            it?.paymentMonthDate
        }.all {
            val paymentMonthDate = it.toCalendar(plusMonths = 1)
            paymentMonthDate.time.isDatePassed
        })
        materialTextView.setTextColor(
            ContextCompat.getColor(materialTextView.context,
                 R.color.red
            )
        )
    else
        materialTextView.setTextColor(
            ContextCompat.getColor(
                materialTextView.context,
                R.color.green
            )
        )
}

@BindingAdapter("layoutWidth")
fun setLayoutWidth(view: View, width: Float) {
    view.layoutParams.apply {
        this.width = 0
    }
    val valueAnimator = ValueAnimator.ofInt(0, width.toInt())
    valueAnimator.addUpdateListener {
        view.layoutParams.apply {
            this.width = it.animatedValue as? Int ?: return@apply
        }
        view.requestLayout()
    }
    valueAnimator.duration = 1000L
    valueAnimator.start()
}

@BindingAdapter("lottieAnimation")
fun setLottieAnimation(lottieAnimationView: LottieAnimationView, animatedJsonFile: String?) {
    animatedJsonFile?.let {
        lottieAnimationView.setAnimation(it)
    }
}

@BindingAdapter("animatedText")
fun setAnimatedText(materialTextView: MaterialTextView, text: String?) {
    text?.let {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) { }
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) {
                materialTextView.text = it
            }
        })
        materialTextView.startAnimation(anim)
    }
}

@BindingAdapter("picassoResourceDrawable")
fun setPicassoDrawable(appCompatImageView: AppCompatImageView, resourceId: Int?) {
    resourceId?.let {
        appCompatImageView.post {
            Picasso.get().load(it).resize(appCompatImageView.width * 2, 0).into(appCompatImageView)
        }
    }
}

@BindingAdapter("bulletsHtmlText")
fun setHtmlTextWithBullets(materialTextView: MaterialTextView, htmlTextResourceId: Int?) {
    htmlTextResourceId?.let { htmlResource ->
        val htmlSpannable = Html.fromHtml(materialTextView.resources.getString(htmlResource), Html.FROM_HTML_MODE_LEGACY)
        val spannableBuilder = SpannableStringBuilder(htmlSpannable)
        val bulletSpans = spannableBuilder.getSpans(0, spannableBuilder.length, BulletSpan::class.java)
        bulletSpans.forEach {
            val start = spannableBuilder.getSpanStart(it)
            val end = spannableBuilder.getSpanEnd(it)
            spannableBuilder.removeSpan(it)
            spannableBuilder.setSpan(
                ImprovedBulletSpan(bulletRadius = 3.px, gapWidth = 8.px),
                start,
                end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        materialTextView.text = spannableBuilder
    }
}

@BindingAdapter("imageTintColor")
fun setImageViewTint(appCompatImageView: AppCompatImageView, tintColor: Int?) {
    tintColor?.let {
        appCompatImageView.imageTintList = ColorStateList.valueOf(it)
    }
}

@BindingAdapter("imageViewBitmap")
fun setImageViewBitmap(appCompatImageView: AppCompatImageView, bitmap: Bitmap?) {
    appCompatImageView.setImageBitmap(bitmap ?: return)
}

@BindingAdapter("nullableTextResource")
fun setNullableTextResource(materialTextView: MaterialTextView, resourceId: Int?) {
    materialTextView.text = materialTextView.context.resources.getString(resourceId ?: return)
}
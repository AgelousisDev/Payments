package com.agelousis.payments.utils.extensions

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.agelousis.payments.R
import com.agelousis.payments.custom.picasso.CircleTransformation
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.utils.custom.ImprovedBulletSpan
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File

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

@BindingAdapter("picassoUrlImage")
fun setPicassoUrlImage(appCompatImageView: AppCompatImageView, imageUrl: String?) {
    Picasso.get().load(imageUrl ?: return).into(
        appCompatImageView,
        object: Callback {
            override fun onSuccess() {
                appCompatImageView.alpha = 0f
                appCompatImageView.animate().alpha(1f)
            }

            override fun onError(e: java.lang.Exception?) {
                appCompatImageView.alpha = 0f
                appCompatImageView.setImageResource(
                    R.drawable.ic_no_wifi
                )
                appCompatImageView.animate().alpha(1f)
            }
        }
    )
}

@BindingAdapter("optionType", "switchStateChanged")
fun switchStateChanged(switchMaterial: SwitchMaterial, optionType: OptionType, optionPresenter: OptionPresenter) {
    switchMaterial.setOnCheckedChangeListener { _, isChecked ->
        when(optionType) {
            OptionType.CHANGE_BIOMETRICS_STATE ->
                optionPresenter.onBiometricsState(
                    state = isChecked
                )
            OptionType.CHANGE_BALANCE_OVERVIEW_STATE ->
                optionPresenter.onBalanceOverviewStateChange(
                    state = isChecked
                )
            else -> {}
        }
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

@BindingAdapter("backgroundViewContextTintColor")
fun setBackgroundViewContextTintColor(view: View, color: Int?) {
    color?.let {
        view.background?.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
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
    appCompatImageView.imageTintList = tintColor?.let { ColorStateList.valueOf(it) }
}

@BindingAdapter("imageViewBitmap")
fun setImageViewBitmap(appCompatImageView: AppCompatImageView, bitmap: Bitmap?) {
    appCompatImageView.setImageBitmap(bitmap ?: return)
}

@BindingAdapter("nullableTextResource")
fun setNullableTextResource(materialTextView: MaterialTextView, resourceId: Int?) {
    materialTextView.text = materialTextView.context.resources.getString(resourceId ?: return)
}

@BindingAdapter("alphaOrBlurEffectViewGroup")
fun setAlphaOrBlurEffectViewGroup(viewGroup: ViewGroup, state: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        if (state)
            viewGroup.setRenderEffect(
                RenderEffect.createBlurEffect(
                    5f,
                    5f,
                    Shader.TileMode.CLAMP
                )
            )
        else
            viewGroup.setRenderEffect(
                null
            )
    else
        viewGroup.alpha = if (state) 0.5f else 1.0f

}
package com.agelousis.payments.widgets.extensions

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.widgets.PaymentsAppWidgetProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Context.updatePaymentsAppWidget() {
    val intent = Intent(this, PaymentsAppWidgetProvider::class.java)
    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
    // since it seems the onUpdate() is only fired on that:
    val ids = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, PaymentsAppWidgetProvider::class.java))
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    sendBroadcast(intent)
}

var SharedPreferences.clientModelList: List<ClientModel>
    set(value) {
        edit(
            commit = true
        ) {
            putString(
                Constants.SHARED_PREFERENCES_CLIENT_MODEL_DATA,
                Gson().toJson(value, object: TypeToken<List<ClientModel>>(){}.type)
            )
        }
    }
    get() = Gson().fromJson(
        getString(
            Constants.SHARED_PREFERENCES_CLIENT_MODEL_DATA,
            null
        ),
        object: TypeToken<List<ClientModel>>(){}.type
    )

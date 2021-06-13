package com.agelousis.payments.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import com.agelousis.payments.R
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.constants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentsAppWidget: AppWidgetProvider() {

    companion object {
        const val CLIENT_MODEL_DATA = "PaymentsAppWidget=clientModelData"
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        initializeClientsAndUpdateWidget(
            context = context ?: return,
            appWidgetManager = appWidgetManager ?: return,
            appWidgetIds = appWidgetIds ?: return
        )
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun initializeClientsAndUpdateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)?.getInt(Constants.SHARED_PREFERENCES_CURRENT_USER_ID_KEY, 0)?.let { userId ->
            uiScope.launch {
                val dbManager = DBManager(
                    context = context,
                )
                dbManager.initializePayments(
                    userId = userId
                ) { list ->
                    for (appWidgetId in appWidgetIds) {
                        val intent = Intent(context, PaymentsWidgetRemoteViewsService::class.java)
                        // Add the app widget ID to the intent extras.
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        intent.putExtra(
                            "bundle",
                            Bundle().also {
                                it.putParcelableArrayList(
                                    CLIENT_MODEL_DATA,
                                    ArrayList(list.filterIsInstance<ClientModel>())
                                )
                            }
                        )
                        intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
                        // Instantiate the RemoteViews object for the app widget layout.
                        val views = RemoteViews(
                            context.packageName,
                            R.layout.payments_collection_widget_layout
                        )
                        views.setRemoteAdapter(R.id.widgetListView, intent)
                        //appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        } ?: run {
            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(
                    context.packageName,
                    R.layout.payments_collection_widget_layout
                )
                views.setEmptyView(R.layout.payment_widget_row_layout, R.id.textViewHeaderTitle)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

}
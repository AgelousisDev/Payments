package com.agelousis.payments.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.agelousis.payments.R

class PaymentsAppWidgetProvider: AppWidgetProvider() {

    /*companion object {
        const val CLIENT_MODEL_DATA = "PaymentsAppWidget=clientModelData"
        private const val WIDGET_LIST_ITEM_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
    }*/

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        /*if (intent.action == WIDGET_LIST_ITEM_ACTION)
            redirectToApp(
                context = context ?: return,
                Gson().fromJson(
                    intent.extras?.getString(CLIENT_MODEL_DATA) ?: return,
                    ClientModel::class.java
                )
            )*/
    }


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
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, PaymentsWidgetRemoteViewsService::class.java)
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            // Instantiate the RemoteViews object for the app widget layout.
            val views = RemoteViews(
                context.packageName,
                R.layout.payments_collection_widget_layout
            )
            views.setEmptyView(R.layout.payment_widget_row_layout, R.id.textViewHeaderTitle)
            views.setRemoteAdapter(R.id.widgetListView, intent)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            /*val appWidgetIntent = Intent(context, PaymentsAppWidgetProvider::class.java)
            appWidgetIntent.action = WIDGET_LIST_ITEM_ACTION
            val widgetPendingIntent = PendingIntent.getBroadcast(
                context, 0, appWidgetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setPendingIntentTemplate(R.id.widgetListView, widgetPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView)*/
        }
    }

    /*private fun redirectToApp(context: Context, clientModel: ClientModel) {
        context.startActivity(
            Intent(
                context,
                NotificationActivity::class.java
            ).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.putExtra(
                    NotificationActivity.BUBBLE_NOTIFICATION_EXTRA,
                    NotificationDataModel(
                        calendar = clientModel.payments?.lastOrNull()?.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.defaultTimeCalendar ?: return,
                        notificationId = 0,
                        title = clientModel.fullName,
                        body = String.format(
                            context.resources.getString(R.string.key_notification_amount_value),
                            clientModel.payments.lastOrNull()?.paymentAmount?.euroFormattedString ?: ""
                        ),
                        date = clientModel.payments.lastOrNull()?.paymentDate?.toDateWith(pattern = Constants.GENERAL_DATE_FORMAT)?.formattedDateWith(pattern = Constants.VIEWING_DATE_FORMAT),
                        groupName = clientModel.groupName,
                        groupImage = clientModel.groupImage,
                        groupTint = clientModel.groupColor
                    )
                )
            }
        )
    }*/

}
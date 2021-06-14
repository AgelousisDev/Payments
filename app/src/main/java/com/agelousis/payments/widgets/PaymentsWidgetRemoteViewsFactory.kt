package com.agelousis.payments.widgets

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.extensions.euroFormattedString


class PaymentsWidgetRemoteViewsFactory(private val context: Context, private val clientModelList: List<ClientModel>): RemoteViewsService.RemoteViewsFactory {

    companion object {
        const val CLIENT_MODEL_EXTRA = "PaymentsWidgetRemoteViewsFactory=clientModelExtra"
    }

    override fun onCreate() {}

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()
        //val uri = Contract.PATH_TODOS_URI
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {}

    override fun getCount() = clientModelList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.payment_widget_row_layout)
        rv.setTextViewText(
            R.id.textViewHeaderTitle,
            clientModelList.getOrNull(index = position)?.fullName
        )
        rv.setTextColor(
            R.id.textViewHeaderTitle,
                clientModelList.getOrNull(
                    index = position
                )?.groupColor ?: return rv
        )
        rv.setTextViewText(
            R.id.textViewFooterTitle,
            clientModelList.getOrNull(index = position)?.totalPaymentAmount?.euroFormattedString
        )
        rv.setTextColor(
            R.id.textViewFooterTitle,
            clientModelList.getOrNull(
                index = position
            )?.groupColor ?: return rv
        )
        rv.setOnClickPendingIntent(
            R.id.linearLayout,
            PendingIntent.getBroadcast(
                context, 0,
                Intent().also { intent ->
                    intent.putExtras(
                        Bundle().also { bundle ->
                            bundle.putParcelable(
                                CLIENT_MODEL_EXTRA,
                                clientModelList.getOrNull(
                                    index = position
                                )
                            )
                        }
                    )
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        return rv
    }

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

}
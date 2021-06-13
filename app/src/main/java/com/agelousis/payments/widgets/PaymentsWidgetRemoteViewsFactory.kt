package com.agelousis.payments.widgets

import android.content.Context
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.extensions.euroFormattedString

class PaymentsWidgetRemoteViewsFactory(private val context: Context, private val clientModelList: List<ClientModel>): RemoteViewsService.RemoteViewsFactory {

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
            "${clientModelList.getOrNull(index = position)?.fullName} ${clientModelList.getOrNull(index = position)?.totalPaymentAmount?.euroFormattedString}"
        )
        return rv
    }

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

}
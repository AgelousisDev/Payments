package com.agelousis.payments.widgets

import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.agelousis.payments.R
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.agelousis.payments.widgets.extensions.clientModelList
import com.google.gson.Gson

class PaymentsWidgetRemoteViewsFactory(private val context: Context): RemoteViewsService.RemoteViewsFactory {

    companion object {
        const val CLIENT_MODEL_EXTRA = "PaymentsWidgetRemoteViewsFactory=clientModelExtra"
    }

    private val clientModelList
        get() = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)?.clientModelList ?: listOf()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        println(clientModelList)
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
            clientModelList.getOrNull(index = position)?.totalPaymentAmount?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)
        )
        rv.setTextColor(
            R.id.textViewFooterTitle,
            clientModelList.getOrNull(
                index = position
            )?.groupColor ?: return rv
        )
        val extras = Bundle()
        extras.putString(
            CLIENT_MODEL_EXTRA,
            Gson().toJson(
                clientModelList.getOrNull(
                    index = position
                )
            )
        )
        /*val intent = Intent()
        intent.putExtras(extras)
        rv.setOnClickFillInIntent(
            R.id.paymentWidgetLayout,
            intent
        )*/
        return rv
    }

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

}
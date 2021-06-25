package com.agelousis.payments.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import com.agelousis.payments.main.ui.payments.models.ClientModel

class PaymentsWidgetRemoteViewsService: RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        intent?.setExtrasClassLoader(
            ClientModel::class.java.classLoader
        )
        return PaymentsWidgetRemoteViewsFactory(
            context = applicationContext,
            clientModelList = intent?.extras?.getBundle("bundle")?.getParcelableArrayList(PaymentsAppWidgetProvider.CLIENT_MODEL_DATA)
                ?: listOf()
        )
    }

}
package com.agelousis.payments.network

import com.agelousis.payments.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkHelper {

    val network: Retrofit? by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.FIREBASE_CLOUD_MESSAGING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> create() = network?.create(T::class.java)

}
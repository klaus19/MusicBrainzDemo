package com.example.android.network



import okhttp3.Interceptor

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MBClient @Inject constructor() {


    companion object {
        private const val BASE_URL = "https://musicbrainz.org/ws/2/"
    }

    /**
     * To avoid the API Throttling we need to make custom header
     * https://musicbrainz.org/doc/MusicBrainz_API/Rate_Limiting
     */
    private val headerInterceptor = Interceptor { chain ->
        var req = chain.request()

        req = req.newBuilder()
            .header("User-agent", "MusicBrainzAndroid/Test (chrehansarwar@gmail.com)")
            .addHeader("Accept", "application/json").build()
        chain.proceed(req)
    }

    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    private fun getRetrofitClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .also { client ->
                if (androidx.viewbinding.BuildConfig.DEBUG) {
                    client.addInterceptor(
                        HttpLoggingInterceptor()
                        .apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                }
            }.build()
    }
}



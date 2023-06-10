package io.laonda.storestamp.net

import android.content.Context
import android.util.Log
import androidx.viewbinding.BuildConfig
import io.laonda.storestamp.application.YsApplication
import io.laonda.storestamp.utils.LoadingDialog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

// 이거 잘못 만들었다.. 나중에 수정하자. 이렇게 하면 싱글톤이 아니자나!
object RetrofitClient {
    private val BASE_URL = "https://api.yellowstamp.io:6081"
//    private val BASE_URL = BuildConfig.DEBUG.takeIf { it == true }?.let {"http://dev.yellowstamp.io:6080"} ?: "https://api.yellowstamp.io:6081"
    var accessToken
        get() = YsApplication.sharedPref.accessToken
        set(value) {
            YsApplication.sharedPref.accessToken = value
        }
    var refreshToken
        get() = YsApplication.sharedPref.refreshToken
        set(value) {
            YsApplication.sharedPref.refreshToken = value
        }
    private var token = ""

    fun getInstance(): CsRetrofitService {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(CsRetrofitService::class.java)
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
            = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        addNetworkInterceptor(HttpLoggingInterceptor().apply {
            level = BuildConfig.DEBUG.takeIf { it == true }?.let {
                HttpLoggingInterceptor.Level.BODY
            } ?: HttpLoggingInterceptor.Level.NONE
        })
        build()
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            if (request().url().toString().contains("/auth/refresh") == true) {
                token = refreshToken
            } else {
                token = accessToken
            }

            val newRequest = request().newBuilder()
                .addHeader("Authorization", "Bearer ${token}")
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
                .build()

            proceed(newRequest)
        }
    }
}
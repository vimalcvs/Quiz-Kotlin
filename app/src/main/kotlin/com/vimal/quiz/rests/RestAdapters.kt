package com.vimal.quiz.rests

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.vimal.quiz.utils.Config.getAdminUrl
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestAdapters {

    private const val TIMEOUT = 10
    private var adminRetrofit: Retrofit? = null

    fun createAPI(context: Context): ApiInterfaces {
        if (adminRetrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .cache(Cache(context.cacheDir, (5 * 1024 * 1024).toLong()))
                .addInterceptor { chain: Interceptor.Chain ->
                    var request = chain.request()
                    request = if (isNetworkConnected(context)) {
                        request.newBuilder()
                            .header("Cache-Control", "public, max-age=" + 1)
                            .build()
                    } else {
                        request.newBuilder()
                            .header(
                                "Cache-Control",
                                "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 30
                            )
                            .build()
                    }
                    chain.proceed(request)
                }
                .build()

            adminRetrofit = Retrofit.Builder()
                .baseUrl(getAdminUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return adminRetrofit!!.create(ApiInterfaces::class.java)
    }

    @Suppress("DEPRECATION")
    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
}

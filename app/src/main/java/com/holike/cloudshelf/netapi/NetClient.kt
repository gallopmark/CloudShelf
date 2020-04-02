package com.holike.cloudshelf.netapi

import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.util.LogCat
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


//kotlin下单例双重校验锁式
class NetClient private constructor() {

    companion object {
        private const val TAG = "NetClient"
        private const val DEFAULT_TIMEOUT = 60 //默认超时时间60秒

        private val mInstance: NetClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetClient()
        }

        fun getInstance(): NetClient = mInstance
    }

    private var mRetrofit: Retrofit

    init {
        val okBuilder = OkHttpClient().newBuilder()
                .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            //调试模式下开启打印信息
            //开启Log,打印网络请求信息
            val httpLoggingInterceptor = HttpLoggingInterceptor label@
            { message ->
                if (message.isNullOrEmpty()) return@label
                LogCat.i(TAG, message)
            }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okBuilder.addInterceptor(httpLoggingInterceptor)
        }
        val sInterceptor = Interceptor { chain: Interceptor.Chain ->
            val rBuilder = chain.request().newBuilder()
            rBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded")
            val cliId = PreferenceSource.getCliId()
            if (!cliId.isNullOrEmpty()) {
                rBuilder.addHeader("cliId", cliId)
                LogCat.e("api", "cliId:$cliId")
            }
            val token = PreferenceSource.getToken()
            if (!token.isNullOrEmpty()) {
                rBuilder.addHeader("token", token)
                LogCat.e("api", "token:$token")
            }
            rBuilder.addHeader("Accept", "application/json")
            chain.proceed(rBuilder.build())
        }
        okBuilder.addInterceptor(sInterceptor)
        val httpClient = okBuilder.build()
        mRetrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
    }

    fun getNetApi(): ApiService = mRetrofit.create(ApiService::class.java)
}
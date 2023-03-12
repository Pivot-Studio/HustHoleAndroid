package cn.pivotstudio.modulec.homescreen.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

class DownloadRepository(
    private val baseUrl: String
) {
    fun getDownloadRetrofitService(
        downloadedLength: Long = 0
    ): DownloadRetrofitService {
        val requestInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader("RANGE", "bytes=$downloadedLength-")
                .build()
                .let { chain.proceed(it) }
        }
        val okhttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .build()
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okhttpClient)
            .baseUrl(baseUrl)
            .build()
        return retrofit.create(DownloadRetrofitService::class.java)
    }

    interface DownloadRetrofitService {
        @GET("{fileName}")
        suspend fun getApk(@Path("fileName")fileName: String): ResponseBody
    }
}
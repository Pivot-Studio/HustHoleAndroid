package cn.pivotstudio.moduleb.rebase.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class DownloadApi(private val BASE_URL: String) {
    fun init(downloadedLength: Long = 0L): DownloadApiService {
        //断点下载
        val requestInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader("RANGE", "bytes=$downloadedLength-")
                .build()
                .let { chain.proceed(it) }
        }
        val okhttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okhttpClient)
            .baseUrl(BASE_URL)
            .build()
        return retrofit.create(DownloadApiService::class.java)
    }
    interface DownloadApiService {
        @GET("{fileName}")
        suspend fun download(@Path("fileName") fileName: String): ResponseBody
    }
}
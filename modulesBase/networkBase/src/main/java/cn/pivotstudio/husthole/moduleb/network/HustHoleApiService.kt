package cn.pivotstudio.husthole.moduleb.network

import android.content.Context
import cn.pivotstudio.husthole.moduleb.network.model.ForestHoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.TokenResponse
import cn.pivotstudio.husthole.moduleb.network.model.User
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.database.MMKVUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://hustholev2.pivotstudio.cn/api/"

object HustHoleApi {
    lateinit var retrofitService: HustHoleApiService
    fun init(context: Context) {

        val requestInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader("os", "android")
                .addHeader("dateTime", DateUtil.getDateTime()).build()
                .let { chain.proceed(it) }
        }

        val tokenInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    MMKVUtil.getMMKV(context).getString("USER_TOKEN_V2")
                )
                .build()
                .let { chain.proceed(it) }
        }

        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okhttpClient)
            .baseUrl(BASE_URL)
            .build()

        retrofitService = retrofit.create(HustHoleApiService::class.java)

    }

}


interface HustHoleApiService {
    @GET("forest/listHole")
    suspend fun getHolesInForest(
        @Query("forestId") forestId: Int,
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<ForestHoleV2>

    @POST("user/signIn")
    suspend fun signIn(
        @Body user: User
    ): TokenResponse

    @GET("forest/findOne")
    suspend fun getForestOverview(
        @Query("forestId") forestId: Int
    ): ForestBrief

    @POST("user/expired")
    suspend fun checkIfLogin(): String?

    /** 用户加入的小树林树洞列表 */
    @GET("forest/listJoinHole")
    suspend fun getAJoinedForestHoles(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST_REPLY",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<ForestHoleV2>

    /** 小树林列表 */
    @GET("forest/list")
    suspend fun getAllForests(
        @Query("descend") descend: Boolean,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<ForestBrief>

    /** 我加入的小树林 */
    @GET("user/forest")
    suspend fun getJoinedForests(
        @Query("descend") descend: Boolean,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("timestamp") timestamp: String
    ): List<ForestBrief>

    @POST("forest/join")
    suspend fun joinTheForestBy(
        @Query("forestId") forestId: String
    )


}


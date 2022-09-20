package cn.pivotstudio.husthole.moduleb.network

import android.content.Context
import cn.pivotstudio.husthole.moduleb.network.model.*
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.database.MMKVUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
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

        val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)

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
            .addInterceptor(httpLoggingInterceptor)
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

    @GET("hole/list")
    suspend fun getHoles(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST_REPLY",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<HoleV2>

    /** 我加入的小树林 */
    @GET("user/forest")
    suspend fun getJoinedForests(
        @Query("descend") descend: Boolean,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("timestamp") timestamp: String
    ): List<ForestBrief>

    /** 用户登录 */
    @POST("user/signIn")
    suspend fun signIn(
        @Body user: RequestBody.User
    ): TokenResponse

    @POST("user/expired")
    suspend fun checkIfLogin(): String?

    //========================================================================================================

    /** 单个小树林树洞列表 */
    @GET("forest/listHole")
    suspend fun getHolesInForest(
        @Query("forestId") forestId: String,
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<HoleV2>

    /** 单个树洞 */
    @GET("forest/findOne")
    suspend fun getForestOverview(
        @Query("forestId") forestId: String
    ): ForestBrief

    /** 用户加入的小树林树洞列表 */
    @GET("forest/listJoinHole")
    suspend fun getAJoinedForestHoles(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST_REPLY",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<HoleV2>

    /** 小树林列表 */
    @GET("forest/list")
    suspend fun getAllForests(
        @Query("descend") descend: Boolean,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<ForestBrief>

    /** 加入小树林 */
    @POST("forest/join")
    suspend fun joinTheForestBy(
        @Body forestId: RequestBody.ForestId
    ): Response<Unit>

    /** 退出小树林 */
    @POST("forest/quit")
    suspend fun quitTheForestBy(
        @Body forestId: RequestBody.ForestId
    ): Response<Unit>

    //========================================================================================================
    @POST("interact/follow")
    suspend fun followTheHole(
        @Body holeId: RequestBody.HoleId
    ): Response<Unit>

    @POST("interact/unfollow")
    suspend fun unFollowTheHole(
        @Body holeId: RequestBody.HoleId
    ): Response<Unit>

    @POST("interact/like")
    suspend fun giveALikeToTheHole(
        @Body holeId: RequestBody.HoleId
    ): Response<Unit>

    @POST("interact/unlike")
    suspend fun unLikeTheHole(
        @Body holeId: RequestBody.HoleId
    )

    //========================================================================================================
    @GET("hole/one")
    suspend fun loadTheHole(
        @Query("holeId") holeId: String
    ): HoleV2


}

enum class ApiStatus {
    SUCCESSFUL,
    LOADING,
    ERROR
}

sealed class ApiResult {

    data class Success<T>(
        val status: ApiStatus = ApiStatus.SUCCESSFUL, var data: T? = null
    ) : ApiResult()

    data class Error(
        val status: ApiStatus = ApiStatus.ERROR,
        val code: Int,
        val errorMessage: String? = null
    ) : ApiResult()

    data class Loading(
        val status: ApiStatus = ApiStatus.LOADING
    ) : ApiResult()
}


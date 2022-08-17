package cn.pivotstudio.husthole.moduleb.network

import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.TokenResponse
import cn.pivotstudio.husthole.moduleb.network.model.User
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://hustholev2.pivotstudio.cn/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(NetworkApi.getOkHttpClientV2())
    .baseUrl(BASE_URL)
    .build()

interface HustHoleApiService {
    @GET("forest/listHole")
    suspend fun getHolesInForest(
        @Query("forestId") forestId: Int,
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "LATEST",
        @Query("offset") offset: Int = 0,
        @Query("timestamp") timestamp: String
    ): List<DetailForestHoleV2>

    @POST("user/signIn")
    suspend fun signIn(
        @Body user: User
    ): TokenResponse

    @GET("forest/findOne")
    suspend fun getForestOverview(
        @Query("forestId") forestId: Int
    ): ForestBrief
}

object HustHoleApi {
    val retrofitService: HustHoleApiService by lazy { retrofit.create(HustHoleApiService::class.java) }
}
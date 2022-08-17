package cn.pivotstudio.modulec.homescreen.network

import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHole
import cn.pivotstudio.husthole.moduleb.network.model.ForestHole
import cn.pivotstudio.modulec.homescreen.model.*
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @classname HSRequestInterface
 * @description: 首页网络请求接口
 * @date 2022/5/3 0:42
 * @version:1.0
 * @author: mhh
 */
interface HSRequestInterface {
    @GET("version")
    fun checkUpdate(): Observable<VersionResponse>

    @GET("holes")
    fun homepageHoles(
        @Query("is_descend") is_descend: Boolean,
        @Query("is_last_reply") is_last_reply: Boolean,
        @Query("start_id") start_id: Int,
        @Query("list_size") list_size: Int
    ): Observable<List<DataBean>>

    @POST
    fun follow(@Url url: String): Observable<MsgResponse>

    @DELETE
    fun deleteFollow(@Url url: String): Observable<MsgResponse>

    @POST
    fun thumbups(@Url url: String): Observable<MsgResponse>

    @DELETE
    fun deleteThumbups(@Url url: String): Observable<MsgResponse>

    @HTTP(method = "DELETE", path = "holes/{hole_id}", hasBody = false)
    fun deleteHole(@Path("hole_id") holeId: String): Observable<MsgResponse>

    @GET
    fun searchSingleHole(@Url url: String): Observable<DataBean>

    @GET("search/hole")
    fun searchHoles(
        @Query("keyword") keyword: String,
        @Query("start_id") start_id: Int,
        @Query("list_size") list_size: Int
    ): Observable<List<DataBean>>

    @GET("notices")
    fun searchNotices(
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): Observable<NoticeResponse>

    @GET("notices")
    suspend fun searchNoticesFlow(
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): NoticeResponse


    /** =========小树林相关的接口========== */
    @GET("forests/holes")
    fun searchForestHoles(
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int,
        @Query("is_last_reply") isLastReply: Boolean
    ): Observable<List<ForestHole>>

    @GET("forests/joined")
    fun searchForestHeads(
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): Observable<ForestHeads>

    @POST("forests/join/{forest_id}")
    fun joinTheForest(
        @Path("forest_id") forestId: String
    ): Observable<MsgResponse>

    @DELETE("forests/quit/{forest_id}")
    fun quitTheForest(
        @Path("forest_id") forestId: String
    ): Observable<MsgResponse>

    @GET("forests/types")
    fun searchForestTypes(
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): Observable<ForestTypes>

    @GET("forests/type/{forest_type}")
    fun searchForestByType(
        @Path("forest_type") type: String,
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): Observable<ForestCardList>

    @GET("forests/{forest_id}/holes")
    fun searchDetailForestHolesByForestId(
        @Path("forest_id") forestId: Int,
        @Query("start_id") startId: Int,
        @Query("list_size") listSize: Int
    ): Observable<List<DetailForestHole>>

    @GET("forests/detail/{forest_id}")
    fun searchDetailForestOverviewByForestId(
        @Path("forest_id") forestId: Int
    ): Observable<ForestCardList>
}
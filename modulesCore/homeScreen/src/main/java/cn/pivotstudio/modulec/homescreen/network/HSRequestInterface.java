package cn.pivotstudio.modulec.homescreen.network;

import java.util.List;

import cn.pivotstudio.modulec.homescreen.model.ForestHead;
import cn.pivotstudio.modulec.homescreen.model.ForestHeads;
import cn.pivotstudio.modulec.homescreen.model.ForestHole;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @classname HSRequestInterface
 * @description: 首页网络请求接口
 * @date 2022/5/3 0:42
 * @version:1.0
 * @author: lzt
 */
public interface HSRequestInterface {
    @GET("version")
    Observable<VersionResponse> checkUpdate();

    @GET("holes?")
    Observable<List<HomepageHoleResponse.DataBean>> homepageHoles(@Query("is_descend") Boolean is_descend,
                                                                  @Query("is_last_reply") Boolean is_last_reply,
                                                                  @Query("start_id") int start_id,
                                                                  @Query("list_size") int list_size);

    @POST
    Observable<MsgResponse> follow(@Url String url);

    @DELETE
    Observable<MsgResponse> deleteFollow(@Url String url);

    @POST
    Observable<MsgResponse> report(@Url String url);

    @POST
    Observable<MsgResponse> thumbups(@Url String url);

    @DELETE
    Observable<MsgResponse> deleteThumbups(@Url String url);

    @HTTP(method = "DELETE", path = "holes/{hole_id}", hasBody = false)
    Observable<MsgResponse> deleteHole(@Path("hole_id") String hole_id);

    @GET
    Observable<HomepageHoleResponse.DataBean> searchSingleHole(@Url String url);

    @GET("search/hole?")
    Observable<List<HomepageHoleResponse.DataBean>> searchHoles(@Query("keyword") String keyword,
                                                                @Query("start_id") int start_id,
                                                                @Query("list_size") int list_size);

    @GET("forests/holes?")
    Observable<List<ForestHole>> searchForestHoles(
            @Query("start_id") int startId,
            @Query("list_size") int listSize,
            @Query("is_last_reply") Boolean isLastReply
            );

    @GET("forests/joined?")
    Observable<ForestHeads> searchForestHeads(
            @Query("start_id") int startId,
            @Query("list_size") int listSize
    );

    @GET("forests/types?")
    Observable<List<String>> searchForestTypes(
            @Query("start_id") int startId,
            @Query("list_size") int listSize
    );

}

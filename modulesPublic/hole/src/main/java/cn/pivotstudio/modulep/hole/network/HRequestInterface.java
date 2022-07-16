package cn.pivotstudio.modulep.hole.network;



import cn.pivotstudio.modulep.hole.model.HoleResponse;
import cn.pivotstudio.modulep.hole.model.MsgResponse;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @classname:HRequestInterface
 * @description:
 * @date:2022/5/8 13:28
 * @version:1.0
 * @author:
 */
public interface HRequestInterface {
    @GET("replies/hot?")
    Observable<ReplyListResponse> getHotReply(@Query("hole_id") int hole_id,
                                           @Query("start_id") int start_id,
                                           @Query("list_size") int list_size);

    @GET("replies/owner?")
    Observable<ReplyListResponse> getOwnerReply(@Query("hole_id") int hole_id,
                                        @Query("start_id") int start_id,
                                        @Query("list_size") int list_size,
                                        @Query("is_descend") Boolean is_descend);
    @GET
    Observable<HoleResponse> getHole(@Url String url);
    @POST
    Observable<MsgResponse> sendReply(@Url String url);

    @GET
    Observable<ReplyListResponse> getReplies(@Url String url);

    @POST
    Observable<MsgResponse> follow(@Url String url);


    @DELETE
    Observable<MsgResponse>deleteFollow(@Url String url);

    @POST
    Observable<MsgResponse> report(@Url String url);

    @POST
    Observable<MsgResponse> thumbups(@Url String url);

    @DELETE
    Observable<MsgResponse> deleteThumbups(@Url String url);


    @HTTP(method = "DELETE", path = "holes/{hole_id}", hasBody = false)
    Observable<MsgResponse>deleteHole(@Path("hole_id") String hole_id);

    @DELETE
    Observable<MsgResponse>deleteReply(@Url String url);

}

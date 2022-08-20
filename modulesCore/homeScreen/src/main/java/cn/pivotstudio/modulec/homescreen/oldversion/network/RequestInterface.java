package cn.pivotstudio.modulec.homescreen.oldversion.network;

import java.util.HashMap;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RequestInterface {//接口

    @POST("mobileLogin")
    Call<ResponseBody> mobileLogin(@Body HashMap map);

    @POST
    Call<ResponseBody> sendVerifyCode(@Url String url);

    @POST("sendVerifyCode")
    Call<ResponseBody> sendVerifyCode2(@Body HashMap map);

    @POST
    Call<ResponseBody> resetPassword(@Url String url);

    @POST
    Call<ResponseBody> verifyCodeMatch(@Url String url);

    @POST("activation")
    Call<ResponseBody> verifyCodeMatch2(@Body HashMap map);

    @POST("mobileRegister")
    Call<ResponseBody> register(@Body HashMap map);

    @GET("forests/types?")
    Call<ResponseBody> getType(@Query("start_id") int start_id, @Query("list_size") int list_size);

    @GET("forests/hot?")
    Call<ResponseBody> getHotForest(@Query("start_id") int start_id,
                                    @Query("list_size") int list_size);

    @HTTP(method = "GET", path = "forests/type/{forest_type}?", hasBody = false)
    Call<ResponseBody> getDetailTypeForest(@Path("forest_type") String forest_type,
                                           @Query("start_id") int start_id,
                                           @Query("list_size") int list_size,
                                           @Query("is_last_active") Boolean is_last_active);

    @POST
    Call<ResponseBody> join(@Url String url);

    @DELETE
    Call<ResponseBody> delete(@Url String url);

    @GET("forests/joined?")
    Call<ResponseBody> joined(@Query("list_size") int list_size, @Query("start_id") int start_id);

    @GET("forests/holes?")
    Call<ResponseBody> joined_holes(@Query("list_size") int list_size,
                                    @Query("start_id") int start_id,
                                    @Query("is_last_reply") Boolean is_last_reply);

    @GET
    Call<ResponseBody> detailholes2(@Url String url);

    @HTTP(method = "GET", path = "forests/detail/{hole_id}", hasBody = false)
    Call<ResponseBody> detailforest(@Path("hole_id") String hole_id);

    @HTTP(method = "GET", path = "forests/{hole_id}/holes?", hasBody = true)
    Call<ResponseBody> detailholes(@Path("hole_id") String hole_id,
                                   @Query("list_size") int list_size,
                                   @Query("start_id") int start_id,
                                   @Query("is_last_active") Boolean is_last_active);

    @POST
    Call<ResponseBody> changeSecurityMode(@Url String url);

    @POST
    Call<ResponseBody> follow(@Url String url);

    @DELETE
    Call<ResponseBody> deletefollow(@Url String url);

    @POST
    Call<ResponseBody> thumbups(@Url String url);

    @DELETE
    Call<ResponseBody> deletethumbups(@Url String url);

    @GET
    Call<ResponseBody> replies(@Url String url);

    @POST
    Call<ResponseBody> replies_add(@Url String url);

    // @POST("replies")
    //Call<ResponseBody> replies_add(@Body HashMap map);

    @POST
    Call<ResponseBody> holes(@Url String url);

    @GET("holes?")
    Call<ResponseBody> homepageholes(@Query("is_descend") Boolean is_descend,
                                     @Query("is_last_reply") Boolean is_last_reply,
                                     @Query("start_id") int start_id,
                                     @Query("list_size") int list_size);

    @GET("search/hole?")
    Call<ResponseBody> search_hole(@Query("keyword") String keyword,
                                   @Query("start_id") int start_id,
                                   @Query("list_size") int list_size);

    @GET
    Call<ResponseBody> holes2(@Url String url);

    @HTTP(method = "DELETE", path = "holes/{hole_id}", hasBody = false)
    Call<ResponseBody> delete_hole(@Path("hole_id") String hole_id);

    @DELETE
    Call<ResponseBody> delete_hole_2(@Url String url);

    // @POST("reports")
    // Call<ResponseBody> report(@Body HashMap map);
    //Call<ResponseBody> report(@Path("hole_id") String hole_id);
    @POST("reports")
    Call<ResponseBody> report(@Body HashMap map);

    //
    @POST
    Call<ResponseBody> report_2(@Url String url);

    // @POST("auth/verifyCodeMatch?email=U202011988.edu.cn&verify_code=1234")
    // Call<ResponseBody> verifyCodeMatch();

    @GET("myself/mydata")
    Call<ResponseBody> myData();

    @GET("myself/myholes?")
    Call<ResponseBody> myHoles(@Query("start_id") int start_id, @Query("list_size") int list_size);

    @GET("myself/myfollow?")
    Call<ResponseBody> myFollow(@Query("start_id") int start_id, @Query("list_size") int list_size);

    @GET("myself/myreplies?")
    Call<ResponseBody> myReplies(@Query("start_id") int start_id,
                                 @Query("list_size") int list_size);

    @GET("myself/latestShareImage")
    Call<ResponseBody> getShareImage();

    @GET("auth")
    Call<ResponseBody> isUnderSecurity();

    @POST("forests/apply")
    Call<ResponseBody> applyForest(@Body RequestBody body);

    @GET("replies/hot?")
    Call<ResponseBody> hotReply(@Query("hole_id") String hole_id,
                                @Query("start_id") int start_id,
                                @Query("list_size") int list_size);

    @GET("replies/owner?")
    Call<ResponseBody> owner(@Query("hole_id") String hole_id,
                             @Query("start_id") int start_id,
                             @Query("list_size") int list_size,
                             @Query("is_descend") String is_descend);

    @GET("auth/checkActivation")
    Call<ResponseBody> checkActivation();

    @GET
    Call<ResponseBody> blockwords(@Url String url);

    @POST
    Call<ResponseBody> addblockword(@Url String url);

    @POST
    Call<ResponseBody> evaluate(@Url String url);

    @POST
    Call<ResponseBody> advice(@Url String url);

    @DELETE
    Call<ResponseBody> deleteblockword(@Url String url);

    @GET("myself/latestShareImage")
    Call<ResponseBody> checkupdate();

    @GET("version")
    Call<ResponseBody> checkupdate2();
}


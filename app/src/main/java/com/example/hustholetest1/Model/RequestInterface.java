package com.example.hustholetest1.Model;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RequestInterface {//接口


    @POST("mobileLogin")
    Call<ResponseBody> mobileLogin(@Body HashMap map);
    @POST("register")
    Call<ResponseBody> register(@Body HashMap map);
    @GET("types?list_size=10&start_id=0")
    Call<ResponseBody> getCall();

    @GET("hot?list_size=10&start_id=0")
    Call<ResponseBody> getCall2();
    @GET
    Call<ResponseBody> testUrl(@Url String url);
    @POST
    Call<ResponseBody> join(@Url String url);
    @DELETE
    Call<ResponseBody> delete(@Url String url);
    @GET("joined?list_size=30&start_id=0")
    Call<ResponseBody> joined();
    @GET("holes?list_size=10&start_id=0&is_last_reply=true")
    Call<ResponseBody> holes();
    @GET
    Call<ResponseBody> detailholes(@Url String url);

    @POST
    Call<ResponseBody> follow(@Url String url);
    @DELETE
    Call<ResponseBody>deletefollow(@Url String url);

    @POST
    Call<ResponseBody> thumbups(@Url String url);
    @DELETE
    Call<ResponseBody> deletethumbups(@Url String url);
    @GET
    Call<ResponseBody> replies(@Url String url);
    @POST
    Call<ResponseBody> replies_add(@Url String url);

    @POST
    Call<ResponseBody> holes(@Url String url);

    @GET
    Call<ResponseBody> holes2(@Url String url);

    @GET
    Call<ResponseBody> search(@Url String url);
    @DELETE
    Call<ResponseBody> delete_hole(@Url String url);
    @DELETE
    Call<ResponseBody> delete_hole_2(@Url String url);
    @POST
    Call<ResponseBody> report(@Url String url);
    @POST
    Call<ResponseBody> report_2(@Url String url);


}


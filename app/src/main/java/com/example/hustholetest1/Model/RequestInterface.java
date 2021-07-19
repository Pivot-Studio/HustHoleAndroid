package com.example.hustholetest1.Model;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RequestInterface {//接口


    @POST("mobileLogin")
    //Call<ResponseBody> mobileLogin(@Body String email, @Body String password);
    Call<ResponseBody> mobileLogin(@Body HashMap map);
    //Call<ResponseBody> mobileLogin(@Field("email") String email, @Field("password") String password);

    @POST("register")
    Call<ResponseBody> register(@Body HashMap map);

    @GET("types?list_size=10&start_id=0")
    Call<ResponseBody> getCall();
    @GET("hot?list_size=10&start_id=0")
    Call<ResponseBody> getCall2();

    @GET
    Call<ResponseBody> testUrl(@Url String url);
}


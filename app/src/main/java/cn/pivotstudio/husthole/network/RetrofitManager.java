package cn.pivotstudio.husthole.network;


import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    public static String API="http://hustholetest.pivotstudio.cn/api/";
    private static Retrofit retrofit;
    private static RequestInterface request;
    public static Retrofit getRetrofit(){
        return retrofit;
    }
    public static RequestInterface getRequest(){
        return request;
    }
    public static void RetrofitBuilder(String URL){
        //Retrofit retrofit1;
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(URL)
                    .client(OkHttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            //retrofit=retrofit1;
            request = retrofit.create(RequestInterface.class);
        //return retrofit1;
    }
}

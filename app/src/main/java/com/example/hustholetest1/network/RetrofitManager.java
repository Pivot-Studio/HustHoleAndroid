package com.example.hustholetest1.network;


import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    static Retrofit retrofit;
    static Retrofit noTokenRetrofit;
    public static Retrofit getRetrofit(){
        return retrofit;
    }
    public static Retrofit getNOTokenRetrofit(){
        return noTokenRetrofit;
    }
    public static void RetrofitBuilder(String URL, Context context,Boolean ifNeedIntercepter){
        Retrofit retrofit1,retrofit2;
        if(ifNeedIntercepter) {
            retrofit1 = new retrofit2.Retrofit.Builder()
                    .baseUrl(URL)
                    .client(OkHttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofit=retrofit1;
        }else{
            retrofit2 = new retrofit2.Retrofit.Builder()
                   .baseUrl(URL)
                   .client(OkHttpUtil.getOkHttpClient2())
                   .addConverterFactory(GsonConverterFactory.create())
                   .build();
            noTokenRetrofit=retrofit2;
       }

        //return retrofit1;
    }
}

package com.example.hustholetest1.network;


import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        return retrofit;
    }
    public static void RetrofitBuilder(String URL){
        Retrofit retrofit1;
            retrofit1 = new retrofit2.Retrofit.Builder()
                    .baseUrl(URL)
                    .client(OkHttpUtil.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofit=retrofit1;


        //return retrofit1;
    }
}

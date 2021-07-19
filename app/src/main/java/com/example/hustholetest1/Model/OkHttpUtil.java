package com.example.hustholetest1.Model;
import com.example.hustholetest1.Model.TokenInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtil {

    public static OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(3000, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(3000, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(3000, TimeUnit.SECONDS);
        //使用拦截器
        System.out.println("总:1");
        httpClientBuilder.addInterceptor(new TokenInterceptor());
        System.out.println("总:2" );
        return httpClientBuilder.build();
    }
}

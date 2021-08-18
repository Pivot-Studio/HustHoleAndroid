package com.example.hustholetest1.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtil {

    public static OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(1000, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(1000, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(1000, TimeUnit.SECONDS);
        //使用拦截器
        httpClientBuilder.addInterceptor(new TokenInterceptor());
        return httpClientBuilder.build();
    }
    public static OkHttpClient getOkHttpClient2() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(1000, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(1000, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(1000, TimeUnit.SECONDS);
        //使用拦截器
        httpClientBuilder.addInterceptor(new TemporaryTokenInterceptor());
        return httpClientBuilder.build();
    }
}

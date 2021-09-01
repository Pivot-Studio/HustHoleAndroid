package com.example.hustholetest1.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class TemporaryTokenInterceptor implements Interceptor{
    private String token; //用于添加的请求头
    private static Context context;
    public static void getContext(Context contexts){
        context=contexts;
    }
    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        //从SharePreferences中获取token
        okhttp3.Response response;
        SharedPreferences editor = context.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        token = editor.getString("token", "");
        System.out.println("Bearer " + token);
        if(token.equals("")) {

            Request request = chain.request()
                    .newBuilder()
                    .build();
            response = chain.proceed(request);
        }else{
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
             response = chain.proceed(request);
        }

        return response;
    }
}

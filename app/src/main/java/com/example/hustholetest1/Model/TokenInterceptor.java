package com.example.hustholetest1.Model;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * token拦截器
 */

public class TokenInterceptor implements Interceptor {
    private String token; //用于添加的请求头
    private static Context context;
    public static void getContext(Context contexts){
    context=contexts;
     }
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        //从SharePreferences中获取token
        //SharedPreferences sharedPreferences =().getSharedPreferences("myConfig", Context.MODE_PRIVATE);
        SharedPreferences editor = context.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        token = editor.getString("token", "");
        System.out.println("Bearer "+token);
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer "+token)
                .build();
        okhttp3.Response response = chain.proceed(request);

        //Log.e("返回数据：",response.body().string());
        return response;
    }


}

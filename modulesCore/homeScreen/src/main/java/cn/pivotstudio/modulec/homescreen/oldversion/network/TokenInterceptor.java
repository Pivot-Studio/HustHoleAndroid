package cn.pivotstudio.modulec.homescreen.oldversion.network;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import cn.pivotstudio.moduleb.database.MMKVUtil;
import okhttp3.Interceptor;
import okhttp3.Request;

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
        MMKVUtil mmkvUtil= MMKVUtil.getMMKVUtils(context);
        String token =mmkvUtil.getString("USER_TOKEN");
//        SharedPreferences editor = context.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
//        token = editor.getString("token", "");
        System.out.println("Bearer " + token);
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            okhttp3.Response response = chain.proceed(request);
        return response;
    }


}

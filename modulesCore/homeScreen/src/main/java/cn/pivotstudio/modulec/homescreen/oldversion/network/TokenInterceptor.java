package cn.pivotstudio.modulec.homescreen.oldversion.network;

import android.content.Context;
import cn.pivotstudio.moduleb.database.MMKVUtil;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * token拦截器
 */

public class TokenInterceptor implements Interceptor {
    private static Context context;
    private String token; //用于添加的请求头

    public static void getContext(Context contexts) {
        context = contexts;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        //从SharePreferences中获取token
        MMKVUtil mmkvUtil = MMKVUtil.getMMKV(context);
        String token = mmkvUtil.getString("USER_TOKEN");
        //        SharedPreferences editor = context.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        //        token = editor.getString("token", "");
        System.out.println("Bearer " + token);
        Request request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
        okhttp3.Response response = chain.proceed(request);
        return response;
    }
}

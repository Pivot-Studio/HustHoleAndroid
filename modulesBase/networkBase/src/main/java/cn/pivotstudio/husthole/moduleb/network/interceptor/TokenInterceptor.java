package cn.pivotstudio.husthole.moduleb.network.interceptor;

import android.content.Context;
import cn.pivotstudio.moduleb.database.MMKVUtil;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * token拦截器
 */
public class TokenInterceptor implements Interceptor {
    MMKVUtil mmkvUtil;

    public TokenInterceptor(Context context) {
        mmkvUtil = MMKVUtil.getMMKVUtils(context);
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        //用于添加的请求头

        String token = mmkvUtil.getString("USER_TOKEN");
        Request request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
        return chain.proceed(request);
    }
}

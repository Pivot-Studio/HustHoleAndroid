package cn.pivotstudio.moduleb.rebase.network.interceptor;

import android.content.Context;
import java.io.IOException;
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * token拦截器
 */
public class TokenInterceptor implements Interceptor {
    MMKVUtil mmkvUtil;

    public TokenInterceptor(Context context) {
        mmkvUtil = MMKVUtil.getMMKV(context);
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        //用于添加的请求头

        String token = "";
        Request request =
                chain.request().newBuilder().build();
        return chain.proceed(request);
    }
}

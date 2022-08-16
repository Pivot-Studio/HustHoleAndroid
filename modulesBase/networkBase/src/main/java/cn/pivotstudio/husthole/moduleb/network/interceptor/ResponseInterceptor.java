package cn.pivotstudio.husthole.moduleb.network.interceptor;

import cn.pivotstudio.husthole.moduleb.network.util.KLog;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author
 * @version:1.0
 * @classname: RequestInterceptor
 * @description: 相应拦截器
 * @date :2022/4/26 14:36
 */
public class ResponseInterceptor implements Interceptor {

    private static final String TAG = "ResponseInterceptor";

    /**
     * 拦截
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        long requestTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        KLog.i(TAG, "requestSpendTime=" + (System.currentTimeMillis() - requestTime) + "ms");
        return response;
    }
}

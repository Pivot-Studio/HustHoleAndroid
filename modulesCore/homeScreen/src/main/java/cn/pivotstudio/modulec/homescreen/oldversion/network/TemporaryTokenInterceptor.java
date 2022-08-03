package cn.pivotstudio.modulec.homescreen.oldversion.network;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;

public class TemporaryTokenInterceptor implements Interceptor {
    private static Context context;
    private String token; //用于添加的请求头

    public static void getContext(Context contexts) {
        context = contexts;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        okhttp3.Response response;

        Request request = chain.request().newBuilder().build();
        response = chain.proceed(request);

        return response;
    }
}

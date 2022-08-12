package cn.pivotstudio.modulec.homescreen.oldversion.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    public static String API = "https://husthole.pivotstudio.cn/api/";
    private static Retrofit retrofit;
    private static RequestInterface request;

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static RequestInterface getRequest() {
        return request;
    }

    public static void RetrofitBuilder(String URL) {
        retrofit = new Retrofit.Builder().baseUrl(URL)
            .client(OkHttpUtil.getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        request = retrofit.create(RequestInterface.class);
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}

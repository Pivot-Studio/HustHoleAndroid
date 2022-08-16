package cn.pivotstudio.husthole.moduleb.network.interceptor;

import cn.pivotstudio.husthole.moduleb.network.INetworkRequiredInfo;
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author
 * @version:1.0
 * @classname: RequestInterceptor
 * @description: 请求拦截器
 * @date :2022/4/26 14:36
 */
public class RequestInterceptor implements Interceptor {
    /**
     * 网络请求信息
     */
    private final INetworkRequiredInfo iNetworkRequiredInfo;

    public RequestInterceptor(INetworkRequiredInfo iNetworkRequiredInfo) {
        this.iNetworkRequiredInfo = iNetworkRequiredInfo;
    }

    /**
     * 拦截
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        String nowDateTime = DateUtil.getDateTime();
        //构建器
        Request.Builder builder = chain.request().newBuilder();
        //添加使用环境
        builder.addHeader("os", "android");
        //添加版本号
        //builder.addHeader("appVersionCode",this.iNetworkRequiredInfo.getAppVersionCode());
        //添加版本名
        // builder.addHeader("appVersionName",this.iNetworkRequiredInfo.getAppVersionName());
        //添加日期时间
        builder.addHeader("datetime", nowDateTime);
        //返回
        return chain.proceed(builder.build());
    }
}

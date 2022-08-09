package cn.pivotstudio.modulec.loginandregister.network;

import cn.pivotstudio.modulec.loginandregister.model.LoginResponse;
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @classname:network
 * @description:登录注册界面的网络请求接口
 * @date:2022/5/2 16:58
 * @version:1.0
 * @author:
 */
public interface LRRequestInterface {
    @POST("auth/mobileLogin")
    Observable<LoginResponse> mobileLogin(@Body HashMap<String, String> map);

    @POST
    Observable<MsgResponse> resetPassword(@Url String url);

    @POST
    Observable<MsgResponse> verifyCode(@Url String url);

    @POST("auth/sendVerifyCode")
    Observable<MsgResponse> verifyCodeMatch(@Body Map<String, String> map);

    @POST("auth/mobileRegister")
    Observable<MsgResponse> register(@Body HashMap<String, String> map);
}

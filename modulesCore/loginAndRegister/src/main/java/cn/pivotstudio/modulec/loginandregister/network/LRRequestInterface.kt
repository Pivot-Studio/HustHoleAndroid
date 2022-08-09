package cn.pivotstudio.modulec.loginandregister.network

import retrofit2.http.POST
import cn.pivotstudio.modulec.loginandregister.model.LoginResponse
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Url
import java.util.HashMap

/**
 * @classname:network
 * @description:登录注册界面的网络请求接口
 * @date:2022/5/2 16:58
 * @version:1.0
 * @author:
 */
interface LRRequestInterface {
    @POST("auth/mobileLogin")
    fun mobileLogin(@Body map: HashMap<String, String>): Observable<LoginResponse>

    @POST
    fun resetPassword(@Url url: String): Observable<MsgResponse>

    @POST
    fun verifyCode(@Url url: String): Observable<MsgResponse>

    @POST("auth/sendVerifyCode")
    fun verifyCodeMatch(@Body map: Map<String, String>): Observable<MsgResponse>

    @POST("auth/mobileRegister")
    fun register(@Body map: Map<String, String>): Observable<MsgResponse>
}
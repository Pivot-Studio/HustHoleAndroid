package cn.pivotstudio.modulec.homescreen.network

import retrofit2.http.POST
import cn.pivotstudio.modulec.homescreen.model.LoginResponse
import cn.pivotstudio.modulec.homescreen.model.MsgResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @classname:network
 * @description:登录注册界面的网络请求接口
 * @date:2022/5/2 16:58
 * @version:1.0
 * @author:
 */
interface LRRequestInterface {
    @POST("auth/mobileLogin")
    fun mobileLogin(@Body map: Map<String, String>): Observable<cn.pivotstudio.modulec.homescreen.model.LoginResponse>

    @POST
    fun resetPassword(@Url url: String): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @Deprecated("弃用")
    @POST
    fun verifyCode(@Url url: String): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @POST("auth/verifyCodeMatch")
    fun codeVerify(
        @Query("email") studentCode: String,
        @Query("verify_code") verifyCode: String
    ): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @POST("auth/sendVerifyCode")
    fun sendVerifyCode(
        @Query("email") studentCode: String,
        @Query("isResetPassword") isResetPassword: Boolean
    ): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @Deprecated("弃用")
    @POST("auth/mobileRegister")
    fun register(@Body map: Map<String, String>): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @POST("auth/mobileRegister")
    fun register(
        @Query("email") studentCode: String,
        @Query("password") password: String
    ): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>

    @POST("auth/mobileChangePassword")
    fun setNewPassword(
        @Query("email") studentCode: String,
        @Query("verify_code") verifyCode: String,
        @Query("new_password") newPassword: String
    ): Observable<cn.pivotstudio.modulec.homescreen.model.MsgResponse>
}
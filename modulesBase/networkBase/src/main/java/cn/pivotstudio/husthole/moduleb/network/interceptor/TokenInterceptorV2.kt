package cn.pivotstudio.husthole.moduleb.network.interceptor

import android.content.Context
import cn.pivotstudio.moduleb.database.MMKVUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class TokenInterceptorV2(private val context: Context) : Interceptor {
    var mmkvUtil: MMKVUtil = MMKVUtil.getMMKV(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //用于添加的请求头
        val token = mmkvUtil.getString("USER_TOKEN_V2")
        val request: Request = chain.request().newBuilder().addHeader(
            "Authorization",
            token
        ).build()
        return chain.proceed(request)
    }
}
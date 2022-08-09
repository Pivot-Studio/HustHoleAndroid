package cn.pivotstudio.modulec.loginandregister.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse
import cn.pivotstudio.modulec.loginandregister.network.LoginAndRegisterNetworkApi
import java.util.HashMap

/**
 * @classname:ForgetPasswordRepository
 * @description:
 * @date:2022/5/1 22:23
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class ForgetPasswordRepository {
    @JvmField
    val failed = MutableLiveData<String>()

    @JvmField
    var mForgetPassword = MutableLiveData<MsgResponse>()
    fun sendVerifyCode(id: String) {
        val map = HashMap<String, String>()
        map["email"] = id
        map["isResetPassword"] = "true"
        LoginAndRegisterNetworkApi.retrofitService
            .verifyCodeMatch(map)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msgResponse: MsgResponse) {
                    mForgetPassword.setValue(msgResponse)
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }
}
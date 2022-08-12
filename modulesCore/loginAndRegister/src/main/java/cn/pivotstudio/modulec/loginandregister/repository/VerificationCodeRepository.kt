package cn.pivotstudio.modulec.loginandregister.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse
import cn.pivotstudio.modulec.loginandregister.network.LoginAndRegisterNetworkApi
import java.util.HashMap

/**
 * @classname:VerificationCodeRepository
 * @description:
 * @date:2022/5/1 23:22
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class VerificationCodeRepository {
    @JvmField
    val failed = MutableLiveData<String>()

    @JvmField
    var mVerificationCode = MutableLiveData<MsgResponse>()
    fun verifyEmailForNetwork(id: String, verify_code: String) {
        NetworkApi.createService(LRRequestInterface::class.java, 2)
            .verifyCode(
                Constant.BASE_URL
                        + "auth/verifyCodeMatch?email="
                        + id
                        + "&verify_code="
                        + verify_code
            )
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msgResponse: MsgResponse) {
                    mVerificationCode.value = msgResponse
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }

//    fun sendVerifyCode(id: String) {
//        val map = HashMap<String, String>()
//        map["email"] = id
//        map["isResetPassword"] = "true"
//        LoginAndRegisterNetworkApi.retrofitService
//            .sendVerifyCode(map)
//            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
//                override fun onSuccess(msgResponse: MsgResponse) {
//
//                }
//
//                override fun onFailure(e: Throwable) {
//                    failed.postValue((e as ResponseThrowable).message)
//                }
//            }))
//    }
}
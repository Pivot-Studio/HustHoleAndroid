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
 * @classname:SetPasswordRepository
 * @description:
 * @date:2022/5/1 21:33
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class SetPasswordRepository {
    @JvmField
    val failed = MutableLiveData<String>()

    @JvmField
    var mSetPassword = MutableLiveData<MsgResponse>()

    fun createAccountForNetwork(id: String, password: String) {
        val map = HashMap<String, String>()
        map["email"] = id
        map["password"] = password
        LoginAndRegisterNetworkApi.retrofitService
            .register(map)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(setPasswordResponse: MsgResponse) {
                    mSetPassword.value = setPasswordResponse
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }
}
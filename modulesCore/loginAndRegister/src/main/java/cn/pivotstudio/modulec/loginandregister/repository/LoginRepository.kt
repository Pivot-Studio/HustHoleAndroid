package cn.pivotstudio.modulec.loginandregister.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.modulec.loginandregister.model.LoginResponse
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.modulec.loginandregister.network.LoginAndRegisterNetworkApi
import java.util.HashMap

/**
 * @classname:LoginRepository
 * @description:
 * @date:2022/4/29 14:20
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class LoginRepository  //  private final MMKVUtil mvUtil;
// @Inject
{
    @JvmField
    val failed = MutableLiveData<String>()

    @JvmField
    var login = MutableLiveData<LoginResponse>()
    fun getLoginForNetwork(id: String, password: String) {
        val map = HashMap<String, String>()
        map["email"] = id
        map["password"] = password
        LoginAndRegisterNetworkApi.retrofitService
            .mobileLogin(map)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<LoginResponse>() {
                override fun onSuccess(loginResponse: LoginResponse) {
                    login.postValue(loginResponse)
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }
}
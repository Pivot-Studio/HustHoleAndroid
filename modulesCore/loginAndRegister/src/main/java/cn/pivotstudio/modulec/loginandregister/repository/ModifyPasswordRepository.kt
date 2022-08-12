package cn.pivotstudio.modulec.loginandregister.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse

/**
 * @classname:ModifyPasswordRepository
 * @description:
 * @date:2022/5/2 1:07
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class ModifyPasswordRepository {
    @JvmField
    val failed = MutableLiveData<String>()

    @JvmField
    var mModifyPassword = MutableLiveData<MsgResponse>()
    fun modifyPasswordForNetwork(id: String, verify_code: String, password: String) {
        NetworkApi.createService(LRRequestInterface::class.java, 2)
            .resetPassword(
                Constant.BASE_URL
                        + "auth/mobileChangePassword?email="
                        + id
                        + "&verify_code="
                        + verify_code
                        + "&new_password="
                        + password
            )
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msgResponse: MsgResponse) {
                    mModifyPassword.value = msgResponse
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }
}
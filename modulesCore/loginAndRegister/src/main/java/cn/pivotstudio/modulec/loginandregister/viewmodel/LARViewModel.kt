package cn.pivotstudio.modulec.loginandregister.viewmodel

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.User
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.loginandregister.R
import cn.pivotstudio.modulec.loginandregister.model.LoginResponse
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse
import cn.pivotstudio.modulec.loginandregister.network.LoginAndRegisterNetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.collections.HashMap

enum class LARState {
    LOGIN, REGISTERED,
    VERIFIED, VERIFYING, COUNT_DOWN_START, COUNT_DOWN_END
}

class LARViewModel : ViewModel() {

    companion object {
        const val TAG = "LARViewModel"
    }

    private var _larState = MutableLiveData<LARState?>()
    private var _tip = MutableLiveData<String?>()
    private var _loginToken = MutableLiveData<String?>()
    private var _verifyCode = MutableLiveData<String?>()

    private var _countDownTime = MutableLiveData<Long?>()
    private var _loginTokenV2 = MutableStateFlow<String>("")

    private var countDownTimer = object : CountDownTimer(90000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _countDownTime.value = millisUntilFinished / 1000
        }

        override fun onFinish() {
            _larState.value = LARState.COUNT_DOWN_END
        }
    }

    fun clear() {
        _larState.value = null
        _tip.value = null
        _loginToken.value = null
        _countDownTime.value = null
        studentCode.value = null
        _verifyCode.value = null
    }

    val larState: LiveData<LARState?> = _larState
    val tip: LiveData<String?> = _tip
    val loginToken: LiveData<String?> = _loginToken
    val studentCode = MutableLiveData<String?>()
    val countDownTime: LiveData<Long?> = _countDownTime
    val verifyCode: LiveData<String?> = _verifyCode
    val loginTokenV2: StateFlow<String> = _loginTokenV2

    fun login(id: String, password: String) {
        val map = HashMap<String, String>()
        map["email"] = id
        map["password"] = password
        LoginAndRegisterNetworkApi.retrofitService
            .mobileLogin(map)
            .compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<LoginResponse>() {
                override fun onSuccess(loginResponse: LoginResponse) {
                    _loginToken.value = loginResponse.token
                    _tip.value = loginResponse.msg
                }

                override fun onFailure(e: Throwable) {
                    _tip.value = (e as ResponseThrowable).message
                }
            })
        viewModelScope.launch {
            flow {
                val result = HustHoleApi.retrofitService.signIn(
                    User(email = id + Constant.EMAIL_SUFFIX, password = password)
                )
                emit(result)
            }.flowOn(Dispatchers.IO).catch { e ->
                e.printStackTrace()
                Log.e(TAG, "login: v2登录失败")
            }.collect {
                _loginTokenV2.emit(it.token)
                Log.e(TAG, "login: v2成功")
            }
        }
    }

    fun register(password: String) {
        studentCode.value?.let {
            LoginAndRegisterNetworkApi.retrofitService
                .register(it, password)
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(t: MsgResponse) {
                        _tip.value = t.msg
                        _larState.value = LARState.REGISTERED
                    }

                    override fun onFailure(e: Throwable?) {
                        _tip.value = (e as ResponseThrowable).message
                    }

                })
        }
    }

    fun setNewPassword(newPassword: String) {
        studentCode.value?.also { studentCode ->
            verifyCode.value?.let { verifyCode ->
                LoginAndRegisterNetworkApi.retrofitService
                    .setNewPassword(studentCode, verifyCode, newPassword)
                    .compose(NetworkApi.applySchedulers())
                    .subscribe(object : BaseObserver<MsgResponse>() {
                        override fun onSuccess(t: MsgResponse) {
                            _larState.value = LARState.REGISTERED
                            _tip.value = t.msg
                        }

                        override fun onFailure(e: Throwable?) {
                            _tip.value = (e as ResponseThrowable).message
                        }

                    })
            }
        }

    }


    fun verify(code: String) {
        _verifyCode.value = code
        studentCode.value?.let {
            LoginAndRegisterNetworkApi.retrofitService
                .codeVerify(it, code)
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(t: MsgResponse) {
                        _larState.value = LARState.VERIFIED
                        _tip.value = t.msg
                    }

                    override fun onFailure(e: Throwable?) {
                        _tip.value = (e as ResponseThrowable).message
                    }

                })
        }
    }

    var isResetPassword = false

    fun sendVerifyCodeToStudentEmail() {
        countDown()
        studentCode.value?.let {
            LoginAndRegisterNetworkApi.retrofitService
                .sendVerifyCode(it, isResetPassword)
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(t: MsgResponse) {
                        _tip.value = t.msg
                    }

                    override fun onFailure(e: Throwable?) {
                        _tip.value = (e as ResponseThrowable).message
                    }

                })
        }
    }

    fun doneShowingTip() {
        _tip.value = null
    }

    fun doneStateChanged() {
        _larState.value = null
    }

    fun doneTokenChange() {
        _loginToken.value = null
    }


    private fun countDown() {
        _larState.value = LARState.COUNT_DOWN_START
        countDownTimer.start()
    }
}
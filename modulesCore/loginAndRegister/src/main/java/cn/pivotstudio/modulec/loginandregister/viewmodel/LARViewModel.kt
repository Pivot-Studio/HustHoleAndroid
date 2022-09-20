package cn.pivotstudio.modulec.loginandregister.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.util.data.CheckStudentCodeUtil
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse
import cn.pivotstudio.modulec.loginandregister.network.LoginAndRegisterNetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    private var _showStudentCodeWarning = MutableLiveData<Boolean?>()
    private var _showPasswordWarning = MutableLiveData<Boolean?>()

    private var _countDownTime = MutableLiveData<Long?>()
    private var _loginTokenV2 = MutableStateFlow("")

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
        _showStudentCodeWarning.value = null
        _showPasswordWarning.value = null
    }

    val larState: LiveData<LARState?> = _larState
    val tip: LiveData<String?> = _tip
    val loginToken: LiveData<String?> = _loginToken
    val studentCode = MutableLiveData<String?>()
    val countDownTime: LiveData<Long?> = _countDownTime
    val loginTokenV2: StateFlow<String> = _loginTokenV2
    val showStudentCodeWarning: LiveData<Boolean?> = _showStudentCodeWarning
    val showPasswordWarning: LiveData<Boolean?> = _showPasswordWarning

    fun login(id: String, password: String) {
        if (!CheckStudentCodeUtil.isStudentCode(id)) {
            _showStudentCodeWarning.value = true
            return
        }
        _showStudentCodeWarning.value = false

        viewModelScope.launch {
            flow {
                val result = HustHoleApi.retrofitService.signIn(
                    RequestBody.User(email = id + Constant.EMAIL_SUFFIX, password = password)
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
        if (!CheckStudentCodeUtil.isLegalPassword(password)) {
            _showPasswordWarning.value = true
            return
        }
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
        if (!CheckStudentCodeUtil.isLegalPassword(newPassword)) {
            _showPasswordWarning.value = true
            return
        }
        studentCode.value?.also { studentCode ->
            _verifyCode.value?.let { verifyCode ->
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
        if (!CheckStudentCodeUtil.isStudentCode(studentCode.value)) {
            _showStudentCodeWarning.value = true
            return
        }
        _showStudentCodeWarning.value = false
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

    fun doneShowingWarning() {
        _showStudentCodeWarning.value = null
        _showPasswordWarning.value = null
    }


    private fun countDown() {
        _larState.value = LARState.COUNT_DOWN_START
        countDownTimer.start()
    }
}
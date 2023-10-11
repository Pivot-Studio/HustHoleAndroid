package cn.pivotstudio.modulec.homescreen.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.rebase.network.model.TokenResponse
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.util.data.CheckStudentCodeUtil
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.modulec.homescreen.repository.LARRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class LARState {
    LOGIN_LOADING, LOGIN_END, LOGIN_ERROR, // 登录状态

    REG_LOADING, REG_END, REG_ERROR,       // 注册状态

    VERIFY_LOADING, VERIFY_END, VERIFY_ERROR, // 验证状态

    VERIFIED, VERIFYING, COUNT_DOWN_START, COUNT_DOWN_END
}

class LARViewModel : ViewModel() {

    private val repo = LARRepo()

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
    val studentCode = MutableLiveData<String?>()
    val countDownTime: LiveData<Long?> = _countDownTime
    val loginTokenV2: StateFlow<String> = _loginTokenV2
    val showStudentCodeWarning: LiveData<Boolean?> = _showStudentCodeWarning
    val showPasswordWarning: LiveData<Boolean?> = _showPasswordWarning

    fun login(id: String, password: String) {
        val formattedId = id.uppercase()
        if (!CheckStudentCodeUtil.isStudentCode(id)) {
            _showStudentCodeWarning.value = true
            return
        }
        _showStudentCodeWarning.value = false

        viewModelScope.launch {
            repo.login(formattedId, password)
                .catch { e ->
                    e.printStackTrace()
                }.collect { apiResult ->
                    when (apiResult) {
                        is ApiResult.Success<*> -> {
                            _loginTokenV2.emit((apiResult.data as TokenResponse).token)
                            _larState.value = LARState.LOGIN_END
                            _tip.value = "登录成功"
                        }
                        is ApiResult.Error -> {
                            _larState.value = LARState.LOGIN_ERROR
                            _tip.value = apiResult.errorMessage
                        }
                        is ApiResult.Loading -> {
                            _larState.value = LARState.LOGIN_LOADING
                            _tip.value = "登录中..."
                        }
                    }
                }
        }
    }

    fun register(password: String) {
        if (!CheckStudentCodeUtil.isLegalPassword(password)) {
            _showPasswordWarning.value = true
            return
        }
        studentCode.value?.let {
            viewModelScope.launch {
                repo.register(
                    email = it + Constant.EMAIL_SUFFIX,
                    password = password,
                    isResetPassword = isResetPassword,
                    code = _verifyCode.value!!
                ).catch { e ->
                    e.printStackTrace()
                }.collect { apiResult ->
                    when (apiResult) {
                        is ApiResult.Success<*> -> {
                            _loginTokenV2.emit((apiResult.data as TokenResponse).token)
                            _tip.value = "验证中..."
                        }
                        is ApiResult.Error -> {
                            _tip.value = apiResult.errorMessage
                        }
                        is ApiResult.Loading -> {
                            _tip.value = "验证成功"
                        }
                    }
                }
            }

        }
    }

    fun setNewPassword(newPassword: String) {
        if (!CheckStudentCodeUtil.isLegalPassword(newPassword)) {
            _showPasswordWarning.value = true
            return
        }
        viewModelScope.launch {
            studentCode.value?.also { studentCode ->
                _verifyCode.value?.let { verifyCode ->
                    repo.setNewPassword(
                        verifyCode,
                        studentCode + Constant.EMAIL_SUFFIX,
                        newPassword,
                        true
                    ).catch { e ->
                        e.printStackTrace()
                    }.collect { apiResult ->
                        when (apiResult) {
                            is ApiResult.Success<*> -> {
                                _loginTokenV2.emit((apiResult.data as TokenResponse).token)
                            }
                            is ApiResult.Error -> {
                                _tip.value = apiResult.errorMessage
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

    }


    fun verify(code: String) {
        _verifyCode.value = code
    }

    var isResetPassword = false

    fun sendVerifyCodeToStudentEmail() {
        if (!CheckStudentCodeUtil.isStudentCode(studentCode.value)) {
            studentCode.value?.let {
                if (it.first().isLowerCase()) {
                    val formattedCode = it.uppercase()
                    studentCode.value = formattedCode
                }
            }
            _showStudentCodeWarning.value = true
            return
        }

        _showStudentCodeWarning.value = false
        countDown()
        studentCode.value?.let {
            viewModelScope.launch {
                repo.sendVerifyCodeToStudentEmail(
                    email = it + Constant.EMAIL_SUFFIX,
                    isResetPassword = isResetPassword
                ).collect { apiResult ->
                    when (apiResult) {
                        is ApiResult.Success<*> -> {
                            _larState.value = LARState.REG_END
                            _tip.value = "发送成功"
                        }
                        is ApiResult.Error -> {
                            _larState.value = LARState.REG_ERROR
                            _tip.value = apiResult.errorMessage
                        }
                        is ApiResult.Loading -> {
                            _larState.value = LARState.REG_LOADING
                        }
                    }
                }
            }
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
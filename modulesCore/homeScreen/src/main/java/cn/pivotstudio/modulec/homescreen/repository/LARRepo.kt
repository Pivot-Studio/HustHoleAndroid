package cn.pivotstudio.modulec.homescreen.repository

import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.moduleb.libbase.constant.Constant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

class LARRepo(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun register(
        email: String,
        isResetPassword: Boolean,
        password: String,
        code: String
    ): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.register(
            RequestBody.VerifyRequest(
                code = code,
                email = email,
                password = password,
                resetPassword = isResetPassword
            )
        )
        checkResponse(response, this)

    }.flowOn(dispatcher)

    suspend fun login(id: String, password: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = HustHoleApi.retrofitService.signIn(
            RequestBody.User(email = id + Constant.EMAIL_SUFFIX, password = password)
        )
        checkResponse(response, this)
    }.flowOn(dispatcher)

    suspend fun setNewPassword(
        verifyCode: String,
        email: String,
        newPassword: String,
        isResetPassword: Boolean
    ): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.register(
            RequestBody.VerifyRequest(
                code = verifyCode,
                email = email,
                password = newPassword,
                resetPassword = isResetPassword
            )
        )
        checkResponse(response, this)

    }.flowOn(dispatcher)

    suspend fun sendVerifyCodeToStudentEmail(email: String, isResetPassword: Boolean) = flow {
        emit(ApiResult.Loading())
        val response = HustHoleApi.retrofitService
            .sendVerifyCode(
                RequestBody.SendVerifyCode(
                    email = email,
                    resetPassword = isResetPassword
                )
            )
        checkResponse(response, this)
    }.flowOn(dispatcher)

    private suspend inline fun <T> checkResponse(
        response: Response<T>,
        flow: FlowCollector<ApiResult>
    ) {
        if (response.isSuccessful) {
            flow.emit(ApiResult.Success(data = response.body()))
        } else {
            val json = response.errorBody()?.string()
            val jsonObject = json?.let { JSONObject(it) }
            val returnCondition = jsonObject?.getString("errorMsg")
            flow.emit(
                ApiResult.Error(
                    code = response.code(),
                    errorMessage = returnCondition
                )
            )
            response.errorBody()?.close()
        }
    }
}
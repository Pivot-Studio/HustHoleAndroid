package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

/**
 * @classname HomeScreenRepository
 * @description:
 * @date 2022/5/3 0:50
 * @version:1.0
 * @author: lzt
 */
@SuppressLint("CheckResult")
class HomeScreenRepository {
    private val hustHoleApiService = HustHoleApi.retrofitService
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    var tip = MutableLiveData<String?>()

    fun getVersion(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getVersion()
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    private suspend inline fun <T> checkResponse(
        response: Response<T>?,
        flow: FlowCollector<ApiResult>
    ) {
        if (response?.isSuccessful == true) {
            flow.emit(ApiResult.Success(data = response.body()))
        } else {
            val json = response?.errorBody()?.string()
            val jsonObject = json?.let { JSONObject(it) }
            val returnCondition = jsonObject?.getString("errorMsg")
            val errorCode = jsonObject?.getString("errorCode")
            flow.emit(
                ApiResult.Error(
                    code = errorCode?.toInt() ?: response?.code() ?: 0,
                    errorMessage = returnCondition
                )
            )
            response?.errorBody()?.close()
        }
    }
}
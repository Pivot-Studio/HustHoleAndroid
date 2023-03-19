package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ProFile
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.model.Type
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

/**
 *@classname MineRepository
 * @description:
 * @date :2022/10/11 21:45
 * @version :1.0
 * @author small fish
 */
@SuppressLint("CheckResult")
class MineRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    var tip = MutableLiveData<String?>()

    fun getVersion(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getVersion()
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun getProfile(): Flow<ProFile> = flow {
        emit(
            hustHoleApiService.getProFile()
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun sendEvaluation(
        score: Int
    ): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.sendEvaluation(
                RequestBody.ScoreRequest(score)
            )
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun sendAdvice(
        adv: String,
        type: Type
    ): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .sendAdvice(RequestBody.FeedBackRequest(adv, type.value))
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }

    private fun refreshTimestamp(): String {
        return DateUtil.getDateTime()
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
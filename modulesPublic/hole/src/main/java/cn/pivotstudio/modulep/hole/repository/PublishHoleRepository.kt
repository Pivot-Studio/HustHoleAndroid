package cn.pivotstudio.modulep.hole.repository

import android.annotation.SuppressLint
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.HustHoleApiService
import cn.pivotstudio.moduleb.rebase.network.model.ForestBrief
import cn.pivotstudio.moduleb.rebase.network.model.RequestBody
import cn.pivotstudio.moduleb.rebase.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

/**
 * @classname:PublishHoleRepository
 * @description:
 * @date:2022/5/5 22:52
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class PublishHoleRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    companion object {
        private const val FOREST_SIZE = 40
    }

    fun publishAHole(forestId: String? = null, content: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.publishAHole(
            RequestBody.HoleRequest(
                forestId = forestId,
                content = content
            )
        )
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }


    fun loadAllForests(): Flow<List<ForestBrief>> = flow {
        emit(
            hustHoleApiService.getAllForests(
                descend = true,
                limit = FOREST_SIZE,
                offset = 0,
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun loadJoinedForestsV2(): Flow<List<ForestBrief>> {
        return flow {
            emit(
                hustHoleApiService.getJoinedForests(
                    descend = true,
                    limit = FOREST_SIZE,
                    offset = 0,
                    timestamp = DateUtil.getDateTime()
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    private suspend inline fun checkResponse(
        response: Response<Unit>,
        flow: FlowCollector<ApiResult>
    ) {
        if (response.isSuccessful) {
            flow.emit(ApiResult.Success(data = Unit))
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
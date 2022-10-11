package cn.pivotstudio.modulep.publishhole.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse
import cn.pivotstudio.modulep.publishhole.model.ForestListsResponse
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse
import cn.pivotstudio.modulep.publishhole.network.PHRequestInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

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
        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            val errorCode = response.code()
            response.errorBody()?.close()
            emit(ApiResult.Error(code = errorCode))
        }
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

}
package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.husthole.moduleb.network.model.*
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

enum class ForestDetailHolesLoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestDetailRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val forestId: String,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    private var _state = MutableLiveData<ForestDetailHolesLoadStatus>()
    var lastStartId = STARTING_ID

    val tip = MutableLiveData<String?>()
    var state = _state

    suspend fun loadHolesByForestId(id: String): Flow<List<HoleV2>> {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        return flow {
            emit(
                HustHoleApi.retrofitService.getHolesInForest(
                    forestId = id,
                    timestamp = lastTimeStamp,
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
            _state.value = ForestDetailHolesLoadStatus.ERROR
        }.onEach { lastTimeStamp = DateUtil.getDateTime() }
    }

    fun loadMoreHolesByForestId(id: String): Flow<List<HoleV2>> {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        return flow {
            emit(
                HustHoleApi.retrofitService
                    .getHolesInForest(
                        forestId = id,
                        offset = lastStartId,
                        timestamp = lastTimeStamp
                    )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
            _state.value = ForestDetailHolesLoadStatus.ERROR
        }
    }

    fun followTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = hustHoleApiService
                .followTheHole(RequestBody.HoleId(hole.holeId))

            checkResponse(response, this)
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    fun unFollowTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .unFollowTheHole(RequestBody.HoleId(hole.holeId))

        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)

        checkResponse(response, this)
    }

    fun joinTheForestV2(): Flow<Boolean> {
        return flow {
            val response =
                hustHoleApiService.joinTheForestBy(forestId = RequestBody.ForestId(forestId.toString()))
            if (response.isSuccessful) {
                emit(true)
            }
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    fun quitTheForestV2(): Flow<Boolean> = flow {
        val response = hustHoleApiService.quitTheForestBy(
            forestId = RequestBody.ForestId(forestId)
        )
        if (response.isSuccessful) {
            emit(false)
        }
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }


    companion object {
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
        const val TAG = "ForestDetailRepository"
    }

    fun giveALikeToTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val theHole = RequestBody.LikeRequest(holeId = hole.holeId)
            val response = if (hole.liked) {
                hustHoleApiService.unLike(theHole)
            } else {
                hustHoleApiService.giveALikeTo(theHole)
            }

            checkResponse(response, this)
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
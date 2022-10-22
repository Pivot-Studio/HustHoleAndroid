package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.husthole.moduleb.network.model.*
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus.LOADING
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response


enum class LoadStatus { LOADING, ERROR, DONE, LATERLOAD }

@SuppressLint("CheckResult")
class ForestRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {

    companion object {
        const val TAG = "ForestRepositoryDebug"
        const val STARTING_ID = 0
        const val INITIAL_OFFSET = 0
        const val HOLES_LIST_SIZE = 20
        const val HEADS_LIST_SIZE = 30
    }

    private var lastOffset = INITIAL_OFFSET

    private var _holeState = MutableLiveData<LoadStatus?>()

    val tip = MutableLiveData<String?>()
    val holeState = _holeState

    fun loadForestHolesV2(): Flow<List<HoleV2>> {
        return flow {
            lastOffset = INITIAL_OFFSET
            refreshTimestamp()
            emit(
                hustHoleApiService.getAJoinedForestHoles(
                    limit = HOLES_LIST_SIZE,
                    timestamp = lastTimeStamp
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    fun loadMoreForestHolesV2(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getAJoinedForestHoles(
                limit = HOLES_LIST_SIZE,
                mode = NetworkConstant.SortMode.LATEST_REPLY,
                offset = lastOffset + HOLES_LIST_SIZE,
                timestamp = lastTimeStamp
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        lastOffset += HOLES_LIST_SIZE
        _holeState.value = LOADING
    }

    fun loadJoinedForestsV2(): Flow<List<ForestBrief>> {
        return flow {
            emit(
                hustHoleApiService.getJoinedForests(
                    descend = true,
                    limit = HEADS_LIST_SIZE,
                    offset = 0,
                    timestamp = lastTimeStamp
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }.onEach { refreshTimestamp() }
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


    fun loadTheHole(hole: HoleV2): Flow<HoleV2> {
        return flow {
            emit(hustHoleApiService.loadTheHole(hole.holeId))
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)

        checkResponse(response, this)
    }

    private fun refreshTimestamp() {
        lastTimeStamp = DateUtil.getDateTime()
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
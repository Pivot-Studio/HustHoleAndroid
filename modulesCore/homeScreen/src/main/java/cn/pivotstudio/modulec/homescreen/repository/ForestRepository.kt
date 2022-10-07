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
    private var _headerLoadState = MutableLiveData<LoadStatus>()
    private var _holes = MutableLiveData<List<ForestHole>>()

    val tip = MutableLiveData<String?>()
    val holeState = _holeState
    val headerLoadState = _headerLoadState

    fun loadForestHolesV2(): Flow<List<HoleV2>> {
        return flow {
            emit(
                hustHoleApiService.getAJoinedForestHoles(
                    limit = HOLES_LIST_SIZE,
                    timestamp = lastTimeStamp
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }.onEach {
            lastOffset = INITIAL_OFFSET
            refreshTimestamp()
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

            if (response.isSuccessful) {
                emit(ApiResult.Success(data = Unit))
            } else {
                emit(
                    ApiResult.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string()
                    )
                )
                response.errorBody()?.close()
            }
        }.flowOn(dispatcher)
    }

    fun followTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = hustHoleApiService
                .followTheHole(RequestBody.HoleId(hole.holeId))

            if (response.isSuccessful) {
                emit(ApiResult.Success(data = Unit))
            } else {
                emit(
                    ApiResult.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string()
                    )
                )
                response.errorBody()?.close()
            }
        }.flowOn(dispatcher)
    }

    fun unFollowTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .unFollowTheHole(RequestBody.HoleId(hole.holeId))

        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            emit(
                ApiResult.Error(
                    code = response.code(),
                    errorMessage = response.errorBody()?.string()
                )
            )
            response.errorBody()?.close()
        }
    }.flowOn(dispatcher)


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

        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            emit(
                ApiResult.Error(
                    code = response.code(),
                    errorMessage = response.errorBody()?.string()
                )
            )
            response.errorBody()?.close()
        }
    }

    fun refreshLoadLaterHole(
        holeId: Int,
        isThumb: Boolean,
        replied: Boolean,
        followed: Boolean,
        thumbNum: Int,
        replyNum: Int,
        followNum: Int
    ) {
        if (holeId < 0) return
        _holes.value?.toMutableList()?.let { newItems ->
            for ((i, newHole) in newItems.withIndex()) {
                if (holeId == newHole.holeId)
                    newItems[i] = newHole.copy().apply {
                        likeNum = thumbNum
                        liked = isThumb
                        this.replied = replied
                        this.followed = followed
                        this.replyNum = replyNum
                        this.followNum = followNum
                    }
            }
            _holes.value = newItems
        }
    }

    private fun refreshTimestamp() {
        lastTimeStamp = DateUtil.getDateTime()
    }
}
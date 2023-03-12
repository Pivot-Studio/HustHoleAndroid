package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

/**
 * @classname: HomePageHoleResponse
 * @description:
 * @date: 2022/5/3 22:55
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class HomePageHoleRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime(),
    private var lastOffset: Int = INITIAL_OFFSET
) {

    companion object {
        const val TAG = "HomePageHoleRepository"
        const val INITIAL_OFFSET = 0
        const val HOLES_LIST_SIZE = 20
    }

    var tip = MutableLiveData<String?>()

    fun loadHoles(sortMode: String): Flow<List<HoleV2>> = flow {
        refreshTimestamp()
        lastOffset = 0
        emit(
            hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                mode = sortMode,
                offset = 0,
                timestamp = lastTimeStamp
            )
        )
    }.flowOn(dispatcher)

    fun loadMoreHoles(sortMode: String): Flow<List<HoleV2>> = flow {
        lastOffset += HOLES_LIST_SIZE
        emit(
            hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                timestamp = lastTimeStamp,
                offset = lastOffset,
                mode = sortMode
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun searchHolesBy(queryKey: String) = flow {
        emit(
            hustHoleApiService.searchHolesByKey(
                key = queryKey,
            )
        )
    }.onEach {
        lastOffset = 0
    }.flowOn(dispatcher)

    fun loadMoreSearchHoles(queryKey: String) = flow {
        emit(
            hustHoleApiService.searchHolesByKey(
                key = queryKey,
                offset = lastOffset
            )
        )
    }.onEach {
        lastOffset += HOLES_LIST_SIZE
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun giveALikeToTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = if (hole.liked) {
                hustHoleApiService.unLike(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            } else {
                hustHoleApiService.giveALikeTo(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            }
            checkResponse(response, this)
        }.flowOn(dispatcher)
    }

    fun followTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = hustHoleApiService
                .followTheHole(RequestBody.HoleId(hole.holeId))

            checkResponse(response, this)
        }.flowOn(dispatcher).catch { it.printStackTrace() }
    }

    fun unFollowTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .unFollowTheHole(RequestBody.HoleId(hole.holeId))

        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }

    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)

        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }

    fun loadTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.loadTheHole(hole.holeId)
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun loadTheHole(holeId: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.loadTheHole(holeId)
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    private fun refreshTimestamp() {
        lastTimeStamp = DateUtil.getDateTime()
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
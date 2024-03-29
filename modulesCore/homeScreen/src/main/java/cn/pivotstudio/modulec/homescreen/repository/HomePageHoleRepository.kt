package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.HustHoleApiService
import cn.pivotstudio.moduleb.rebase.network.model.HoleV2
import cn.pivotstudio.moduleb.rebase.network.model.RequestBody
import cn.pivotstudio.moduleb.rebase.network.util.DateUtil
import cn.pivotstudio.moduleb.rebase.network.util.NetworkConstant
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
    var lastOffset: Int = INITIAL_OFFSET
) {

    companion object {
        const val TAG = "HomePageHoleRepository"
        const val INITIAL_OFFSET = 0
        const val HOLES_LIST_SIZE = 20
    }

    var tip = MutableLiveData<String?>()

    fun getMyFollow(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val resp = hustHoleApiService.getMyFollow(
            offset = 0,
            timestamp = lastTimeStamp,
            mode = sortMode
        )
        checkResponse(resp, this)
    }.flowOn(dispatcher).onEach {
        lastOffset = 0
    }.catch {
        it.printStackTrace()
    }

    fun loadMoreFollow(sortMode: String): Flow<ApiResult> = flow {
        lastOffset += NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE
        emit(ApiResult.Loading())
        val resp = hustHoleApiService.getMyFollow(
            lastOffset + HOLES_LIST_SIZE,
            lastTimeStamp,
            sortMode
        )
        checkResponse(resp, this)
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadRecHoles(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        refreshTimestamp()
        val response = hustHoleApiService.getRec(
            mode = sortMode,
            timestamp = lastTimeStamp
        )
        checkResponse(response, this)
    }.onEach{
        lastOffset = 0
    }.flowOn(dispatcher).catch {
        tip.value = it.message
        it.printStackTrace()
    }

    fun loadMoreRecHoles(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getRec(
            timestamp = lastTimeStamp,
            offset = lastOffset,
            mode = sortMode
        )
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadHoles(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        refreshTimestamp()
        val response = hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                mode = sortMode,
                offset = 0,
                timestamp = lastTimeStamp
        )
        checkResponse(response, this)
    }.onEach {
        lastOffset = 0
    }.flowOn(dispatcher).catch {
        tip.value = it.message
        it.printStackTrace()
    }

    fun loadMoreHoles(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getHoles(
            limit = HOLES_LIST_SIZE,
            timestamp = lastTimeStamp,
            offset = lastOffset + HOLES_LIST_SIZE,
            mode = sortMode
        )
        checkResponse(response, this)
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
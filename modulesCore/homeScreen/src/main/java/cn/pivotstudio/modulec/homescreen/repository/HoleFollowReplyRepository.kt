package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response


/**
 *@classname HoleFollowReplyRepository
 * @description:
 * @date :2022/10/12 21:32
 * @version :1.0
 * @author
 */
@SuppressLint("CheckResult")
class HoleFollowReplyRepository {
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private var holeOffset: Int = 0
    private var followOffset: Int = 0
    private var replyOffset: Int = 0

    var tip = MutableLiveData<String?>()

    fun getMyHole(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getMyHole(refreshTimestamp())
        )
    }.flowOn(dispatcher).onEach {
        holeOffset = 0
    }

    fun getMyFollow(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getMyFollow()
        )
    }.flowOn(dispatcher).onEach {
        followOffset = 0
    }

    fun getMyReply(): Flow<List<Reply>> = flow {
        emit(
            hustHoleApiService.getMyReply(refreshTimestamp())
        )
    }.flowOn(dispatcher).onEach {
        replyOffset = 0
    }

    fun loadMoreHole(): Flow<List<HoleV2>> = flow {
        holeOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyHole(
                refreshTimestamp(),
                holeOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadMoreFollow(): Flow<List<HoleV2>> = flow {
        followOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyFollow(
                followOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadMoreReply(): Flow<List<Reply>> = flow {
        replyOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyReply(
                refreshTimestamp(),
                replyOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)

        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }

    fun deleteReply(
        replyId: String
    ): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val resp = hustHoleApiService.deleteTheReply(replyId)
        checkResponse(resp, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }

    private fun refreshTimestamp(): String {
        return DateUtil.getDateTime()
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
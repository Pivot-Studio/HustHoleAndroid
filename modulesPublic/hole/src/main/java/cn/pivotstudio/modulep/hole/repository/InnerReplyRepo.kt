package cn.pivotstudio.modulep.hole.repository

import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.HustHoleApiService
import cn.pivotstudio.moduleb.rebase.network.model.Reply
import cn.pivotstudio.moduleb.rebase.network.model.RequestBody
import cn.pivotstudio.moduleb.rebase.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response

class InnerReplyRepo(
    private val holeId: String,
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastOffset: Int = 0,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    companion object {
        const val LIST_SIZE = 20
    }

    val tip = MutableLiveData<String?>()

    fun loadInnerReplies(replyId: String): Flow<List<Reply>> = flow {
        emit(
            hustHoleApiService.getInnerReplies(
                replyId = replyId,
                timestamp = lastTimeStamp
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        lastTimeStamp = DateUtil.getDateTime()
        lastOffset = 0
    }

    fun loadMoreReplies(replyId: String): Flow<List<Reply>> = flow {
        emit(
            hustHoleApiService.getInnerReplies(
                replyId = replyId,
                timestamp = lastTimeStamp,
                offset = lastOffset + LIST_SIZE,
                limit = LIST_SIZE
            )
        )
    }.onEach {
        lastOffset += LIST_SIZE
    }.flowOn(dispatcher)

    fun sendAComment(content: String, repliedId: String?): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response =
            hustHoleApiService.sendAComment(
                comment = RequestBody.Comment(
                    holeId = holeId,
                    repliedId = repliedId,
                    content = content
                )
            )
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun deleteTheReply(reply: Reply): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheReply(reply.replyId)
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun giveALikeTo(reply: Reply): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val theReply = RequestBody.LikeRequest(
                holeId = reply.holeId,
                replyId = reply.replyId
            )
            val response = if (reply.thumb) {
                hustHoleApiService.unLike(theReply)
            } else {
                hustHoleApiService.giveALikeTo(theReply)
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
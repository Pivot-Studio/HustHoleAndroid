package cn.pivotstudio.modulep.hole.repository

import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

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
    }.onCompletion {
        lastOffset += HoleRepository.LIST_SIZE
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
            hustHoleApiService.sendAComment(comment = RequestBody.Comment(
                holeId = holeId,
                repliedId = repliedId,
                content = content
            ))
        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            val errorCode = response.code()
            response.errorBody()?.close()
            emit(ApiResult.Error(code = errorCode))
        }
    }.flowOn(dispatcher)

    fun deleteTheReply(reply: Reply): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheReply(reply.replyId)

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
}
package cn.pivotstudio.modulep.hole.repository

import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class InnerReplyRepo(
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
}
package cn.pivotstudio.modulec.homescreen.repository

import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.HustHoleApiService
import cn.pivotstudio.moduleb.rebase.network.model.ReplyNotice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class NoticeRepo(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastOffset: Int = 0
) {

    companion object {
        const val TAG = "NoticeRepo"
        const val LIST_SIZE = 20
    }

    var state = MutableLiveData<LoadStatus?>()

    fun loadRepliesV2(): Flow<List<ReplyNotice>> = flow {
        emit(hustHoleApiService.getReplyNotice())
    }.flowOn(dispatcher).catch { e ->
        state.value = LoadStatus.ERROR
        e.printStackTrace()
    }.onEach {
        lastOffset = 0
        state.value = LoadStatus.LOADING
    }.onCompletion {
        lastOffset += LIST_SIZE
        state.value = LoadStatus.DONE
    }

    fun loadMoreV2(): Flow<List<ReplyNotice>> = flow {
        emit(
            hustHoleApiService.getReplyNotice(
                offset = lastOffset
            )
        )
    }.onEach {
        lastOffset += LIST_SIZE
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        state.value = LoadStatus.LOADING
    }.onCompletion {
        state.value = LoadStatus.DONE
    }

}
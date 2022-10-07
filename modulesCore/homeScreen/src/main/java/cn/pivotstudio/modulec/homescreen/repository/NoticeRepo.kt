package cn.pivotstudio.modulec.homescreen.repository

import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.model.ReplyNotice
import cn.pivotstudio.modulec.homescreen.model.Notice
import cn.pivotstudio.modulec.homescreen.model.NoticeResponse
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoticeRepo(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastOffset: Int = 0
) {

    companion object {
        const val TAG = "NoticeRepo"
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
        const val SORT_BY_LATEST_REPLY = true
    }

    var state = MutableLiveData<LoadStatus?>()

    private var lastStartId = ForestRepository.STARTING_ID

    fun loadReplies(stateFlow: MutableStateFlow<List<Notice>>) {
        HomeScreenNetworkApi.retrofitService
            .searchNotices(STARTING_ID, LIST_SIZE)
            .compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<NoticeResponse>() {
                override fun onSuccess(result: NoticeResponse) {
                    CoroutineScope(Dispatchers.IO).launch {
                        result.notices?.let { stateFlow.emit(it) }
                        lastStartId = STARTING_ID
                    }
                    state.value = LoadStatus.DONE
                }

                override fun onFailure(e: Throwable?) {
                    state.value = LoadStatus.ERROR
                }

            })
    }

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

    fun loadMore(stateFlow: MutableStateFlow<List<Notice>>) {
        val newItems = stateFlow.value.toMutableList()
        HomeScreenNetworkApi.retrofitService
            .searchNotices(lastStartId + LIST_SIZE, LIST_SIZE)
            .compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<NoticeResponse>() {
                override fun onSuccess(result: NoticeResponse) {
                    state.value = LoadStatus.DONE
                    result.notices?.let {
                        newItems.addAll(it)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        stateFlow.emit(newItems)
                    }
                    lastStartId += LIST_SIZE
                }

                override fun onFailure(e: Throwable?) {
                    state.value = LoadStatus.ERROR
                }

            })
    }

}
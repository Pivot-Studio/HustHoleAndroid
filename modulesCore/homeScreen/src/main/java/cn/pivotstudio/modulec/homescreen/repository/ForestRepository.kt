package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.husthole.moduleb.network.model.ForestHole
import cn.pivotstudio.husthole.moduleb.network.model.ForestHoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus.ERROR
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus.LOADING
import cn.pivotstudio.moduleb.libbase.constant.Constant
import io.reactivex.Observable
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
        const val SORT_BY_LATEST_REPLY = true
    }

    private var lastStartId = STARTING_ID

    private var lastOffset = INITIAL_OFFSET

    private var _holeState = MutableLiveData<LoadStatus?>()
    private var _headerLoadState = MutableLiveData<LoadStatus>()
    private var _holes = MutableLiveData<List<ForestHole>>()
    private var _headers = MutableLiveData<ForestHeads>()

    val tip = MutableLiveData<String?>()
    val headers = _headers
    val holes = _holes
    val holeState = _holeState
    val headerLoadState = _headerLoadState

    fun loadForestHoles() {
        _holeState.value = LOADING
        retrofitService.searchForestHoles(STARTING_ID, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(items: List<ForestHole>) {
                    holes.value = items
                    lastStartId = STARTING_ID
                    _holeState.value = if (items.isNotEmpty()) LoadStatus.DONE else null
                }

                override fun onFailure(e: Throwable?) {
                    _holeState.value = ERROR
                }
            }))
    }

    fun loadForestHolesV2(): Flow<List<ForestHoleV2>> {
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
            refreshTimestamp()
        }
    }


    fun loadMoreForestHoles() {
        _holeState.value = LOADING
        retrofitService.searchForestHoles(
            lastStartId + HOLES_LIST_SIZE, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY
        ).compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
            override fun onSuccess(result: List<ForestHole>) {
                val newItems = holes.value!!.toMutableList()
                val offset = result.indexOfFirst {
                    it.holeId == newItems.last().holeId
                } // 找第一个相同的index，没有相同的元素会返回 -1
                newItems.addAll(result.subList(offset + 1, result.lastIndex))
                holes.value = newItems
                lastStartId += HOLES_LIST_SIZE
                _holeState.value = if (newItems.isNotEmpty()) LoadStatus.DONE else null
            }

            override fun onFailure(e: Throwable?) {
                _holeState.value = ERROR
            }

        }))
    }

    fun loadMoreForestHolesV2(): Flow<List<ForestHoleV2>> = flow {
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

    fun loadForestHeader() {
        retrofitService.searchForestHeads(STARTING_ID, HEADS_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestHeads>() {
                override fun onSuccess(items: ForestHeads?) {
                    headers.value = items
                }

                override fun onFailure(e: Throwable?) {
                }

            }))
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

    fun giveALikeToTheHole(hole: Hole) {
        (hole as ForestHole).let {
            val observable: Observable<MsgResponse> = if (!it.liked) {
                retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            } else {
                retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            }
            observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId == newHole.holeId) newItems[i] = newHole.copy().apply {
                            likeNum = if (!it.liked) likeNum.inc() else likeNum.dec()
                            liked = liked.not()
                        }
                    }
                    _holes.value = newItems
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable) {
                    tip.value = "❌"
                }
            }))
        }
    }

    fun followTheHole(hole: ForestHole) {
        val observable: Observable<MsgResponse> = if (!hole.followed) {
            retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
        } else {
            retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
        }
        observable.compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId == newHole.holeId) newItems[i] = newHole.copy().apply {
                            followNum = if (!hole.followed) followNum.inc() else followNum.dec()
                            followed = followed.not()
                        }
                    }
                    _holes.value = newItems
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable) {
                    tip.value = "❌"
                }
            })

    }

    // 只有自己的树洞可以删除
    fun deleteTheHole(hole: ForestHole) {
        if (hole.isMine) {
            retrofitService.deleteHole(hole.holeId.toString())
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(response: MsgResponse) {
                        val newItems = _holes.value!!.toMutableList()
                        newItems.remove(hole)
                        _holes.value = newItems
                        tip.value = response.msg
                    }

                    override fun onFailure(e: Throwable?) {
                        tip.value = "❌"
                    }
                })
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
package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.model.Hole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus.ERROR
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus.LOADING
import cn.pivotstudio.moduleb.libbase.constant.Constant
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


enum class LoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestRepository {
    private var _holeState = MutableLiveData<LoadStatus>()
    private var _headerLoadState = MutableLiveData<LoadStatus>()

    private var _forestHoles = MutableLiveData<List<ForestHole>>()
    private var _forestHeads = MutableLiveData<ForestHeads>()
    private var lastStartId = STARTING_ID

    val forestHeads = _forestHeads
    val forestHoles = _forestHoles
    val holeState = _holeState
    val headerLoadState = _headerLoadState

    val loadToast = MutableSharedFlow<String>()
    val changedHoleSharedFlow = MutableSharedFlow<Int>()

    fun loadForestHoles() {
        _holeState.value = LOADING
        retrofitService.searchForestHoles(STARTING_ID, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(items: List<ForestHole>) {
                    forestHoles.value = items
                    lastStartId = STARTING_ID
                    _holeState.value = LoadStatus.DONE
                }

                override fun onFailure(e: Throwable?) {
                    _holeState.value = ERROR
                }
            }))
    }

    fun loadMoreForestHoles() {
        _holeState.value = LOADING
        retrofitService.searchForestHoles(
            lastStartId + HOLES_LIST_SIZE,
            HOLES_LIST_SIZE,
            SORT_BY_LATEST_REPLY
        ).compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
            override fun onSuccess(result: List<ForestHole>) {
                val newItems = forestHoles.value!!.toMutableList()
                newItems.addAll(result)
                forestHoles.value = newItems
                lastStartId += HOLES_LIST_SIZE
                _holeState.value = LoadStatus.DONE
            }

            override fun onFailure(e: Throwable?) {
                _holeState.value = ERROR
            }

        }))
    }

    fun loadForestHeads() {
        retrofitService.searchForestHeads(STARTING_ID, HEADS_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestHeads>() {
                override fun onSuccess(items: ForestHeads?) {
                    forestHeads.value = items
                }

                override fun onFailure(e: Throwable?) {
                }

            }))
    }

    fun giveALikeToTheHole(hole: Hole) {
        (hole as ForestHole).let { it ->
            val observable: Observable<MsgResponse> = if (!it.liked) {
                retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            } else {
                retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            }
            observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    if (!it.liked) it.likeNum ++
                    else it.likeNum--
                    it.liked = !it.liked

                    CoroutineScope(Dispatchers.Main).launch {
                        loadToast.emit(
                            msg.msg
                        )
                        _forestHoles.value?.indexOfFirst { firstHole ->
                            it.holeId == firstHole.holeId
                        }?.let { index ->
                            changedHoleSharedFlow.emit(
                                index
                            )
                        }
                    }
                }

                override fun onFailure(e: Throwable) {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadToast.emit("❌")
                    }
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
        observable
            .compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (hole.followed) hole.followNum.dec()
                        else hole.followNum.inc()
                        hole.followed = hole.followed.not()
                        loadToast.emit(
                            msg.msg
                        )
                        _forestHoles.value?.indexOfFirst { firstHole ->
                            hole.holeId == firstHole.holeId
                        }?.let { index ->
                            changedHoleSharedFlow.emit(
                                index
                            )
                        }
                    }
                }

                override fun onFailure(e: Throwable) {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadToast.emit(
                            "❌"
                        )
                    }
                }
            })

    }

    // 只有自己的树洞可以删除
    fun deleteTheHole(hole: ForestHole) {
        if (hole.isMine) {
            retrofitService.deleteHole(hole.holeId.toString())
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(successMsg: MsgResponse) {
                        CoroutineScope(Dispatchers.IO).launch {
                            loadToast.emit(
                                successMsg.msg
                            )
                        }
                    }

                    override fun onFailure(e: Throwable?) {
                        CoroutineScope(Dispatchers.IO).launch {
                            loadToast.emit(
                                "❌"
                            )
                        }
                    }
                })
        }
    }


    companion object {
        const val TAG = "ForestRepositoryDebug"
        const val STARTING_ID = 0
        const val HOLES_LIST_SIZE = 20
        const val HEADS_LIST_SIZE = 20
        const val SORT_BY_LATEST_REPLY = true
    }

}
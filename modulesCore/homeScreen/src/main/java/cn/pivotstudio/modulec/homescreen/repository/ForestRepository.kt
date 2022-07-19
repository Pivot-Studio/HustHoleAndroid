package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import com.example.libbase.constant.Constant
import io.reactivex.Observable


enum class LoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestRepository {
    private var _holeState = MutableLiveData<LoadStatus>()
    private var _forestHoles = MutableLiveData<List<ForestHole>>()
    private var _forestHeads = MutableLiveData<ForestHeads>()
    private var lastStartId = STARTING_ID

    val forestHeads = _forestHeads
    val forestHoles = _forestHoles
    val state = _holeState


    fun loadForestHoles() {
        _holeState.value = LoadStatus.LOADING
        retrofitService
            .searchForestHoles(STARTING_ID, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(items: List<ForestHole>) {
                    forestHoles.value = items
                    lastStartId = STARTING_ID
                    _holeState.value = LoadStatus.DONE
                }

                override fun onFailure(e: Throwable?) {
                    _holeState.value = LoadStatus.ERROR
                }
            }))
    }

    fun loadMoreForestHoles() {
        _holeState.value = LoadStatus.LOADING
        retrofitService
            .searchForestHoles(lastStartId + HOLES_LIST_SIZE, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(result: List<ForestHole>) {
                    val newItems = forestHoles.value!!.toMutableList()
                    newItems.addAll(result)
                    forestHoles.value = newItems
                    lastStartId += HOLES_LIST_SIZE
                    _holeState.value = LoadStatus.DONE
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                    _holeState.value = LoadStatus.ERROR
                }

            }))
    }

    fun loadForestHeads() {
        retrofitService
            .searchForestHeads(STARTING_ID, HEADS_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestHeads>() {
                override fun onSuccess(items: ForestHeads?) {
                    forestHeads.value = items
                }

                override fun onFailure(e: Throwable?) {
                }

            }))
    }

    fun giveALikeToTheHole(hole: ForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.liked) {
                retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            } else {
                retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            }
            observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    Log.d(TAG, "onSuccess: ${msg.msg}")
                }

                override fun onFailure(e: Throwable) {
                    Log.d(TAG, "点赞失败")
                }
            }))
        }
    }

    fun followTheHole(hole: ForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.followed) {
                retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
            } else {
                retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
            }
            observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    Log.d(TAG, "onSuccess: ${msg.msg}")
                }

                override fun onFailure(e: Throwable) {
                    Log.d(TAG, "关注失败")
                }
            }))
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
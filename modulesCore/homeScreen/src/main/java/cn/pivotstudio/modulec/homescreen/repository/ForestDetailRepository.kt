package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import com.example.libbase.constant.Constant
import io.reactivex.Observable

enum class ForestDetailHolesLoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestDetailRepository {
    private var _holes = MutableLiveData<List<DetailForestHole>>()
    private var _state = MutableLiveData<ForestDetailHolesLoadStatus>()
    private var _overview = MutableLiveData<ForestCard>()
    private var lastStartId = STARTING_ID

    val holes = _holes
    val state = _state
    val overview = _overview

    fun loadHolesByForestId(id: Int) {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        HomeScreenNetworkApi.retrofitService
            .searchDetailForestHolesByForestId(id, STARTING_ID, LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<DetailForestHole>>() {
                override fun onSuccess(result: List<DetailForestHole>?) {
                    holes.value = result
                    lastStartId = STARTING_ID
                    _state.value = ForestDetailHolesLoadStatus.DONE
                }
                override fun onFailure(e: Throwable?) {
                    _state.value = ForestDetailHolesLoadStatus.ERROR
                }
            }))
    }

    fun loadMoreHolesByForestId(id: Int) {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        HomeScreenNetworkApi.retrofitService
            .searchDetailForestHolesByForestId(id, lastStartId + LIST_SIZE, LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<DetailForestHole>>() {
                override fun onSuccess(result: List<DetailForestHole>) {
                    val newItems = holes.value!!.toMutableList()
                    newItems.addAll(result)
                    holes.value = newItems
                    lastStartId += LIST_SIZE
                    _state.value = ForestDetailHolesLoadStatus.DONE
                }

                override fun onFailure(e: Throwable?) {
                    _state.value = ForestDetailHolesLoadStatus.ERROR
                }
            }))
    }

    fun loadOverviewByForestId(id: Int) {
        HomeScreenNetworkApi.retrofitService
            .searchDetailForestOverviewByForestId(id)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestCardList>() {
                override fun onSuccess(result: ForestCardList) {
                    overview.value = result.forests[0]
                    _state.value = ForestDetailHolesLoadStatus.LOADING
                }

                override fun onFailure(e: Throwable?) {
                    _state.value = ForestDetailHolesLoadStatus.ERROR
                }

            }))
    }

    fun giveALikeToTheHole(hole: DetailForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.liked) {
                HomeScreenNetworkApi.retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            } else {
                HomeScreenNetworkApi.retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
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

    fun followTheHole(hole: DetailForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.followed) {
                HomeScreenNetworkApi.retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
            } else {
                HomeScreenNetworkApi.retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
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

    fun giveALikeToTheHole(hole: DetailForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.liked) {
                HomeScreenNetworkApi.retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
            } else {
                HomeScreenNetworkApi.retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + it.holeId + "/-1")
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

    fun followTheHole(hole: DetailForestHole) {
        hole.let {
            val observable: Observable<MsgResponse> = if (!it.followed) {
                HomeScreenNetworkApi.retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
            } else {
                HomeScreenNetworkApi.retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
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
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
        const val TAG = "ForestDetailRepository"
    }

}
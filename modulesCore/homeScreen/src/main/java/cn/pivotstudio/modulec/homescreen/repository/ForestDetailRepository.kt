package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.model.Hole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import io.reactivex.Observable

enum class ForestDetailHolesLoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestDetailRepository {
    private var _holes = MutableLiveData<List<DetailForestHole>>()
    private var _state = MutableLiveData<ForestDetailHolesLoadStatus>()
    private var _overview = MutableLiveData<ForestCard>()
    private var lastStartId = STARTING_ID

    val tip = MutableLiveData<String?>()
    val holes = _holes
    val state = _state

    val overview = _overview

    fun loadHolesByForestId(id: Int) {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        HomeScreenNetworkApi.retrofitService.searchDetailForestHolesByForestId(
            id, STARTING_ID, LIST_SIZE
        ).compose(NetworkApi.applySchedulers(object : BaseObserver<List<DetailForestHole>>() {
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
        HomeScreenNetworkApi.retrofitService.searchDetailForestHolesByForestId(
            id, lastStartId + LIST_SIZE, LIST_SIZE
        ).compose(NetworkApi.applySchedulers(object : BaseObserver<List<DetailForestHole>>() {
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
        HomeScreenNetworkApi.retrofitService.searchDetailForestOverviewByForestId(id)
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
        if (!hole.liked) {
            HomeScreenNetworkApi.retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        } else {
            HomeScreenNetworkApi.retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        }.apply {
            compose(NetworkApi.applySchedulers()).subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId == newHole.holeId) newItems[i] = newHole.copy().apply {
                            likeNum.inc()
                            liked = liked.not()
                        }
                    }
                    _holes.value = newItems
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable?) {
                    tip.value = "❌"
                }

            })
        }


    }

    fun followTheHole(hole: DetailForestHole) {
        val observable: Observable<MsgResponse> = if (!hole.followed) {
            HomeScreenNetworkApi.retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
        } else {
            HomeScreenNetworkApi.retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
        }
        observable.compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId == newHole.holeId) newItems[i] = newHole.copy().apply {
                            followNum.inc()
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

    fun deleteTheHole(hole: Hole) {
        (hole as DetailForestHole).takeIf {
            it.isMine
        }?.let {
            HomeScreenNetworkApi.retrofitService.deleteHole(it.holeId.toString())
                .compose(NetworkApi.applySchedulers())
                .subscribe(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(response: MsgResponse) {
                        val newItems = _holes.value!!.toMutableList()
                        newItems.remove(it)
                        _holes.value = newItems
                        tip.value = response.msg
                    }

                    override fun onFailure(e: Throwable?) {
                        tip.value = "❌"
                    }
                })
        }
    }

    fun joinTheForest(forestId: Int) {
        HomeScreenNetworkApi.retrofitService.joinTheForest(forestId.toString())
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    _overview.value?.Joined = true
                    tip.value = "加入成功"
                }

                override fun onFailure(e: Throwable?) {
                    tip.value = "❌"
                }
            }))
    }

    fun quitTheForest(forestId: Int) {
        HomeScreenNetworkApi.retrofitService.quitTheForest(forestId.toString())
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    _overview.value?.Joined = false
                    tip.value = "退出小树林"
                }

                override fun onFailure(e: Throwable?) {
                    tip.value = "❌"
                }
            }))
    }

    companion object {
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
        const val TAG = "ForestDetailRepository"
    }

}
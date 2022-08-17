package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHoleV2
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHole
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

enum class ForestDetailHolesLoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestDetailRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val forestId: Int,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    private var _holes = MutableLiveData<List<DetailForestHole>>()
    private var _state = MutableLiveData<ForestDetailHolesLoadStatus>()
    private var _overview = MutableLiveData<ForestCard>()
    var lastStartId = STARTING_ID

    val tip = MutableLiveData<String?>()
    val holes = _holes
    var state = _state

    val overview = _overview



    suspend fun loadHolesByForestId(id: Int): Flow<List<DetailForestHoleV2>> {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        return flow {
            emit(HustHoleApi.retrofitService.getHolesInForest(
                forestId = id,
                timestamp = lastTimeStamp
            ))
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
            _state.value = ForestDetailHolesLoadStatus.ERROR
        }.onEach { lastTimeStamp = DateUtil.getDateTime() }
    }

    fun loadMoreHolesByForestId(id: Int): Flow<List<DetailForestHoleV2>> {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        return flow {
            emit(
                HustHoleApi.retrofitService
                    .getHolesInForest(
                        forestId = id,
                        offset = lastStartId,
                        timestamp = lastTimeStamp
                    )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
            _state.value = ForestDetailHolesLoadStatus.ERROR
        }
    }

    suspend fun loadBriefByForestId(): Flow<ForestBrief> {
        return flow {
            emit(hustHoleApiService.getForestOverview(forestId))
        }
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

    fun giveALikeToTheHole(hole: DetailForestHoleV2) {
        if (!hole.liked) {
            HomeScreenNetworkApi.retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        } else {
            HomeScreenNetworkApi.retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        }.apply {
            compose(NetworkApi.applySchedulers()).subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId.toInt() == newHole.holeId) newItems[i] =
                            newHole.copy().apply {
                                likeNum = if (!hole.liked) likeNum.inc() else likeNum.dec()
                                liked = liked.not()
                            }
                    }
                    _holes.value = newItems
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable?) {
                    tip.value = "点赞失败"
                }

            })
        }


    }

    fun followTheHole(hole: DetailForestHoleV2) {
        val observable: Observable<MsgResponse> = if (!hole.isFollow) {
            HomeScreenNetworkApi.retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
        } else {
            HomeScreenNetworkApi.retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
        }
        observable.compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    val newItems = _holes.value!!.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId.toInt() == newHole.holeId) newItems[i] =
                            newHole.copy().apply {
                                followNum = if (!hole.isFollow) followNum.inc() else followNum.dec()
                                followed = followed.not()
                            }
                    }
                    _holes.value = newItems
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable) {
                    tip.value = "收藏失败"
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
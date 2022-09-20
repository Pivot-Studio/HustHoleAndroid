package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.model.*
import cn.pivotstudio.moduleb.libbase.constant.Constant
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
    private val forestId: String,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    private var _state = MutableLiveData<ForestDetailHolesLoadStatus>()
    var lastStartId = STARTING_ID

    val tip = MutableLiveData<String?>()
    var state = _state

    suspend fun loadHolesByForestId(id: String): Flow<List<HoleV2>> {
        _state.value = ForestDetailHolesLoadStatus.LOADING
        return flow {
            emit(
                HustHoleApi.retrofitService.getHolesInForest(
                    forestId = id,
                    timestamp = lastTimeStamp,
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
            _state.value = ForestDetailHolesLoadStatus.ERROR
        }.onEach { lastTimeStamp = DateUtil.getDateTime() }
    }

    fun loadMoreHolesByForestId(id: String): Flow<List<HoleV2>> {
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

    fun giveALikeToTheHole(hole: HoleV2) {
        if (!hole.liked) {
            HomeScreenNetworkApi.retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        } else {
            HomeScreenNetworkApi.retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + hole.holeId + "/-1")
        }.apply {
            compose(NetworkApi.applySchedulers()).subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
                    tip.value = response.msg
                }

                override fun onFailure(e: Throwable?) {
                    tip.value = "点赞失败"
                }

            })
        }


    }

    fun followTheHole(hole: HoleV2) {
        val observable: Observable<MsgResponse> = if (!hole.isFollow) {
            HomeScreenNetworkApi.retrofitService.follow(Constant.BASE_URL + "follows/" + hole.holeId)
        } else {
            HomeScreenNetworkApi.retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole.holeId)
        }
        observable.compose(NetworkApi.applySchedulers())
            .subscribe(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(response: MsgResponse) {
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

                    }

                    override fun onFailure(e: Throwable?) {
                        tip.value = "❌"
                    }
                })
        }
    }

    fun joinTheForestV2(): Flow<Boolean> {
        return flow {
            val response =
                hustHoleApiService.joinTheForestBy(forestId = RequestBody.ForestId(forestId.toString()))
            if (response.isSuccessful) {
                emit(true)
            }
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    fun quitTheForestV2(): Flow<Boolean> = flow {
        val response = hustHoleApiService.quitTheForestBy(
            forestId = RequestBody.ForestId(forestId)
        )
        if (response.isSuccessful) {
            emit(false)
        }
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }


    companion object {
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
        const val TAG = "ForestDetailRepository"
    }

}
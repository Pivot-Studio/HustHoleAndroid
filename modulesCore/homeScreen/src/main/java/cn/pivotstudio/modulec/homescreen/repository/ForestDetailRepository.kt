package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi

enum class ForestDetailHolesLoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestDetailRepository {
    private var _holes = MutableLiveData<List<DetailForestHole>>()
    private var _state: ForestDetailHolesLoadStatus? = null
    private var _overview = MutableLiveData<ForestCard>()

    val holes = _holes
    val state = _state
    val overview = _overview

    fun loadHolesByForestId(id: Int) {
        HomeScreenNetworkApi.retrofitService
            .searchDetailForestHolesByForestId(id, STARTING_ID, LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<DetailForestHole>>() {
                override fun onSuccess(result: List<DetailForestHole>?) {
                    holes.value = result
                    _state = ForestDetailHolesLoadStatus.LOADING
                }
                override fun onFailure(e: Throwable?) {
                    _state = ForestDetailHolesLoadStatus.ERROR
                }
            }))
    }

    fun loadOverviewByForestId(id: Int) {
        HomeScreenNetworkApi.retrofitService
            .searchDetailForestOverviewByForestId(id)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestCardList>() {
                override fun onSuccess(result: ForestCardList) {
                    overview.value = result.forests[0]
                    _state = ForestDetailHolesLoadStatus.LOADING
                }

                override fun onFailure(e: Throwable?) {
                    _state = ForestDetailHolesLoadStatus.ERROR
                }

            }))
    }

    companion object {
        const val STARTING_ID = 0
        const val LIST_SIZE = 20
    }

}
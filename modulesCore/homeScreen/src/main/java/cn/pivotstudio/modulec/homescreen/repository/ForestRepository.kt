package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi

const val TAG = "ForestRepository1"

enum class ForestHoleStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestRepository {
    private var _state: ForestHoleStatus? = null
    val state = _state

    private var _forestHoles = MutableLiveData<List<ForestHole>>()
    val forestHoles = _forestHoles

    fun loadForestHoles() {
        _state = ForestHoleStatus.LOADING
        Log.d(TAG, "Loading")
        HomeScreenNetworkApi.retrofitService
            .searchForestHoles(STARTING_ID, LIST_SIZE, sortByLatestReply)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                @SuppressLint("CheckResult")
                override fun onSuccess(items: List<ForestHole>) {
                    _state = ForestHoleStatus.DONE
                    forestHoles.value = items
                    Log.d(TAG, "onSuccess: ${items.size}" )
                }

                override fun onFailure(e: Throwable?) {
                    _state = ForestHoleStatus.ERROR
                }
            }))
    }

    companion object {
        const val STARTING_ID = 0
        const val LIST_SIZE = 10
        const val sortByLatestReply = true
    }

}
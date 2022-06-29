package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import com.example.libbase.constant.Constant
import io.reactivex.Observable
import kotlinx.coroutines.flow.*

const val TAG = "ForestRepositoryDebug"

enum class ForestHoleStatus { LOADING, ERROR, DONE }
enum class ForestHeadStatus { LOADING, ERROR, DONE }
enum class LoadStatus { LOADING, ERROR, DONE }

@SuppressLint("CheckResult")
class ForestRepository {
    private var _holeState: ForestHoleStatus? = null
    private var _headState: ForestHeadStatus? = null
    private var _forestHoles = MutableLiveData<List<ForestHole>>()
    private var _forestHeads = MutableLiveData<ForestHeads>()
    private var lastStartId = STARTING_ID

    val forestHeads = _forestHeads
    val forestHoles = _forestHoles
    val state: LoadStatus
        get() {
            if (_holeState == ForestHoleStatus.LOADING && _headState == ForestHeadStatus.LOADING)
                return LoadStatus.DONE

            if (_holeState == ForestHoleStatus.ERROR || _headState == ForestHeadStatus.ERROR)
                return LoadStatus.ERROR

            return LoadStatus.LOADING
        }

    fun loadForestHoles() {
        _holeState = ForestHoleStatus.LOADING
        HomeScreenNetworkApi.retrofitService
            .searchForestHoles(STARTING_ID, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(items: List<ForestHole>) {
                    _holeState = ForestHoleStatus.DONE
                    forestHoles.value = items
                }

                override fun onFailure(e: Throwable?) {
                    _holeState = ForestHoleStatus.ERROR
                }
            }))
    }

    fun loadMoreForestHoles() {
        retrofitService
            .searchForestHoles(lastStartId + HOLES_LIST_SIZE, HOLES_LIST_SIZE, SORT_BY_LATEST_REPLY)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestHole>>() {
                override fun onSuccess(result: List<ForestHole>) {
                    val newItems = forestHoles.value!!.toMutableList()
                    newItems.addAll(result)
                    forestHoles.value = newItems
                    lastStartId += HOLES_LIST_SIZE
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                }

            }))
    }

    fun loadForestHeads() {
        _headState = ForestHeadStatus.LOADING
        HomeScreenNetworkApi.retrofitService
            .searchForestHeads(STARTING_ID, HEADS_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestHeads>() {
                override fun onSuccess(items: ForestHeads?) {
                    _headState = ForestHeadStatus.DONE
                    forestHeads.value = items
                }

                override fun onFailure(e: Throwable?) {
                    _headState = ForestHeadStatus.ERROR
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
                    e.printStackTrace()
                }
            }))
        }
    }

    companion object {
        const val STARTING_ID = 0
        const val HOLES_LIST_SIZE = 10
        const val HEADS_LIST_SIZE = 20
        const val SORT_BY_LATEST_REPLY = true
    }

}
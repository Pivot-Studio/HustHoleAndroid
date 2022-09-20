package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.model.ForestTypes
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.internal.wait

@SuppressLint("CheckResult")
class AllForestRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    private var _forestCards = HashSet<Pair<String, ForestCardList>>()
    val loadState = MutableLiveData<LoadStatus?>()
    val forestCards = _forestCards

    fun loadAllForests(): Flow<List<ForestBrief>> = flow {
        emit(
            hustHoleApiService.getAllForests(
                descend = true,
                limit = FOREST_SIZE,
                offset = 0,
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        refreshTimestamp()
    }

    private fun refreshTimestamp() {
        lastTimeStamp = DateUtil.getDateTime()
    }

    companion object {
        const val FOREST_SIZE = 40
        const val TAG = "AllForestRepository"
    }
}
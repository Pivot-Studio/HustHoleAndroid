package cn.pivotstudio.modulec.homescreen.repository

import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.HustHoleApiService
import cn.pivotstudio.moduleb.rebase.network.model.ForestBrief
import cn.pivotstudio.moduleb.rebase.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class AllForestRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {
    val loadState = MutableLiveData<LoadStatus?>()

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
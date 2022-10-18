package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ProFile
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 *@classname MineRepository
 * @description:
 * @date :2022/10/11 21:45
 * @version :1.0
 * @author small fish
 */
@SuppressLint("CheckResult")
class MineRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime(),
    private var lastOffset: Int = HomePageHoleRepository.INITIAL_OFFSET
) {
    fun getProfile(): Flow<ProFile> = flow {
        emit(
            hustHoleApiService.getProFile()
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }


}
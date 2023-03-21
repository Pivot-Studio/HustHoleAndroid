package cn.pivotstudio.modulec.homescreen.repository

import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class HomeHoleRepository {
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private var lastTimeStamp: String = DateUtil.getDateTime()
    private var lastOffset: Int = HomePageHoleRepository.INITIAL_OFFSET
}
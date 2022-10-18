package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


/**
 *@classname HoleFollowReplyRepository
 * @description:
 * @date :2022/10/12 21:32
 * @version :1.0
 * @author
 */
@SuppressLint("CheckResult")
class HoleFollowReplyRepository  {
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private var holeOffset: Int = 0
    private var followOffset: Int = 0
    private var replyOffset: Int = 0

    var tip = MutableLiveData<String?>()

    fun getMyHole(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getMyHole(refreshTimestamp())
        )
    }.flowOn(dispatcher).onEach {
        holeOffset = 0
    }

    fun getMyFollow(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getMyFollow()
        )
    }.flowOn(dispatcher).onEach {
        followOffset = 0
    }

    fun getMyReply(): Flow<List<Reply>> = flow {
        emit(
            hustHoleApiService.getMyReply(refreshTimestamp())
        )
    }.flowOn(dispatcher).onEach {
        replyOffset = 0
    }

    fun loadMoreHole(): Flow<List<HoleV2>> = flow {
        holeOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyHole(
                refreshTimestamp(),
                holeOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadMoreFollow(): Flow<List<HoleV2>> = flow {
        followOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyFollow(
                followOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadMoreReply(): Flow<List<Reply>> = flow {
        replyOffset += CONSTANT_STANDARD_LOAD_SIZE
        emit(
            hustHoleApiService.getMyReply(
                refreshTimestamp(),
                replyOffset
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    private fun refreshTimestamp(): String {
        return DateUtil.getDateTime()
    }
}
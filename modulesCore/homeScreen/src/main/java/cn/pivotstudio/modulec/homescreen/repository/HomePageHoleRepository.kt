package cn.pivotstudio.modulec.homescreen.repository

import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.constant.Constant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.ArrayList

/**
 * @classname: HomePageHoleResponse
 * @description:
 * @date: 2022/5/3 22:55
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class HomePageHoleRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastTimeStamp: String = DateUtil.getDateTime(),
    private var lastOffset: Int = INITIAL_OFFSET
) {

    companion object {
        const val TAG = "HomePageHoleRepository"
        const val INITIAL_OFFSET = 0
        const val HOLES_LIST_SIZE = 20
    }

    var pHomePageHoles = MutableLiveData<HomepageHoleResponse>()
    var tip = MutableLiveData<String?>()

    fun loadHoles(sortMode: String): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                mode = sortMode,
                timestamp = lastTimeStamp
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        refreshTimestamp()
        lastOffset = 0
    }

    fun loadMoreHoles(sortMode: String): Flow<List<HoleV2>> = flow {
        lastOffset += HOLES_LIST_SIZE
        emit(
            hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                timestamp = lastTimeStamp,
                offset = lastOffset,
                mode = sortMode
            )
        )
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun searchHolesBy(queryKey: String) = flow {
        emit(hustHoleApiService.searchHolesByKey(
            key = queryKey,
        ))
    }.onEach {
        lastOffset = 0
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun loadMoreSearchHoles(queryKey: String) = flow {
        emit(hustHoleApiService.searchHolesByKey(
            key = queryKey,
            offset = lastOffset
        ))
    }.onEach {
        lastOffset += HOLES_LIST_SIZE
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun giveALikeToTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = if (hole.liked) {
                hustHoleApiService.unLikeTheHole(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            } else {
                hustHoleApiService.giveALikeToTheHole(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            }

            if (response.isSuccessful) {
                emit(ApiResult.Success(data = Unit))
            } else {
                emit(
                    ApiResult.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string()
                    )
                )
                response.errorBody()?.close()
            }
        }.flowOn(dispatcher)
    }

    fun followTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = hustHoleApiService
                .followTheHole(RequestBody.HoleId(hole.holeId))

            if (response.isSuccessful) {
                emit(ApiResult.Success(data = Unit))
            } else {
                emit(
                    ApiResult.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string()
                    )
                )
                response.errorBody()?.close()
            }
        }.flowOn(dispatcher)
    }

    fun unFollowTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .unFollowTheHole(RequestBody.HoleId(hole.holeId))

        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            emit(
                ApiResult.Error(
                    code = response.code(),
                    errorMessage = response.errorBody()?.string()
                )
            )
            response.errorBody()?.close()
        }
    }.flowOn(dispatcher)

    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)

        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            emit(
                ApiResult.Error(
                    code = response.code(),
                    errorMessage = response.errorBody()?.string()
                )
            )
            response.errorBody()?.close()
        }
    }

    /**
     * 搜索单个树洞
     *
     * @param et 树洞号
     */
    fun searchSingleHoleForNetwork(et: String) {
        retrofitService.searchSingleHole(Constant.BASE_URL + "holes/" + et)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<DataBean>() {
                override fun onSuccess(requestedData: DataBean) {
                    //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                    requestedData.is_last_reply = false
                    //封装为list
                    val requestDataList: MutableList<DataBean> = ArrayList()
                    requestDataList.add(requestedData)

                    //手动补充为完整response
                    val homepageHoleResponse = HomepageHoleResponse()
                    homepageHoleResponse.data = requestDataList
                    homepageHoleResponse.model = "SEARCH_HOLE"
                    pHomePageHoles.setValue(homepageHoleResponse)
                }

                override fun onFailure(e: Throwable) {
                    tip.value = (e as ResponseThrowable).message
                }
            }))
    }

    fun loadTheHole(hole: HoleV2): Flow<HoleV2> {
        return flow {
            emit(hustHoleApiService.loadTheHole(hole.holeId))
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

    /**
     * 举报或删除
     *
     * @param hole_id 树洞号
     * @param is_mine 是否是自己发布的树洞
     */
    fun moreActionForNetwork(hole_id: Int, is_mine: Boolean) {
        if (is_mine) {
            retrofitService.deleteHole(hole_id.toString())
                .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                    override fun onSuccess(msg: MsgResponse) {
                        val newItems = pHomePageHoles.value!!.data.toMutableList()
                        val model = pHomePageHoles.value!!.model
                        newItems.removeIf {
                            it.hole_id == hole_id
                        }
                        pHomePageHoles.value = HomepageHoleResponse().apply {
                            data = newItems
                            this.model = model
                        }
                        tip.value = msg.msg
                    }

                    override fun onFailure(e: Throwable) {
                        tip.value = (e as ResponseThrowable).message
                    }
                }))
        } else {
            ARouter.getInstance().build("/report/ReportActivity")
                .withInt(Constant.HOLE_ID, hole_id)
                .withInt(Constant.REPLY_LOCAL_ID, -1)
                .withString(Constant.ALIAS, "洞主")
                .navigation()
        }
    }

    private fun refreshTimestamp() {
        lastTimeStamp = DateUtil.getDateTime()
    }

}
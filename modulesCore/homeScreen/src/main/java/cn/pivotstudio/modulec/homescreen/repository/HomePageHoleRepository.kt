package cn.pivotstudio.modulec.homescreen.repository

import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi.retrofitService
import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.constant.Constant
import io.reactivex.Observable
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
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {

    companion object {
        const val TAG = "HomePageHoleRepository"
        const val STARTING_ID = 0
        const val INITIAL_OFFSET = 0
        const val HOLES_LIST_SIZE = 20
    }

    var pHomePageHoles = MutableLiveData<HomepageHoleResponse>()
    var tip = MutableLiveData<String?>()

    /**
     * 获取正常状态树洞
     *
     * @param mHolesSequenceCondition 是新更新还是新发布
     * @param mStartingLoadId         起始id
     */
    fun getHolesForNetwork(mHolesSequenceCondition: Boolean, mStartingLoadId: Int) {
        retrofitService.homepageHoles(
            true,
            mHolesSequenceCondition,
            mStartingLoadId,
            Constant.CONSTANT_STANDARD_LOAD_SIZE
        ).compose(NetworkApi.applySchedulers(object :
            BaseObserver<List<DataBean>>() {
            override fun onSuccess(requestedDataList: List<DataBean>) {
                //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                for (item in requestedDataList) {
                    item.is_last_reply = mHolesSequenceCondition
                }
                if (mStartingLoadId != 0) { //上拉加载得到

                    //手动补充为完整response
                    val lastRequestedData = pHomePageHoles.value
                    lastRequestedData!!.data.addAll(requestedDataList)
                    lastRequestedData.model = "LOAD_MORE"
                    pHomePageHoles.setValue(lastRequestedData)
                } else { //下拉刷新或者搜索得到

                    //手动补充为完整response
                    val homepageHoleResponse = HomepageHoleResponse()
                    homepageHoleResponse.data = requestedDataList
                    homepageHoleResponse.model = "REFRESH"
                    pHomePageHoles.setValue(homepageHoleResponse)
                }
            }

            override fun onFailure(e: Throwable) {
                tip.value = (e as ResponseThrowable).message
            }
        }))
    }

    fun loadHoles(): Flow<List<HoleV2>> = flow {
        emit(
            hustHoleApiService.getHoles(
                limit = HOLES_LIST_SIZE,
                timestamp = lastTimeStamp
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        refreshTimestamp()
    }

    /**
     * 关键词搜索树洞
     *
     * @param et              关键词内容
     * @param mStartingLoadId 起始id
     */
    fun searchHolesForNetwork(et: String?, mStartingLoadId: Int) {
        if (et != null) {
            retrofitService.searchHoles(et, mStartingLoadId, Constant.CONSTANT_STANDARD_LOAD_SIZE)
                .compose(NetworkApi.applySchedulers(object :
                    BaseObserver<List<DataBean>>() {
                    override fun onSuccess(requestedDataList: List<DataBean>) {
                        //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                        for (item in requestedDataList) {
                            item.is_last_reply = false
                        }
                        if (mStartingLoadId != 0) { //上拉加载得到
                            //手动补充为完整response
                            val lastRequestedDataList = pHomePageHoles.value
                            lastRequestedDataList!!.data.addAll(requestedDataList)
                            lastRequestedDataList.model = "SEARCH_LOAD_MORE"
                            pHomePageHoles.setValue(lastRequestedDataList)
                        } else { //下拉刷新或者搜索得到

                            //手动补充为完整response
                            val homepageHoleResponse = HomepageHoleResponse()
                            homepageHoleResponse.data = requestedDataList
                            homepageHoleResponse.model = "SEARCH_REFRESH"
                            pHomePageHoles.setValue(homepageHoleResponse)
                        }
                    }

                    override fun onFailure(e: Throwable?) {
                        tip.value = (e as ResponseThrowable).message
                    }
                }))
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

    /**
     * 点赞
     *
     * @param holeId   树洞号
     * @param likeNum  网络请求成功前的点赞数量
     * @param liked    网络请求成功前是否被点赞
     * @param dataBean item的所有数据
     */
    fun giveALikeToAHole(holeId: Int, likeNum: Int, liked: Boolean, dataBean: DataBean) {
        val observable: Observable<MsgResponse>
        observable = if (!liked) {
            retrofitService.thumbups(Constant.BASE_URL + "thumbups/" + holeId + "/-1")
        } else {
            retrofitService.deleteThumbups(Constant.BASE_URL + "thumbups/" + holeId + "/-1")
        }
        observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
            override fun onSuccess(msg: MsgResponse) {
                if (liked) {
                    dataBean.thumbup_num = likeNum - 1
                } else {
                    dataBean.thumbup_num = likeNum + 1
                }
                dataBean.is_thumbup = !liked
                tip.value = msg.msg
            }

            override fun onFailure(e: Throwable) {
                tip.value = (e as ResponseThrowable).message
            }
        }))
    }

    /**
     * 收藏
     *
     * @param hole_id    树洞号
     * @param follow_num 网络请求成功前的收藏数量
     * @param is_follow  网络请求成功前是否被收藏
     * @param dataBean   item的所有数据
     */
    fun followForNetwork(hole_id: Int, follow_num: Int, is_follow: Boolean, dataBean: DataBean) {
        val observable: Observable<MsgResponse> = if (!is_follow) {
            retrofitService.follow(Constant.BASE_URL + "follows/" + hole_id)
        } else {
            retrofitService.deleteFollow(Constant.BASE_URL + "follows/" + hole_id)
        }
        observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
            override fun onSuccess(msg: MsgResponse) {
                if (is_follow) {
                    dataBean.follow_num = follow_num - 1
                } else {
                    dataBean.follow_num = follow_num + 1
                }
                dataBean.is_follow = !is_follow
                tip.value = msg.msg
            }

            override fun onFailure(e: Throwable) {
                tip.value = (e as ResponseThrowable).message
            }
        }))
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
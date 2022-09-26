package cn.pivotstudio.modulep.hole.repository

import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.DB
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.modulep.hole.model.HoleResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.moduleb.database.repository.CustomDisposable
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulep.hole.network.HRequestInterface
import io.reactivex.schedulers.Schedulers
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.database.bean.Hole
import cn.pivotstudio.moduleb.libbase.constant.Constant
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.util.data.GetUrlUtil
import cn.pivotstudio.modulep.hole.model.MsgResponse
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * @classname:HoleRepository
 * @description:
 * @date:2022/5/8 13:32
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class HoleRepository(
    private val holeId: String,
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private var lastOffset: Int = 0,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {

    companion object {
        const val LIST_SIZE = 20
    }

    var pHole: MutableLiveData<HoleResponse> = MutableLiveData()
    var pReplyList: MutableLiveData<ReplyListResponse> = MutableLiveData()
    var pInputText: MutableLiveData<ReplyResponse> = MutableLiveData()
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>> = MutableLiveData()
    var pClickMsg: MutableLiveData<MsgResponse> = MutableLiveData()
    var pSendReply: MutableLiveData<MsgResponse> = MutableLiveData()
    val failed: MutableLiveData<String> = MutableLiveData()
    private var hole: Hole? = null
    fun getInputTextForLocalDB(hole_id: Int?) {
        val flowable = DB!!.holeDao().findById(
            hole_id!!
        )
        CustomDisposable.addDisposable(flowable) { holeEt: Hole? ->
            if (holeEt != null) {
                hole = holeEt
                val requestedData = ReplyResponse()
                requestedData.content = holeEt.content
                requestedData.alias = holeEt.alias
                requestedData.is_mine = holeEt.is_mine
                requestedData.reply_local_id = holeEt.reply_local_id
                pInputText.postValue(requestedData)
            }
        }
    }

    val usedEmojiForLocalDB: Unit
        get() {
            val mmkvUtil = MMKVUtil.getMMKV(BaseApplication.context)
            var list = mmkvUtil.getArray(Constant.UsedEmoji, 0) as LinkedList<Int>
            if (list == null) {
                list = LinkedList()
                mmkvUtil.put(Constant.UsedEmoji, list)
                // pUsedEmojiList.postValue(new LinkedList<Integer>());
            } else {
                pUsedEmojiList.postValue(list)
            }
        }

    fun saveInputTextForLocalDB(
        hole_id: Int?,
        text: String?,
        alias: String?,
        is_mine: Boolean?,
        reply_local_id: Int?
    ) {
        val hole = Hole(
            hole_id!!, text, alias, is_mine, reply_local_id
        )
        val insert = DB!!.holeDao().insert(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据保存成功"
            )
        }
    }

    fun updateInputTextForLocalDB(
        hole_id: Int?,
        text: String?,
        alias: String?,
        is_mine: Boolean?,
        reply_local_id: Int?
    ) {
        val hole = Hole(
            hole_id!!, text, alias, is_mine, reply_local_id
        )
        val insert = DB!!.holeDao().update(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据更新成功"
            )
        }
    }

    fun deleteInputTextForLocalDB() {
        val insert = DB!!.holeDao().delete(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据删除成功"
            )
        }
    }

    fun getListForNetwork(hole_id: Int, is_descend: Boolean, start_id: Int, isOwner: Boolean) {
        val hotReplyObservable = NetworkApi.createService(
            HRequestInterface::class.java, 2
        ).getHotReply(hole_id, 0, 3)

        val repliesObservable: Observable<ReplyListResponse> = if (!isOwner) {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .getReplies(Constant.BASE_URL + "replies?hole_id=" + hole_id + "&is_descend=" + is_descend + "&start_id=" + start_id + "&list_size=" + Constant.CONSTANT_STANDARD_LOAD_SIZE)
        } else {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .getOwnerReply(hole_id, start_id, Constant.CONSTANT_STANDARD_LOAD_SIZE, is_descend)
        }
        val holeObservable = NetworkApi.createService(
            HRequestInterface::class.java, 2
        ).getHole(Constant.BASE_URL + "holes/" + hole_id)
        val result = Observable.zip(
            hotReplyObservable.subscribeOn(Schedulers.io()), repliesObservable.subscribeOn(
                Schedulers
                    .io()
            ), holeObservable.subscribeOn(Schedulers.io())
        ) { hotReplyResponse, repliesResponse, holeResponse ->
            pHole.postValue(holeResponse)
            //依据是否是只看洞主，加载热评内容
            if (isOwner || start_id != 0) {
                //设置热评标识
                for (requestedData in repliesResponse.msg) {
                    requestedData.is_hot = false
                }
                repliesResponse
            } else {
                //设置热评标识
                for (requestedData in repliesResponse.msg) {
                    requestedData.is_hot = false
                }
                for (requestedData in hotReplyResponse.msg) {
                    requestedData.is_hot = true
                }
                hotReplyResponse.msg.addAll(repliesResponse.msg)
                hotReplyResponse
            }
            //单独将树洞的信息发送出去
        }
        result.compose(NetworkApi.applySchedulers(object : BaseObserver<ReplyListResponse>() {
            override fun onSuccess(requestedDataList: ReplyListResponse) {

                // if(requestedDataList.getMsg().size()==0){//加载数据为空时
                if (start_id != 0) { //说明初始数据就是空的
                    val lastRequestedDataList = pReplyList.value
                    lastRequestedDataList!!.msg.addAll(requestedDataList.msg)
                    if (requestedDataList.msg.size == 0) { //加载数据为空时
                        lastRequestedDataList.mode = "LOAD_ALL"
                    } else {
                        lastRequestedDataList.mode = "LOAD_MORE"
                    }
                    pReplyList.setValue(lastRequestedDataList)
                } else { //下拉刷新得到的数据是空的
                    if (requestedDataList.msg.size == 0) { //加载数据为空时
                        requestedDataList.mode = "NO_REPLY"
                    } else {
                        requestedDataList.mode = "REFRESH"
                    }
                    pReplyList.setValue(requestedDataList)
                }
            }

            override fun onFailure(e: Throwable) {
                failed.postValue((e as ResponseThrowable).message)
            }
        }))
    }

    /**
     * 点赞单个评论
     *
     * @param hole_id     树洞号
     * @param thumbup_num 网络请求成功前的点赞数量
     * @param is_thumbup  网络请求成功前是否被点赞
     * @param dataBean    item的所有数据
     */
    fun thumbupReplyForNetwork(
        hole_id: Int,
        reply_local_id: Int,
        thumbup_num: Int,
        is_thumbup: Boolean,
        dataBean: ReplyResponse
    ) {
        val observable: Observable<MsgResponse>
        observable = if (!is_thumbup) {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .thumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/" + reply_local_id)
        } else {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .deleteThumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/" + reply_local_id)
        }
        observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
            override fun onSuccess(msg: MsgResponse) {
                if (is_thumbup) {
                    dataBean.thumbup_num = thumbup_num - 1
                } else {
                    dataBean.thumbup_num = thumbup_num + 1
                }
                dataBean.is_thumbup = !is_thumbup
                val isHot = dataBean.is_hot //获取当前data是否是热门的
                //从总数据中遍历
                var traversal = 0
                for (reply in pReplyList.value!!.msg) {
                    //如果当前数据的id和点赞的id相同并且不是他自己，另一个需要同步的数据的ishot一定与此相反
                    if (reply.reply_local_id == reply_local_id && reply.is_hot == !isHot) {
                        if (is_thumbup) {
                            reply.thumbup_num = thumbup_num - 1
                        } else {
                            reply.thumbup_num = thumbup_num + 1
                        }
                        reply.is_thumbup = !is_thumbup
                        break //只需要同步一种数据
                    }
                    //如果被点赞的是热评序列，就要一直从数据中查找到最后一项，直到查找到需要的值
                    //如果被点赞的不是热评序列，查询完热评后仍然没有所要的结果就说明点赞的不是热评，不需要继续查询了
                    if (traversal > 2 && !isHot) {
                        break
                    }
                    traversal++
                }
                pClickMsg.setValue(msg)
            }

            override fun onFailure(e: Throwable) {
                failed.postValue((e as ResponseThrowable).message)
            }
        }))
    }

    fun thumbupForNetwork(
        hole_id: Int,
        thumbup_num: Int,
        is_thumbup: Boolean,
        dataBean: HoleResponse
    ) {
        val observable: Observable<MsgResponse>
        observable = if (!is_thumbup) {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .thumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/-1")
        } else {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .deleteThumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/-1")
        }
        observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
            override fun onSuccess(msg: MsgResponse) {
                if (is_thumbup) {
                    dataBean.thumbup_num = thumbup_num - 1
                } else {
                    dataBean.thumbup_num = thumbup_num + 1
                }
                dataBean.is_thumbup = !is_thumbup
                pClickMsg.setValue(msg)
            }

            override fun onFailure(e: Throwable) {
                failed.postValue((e as ResponseThrowable).message)
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
    fun followForNetwork(
        hole_id: Int,
        follow_num: Int,
        is_follow: Boolean,
        dataBean: HoleResponse
    ) {
        val observable: Observable<MsgResponse>
        observable = if (!is_follow) {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .follow(Constant.BASE_URL + "follows/" + hole_id)
        } else {
            NetworkApi.createService(HRequestInterface::class.java, 2)
                .deleteFollow(Constant.BASE_URL + "follows/" + hole_id)
        }
        observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
            override fun onSuccess(msg: MsgResponse) {
                if (is_follow) {
                    dataBean.follow_num = follow_num - 1
                } else {
                    dataBean.follow_num = follow_num + 1
                }
                dataBean.is_follow = !is_follow
                pClickMsg.setValue(msg)
            }

            override fun onFailure(e: Throwable) {
                failed.postValue((e as ResponseThrowable).message)
            }
        }))
    }

    /**
     * 举报或删除
     *
     * @param hole_id 树洞号
     * @param is_mine 是否是自己发布的树洞
     */
    fun moreActionForNetwork(hole_id: Int, is_mine: Boolean, reply_local_id: Int, alias: String?) {
        val observable: Observable<MsgResponse>
        if (is_mine) {
            observable = if (reply_local_id == -1) {
                NetworkApi.createService(HRequestInterface::class.java, 2)
                    .deleteHole(hole_id.toString())
            } else {
                NetworkApi.createService(HRequestInterface::class.java, 2)
                    .deleteReply(Constant.BASE_URL + "replies/" + hole_id + "/" + reply_local_id)
            }
            observable.compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    msg.model = if (reply_local_id == -1) "DELETE_HOLE" else "DELETE_REPLY"
                    pClickMsg.value = msg
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
        } else {
            ARouter.getInstance().build("/report/ReportActivity")
                .withInt(Constant.HOLE_ID, hole_id)
                .withInt(Constant.REPLY_LOCAL_ID, reply_local_id)
                .withString(Constant.ALIAS, alias)
                .navigation()
        }
    }

    fun sendReplyForNetwork(content: String?, hole_id: Int, user_id: Int) {
        NetworkApi.createService(HRequestInterface::class.java, 2)
            .sendReply(
                Constant.BASE_URL + "replies?hole_id=" + hole_id + "&content=" + GetUrlUtil.getURLEncoderString(
                    content
                ) + "&wanted_local_reply_id=" + user_id
            )
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    pSendReply.value = msg
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }

    fun loadHole(): Flow<HoleV2> = flow {
        emit(hustHoleApiService.loadTheHole(holeId))
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun loadReplies(): Flow<List<ReplyWrapper>> = flow {
        emit(
            hustHoleApiService.getHoleReplies(
                holeId = holeId,
                timestamp = lastTimeStamp,
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onCompletion {
        lastOffset = LIST_SIZE
    }

    fun loadMoreReplies(): Flow<List<ReplyWrapper>> = flow {
        emit(
            hustHoleApiService.getHoleReplies(
                holeId = holeId,
                timestamp = lastTimeStamp,
                offset = lastOffset
            )
        )
    }.onEach {
        lastOffset += LIST_SIZE
    }.catch { e ->
        e.printStackTrace()
    }.flowOn(dispatcher)


}
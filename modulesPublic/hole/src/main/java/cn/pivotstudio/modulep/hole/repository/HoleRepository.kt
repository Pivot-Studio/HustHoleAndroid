package cn.pivotstudio.modulep.hole.repository

import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.DB
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.modulep.hole.model.HoleResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.moduleb.database.repository.CustomDisposable
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.modulep.hole.network.HRequestInterface
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.database.bean.Hole
import cn.pivotstudio.moduleb.libbase.constant.Constant
import com.alibaba.android.arouter.launcher.ARouter
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

    var pInputText: MutableLiveData<ReplyResponse> = MutableLiveData()
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>> = MutableLiveData()
    var pClickMsg: MutableLiveData<MsgResponse> = MutableLiveData()
    val failed: MutableLiveData<String> = MutableLiveData()
    private var hole: Hole? = null
    fun getInputTextForLocalDB(hole_id: Int?) {
        val flowable = DB!!.holeDao().findById(hole_id!!
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
            val list = mmkvUtil.getArray(Constant.UsedEmoji, 0) as LinkedList<Int>
            pUsedEmojiList.postValue(list)
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


    fun thumbupForNetwork(
        hole_id: Int,
        thumbup_num: Int,
        is_thumbup: Boolean,
        dataBean: HoleResponse
    ) {
        val observable: Observable<MsgResponse> = if (!is_thumbup) {
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
        val observable: Observable<MsgResponse> = if (!is_follow) {
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

    fun sendAComment(content: String, replyId: String?): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response =
            hustHoleApiService.sendAComment(comment = RequestBody.Comment(
                holeId = holeId,
                replyId = replyId,
                content = content
            ))
        if (response.isSuccessful) {
            emit(ApiResult.Success(data = Unit))
        } else {
            val errorCode = response.code()
            response.errorBody()?.close()
            emit(ApiResult.Error(code = errorCode))
        }
    }.flowOn(dispatcher)

    fun loadHole(): Flow<HoleV2> = flow {
        emit(hustHoleApiService.loadTheHole(holeId))
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }.onEach {
        lastTimeStamp = DateUtil.getDateTime()
    }

    fun loadReplies(): Flow<List<ReplyWrapper>> = flow {
        lastTimeStamp = DateUtil.getDateTime()
        emit(
            hustHoleApiService.getHoleReplies(
                holeId = holeId,
                timestamp = lastTimeStamp,
                limit = LIST_SIZE
            )
        )
    }.flowOn(dispatcher).onEach {
        lastOffset = 0
    }

    fun loadMoreReplies(): Flow<List<ReplyWrapper>> = flow {
        emit(
            hustHoleApiService.getHoleReplies(
                holeId = holeId,
                timestamp = lastTimeStamp,
                offset = lastOffset + LIST_SIZE,
                limit = LIST_SIZE
            )
        )
    }.onEach {
        lastOffset += LIST_SIZE
    }.flowOn(dispatcher)
}